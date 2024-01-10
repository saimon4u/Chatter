package com.example.chatter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendRequestPage extends AppCompatActivity {
    private RecyclerView notificationRV;
    private ArrayList<User> friendRequestList;
    private FriendRequestAdapter friendRequestAdapter;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_page);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        friendRequestList = new ArrayList<>();
        notificationRV = findViewById(R.id.notificationRV);
        notificationRV.setLayoutManager(new LinearLayoutManager(this));
        friendRequestAdapter = new FriendRequestAdapter(this,friendRequestList);
        notificationRV.setAdapter(friendRequestAdapter);
        DatabaseReference reference = database.getReference().child("user").child(auth.getUid()).child("request");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendRequestList.clear();
                if(snapshot.exists()){
                    for(DataSnapshot data: snapshot.getChildren()){
                        friendRequestList.add(data.getValue(User.class));
                    }
                }
                else Toast.makeText(FriendRequestPage.this, "There is no friend request.", Toast.LENGTH_SHORT).show();
                friendRequestAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}