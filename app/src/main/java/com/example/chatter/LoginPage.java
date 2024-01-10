package com.example.chatter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginPage extends AppCompatActivity{
    private Button loginBtn;
    private EditText loginMail,loginPass;
    private TextView loginToSignUp;
    private FirebaseAuth auth;
    private final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private android.app.ProgressDialog progressDialog;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait....");
        progressDialog.setCancelable(false);
        auth = FirebaseAuth.getInstance();
        loginBtn = findViewById(R.id.newLoginBtn);
        loginMail = findViewById(R.id.new_login_email);
        loginPass = findViewById(R.id.new_login_password);
        loginToSignUp = findViewById(R.id.new_login_to_sign_up);
        loginToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginPage.this,RegistrationPage.class);
                startActivity(intent);
                finish();
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = loginMail.getText().toString();
                String pass = loginPass.getText().toString();
                if(TextUtils.isEmpty(mail)){
                    progressDialog.dismiss();
                    Toast.makeText(LoginPage.this,"Enter your email please!",Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(pass)){
                    progressDialog.dismiss();
                    Toast.makeText(LoginPage.this,"Enter your password please!",Toast.LENGTH_SHORT).show();
                }
                else if(!mail.matches(emailPattern)){
                    progressDialog.dismiss();
                    loginMail.setError("Give a valid email address..");
                }
                else if(pass.length() < 6){
                    progressDialog.dismiss();
                    loginPass.setError("Must be 7 digit long..");
                }
                else{
                    auth.signInWithEmailAndPassword(mail,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                progressDialog.show();
                                try {
                                    Intent intent = new Intent(LoginPage.this,HomePage.class);
                                    startActivity(intent);
                                    finish();
                                }
                                catch (Exception e){
                                    Toast.makeText(LoginPage.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                Toast.makeText(LoginPage.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}