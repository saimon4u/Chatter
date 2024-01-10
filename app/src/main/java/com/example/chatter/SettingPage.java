package com.example.chatter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingPage extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private CircleImageView settingProfileImg;
    private TextView settingUserName,settingShowEmail,oldUserName,oldStatus,oldEmail;
    private String email,password;
    private LinearLayout logoutBtn,userNameLayout,statusLayout,emailLayout,currentUsersLayout;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private Uri setImageUri;
    private Button save;
    private Date date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_page);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        settingProfileImg = findViewById(R.id.setting_profileImg);
        settingUserName = findViewById(R.id.setting_userName);
        settingShowEmail = findViewById(R.id.setting_show_email);
        oldEmail = findViewById(R.id.old_email);
        oldUserName = findViewById(R.id.old_username);
        oldStatus = findViewById(R.id.old_status);
        logoutBtn = findViewById(R.id.logout_layout);
        userNameLayout = findViewById(R.id.username_layout);
        statusLayout = findViewById(R.id.status_layout);
        emailLayout = findViewById(R.id.email_layout);
        currentUsersLayout = findViewById(R.id.currentUsers);
        save = findViewById(R.id.save_changesBtn);
        date = new Date();
        DatabaseReference reference = database.getReference().child("user").child(auth.getUid());
        StorageReference storageReference = storage.getReference().child("Upload").child(auth.getUid());
        logoutBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(SettingPage.this,R.style.dialog);
                dialog.setContentView(R.layout.dialog_layout);
                Button yesBtn,noBtn;
                yesBtn = dialog.findViewById(R.id.yesBtn);
                noBtn = dialog.findViewById(R.id.noBtn);
                DatabaseReference reference1 = database.getReference().child("user").child(auth.getUid());
                yesBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                reference1.child("currentStatus").setValue("offline");
                                Date date = new Date();
                                reference1.child("lastSeen").setValue(date.getTime());
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(SettingPage.this,LoginPage.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                });
                noBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                if(snapshot.exists()){
                    email = snapshot.child("mail").getValue().toString();
                    password = snapshot.child("pass").getValue().toString();
                    String name = snapshot.child("userName").getValue().toString();
                    String profilePic = snapshot.child("profilePic").getValue().toString();
                    String status = snapshot.child("status").getValue().toString();
                    settingUserName.setText(name);
                    settingShowEmail.setText(email);
                    oldEmail.setText(email);
                    oldStatus.setText(status);
                    oldUserName.setText(name);
                    Picasso.get().load(profilePic).into(settingProfileImg);
                }
                else{
                    Log.d("snapshot", "onDataChange: ");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        userNameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(SettingPage.this,R.style.dialog);
                dialog.setContentView(R.layout.setting_dialog_layout);
                Button enterBtn,cancelBtn;
                EditText value;
                TextView fixedValue;
                enterBtn = dialog.findViewById(R.id.enterBtn);
                cancelBtn = dialog.findViewById(R.id.cancelBtn);
                value = dialog.findViewById(R.id.value);
                fixedValue = dialog.findViewById(R.id.fixedValue);
                fixedValue.setText("Enter a new User Name");
                enterBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view){
                        String changedValue = value.getText().toString();
                        if(TextUtils.isEmpty(changedValue)){
                            Toast.makeText(SettingPage.this,"Enter a valid user name",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            dialog.dismiss();
                            oldUserName.setText(changedValue);
                            settingUserName.setText(changedValue);
                        }
                    }
                });
                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        emailLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(SettingPage.this,R.style.dialog);
                dialog.setContentView(R.layout.setting_dialog_layout);
                Button enterBtn,cancelBtn;
                EditText value;
                TextView fixedValue;
                enterBtn = dialog.findViewById(R.id.enterBtn);
                cancelBtn = dialog.findViewById(R.id.cancelBtn);
                value = dialog.findViewById(R.id.value);
                fixedValue = dialog.findViewById(R.id.fixedValue);
                fixedValue.setText("Enter a new Email");
                enterBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view){
                        String changedValue = value.getText().toString();
                        if(TextUtils.isEmpty(changedValue)){
                            Toast.makeText(SettingPage.this,"Enter a valid user name",Toast.LENGTH_SHORT).show();
                        }
                        else  if (!changedValue.matches(emailPattern)) {
                            value.setError("Type A Valid Email Here");
                        }
                        else{
                            dialog.dismiss();
                            oldEmail.setText(changedValue);
                            settingShowEmail.setText(changedValue);
                        }
                    }
                });
                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        statusLayout.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(SettingPage.this,R.style.dialog);
                dialog.setContentView(R.layout.setting_dialog_layout);
                Button enterBtn,cancelBtn;
                EditText value;
                TextView fixedValue;
                enterBtn = dialog.findViewById(R.id.enterBtn);
                cancelBtn = dialog.findViewById(R.id.cancelBtn);
                value = dialog.findViewById(R.id.value);
                fixedValue = dialog.findViewById(R.id.fixedValue);
                fixedValue.setText("Enter a new Status");
                enterBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view){
                        String changedValue = value.getText().toString();
                        if(TextUtils.isEmpty(changedValue)){
                            Toast.makeText(SettingPage.this,"Enter a valid user name",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            dialog.dismiss();
                            oldStatus.setText(changedValue);
                        }
                    }
                });
                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        settingProfileImg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),10);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String changedUserName = oldUserName.getText().toString();
                String changedStatus = oldStatus.getText().toString();
                String changedEmail = oldEmail.getText().toString();
                ArrayList <User> friendList = new ArrayList<>();
                DatabaseReference reference1 = database.getReference().child("user").child(auth.getUid()).child("friends");
                reference1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                                friendList.add(dataSnapshot.getValue(User.class));
                            }
                            for(User user : friendList){
                                Log.d("TAG", user.getUserName());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                ArrayList <User> requestList = new ArrayList<>();
                DatabaseReference reference2 = database.getReference().child("user").child(auth.getUid()).child("request");
                reference2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                                requestList.add(dataSnapshot.getValue(User.class));
                            }
                            for(User user : requestList){
                                Log.d("TAG", user.getUserName());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                
                if(setImageUri != null){
                    storageReference.putFile(setImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String finalImgUri = uri.toString();
                                    User user = new User(auth.getUid(),changedUserName,changedEmail,password,finalImgUri,changedStatus,"online",date.getTime());
                                    reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>(){
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
//                                                DatabaseReference reference3 = database.getReference().child("user").child(auth.getUid()).child("friends");
                                                pushValue(reference1,friendList);
                                                pushValue(reference2,requestList);
                                                Toast.makeText(SettingPage.this,"Data is saved!",Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(SettingPage.this,HomePage.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else{
                                                Toast.makeText(SettingPage.this,"Something went wrong...",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
                else{
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String finalImgUri = uri.toString();
                            User user = new User(auth.getUid(),changedUserName,changedEmail,password,finalImgUri,changedStatus,"online",date.getTime());
                            reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        pushValue(reference1,friendList);
                                        pushValue(reference2,requestList);
                                        Toast.makeText(SettingPage.this, "Data is saved!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SettingPage.this,HomePage.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else{
                                        Toast.makeText(SettingPage.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
        currentUsersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingPage.this, CurrentUsersPage.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 10){
            if(data != null){
                setImageUri = data.getData();
                settingProfileImg.setImageURI(setImageUri);
            }
        }
    }
    private void pushValue(DatabaseReference reference, ArrayList<User> list){
        for(User item: list){
            reference.push().setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            });
        }
    }
}