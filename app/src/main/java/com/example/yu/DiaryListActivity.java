package com.example.yu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DiaryListActivity extends AppCompatActivity {
    private ListView diaryListView;
    private List<String> diaryKeys = new ArrayList<>(); // 用于存储日记键值的列表
    Button b1;
    private Intent musicIntent;

    protected void onCreate(Bundle savedInstanceState) {
        hideNavigationBar();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_list);
        b1 = (Button) findViewById(R.id.add_diary_button);
        diaryListView = findViewById(R.id.diary_list);

        // Populate and display diary list
        displayDiaryList();

        // Set item click and long click listeners
        diaryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 处理项目点击事件
                String selectedDiary = diaryKeys.get(position); // 获取日记的 key

                // 启动 DiaryEntryActivity，并传递选定的日记键
                Intent intent = new Intent(DiaryListActivity.this, DiaryEntryActivity.class);
                intent.putExtra("DIARY_KEY", selectedDiary);
                startActivity(intent);
                Log.d("key", "choose: " +  selectedDiary);
            }
        });


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在按钮点击时跳转到另一个页面的代码
                Intent intent = new Intent(DiaryListActivity.this, d1.class);  // 替换为你要跳转的 Activity
                startActivity(intent);
            }
        });
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // stopService(musicIntent);
                Intent intent = new Intent(DiaryListActivity.this, choose.class);  // 替换为你要跳转的 Activity
                startActivity(intent);
            }
        });
    }

    private void displayDiaryList() {
        // 获取当前登录用户
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // 获取用户的 UID
            String uid = currentUser.getUid();

            // 构建数据库引用
            DatabaseReference diaryRef = FirebaseDatabase.getInstance().getReference("diaries").child(uid);

            // 从 Firebase 读取数据
            diaryRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<String> dateList = new ArrayList<>();

                    diaryKeys.clear(); // 清空列表
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // 获取日期
                        String date = snapshot.child("date").getValue(String.class);
                        String key = snapshot.getKey(); // 获取日记的 key
                        Log.d("key", "list: " +  key);
                        dateList.add(date);
                        diaryKeys.add(key); // 将 key 添加到列表中
                    }

                    // 更新 ListView 显示获取到的数据
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(DiaryListActivity.this, android.R.layout.simple_list_item_1, dateList);
                    diaryListView.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // 处理错误
                    Log.e("DiaryListActivity", "Error reading diaries from Firebase: " + databaseError.getMessage());
                }
            });
        }
    }


    // 在 getKeyAtPosition 方法中获取 push key
    private String getKeyAtPosition(int position) {
        if (position >= 0 && position < diaryKeys.size()) {
            return diaryKeys.get(position);
        } else {
            // 处理越界的情况，可能是列表为空或者索引超出了列表的范围
            return null; // 或者返回一个默认值，具体视你的逻辑而定
        }
    }

    // ...
    // 隐藏导航栏的方法
    private void hideNavigationBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }



}

