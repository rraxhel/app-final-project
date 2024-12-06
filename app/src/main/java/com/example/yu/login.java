package com.example.yu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class login extends Activity {
    Activity context = this;
    Button b1;
    EditText et2, et3;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login); // 設置布局文件
        b1 = (Button) findViewById(R.id.login);
        et2 = (EditText) findViewById(R.id.account);
        et3 = (EditText) findViewById(R.id.pass);

        auth = FirebaseAuth.getInstance();


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = et2.getText().toString();
                String password = et3.getText().toString();
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    // 使用者未填寫電子郵件或密碼，顯示相應的提示
                    Toast.makeText(context, "請填寫完整的電子郵件和密碼", Toast.LENGTH_SHORT).show();
                    return;
                }
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
                    @Override

                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            String uid = user.getUid();
                            Intent intent = new Intent(context, choose.class);
                            intent.putExtra("USER_UID", uid);
                            finish();
                            startActivity(intent);

                            Toast.makeText(context, "登入成功" + user.getEmail(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "登入失敗" + getErrorMessage(task.getException()), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
                // 點擊按鈕後隱藏鍵盤
                hideKeyboard();
            }
        });

    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private String getErrorMessage(Exception exception) {
        if (exception instanceof FirebaseAuthException) {
            FirebaseAuthException authException = (FirebaseAuthException) exception;
            String errorCode = authException.getErrorCode();
            Log.e("FirebaseAuth", "Error: " + exception.getMessage(), exception);

            // 根據錯誤代碼返回相應的繁體中文錯誤消息
            switch (errorCode) {
                case "ERROR_INVALID_EMAIL":
                    return "無效的電子郵件地址";
                case "ERROR_WRONG_PASSWORD":
                    return "密碼不正確，請重新輸入";
                case "ERROR_USER_NOT_FOUND":
                    return "找不到使用者，請確認您的帳號";
                case "ERROR_USER_DISABLED":
                    return "此帳號已被禁用，請聯絡支援人員";
                case "INVALID_LOGIN_CREDENTIALS":
                    return "可能由於多次輸入錯誤的登入憑據，請稍後再試或重設密碼";

            }
        } else {
            return "請檢查輸入信息";
        }
        return "請檢查輸入信息";
    }





}


