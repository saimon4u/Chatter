package com.example.chatter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class ChatBox extends AppCompatActivity{
    private CircleImageView backBtn,menuOptions,profileImg;
    private RecyclerView msgBox;
    private CardView sendMsg;
    private EditText writeMsg;
    private TextView profileName;
    private ArrayList <MessageModel> msgList;
    private MessageBoxAdapter adapter;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    public static String senderImg;
    public static String imageGetter;
    private String receiverImg,receiverUid,receiverName,senderUid,senderRoom,receiverRoom;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_box);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        receiverName = getIntent().getStringExtra("name");
        receiverUid = getIntent().getStringExtra("uID");
        receiverImg = getIntent().getStringExtra("receiverImg");
        backBtn = findViewById(R.id.back);
        profileImg = findViewById(R.id.receiver_img);
        profileName = findViewById(R.id.receiver_userName);
        menuOptions = findViewById(R.id.menu_option);
        msgBox = findViewById(R.id.msg_box);
        writeMsg = findViewById(R.id.write_msg_editBox);
        sendMsg = findViewById(R.id.send_msg);
        msgList = new ArrayList<>();
        Picasso.get().load(receiverImg).into(profileImg);
        profileName.setText("" + receiverName);
        senderUid = auth.getUid();
        senderRoom = senderUid + receiverUid;
        receiverRoom = receiverUid + senderUid;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        msgBox.setLayoutManager(linearLayoutManager);
        adapter = new MessageBoxAdapter(this,msgList,receiverRoom,senderRoom);
        msgBox.setAdapter(adapter);

        DatabaseReference reference = database.getReference().child("user").child(auth.getUid());
        DatabaseReference chatReference = database.getReference().child("chats").child(senderRoom).child("messages");
        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                msgList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    MessageModel model = dataSnapshot.getValue(MessageModel.class);
                    msgList.add(model);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("profilePic").getValue() != null){
                    senderImg = snapshot.child("profilePic").getValue().toString();
                }
                imageGetter = receiverImg;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = writeMsg.getText().toString();
                if(message.isEmpty()){
                    Toast.makeText(ChatBox.this, "Enter a message to send...", Toast.LENGTH_SHORT).show();
                    return;
                }
                writeMsg.setText("");
                Date date = new Date();
                MessageModel model = new MessageModel(message,senderUid,date.getTime());
                database.getReference().child("chats").child(senderRoom).child("messages").push().setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        database.getReference().child("chats").child(receiverRoom).child("messages").push().setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });
                    }
                });
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Chatter.setAppForeground(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Chatter.setAppForeground(true);
    }
}