package com.example.yu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class page3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideNavigationBar();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page3);

        double dbValue = getIntent().getDoubleExtra("DB_VALUE", 0.0);

        TextView dbTextView = findViewById(R.id.resultTextView);
        dbTextView.setText(dbValue+"db");

        ImageButton backButton = findViewById(R.id.returnButton);
        backButton.setOnClickListener(v -> finish());
    }
    private void hideNavigationBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }

}