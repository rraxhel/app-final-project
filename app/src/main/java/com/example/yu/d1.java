package com.example.yu;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class d1 extends AppCompatActivity {

    private TextView selectedDateTextView;
    private Spinner moodSpinner;
    private EditText diaryEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
      //  hideNavigationBar();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dairy);

        selectedDateTextView = findViewById(R.id.selectedDateTextView);
        diaryEditText = findViewById(R.id.diaryEditText);



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

        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDiaryEntry();
            }
        });

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(d1.this, DiaryListActivity.class);
                startActivity(intent);
                finish();
            }
        });
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

    private void addDiaryEntry() {
        String selectedDate = selectedDateTextView.getText().toString();
        MoodItem selectedMoodItem = (MoodItem) moodSpinner.getSelectedItem();
        String diaryText = diaryEditText.getText().toString();
        if (selectedDate.isEmpty()) {
            Toast.makeText(d1.this, "時間未選擇", Toast.LENGTH_SHORT).show();
            return;
        }

        if (diaryText.isEmpty()) {
            Toast.makeText(d1.this, "日記內容未撰寫", Toast.LENGTH_SHORT).show();
            return;
        }
        // 获取当前登录用户的 UID
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();

            // 获取数据库引用
            DatabaseReference diaryRef = FirebaseDatabase.getInstance().getReference("diaries").child(uid);

            Map<String, Object> diaryEntry = new HashMap<>();
            diaryEntry.put("date", selectedDate);
            diaryEntry.put("mood", selectedMoodItem.getImageResource());
            diaryEntry.put("diaryText", diaryText);
            Toast.makeText(d1.this, "日記已保存", Toast.LENGTH_SHORT).show();
            // 使用 push() 方法添加日记，Firebase 会生成唯一的 push ID
            String newDiaryKey = diaryRef.push().getKey();
            diaryRef.child(newDiaryKey).setValue(diaryEntry);

    } else {
        // 保存失败
        Toast.makeText(d1.this, "日記保存失敗 ", Toast.LENGTH_SHORT).show();
    }
    }
    // 隐藏导航栏的方法
    private void hideNavigationBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }


}
