package com.example.chatter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CurrentUsersPage extends AppCompatActivity {
    private CircleImageView back;
    private RecyclerView currentUsersRV;
    private CurrentUsersAdapter currentUsersAdapter;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private ArrayList<User> currentUserArrayList;
    private ArrayList <String> myFriends;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_users);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child("user");
        currentUserArrayList = new ArrayList<User>();
        back = findViewById(R.id.currentUsers_back);
        currentUsersRV = findViewById(R.id.current_usersRV);
        currentUsersRV.setLayoutManager(new LinearLayoutManager(this));
        currentUsersAdapter = new CurrentUsersAdapter(this, currentUserArrayList);
        currentUsersRV.setAdapter(currentUsersAdapter);
        myFriends = new ArrayList<>();
        DatabaseReference databaseReference = database.getReference().child("user").child(auth.getUid()).child("friends");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot data: snapshot.getChildren()){
                        myFriends.add(data.child("userID").getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                if(isFinishing())return;
                currentUserArrayList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    if(!user.getUserID().equals(auth.getUid()) && !myFriends.contains(user.getUserID()))
                        currentUserArrayList.add(user);
                }
                if(currentUserArrayList.size()==0)
                    Toast.makeText(CurrentUsersPage.this, "There is no user to add", Toast.LENGTH_SHORT).show();
                currentUsersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error){

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}