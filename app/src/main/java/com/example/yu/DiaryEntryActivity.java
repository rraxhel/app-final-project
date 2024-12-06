
package com.example.yu;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiaryEntryActivity extends AppCompatActivity {

    private EditText diaryEditText;
    private String diaryKey;
    private Spinner moodSpinner;
    private TextView selectedDateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //hideNavigationBar();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_entry);
        diaryEditText = findViewById(R.id.diaryEditText);

        selectedDateTextView = findViewById(R.id.selectedDateTextView);

        Intent intent = getIntent();
        if (intent != null) {
            diaryKey = intent.getStringExtra("DIARY_KEY");
            Log.d("key", "sure: " +  diaryKey);
        }
        loadDiaryContent();

        // Setup your spinner with options and set a listener
        setupMoodSpinner(); // 添加这一行

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在按钮点击时执行返回上一层的操作
                finish();
            }
        });

        Button saveButton = findViewById(R.id.addButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDiaryEntry();
            }
        });

        Button deleteButton = findViewById(R.id.delButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDiaryEntry();
            }
        });
    }

    private void setupMoodSpinner() {
        // Replace with your mood options
        // 准备数据源
        List<MoodItem> moodItems = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            int imageResource = getResources().getIdentifier("mood" + i, "drawable", getPackageName());
            moodItems.add(new MoodItem(imageResource, "Mood " + i));
        }

        // 创建适配器
        MoodAdapter moodAdapter = new MoodAdapter(this, moodItems);

        moodSpinner = findViewById(R.id.moodSpinner);
        // 设置适配器给 Spinner
        moodSpinner.setAdapter(moodAdapter);

        // 设置 Spinner 的选择监听器
        moodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 处理 Spinner 选择事件
                MoodItem selectedMoodItem = (MoodItem) parent.getItemAtPosition(position);
                // 在这里执行你的逻辑，可以获取 selectedMoodItem 中的图片资源和文字描述
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 未选择任何项时的处理
            }
        });
    }

    private void loadDiaryContent() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && diaryKey != null) {
            String uid = currentUser.getUid();

            DatabaseReference diaryRef = FirebaseDatabase.getInstance().getReference("diaries").child(uid).child(diaryKey);

            diaryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String diaryContent = dataSnapshot.child("diaryText").getValue(String.class);
                        int moodImageId = dataSnapshot.child("mood").getValue(Integer.class); // 使用 moodImageId 而不是 mood
                        String date = dataSnapshot.child("date").getValue(String.class);

                        // 设置 Spinner 的选择
                        moodSpinner.setSelection(getIndexOfResourceId(moodImageId));

                        // 设置日记内容到 EditText
                        diaryEditText.setText(diaryContent);
                        // 设置日期到 TextView
                        selectedDateTextView.setText(date);

                        Log.d("DiaryEntryActivity", "diaryContent: " + diaryContent);
                        Log.d("DiaryEntryActivity", "moodImageId: " + moodImageId);
                        Log.d("DiaryEntryActivity", "date: " + date);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // 处理错误
                    Toast.makeText(DiaryEntryActivity.this, "Error loading diary content", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // 辅助方法：根据资源ID获取在Spinner数据源中的位置
    private int getIndexOfResourceId(int resourceId) {
        // 这里假设你的 Spinner 的数据源是 MoodItem 对象的集合
        // 你需要根据实际情况调整
        List<MoodItem> moodItemList = getYourMoodItemList();
        for (int i = 0; i < moodItemList.size(); i++) {
            if (moodItemList.get(i).getImageResource() == resourceId) {
                return i;
            }
        }
        // 如果找不到，返回一个默认位置
        return 0;
    }
    private void saveDiaryEntry() {
        // 获取当前用户 UID
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();

            // 获取数据库引用
            DatabaseReference diaryRef = FirebaseDatabase.getInstance().getReference("diaries").child(uid).child(diaryKey);

            // 获取日记内容和心情的值
            String diaryText = diaryEditText.getText().toString();
            MoodItem selectedMoodItem = (MoodItem) moodSpinner.getSelectedItem();
            int moodImageId = selectedMoodItem.getImageResource();
            String selectedDate = selectedDateTextView.getText().toString();
            // 将更新的数据存储到 Map 中
            Map<String, Object> updatedDiaryEntry = new HashMap<>();
            updatedDiaryEntry.put("diaryText", diaryText);
            updatedDiaryEntry.put("mood", moodImageId);
            updatedDiaryEntry.put("date", selectedDate);

            // 更新数据库中的日记条目
            diaryRef.updateChildren(updatedDiaryEntry, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    if (error == null) {
                        // 保存成功
                        Toast.makeText(DiaryEntryActivity.this, "日記保存成功", Toast.LENGTH_SHORT).show();
                        // 可以选择在保存成功后执行其他操作，比如返回上一页
                        finish();
                    } else {
                        // 保存失败
                        Toast.makeText(DiaryEntryActivity.this, "日記保存失敗1", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private List<MoodItem> getYourMoodItemList() {
        // 这里可以是从本地数据源或其他地方获取 MoodItem 对象的集合
        // 替换为你的实际逻辑
        List<MoodItem> moodItemList = new ArrayList<>();
        // 添加 MoodItem 对象到集合中
        moodItemList.add(new MoodItem(R.drawable.mood1, "2131230879"));
        moodItemList.add(new MoodItem(R.drawable.mood2, "2131230881"));
        moodItemList.add(new MoodItem(R.drawable.mood3, "2131230882"));
        moodItemList.add(new MoodItem(R.drawable.mood4, "2131230883"));
        moodItemList.add(new MoodItem(R.drawable.mood5, "2131230884"));
        moodItemList.add(new MoodItem(R.drawable.mood6, "2131230885"));
        moodItemList.add(new MoodItem(R.drawable.mood7, "2131230886"));
        moodItemList.add(new MoodItem(R.drawable.mood8, "2131230887"));
        moodItemList.add(new MoodItem(R.drawable.mood9, "2131230888"));
        moodItemList.add(new MoodItem(R.drawable.mood10, "2131230880"));

        // ... 添加其他 MoodItem 对象
        return moodItemList;
    }

    public void showDatePickerDialog(View view) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // 处理选定的日期
                        String selectedDate = String.format("%d, %02d/%02d", year, month + 1, dayOfMonth);
                        selectedDateTextView.setText(selectedDate);
                    }
                },
                year, month, day);

        datePickerDialog.show();
    }

    private void deleteDiaryEntry() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && diaryKey != null) {
            String uid = currentUser.getUid();
            DatabaseReference diaryRef = FirebaseDatabase.getInstance().getReference("diaries").child(uid).child(diaryKey);

            // 删除日记条目
            diaryRef.removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    if (error == null) {

                        Toast.makeText(DiaryEntryActivity.this, "日記成功刪除", Toast.LENGTH_SHORT).show();

                        finish();
                    } else {

                        Toast.makeText(DiaryEntryActivity.this, "日記刪除失敗", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private void hideNavigationBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }


}
