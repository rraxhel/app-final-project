package com.example.yu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class main extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout); // 設置布局文件

        // 綁定按鈕
        Button buttonlogin = findViewById(R.id.login);
        Button buttonre = findViewById(R.id.register);

        // 設置 buttonlogin 按鈕點擊事件
        buttonlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 啟動 login Activity
                Intent intent = new Intent(main.this, login.class);
                startActivity(intent);
            }
        });

        // 設置 buttonre 按鈕點擊事件
        buttonre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 啟動 register Activity（假設這是你的 register Activity）
                Intent intent = new Intent(main.this, register.class);
                startActivity(intent);
            }
        });
    }
}

