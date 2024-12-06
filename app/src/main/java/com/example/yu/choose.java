package com.example.yu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class choose extends Activity {
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideNavigationBar();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main); // 设置布局文件
        //intent = new Intent(choose.this, MyIntentService.class);
       // String action = MyIntentService.ACTION_MUSIC;
        // 设置action
       // intent.setAction(action);
       // startService(intent);
        // 绑定按钮
        Button buttonoo = findViewById(R.id.oo);
        Button buttonre = findViewById(R.id.wr);
        Button buttonta = findViewById(R.id.talk);

        buttonoo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 启动喊
                Intent intent = new Intent(choose.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // 设置 buttonre 按钮点击事件
        buttonre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //stopService(intent);
                Intent intent = new Intent(choose.this, DiaryListActivity.class);
                startActivity(intent);

            }
        });

        buttonta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 启动说话
                Intent intent = new Intent(choose.this, Chat.class);
                startActivity(intent);
                Log.d("choose", "Button ta clicked, starting Chat");
            }
        });
    }
    // 隐藏导航栏的方法
    // 隐藏导航栏的方法
    private void hideNavigationBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }

   // @Override
   // protected void onPause() {
   //     super.onPause();
        // 停止音乐服务
  //      stopService(intent);
  //  }


}

