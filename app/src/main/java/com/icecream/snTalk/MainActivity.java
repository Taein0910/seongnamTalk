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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private EditText editdt;
    private Button sendbt;
    private String msg;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private DatabaseReference messsageRef=FirebaseDatabase.getInstance().getReference("room");
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> adapter2;
    List<Object> Array = new ArrayList<Object>();
    List<Object> Array2 = new ArrayList<Object>();
    private ChildEventListener mChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        mListView = (ListView) findViewById(R.id.list_view);
        editdt = (EditText) findViewById(R.id.editText);
        sendbt = (Button) findViewById(R.id.sendbt);

        initDatabase();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());

        mListView.setAdapter(adapter);
        //mListView.setAdapter(adapter2);


        sendbt.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editdt.getText().toString().length() != 0 && !editdt.getText().toString().equals("text")) { //메시지 공백인지 확인
                    Log.e("error", "click");
                    msg = editdt.getText().toString();
                    editdt.setText("");

                    if(msg != "Text") {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("name", msg);
                        map.put("participant", "빈방");

                        messsageRef.push().setValue(map);  // 기본 database 하위 message라는 child에 chatData를 list로 만들기
                        //hideSoftKeyboard(MainActivity.this);
                    } else {Log.e("tag", "text자동전송방지");}

                } else {
                    Toast.makeText(MainActivity.this, "방 이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }


            }
        });

        mReference = mDatabase.getReference("room"); // 변경값을 확인할 child 이름
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapter.clear();

                for (DataSnapshot messageData : dataSnapshot.getChildren()) {

                    // child 내에 있는 데이터만큼 반복합니다.
                    if(messageData.child("name").getValue() != null) {

                        Log.e("tag", "my message");
                        String msg2 = messageData.child("name").getValue().toString();
                        final String participant = messageData.child("participant").getValue().toString();
                        if(msg2 != "Text") {
                            Array.add(msg2);
                            adapter.add(msg2+" ("+participant+")");
                        }

                    } else {
                        Log.e("tag", "빈방");



                    }
                    adapter.notifyDataSetChanged();
                    mListView.setSelection(adapter.getCount()-1);


                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ///////여기 만들 차례!!!(인원수 예외 처리)
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                String roomName = mListView.getItemAtPosition(position).toString();
                intent.putExtra("roomName",roomName); /*송신*/

                mReference = mDatabase.getInstance().getReference("room").getRef().child("participant");
                mReference.setValue("대기중");

                startActivity(intent);

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
