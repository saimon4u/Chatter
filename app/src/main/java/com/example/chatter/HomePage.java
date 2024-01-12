package com.example.chatter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomePage extends AppCompatActivity {
    private RecyclerView chatsRV;
    private CircleImageView profileImg, friendReq;
    private TextView homeUserName, homeStatus;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private ArrayList<User> userArrayList;
    private DatabaseReference statusReference;
    private ChatsAdapter chatsAdapter;
    private boolean s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(HomePage.this, LoginPage.class);
            startActivity(intent);
            finish();
        } else {
            database = FirebaseDatabase.getInstance();
            statusReference = database.getReference().child("user").child(auth.getUid());
            DatabaseReference reference = database.getReference().child("user").child(auth.getUid()).child("friends");
            statusReference.child("currentStatus").setValue("online");
            Date date = new Date();
            statusReference.child("lastSeen").setValue(date.getTime());
            userArrayList = new ArrayList<User>();
            profileImg = findViewById(R.id.home_profile_img);
            homeStatus = findViewById(R.id.home_userStatus);
            homeUserName = findViewById(R.id.home_userName);
            friendReq = findViewById(R.id.friend_req);
            chatsRV = findViewById(R.id.chatsRV);
            chatsRV.setLayoutManager(new LinearLayoutManager(this));
            chatsAdapter = new ChatsAdapter(HomePage.this, userArrayList, auth.getUid());
            chatsRV.setAdapter(chatsAdapter);
            reference.addValueEventListener(new ValueEventListener(){
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (isFinishing()) return;
                    userArrayList.clear();
                    if (snapshot.exists()){
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            User user = dataSnapshot.getValue(User.class);
                            userArrayList.add(user);
                        }
                    }else{
                        Toast.makeText(HomePage.this, "You have no friends yet..", Toast.LENGTH_SHORT).show();
                    }
                    chatsAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error){

                }
            });

            DatabaseReference databaseReference = database.getReference().child("user");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String id = "ok";
                    if (auth.getUid() != null) id = auth.getUid();
                    String pic = "hello";
                    if (snapshot.child(id).child("profilePic").getValue() != null) {
                        pic = snapshot.child(id).child("profilePic").getValue().toString();
                    }
                    String userName = snapshot.child(id).child("userName").getValue().toString();
                    String status = snapshot.child(id).child("status").getValue().toString();
                    homeStatus.setText(status);
                    Picasso.get().load(pic).into(profileImg);
                    homeUserName.setText(userName);
                    if(snapshot.exists()){
                        for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                            User user = dataSnapshot.getValue(User.class);
                            if(user != null && userArrayList.size() > 0){
                                for(int i=0; i<userArrayList.size(); i++){
                                    if(user.getUserID().equals(userArrayList.get(i).getUserID())){
                                        userArrayList.set(i,user);
                                    }
                                }
                                chatsAdapter.notifyDataSetChanged();
                            }
//                            Log.d("TAG", user.getUserName());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            profileImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HomePage.this, SettingPage.class);
                    startActivity(intent);
                }
            });
            friendReq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HomePage.this, FriendRequestPage.class);
                    startActivity(intent);
                }
            });
        }

    }



    @Override
    protected void onPause() {
        super.onPause();
        Chatter.setAppForeground(false);
//        if(!Chatter.isAppForeground()){
//            statusReference.child("currentStatus").setValue("offline");
//            Date date = new Date();
//            statusReference.child("lastSeen").setValue(date.getTime());
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Chatter.setAppForeground(true);
//        if(Chatter.isAppForeground()) {
//            statusReference.child("currentStatus").setValue("online");
//            Date date = new Date();
//            statusReference.child("lastSeen").setValue(date.getTime());
//        }
    }
}