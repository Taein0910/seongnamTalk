package com.icecream.snTalk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

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
    private String key;
    private int debugCode;

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
                        SimpleDateFormat format1 = new SimpleDateFormat ( "yyyy-MM-dd");
                        Date time = new Date();
                        String nowtime = format1.format(time);

                            Map<String, String> map = new HashMap<String, String>();
                            map.put("name", msg);
                            map.put("participant", "0");
                            map.put("lastTime", nowtime);

                            messsageRef.push().setValue(map);  // 기본 database 하위 message라는 child에 chatData를 list로 만들기
                            //hideSoftKeyboard(MainActivity.this);


                    } else {Log.e("tag", "text자동전송방지");}

                } else {
                    Toasty.warning(MainActivity.this, "방 이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
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
                        key = messageData.child("participant").getValue().toString();
                        String participant = messageData.child("participant").getValue().toString();
                        if(Integer.parseInt(participant)<0) {
                            participant="0";

                        }
                        if(msg2 != "Text") {
                            Array.add(msg2);
                            adapter.add(msg2+" ("+participant+"명)");
                        }

                        try{ // String Type을 Date Type으로 캐스팅하면서 생기는 예외로 인해 여기서 예외처리 해주지 않으면 컴파일러에서 에러가 발생해서 컴파일을 할 수 없다.
                            String chatDate = messageData.child("lastTime").getValue().toString();
                            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-mm-dd");
                            // date1, date2 두 날짜를 parse()를 통해 Date형으로 변환.
                            Date FirstDate = format1.parse(chatDate);

                            SimpleDateFormat format2 = new SimpleDateFormat ( "yyyy-MM-dd");
                            Date time = new Date();
                            String nowtime = format1.format(time);
                            Date SecondDate = format1.parse(nowtime);

                            long calDate = FirstDate.getTime() - SecondDate.getTime();

                            //현재 날짜와 채팅방 마지막 채팅 시간의 차이 일수
                            long calDateDays = calDate / ( 24*60*60*1000);

                            calDateDays = Math.abs(calDateDays);
                            if(calDateDays >= 1) {
                                Log.e("seongnamTalk", "오래된 채팅방 삭제");
                                messageData.getRef().removeValue(); //채팅방 삭제
                            } else {
                                //
                            }
                        }
                        catch(ParseException e)
                        {
                            // 예외 처리
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
                Toasty.error(MainActivity.this, "오류가 발생했습니다 :"+databaseError.toString(), Toast.LENGTH_SHORT).show();

            }
        });

        ///////인원수 예외 처리 필요!!!!!!!!!!
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                String subString = mListView.getItemAtPosition(position).toString();
                final int currentParticipant = Integer.parseInt(subString.substring(subString.length()-3, subString.length()-2));
                if(currentParticipant >= 2){
                    Toasty.error(MainActivity.this, "이미 정원이 대화중인 채팅방입니다.", Toast.LENGTH_SHORT).show();
                } else{
                    mReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(debugCode==0) {
                                final String[] roomArray = new String[10000];


                                int i=0;
                                for (DataSnapshot child : dataSnapshot.getChildren()) {

                                    roomArray[i] = child.getKey();
                                    i++;
                                }

                                Log.e("debugging", "het");
                                goToChat(roomArray[position], position, currentParticipant);
                            }



                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });



                }


            }
        });
    }

    private void goToChat(String key, int position, int curParticipant) {
debugCode=1;
        if(curParticipant==0) {
            FirebaseDatabase.getInstance().getReference().child("room").child(key).child("participant").setValue(1);
        } else if(curParticipant==1) {
            FirebaseDatabase.getInstance().getReference().child("room").child(key).child("participant").setValue(2);
        }

        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        String roomName = mListView.getItemAtPosition(position).toString();
        roomName = roomName.replace(roomName.substring(roomName.length()-5, roomName.length()), "");
        intent.putExtra("roomName",roomName); /*송신*/
        intent.putExtra("roomKey", key);
        intent.putExtra("roomParticipant", curParticipant);
        startActivity(intent);

        finish();
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
                Toast.makeText(MainActivity.this, "오류가 발생했습니다 :"+databaseError.toString(), Toast.LENGTH_SHORT).show();

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

