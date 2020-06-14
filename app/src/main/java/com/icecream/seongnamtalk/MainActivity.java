package com.icecream.seongnamtalk;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Rect;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private EditText editdt;
    private Button sendbt;
    private String msg;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private DatabaseReference messsageRef=FirebaseDatabase.getInstance().getReference("message");
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> adapter2;
    List<Object> Array = new ArrayList<Object>();
    List<Object> Array2 = new ArrayList<Object>();
    private ChildEventListener mChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final String idByANDROID_ID =
                Settings.Secure.getString(MainActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID); //디바이스 고유값 가져오기(보낸사람 구분 시 필요)

        mListView = (ListView) findViewById(R.id.list_view);
        editdt = (EditText) findViewById(R.id.editText);
        sendbt = (Button) findViewById(R.id.sendbt);

        initDatabase();

        adapter = new ArrayAdapter<String>(this, R.layout.mylistitem, new ArrayList<String>());

        mListView.setAdapter(adapter);
        //mListView.setAdapter(adapter2);

        sendbt.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editdt.getText().toString().length() != 0) { //메시지 공백인지 확인
                    Log.e("error", "click");
                    msg = editdt.getText().toString();
                    editdt.setText("");

                    Map<String, String> map = new HashMap<String, String>();
                    map.put("username", idByANDROID_ID);
                    map.put("msg", msg);

                    messsageRef.push().setValue(map);  // 기본 database 하위 message라는 child에 chatData를 list로 만들기
                    //hideSoftKeyboard(MainActivity.this);
                } else {
                    Toast.makeText(MainActivity.this, "메시지를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }


            }
        });

        mReference = mDatabase.getReference("message"); // 변경값을 확인할 child 이름
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapter.clear();

                for (DataSnapshot messageData : dataSnapshot.getChildren()) {

                    // child 내에 있는 데이터만큼 반복합니다.
                    if(messageData.child("username").getValue().toString().equals(idByANDROID_ID)) {

                        Log.e("tag", "my message");
                        String msg2 = messageData.child("msg").getValue().toString();
                        Array.add(msg2);
                        adapter.add("나: "+msg2);
                    } else {
                        Log.e("tag", "another person sended the message");

                        String msg2 = messageData.child("msg").getValue().toString();
                        Array.add(msg2);
                        adapter.add(" "+msg2);

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

