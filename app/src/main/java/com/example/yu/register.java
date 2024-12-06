package com.example.yu;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class register extends Activity {
    Activity context = this;
    Button b1;
    TextView  tv3, tv4;
    EditText  et2, et3;
    String email;

    int duration = Toast.LENGTH_SHORT;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register); // 設置布局文件
        b1 = (Button) findViewById(R.id.register);

        et2 = (EditText) findViewById(R.id.pass);
        et3 = (EditText) findViewById(R.id.email);

        tv3 = (TextView) findViewById(R.id.textView3);
        tv4 = (TextView) findViewById(R.id.textView4);

        auth = FirebaseAuth.getInstance();

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                String email = et3.getText().toString();
                String password = et2.getText().toString();
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    // 使用者未填寫電子郵件或密碼，顯示相應的提示
                    Toast.makeText(context, "請填寫完整的電子郵件和密碼", Toast.LENGTH_SHORT).show();
                    return;
                }
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            et2.setText(null);
                            et3.setText(null);
                            Toast.makeText(context, "註冊成功" + user.getEmail(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "註冊失敗" +  getErrorMessage(task.getException()), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
                case "ERROR_EMAIL_ALREADY_IN_USE":
                    return "該電子郵件地址已經被使用";
                case "ERROR_WEAK_PASSWORD":
                    return "密碼須為6碼";
            }
        } else {
            return "註冊失敗，請檢查輸入信息";
        }
        return "請檢查輸入信息";
    }
}
