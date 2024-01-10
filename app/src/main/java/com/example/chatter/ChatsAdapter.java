package com.example.chatter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.viewholder> {
    private Context homePage;
    private ArrayList <User> userArrayList;
    private String senderUid;
    public ChatsAdapter(Context homePage, ArrayList<User> userArrayList, String senderUid) {
        this.homePage = homePage;
        this.userArrayList = userArrayList;
        this.senderUid = senderUid;
    }

    @NonNull
    @Override
    public ChatsAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(homePage).inflate(R.layout.user_item,parent,false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatsAdapter.viewholder holder, int position) {
        User user = userArrayList.get(position);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("user").child(user.getUserID()).child("currentStatus");
        holder.userName.setText(user.getUserName());
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.getValue(String.class).equals("online")){
//                    holder.userStatus.setText("Online Now");
//                    holder.activeLight.setImageResource(R.drawable.green);
//                }
//                else{
//                    Date date = new Date(user.getLastSeen());
//                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm a dd MMM",Locale.getDefault());
//                    String val = "Last Seen : " + simpleDateFormat.format(date);
//                    holder.userStatus.setText(val);
//                    holder.activeLight.setImageResource(R.drawable.red);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
        if(user.getCurrentStatus().equals("online")){
            holder.userStatus.setText("Online now");
            holder.activeLight.setImageResource(R.drawable.green);
        }
        else{
            Date date = new Date(user.getLastSeen());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm a dd MMM",Locale.getDefault());
            String val = "Last Seen : " + simpleDateFormat.format(date);
            holder.userStatus.setText(val);
            holder.activeLight.setImageResource(R.drawable.red);
        }
        Picasso.get().load(user.getProfilePic()).into(holder.userImg);
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(homePage,ChatBox.class);
                intent.putExtra("name", user.getUserName());
                intent.putExtra("receiverImg", user.getProfilePic());
                intent.putExtra("uID", user.getUserID());
                homePage.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        CircleImageView userImg,activeLight;
        TextView userName;
        TextView userStatus;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            userImg = itemView.findViewById(R.id.userImg);
            userName = itemView.findViewById(R.id.userName);
            userStatus = itemView.findViewById(R.id.userStatus);
            activeLight = itemView.findViewById(R.id.activeLight);
        }
    }
}
