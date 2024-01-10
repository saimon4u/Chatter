package com.example.chatter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.viewholder> {
    private Context context;
    private ArrayList <User> friendRequestList;

    public FriendRequestAdapter(Context context, ArrayList<User> friendRequestList) {
        this.context = context;
        this.friendRequestList = friendRequestList;
    }

    @NonNull
    @Override
    public FriendRequestAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_layout,parent,false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestAdapter.viewholder holder, int position) {
        User user = friendRequestList.get(position);
        holder.userName.setText(user.getUserName());
        Picasso.get().load(user.getProfilePic()).into(holder.userImg);
        holder.reject.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("user").child(user.getUserID()).child("friends");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot data: snapshot.getChildren()){
                            if(data.child("userID").getValue(String.class).equals(FirebaseAuth.getInstance().getUid())){
                                databaseReference.child(data.getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                    }
                                });
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("request");
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot data: snapshot.getChildren()){
                            if(user.getUserID().equals(data.child("userID").getValue(String.class))){
                                reference.child(data.getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                    }
                                });
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("request");
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot){
                        for(DataSnapshot data: snapshot.getChildren()){
                            if(user.getUserID().equals(data.child("userID").getValue(String.class))){
                                reference.child(data.getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                    }
                                });
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("friends");
                reference1.push().setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount(){
        return friendRequestList.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        CircleImageView userImg;
        TextView userName;
        Button accept,reject;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            userImg = itemView.findViewById(R.id.not_img);
            userName = itemView.findViewById(R.id.not_name);
            accept = itemView.findViewById(R.id.not_acceptBtn);
            reject = itemView.findViewById(R.id.not_rejectBtn);
        }
    }
}
