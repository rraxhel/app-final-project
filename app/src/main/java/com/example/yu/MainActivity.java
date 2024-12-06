package com.example.yu;

// MainActivity.java
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageButton;

import com.example.yu.R;
import com.example.yu.page1;
import com.example.yu.page2;
import com.example.yu.page3;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    //權限請求
    //private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean hasPermission = false;
    private boolean isRecoding = false;
    private boolean recordImg = true;
    private MediaRecorder mediaRecorder;
    private ImageButton btRecord;
    //private long recordingStartTime;//錄製時間
    private double db;//紀錄分貝結果

    //record file
    private static final String AUDIO_FILE_NAME = "recorded_audio.mp3";
    private String audioFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideNavigationBar();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //請求權限
        checkPermission();
        btRecord = findViewById(R.id.recordButton);
        btRecord.setOnClickListener(v -> {
            if(recordImg){
                btRecord.setImageResource(R.drawable.stop); //change stop img
                //按下按鈕即開始錄音
                startMeasure();
                Log.d("Onclick","btStart click");
            }else {
                btRecord.setImageResource(R.drawable.record);
                stopMeasure();
                Log.d("Onclick","btStop click");
            }
            recordImg = !recordImg;
        });
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // stopService(musicIntent);
                Intent intent = new Intent(MainActivity.this, choose.class);  // 替换为你要跳转的 Activity
                startActivity(intent);
            }
        });
    }

    /**確認是否有麥克風使用權限*/
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(this
                        , Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this
                    , new String[]{Manifest.permission.RECORD_AUDIO}, 100);
        } else {
            hasPermission = true;
        }
    }

    /**取得權限回傳*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            hasPermission = true;
        }
    }

    /**file path*/
    private String getAudioFilePath() {
        File dir = getFilesDir();
        File audioFile = new File(dir, AUDIO_FILE_NAME);
        return audioFile.getAbsolutePath();
    }

    /**開始檢測*/
    private void startMeasure() {
        if (!hasPermission || isRecoding) return;

        /*
        //record 2sec
        recordingStartTime = System.currentTimeMillis();
        Handler handler = new Handler();
        handler.postDelayed(() -> stopMeasure(), 2000);  // 2sec後停止錄音
        Log.d("AudioRecording", "File path: " + getFilesDir().getAbsolutePath() + "/audio.3gp");
        */
        // 獲取錄音文件路径
        audioFilePath = getAudioFilePath();

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); //設置聲音來源
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); //設置輸出格式 mp3
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC); //設置編碼方式AAC
        //mediaRecorder.setOutputFile("/dev/null"); //設置輸出資料夾路徑
        //mediaRecorder.setOutputFile(getFilesDir().getAbsolutePath() + "/audio.3gp");
        //輸出文件路徑
        mediaRecorder.setOutputFile(audioFilePath);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            handler.post(taskMeasure);//使用Handler
            isRecoding = true;
        } catch (IOException e) {
            Log.e("recordStart","IOException");
            e.printStackTrace();
        }
    }

    /**透過HandlerTask 取得檢測結果*/
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 1:

                    int amp = mediaRecorder.getMaxAmplitude();//取得當前音訊的最大振幅
                    Log.d("AudioAmplitude", "Amplitude: " + amp);
                    //公式：Gdb = 20log10(V1/V0)
                    //Google已提供方法幫你取得麥克風的檢測電壓(V1)以及參考電壓(V0)
                    double dB = 20*(Math.log10(Math.abs(amp)));
                    db = Math.round(dB);
                    //if -Infinity
                    /*if (Math.round(dB) == -9223372036854775808.0) tvResult.setText("0 db");
                    else tvResult.setText(Math.round(db)+" db");*/
                    //switchToPageByDb(db);
                    break;
            }
            super.handleMessage(msg);

        }
    };
    private Runnable taskMeasure = new Runnable() {
        @Override
        public void run() {
            handler.sendEmptyMessage(1);//發送消息類型1，handleMessage會進行相應操做
            //每500毫秒抓取一次檢測結果
            handler.postDelayed(this,500);
        }
    };

    /**關閉檢測*/
    private void stopMeasure() {
        if (!hasPermission || !isRecoding) return;
        handler.removeCallbacks(taskMeasure);
        try {
            mediaRecorder.stop();
            mediaRecorder.release();

            //int maxAmplitude = mediaRecorder.getMaxAmplitude();

        } catch (IllegalStateException e) {
            Log.e("recordStop", "IllegalStateException:");
            e.printStackTrace();
        }
        isRecoding = false;
        if (mediaRecorder != null) {

            /*if (db <= 65) switchToPage1(db);
            else if (db <= 85 && db >= 65) {
                switchToPage2(db);
            } else switchToPage3(db);*/
            switchToPageByDb(db);
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }
    /**切換頁面判斷*/
    private void switchToPageByDb(double db) {
        runOnUiThread(() -> {
            if (db <= 65) {
                switchToPage1(db);
            } else if (db <= 85 && db >= 65) {
                switchToPage2(db);
            } else {
                switchToPage3(db);
            }
        });
    }
    /**切換頁面*/
    private void switchToPage1(double db) {
        runOnUiThread(() -> {
            Intent intent = new Intent(MainActivity.this, page1.class);
            intent.putExtra("DB_VALUE", db);  // "DB_VALUE" 識別分貝數字的鍵
            startActivity(intent);
        });
    }
    private void switchToPage2(double db) {
        runOnUiThread(() -> {
            Intent intent = new Intent(MainActivity.this, page2.class);
            intent.putExtra("DB_VALUE", db);  // "DB_VALUE" 識別分貝數字的鍵
            startActivity(intent);
        });
    }
    private void switchToPage3(double db) {
        runOnUiThread(() -> {
            Intent intent = new Intent(MainActivity.this, page3.class);
            intent.putExtra("DB_VALUE", db);  // "DB_VALUE" 識別分貝數字的鍵
            startActivity(intent);
        });
    }

    @Override
    protected void onStop() {
        stopMeasure();
        super.onStop();

    }
    private void hideNavigationBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }

}




