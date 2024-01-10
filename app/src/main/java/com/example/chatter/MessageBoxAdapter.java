package com.example.chatter;
import static com.example.chatter.ChatBox.imageGetter;
import static com.example.chatter.ChatBox.senderImg;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageBoxAdapter extends RecyclerView.Adapter {
    private Context context;
    private ArrayList <MessageModel> messageList;
    private final int itemSend = 1;
    private final int itemReceived = 2;
    private String receiverRoom,senderRoom;

    public MessageBoxAdapter(Context context, ArrayList<MessageModel> messageList, String receiverRoom, String senderRoom) {
        this.context = context;
        this.messageList = messageList;
        this.receiverRoom = receiverRoom;
        this.senderRoom = senderRoom;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == itemSend){
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout,parent,false);
            return new SenderViewholder(view);
        }
        else{
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_layout,parent,false);
            return new ReceiverViewholder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel model = messageList.get(position);



        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view){
                new AlertDialog.Builder(context).setTitle("Delete")
                        .setMessage("Are you sure you want to delete this message?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).child("messages");
                                dbReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                                                if(model.getTimeStamp() == messageSnapshot.child("timeStamp").getValue(Long.class)){
                                                    dbReference.child(messageSnapshot.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messageSnapshot.child("senderId").getValue().toString())){
                                                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("chats").child(receiverRoom).child("messages");
                                                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener(){
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                                                                        if (dataSnapshot.exists()) {
                                                                            for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                                                                                if(model.getTimeStamp() == messageSnapshot.child("timeStamp").getValue(Long.class)){
                                                                                    databaseReference.child(messageSnapshot.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            Log.d("TAG", "onComplete: " + databaseReference.child(messageSnapshot.getKey()).child("timeStamp"));
                                                                                        }
                                                                                    });
                                                                                }
                                                                            }
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                        // Handle errors
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        } else {
                                            // No messages found
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();

                return false;
            }
        });


        if(holder.getClass() == SenderViewholder.class){
            SenderViewholder viewHolder = (SenderViewholder)holder;
            viewHolder.sendMsg.setText(model.getMessage());
            Picasso.get().load(senderImg).into(viewHolder.senderImg);
        }
        else{
            ReceiverViewholder viewHolder = (ReceiverViewholder)holder;
            viewHolder.receiveMsg.setText(model.getMessage());
            Picasso.get().load(imageGetter).into(viewHolder.receiverImg);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        MessageModel model = messageList.get(position);
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(model.getSenderId())){
            return itemSend;
        }
        return itemReceived;
    }

    class SenderViewholder extends RecyclerView.ViewHolder{
        CircleImageView senderImg;
        TextView sendMsg;
        public SenderViewholder(@NonNull View itemView) {
            super(itemView);
            senderImg = itemView.findViewById(R.id.sendMsgPic);
            sendMsg = itemView.findViewById(R.id.sendMsgText);
        }
    }
    class ReceiverViewholder extends RecyclerView.ViewHolder{
        CircleImageView receiverImg;
        TextView receiveMsg;
        public ReceiverViewholder(@NonNull View itemView) {
            super(itemView);
            receiverImg = itemView.findViewById(R.id.recMsgPic);
            receiveMsg = itemView.findViewById(R.id.recMsgText);
        }
    }
}
