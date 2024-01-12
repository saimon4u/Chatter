package com.example.chatter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrationPage extends AppCompatActivity {
    private LinearLayout profileImg;
    private EditText userName,email,password,confirmPassword;
    private Button submitBtn;
    private TextView signIn;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private Uri imgUri;
    String imageLink;
    private CircleImageView imgIcon;
    private Date date;
    private android.app.ProgressDialog progressDialog;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        profileImg = findViewById(R.id.reg_img);
        userName = findViewById(R.id.reg_username);
        password = findViewById(R.id.reg_password);
        date = new Date();
        email = findViewById(R.id.reg_email);
        confirmPassword = findViewById(R.id.reg_confirm_password);
        signIn = findViewById(R.id.reg_sign_in_btn);
        imgIcon = findViewById(R.id.profileImg);
        submitBtn = findViewById(R.id.reg_submit_btn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait....");
        progressDialog.setCancelable(false);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationPage.this,LoginPage.class);
                startActivity(intent);
                finish();
            }
        });
        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select your image"),50);
            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = userName.getText().toString();
                String mail = email.getText().toString();
                String pass = password.getText().toString();
                String cPass = confirmPassword.getText().toString();
                String bio = "Hey I'm " + name;
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(mail) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(cPass)){
                    Toast.makeText(RegistrationPage.this, "Please Enter Valid Information", Toast.LENGTH_SHORT).show();
                }
                else  if (!mail.matches(emailPattern)){
                    email.setError("Type A Valid Email Here");
                }
                else if (pass.length()<6){
                    password.setError("Password Must Be 6 Characters Or More");
                }
                else if (!pass.equals(cPass)){
                    password.setError("The Password Didn't Match");
                }
                else{
                    auth.createUserWithEmailAndPassword(mail,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                progressDialog.show();
                                String id = task.getResult().getUser().getUid();
                                DatabaseReference reference = database.getReference().child("user").child(id);
                                StorageReference storageReference = storage.getReference().child("Upload").child(id);
                                if(imgUri != null){
                                    storageReference.putFile(imgUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if(task.isSuccessful()){
                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        imageLink = uri.toString();
                                                        User user = new User(id,name,mail,pass,imageLink,bio,"online",date.getTime());
                                                        reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>(){
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
//                                                                    progressDialog.show();
                                                                    Intent intent = new Intent(RegistrationPage.this,HomePage.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                                else{
                                                                    Toast.makeText(RegistrationPage.this, "Error occurred..", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                                else{
                                    String bio = "Hey I'm " + name;
                                    imageLink = "https://firebasestorage.googleapis.com/v0/b/chatter-1e3f0.appspot.com/o/dummy%2Fron.png?alt=media&token=1ea261fe-57e9-4e57-8b05-2e6465a713f8";
                                    User user = new User(id,name,mail,pass,imageLink,bio,"online",date.getTime());
                                    reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
//                                                progressDialog.show();
                                                Intent intent = new Intent(RegistrationPage.this,HomePage.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else{
                                                Toast.makeText(RegistrationPage.this, "Error occurred...", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                            else{
                                Toast.makeText(RegistrationPage.this, task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==50){
            if (data!=null){
                imgUri = data.getData();
                imgIcon.setImageURI(imgUri);
            }
        }
    }
}