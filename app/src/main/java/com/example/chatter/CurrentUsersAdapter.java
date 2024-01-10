package com.example.chatter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

public class CurrentUsersAdapter extends RecyclerView.Adapter<CurrentUsersAdapter.viewholder> {
    private Context currentUsersPage;
    private ArrayList <User> currentUsers;

    public CurrentUsersAdapter(Context currentUsersPage, ArrayList<User> currentUsers) {
        this.currentUsersPage = currentUsersPage;
        this.currentUsers = currentUsers;
    }

    @NonNull
    @Override
    public CurrentUsersAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(currentUsersPage).inflate(R.layout.current_user_layout,parent,false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrentUsersAdapter.viewholder holder, int position) {
        User user = currentUsers.get(position);
        holder.currentUserName.setText(user.getUserName());
        Picasso.get().load(user.getProfilePic()).into(holder.currentUserImg);
        holder.addBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Toast.makeText(currentUsersPage, "Request sent", Toast.LENGTH_SHORT).show();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("friends");
                reference.push().setValue(user).addOnCompleteListener(new OnCompleteListener<Void>(){
                    @Override
                    public void onComplete(@NonNull Task<Void> task){
                        DatabaseReference refer = FirebaseDatabase.getInstance().getReference().child("user");
                        refer.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot data: snapshot.getChildren()){
                                    if(FirebaseAuth.getInstance().getUid().equals(data.child("userID").getValue(String.class))){
                                        User u = data.getValue(User.class);
                                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("user").child(user.getUserID()).child("request");
                                        reference1.push().setValue(u).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
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
            }
        });
    }

    @Override
    public int getItemCount() {
        return currentUsers.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        CircleImageView currentUserImg,addBtn;
        TextView currentUserName;
        public viewholder(@NonNull View itemView){
            super(itemView);
            currentUserImg = itemView.findViewById(R.id.currentUserImg);
            currentUserName = itemView.findViewById(R.id.currentUserName);
            addBtn = itemView.findViewById(R.id.add_friend);
        }
    }
}
