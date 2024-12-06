package com.example.yu;

import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.RECORD_AUDIO;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.microsoft.cognitiveservices.speech.CancellationReason;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisCancellationDetails;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisResult;
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Chat extends AppCompatActivity{



    String defaultQuestion="跟我用繁體中文聊天，當個好的聆聽者，聽我分享事情";


    public String public_result;
    boolean speak_or_not = true;

    private static String speechSubscriptionKey = "f96e934ccc6846fb9b6e10135b564017";
    private static String serviceRegion = "eastasia";



    EditText editText;
    ImageButton send,mic;
    TextView textView;

    TextToSpeech tts;

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    //訊息超時timeout改為60s(預設:10s)
    OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .build();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        }
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // stopService(musicIntent);
                Intent intent = new Intent(Chat.this, choose.class);  // 替换为你要跳转的 Activity
                startActivity(intent);
            }
        });
//        SpeechConfig config = SpeechConfig.fromSubscription(speechSubscriptionKey, serviceRegion);
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR){
                    tts.setLanguage(Locale.CHINESE);
                    //config.setSpeechSynthesisVoiceName("zh-TW-YunJheNeural");
                }

            }
        });

        editText = findViewById(R.id.edit_text);
        send = findViewById(R.id.send_btn);
        textView = findViewById(R.id.textView);
        //mic = findViewById(R.id.mic);

        textView.setText("你好啊~\n有甚麼事情都可以跟我分享歐!!");


        int requestCode = 5;
        ActivityCompat.requestPermissions(Chat.this, new String[]{RECORD_AUDIO, INTERNET}, requestCode);


        // 添加初始系统消息
        JSONObject systemMessage = new JSONObject();
        JSONArray messages = new JSONArray();
        try {
            systemMessage.put("role","You are a good chat companion.");
            systemMessage.put("content", "跟我用繁體中文聊天");

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        messages.put(systemMessage);

        //callAPI(defaultQuestion);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_reply=editText.getText().toString();
//                callAPI("(用繁體中文回覆,不要用簡體中文，50個字內回答完)"+user_reply);
                callAPI("Please reply in Traditional Chinese, do not use Simplified Chinese. Answer within 50 words."+user_reply);
////
                editText.setText(" ");

            }
        });

//        mic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try (SpeechConfig config = SpeechConfig.fromSubscription(speechSubscriptionKey, serviceRegion)) {
//
//                    try (com.microsoft.cognitiveservices.speech.SpeechRecognizer reco = new com.microsoft.cognitiveservices.speech.SpeechRecognizer(config)) {
//                        Future<SpeechRecognitionResult> task = reco.recognizeOnceAsync();
//                        SpeechRecognitionResult result = task.get();
//                        Toast.makeText(Chat.this, result.getReason().toString(), Toast.LENGTH_SHORT).show();
//
//
//                        if (result.getReason() == ResultReason.RecognizedSpeech) {
//                            Toast.makeText(Chat.this, "TTTT", Toast.LENGTH_SHORT).show();
//                            callAPI(result.getText());
//
//                        } else {
//
//                            //txt.setText("Error recognizing. Did you update the subscription info?" + System.lineSeparator() + result.toString());
//                        }
//                    }
//                } catch (Exception ex) {
//                    Log.e("SpeechSDKDemo", "unexpected " + ex.getMessage());
//                    assert (false);
//                }
//            }
//
//        });
    }



    //調用API
    void callAPI(String question) {
        //OkHttp庫
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "gpt-3.5-turbo");
            //建構對話歷史
            JSONArray messageArr = new JSONArray();

            JSONObject obj = new JSONObject();
            obj.put("role", "user");
            obj.put("content", question);
            messageArr.put(obj);

            jsonBody.put("messages", messageArr);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        // 使用 BuildConfig.OPENAI_API_KEY
        String apiKey = BuildConfig.OPENAI_API_KEY;
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer"+ apiKey)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
                //如果網路請求成功
                if (response.isSuccessful()) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");

                        //要修改UI(TextView:bot)->需切換到主要執行緒
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (speak_or_not == true) {

                                    tts.speak(result.trim(), TextToSpeech.QUEUE_FLUSH, null);
                                    //tts(result.trim());
                                    //addResponse(result.trim());
                                    textView.setText(result.trim());
                                }

                            }
                        });


                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }


                } else {
                    //addResponse("Failed to load due to "+response.body().toString());
                    String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                    //text.setText("Failed to load due to " + errorBody);
                }
            }

            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {

            }

        });
    }

    public void tts(String text) {

        try (SpeechConfig config = SpeechConfig.fromSubscription(speechSubscriptionKey, serviceRegion)) {
            // https://aka.ms/speech/voices/neural 腔調


            config.setSpeechSynthesisVoiceName("zh-TW-YunJheNeura");


            try (SpeechSynthesizer synth = new SpeechSynthesizer(config)) {
                assert (config != null);
                assert (synth != null);

                //String text = public_result;

                Future<SpeechSynthesisResult> task = synth.SpeakTextAsync(text);
                assert (task != null);

                SpeechSynthesisResult result = task.get();
                assert (result != null);


                if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
                    System.out.println("Speech synthesized to speaker for text [" + text + "]");




                } else if (result.getReason() == ResultReason.Canceled) {
                    SpeechSynthesisCancellationDetails cancellation = SpeechSynthesisCancellationDetails
                            .fromResult(result);
                    //Toast.makeText(speech_to_text.this, "CANCELED: Reason=" + cancellation.getReason(), Toast.LENGTH_LONG).show();
                    System.out.println("CANCELED: Reason=" + cancellation.getReason());

                    if (cancellation.getReason() == CancellationReason.Error) {
                        //Toast.makeText(speech_to_text.this, "CANCELED: ErrorCode=" + cancellation.getErrorCode(), Toast.LENGTH_LONG).show();
                        System.out.println("CANCELED: ErrorCode=" + cancellation.getErrorCode());
                        //System.out.println("CANCELED: ErrorDetails=" + cancellation.getErrorDetails());
                        //System.out.println("CANCELED: Did you update the subscription info?");
                    }
                }


            }
        } catch (Exception ex) {
            //Toast.makeText(speech_to_text.this,"Unexpected exception: " + ex.getMessage(),Toast.LENGTH_LONG).show();
            assert (false);
            System.exit(1);
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