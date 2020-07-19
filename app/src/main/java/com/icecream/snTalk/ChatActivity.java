package com.icecream.snTalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class ChatActivity extends AppCompatActivity {

    private ListView mListView;
    private EditText editdt;
    private Button sendbt;
    private String msg;
    private TextView mTextview;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> adapter2;
    List<String> Array = new ArrayList<String>();
    List<Object> Array2 = new ArrayList<Object>();
    private ChildEventListener mChild;
    private String roomKey;
    private int roomParticipant;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final String idByANDROID_ID =
                Settings.Secure.getString(ChatActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID); //디바이스 고유값 가져오기(보낸사람 구분 시 필요)

        mListView = (ListView) findViewById(R.id.list_view);
        editdt = (EditText) findViewById(R.id.editText);
        sendbt = (Button) findViewById(R.id.sendbt);
        mTextview = (TextView) findViewById(R.id.currentRoomName);

        Intent intent = getIntent(); //방 정보 데이터 수신
        String roomName = intent.getExtras().getString("roomName");
        roomKey = intent.getExtras().getString("roomKey");
        roomParticipant = intent.getExtras().getInt("roomParticipant")+1;

        mTextview.setText(roomName);

        final DatabaseReference messsageRef=FirebaseDatabase.getInstance().getReference("message").child(roomName);

        initDatabase();

        adapter = new ArrayAdapter<String>(this, R.layout.mylistitem, R.id.TextView1, new ArrayList<String>());
        adapter2 = new ArrayAdapter<String>(this, R.layout.mylistitem, R.id.text_message_time, new ArrayList<String>());

        mListView.setAdapter(adapter);
        //mListView.setAdapter(adapter2);

        sendbt.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editdt.getText().toString().length() != 0 && !editdt.getText().toString().equals("text")) { //메시지 공백인지 확인
                    Log.e("error", "click");
                    msg = editdt.getText().toString();
                    editdt.setText("");

                    if(msg != "Text" && msg != "text") {
                        SimpleDateFormat format2 = new SimpleDateFormat ( "MM-dd HH:mm");
                        Date time2 = new Date();
                        String nowtime2 = format2.format(time2);

                        Map<String, String> map = new HashMap<String, String>();
                        map.put("username", idByANDROID_ID);
                        map.put("msg", msg);
                        map.put("timestamp", nowtime2);

                        SimpleDateFormat format1 = new SimpleDateFormat ( "yyyy-MM-dd");
                        Date time = new Date();
                        String nowtime = format1.format(time);
                        FirebaseDatabase.getInstance().getReference().child("room").child(roomKey).child("lastTime").setValue(nowtime);

                        messsageRef.push().setValue(map);  // 기본 database 하위 message라는 child에 chatData를 list로 만들기
                        //hideSoftKeyboard(MainActivity.this);
                    } else {Log.e("tag", "text자동전송방지");}

                } else {
                    Toasty.warning(ChatActivity.this, "메시지를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }


            }
        });

        mReference = mDatabase.getReference("message").child(roomName); // 변경값을 확인할 child 이름
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapter.clear();
                adapter2.clear();

                for (DataSnapshot messageData : dataSnapshot.getChildren()) {

                    // child 내에 있는 데이터만큼 반복합니다.
                    if(messageData.child("username").getValue().toString().equals(idByANDROID_ID)) {

                        Log.e("tag", "my message");
                        String msg2 = messageData.child("msg").getValue().toString();
                        String timestamp = messageData.child("timestamp").getValue().toString();
                        if(msg2 != "Text" && msg2 != "text") {
                            Array.add(msg2);
                            Array.add(timestamp);

                            adapter.add("나: "+msg2);
                            adapter2.add(timestamp);
                        }

                    } else {
                        Log.e("tag", "another person sended the message");

                        String msg2 = messageData.child("msg").getValue().toString();
                        String timestamp = messageData.child("timestamp").getValue().toString();
                        if(msg2 != "Text" && msg2 != "text") {
                            Array.add(msg2);
                            Array.add(timestamp);
                            adapter.add(msg2);
                            adapter2.add(timestamp);

                        }


                    }
                    adapter.notifyDataSetChanged();
                    mListView.setSelection(adapter.getCount()-1);


                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initDatabase() {

        mDatabase = FirebaseDatabase.getInstance();

        mReference = mDatabase.getReference("log");
        mReference.child("log").setValue("check");

        mChild = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mReference.addChildEventListener(mChild);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mReference.removeEventListener(mChild);
    }

    /*키보드 내리기 함수
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
        activity.getCurrentFocus().getWindowToken(), 0);
    }
    */

    @Override
    public void onBackPressed() {
        FirebaseDatabase.getInstance().getReference().child("room").child(roomKey).child("participant").setValue(roomParticipant-1);
        Intent intent = new Intent(ChatActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View focusView = getCurrentFocus();
        if (focusView != null) {
            Rect rect = new Rect();
            focusView.getGlobalVisibleRect(rect);
            int x = (int) ev.getX(), y = (int) ev.getY();
            if (!rect.contains(x, y)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                focusView.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}

