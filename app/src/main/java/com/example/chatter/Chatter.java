package com.example.chatter;

import android.app.Application;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class Chatter extends Application {
    private static boolean isAppForeground;

    public static boolean isAppForeground(){
        return isAppForeground;
    }

    public static void setAppForeground(boolean status){
        if(isAppForeground != status){
            isAppForeground = status;
            booleanChangeListener();
        }
    }

    private static void booleanChangeListener() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid());
        if(isAppForeground){
            reference.child("currentStatus").setValue("online");
            Date date = new Date();
            reference.child("lastSeen").setValue(date.getTime());
        }
        else{
            reference.child("currentStatus").setValue("offline");
            Date date = new Date();
            reference.child("lastSeen").setValue(date.getTime());
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
