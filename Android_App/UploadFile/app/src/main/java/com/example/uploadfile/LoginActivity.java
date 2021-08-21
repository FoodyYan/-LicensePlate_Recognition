package com.example.uploadfile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText text_user, text_password;
    private TextView text_forgot_password;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public static final String MyPREF = "MyPrefs";//final表示值不能改  把MyPREF當成Key
    public static final String Account = "accountKey";
    SharedPreferences sharedpreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);
        sharedpreferences = getSharedPreferences(MyPREF, Context.MODE_PRIVATE);
        text_user = findViewById(R.id.text_user);
        text_password = findViewById(R.id.text_password);
        text_forgot_password = findViewById(R.id.textview_forgetps);
        //  Intent intent = getIntent();
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("TAG", user.getUid());
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else {
                    // User is signed out
                    Log.d("Tag", "user ==null");
                }
                // ...
            }
        };
    }

    public void buttonOnClick(View view) {
        String email = text_user.getText().toString();

        String password = text_password.getText().toString();
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Account, email);
        editor.commit();
        if (email.length() == 0 && password.length() == 0) {
            AlertDialog.Builder builderen = new AlertDialog.Builder(LoginActivity.this);
// 2. Chain together various setter methods to set the dialog characteristics
            builderen.setMessage("請輸入帳號密碼！");
            AlertDialog dialogen = builderen.create();
            dialogen.show();
        } else if (email.length() == 0) {
            AlertDialog.Builder builderen = new AlertDialog.Builder(LoginActivity.this);
// 2. Chain together various setter methods to set the dialog characteristics
            builderen.setMessage("請輸入帳號");
            AlertDialog dialogen = builderen.create();
            dialogen.show();
        } else if (password.length() == 0) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
// 2. Chain together various setter methods to set the dialog characteristics
            builder1.setMessage("請輸入密碼")
                    .setTitle(R.string.dialog_title);
            AlertDialog dialog1 = builder1.create();
            dialog1.show();
        } else {

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Log.d("TAG", "登入成功");
                                text_password.setText("");
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
// 2. Chain together various setter methods to set the dialog characteristics
                                builder.setMessage(R.string.dialog_message)
                                        .setTitle(R.string.dialog_title);
                                AlertDialog dialog = builder.create();
                                dialog.show();

                            }
                        }
                    });
        }

     /*   if (text_user.getText().toString().equals("admin") && text_password.getText().toString().equals("admin")) {

            Intent intent = new Intent(this, Home_Page.class);
            startActivity(intent);
            //text_password.setText("");
            //text_user.setText("");
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
// 2. Chain together various setter methods to set the dialog characteristics
            builder.setMessage(R.string.dialog_message)
                    .setTitle(R.string.dialog_title);
            AlertDialog dialog = builder.create();
            dialog.show();
        }*/

    }

    public void buttonExit(View view) {
        System.exit(0);
        //finish();
    }

    public void buttonCancel(View view) {
        text_password.setText("");
        text_user.setText("");
    }

    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String temp1 = sharedpreferences.getString(Account, "0624082@nkust.edu.tw");
        text_user.setText(temp1);
    }


    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Account, text_user.getText().toString());
        editor.commit();//確認更改
    }

    public void reset(View view) {
        if (text_user.length() == 0) {
            AlertDialog.Builder builderen = new AlertDialog.Builder(LoginActivity.this);
// 2. Chain together various setter methods to set the dialog characteristics
            builderen.setMessage("請輸入郵箱地址！");
            AlertDialog dialogen = builderen.create();
            dialogen.show();
        } else {
            mAuth.sendPasswordResetEmail(text_user.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                AlertDialog.Builder builderre = new AlertDialog.Builder(LoginActivity.this);
// 2. Chain together various setter methods to set the dialog characteristics
                                builderre.setMessage("已將密碼重設連結寄到您的信箱");
                                AlertDialog dialogre = builderre.create();
                                dialogre.show();
                            } else {
                                AlertDialog.Builder builderfa = new AlertDialog.Builder(LoginActivity.this);
// 2. Chain together various setter methods to set the dialog characteristics
                                builderfa.setMessage("請輸入正確的郵箱，查無此帳號");
                                AlertDialog dialogfa = builderfa.create();
                                dialogfa.show();
                            }
                        }
                    });
        }
    }
}
