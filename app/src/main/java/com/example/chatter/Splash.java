package com.example.chatter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if(isWorkingInternetPresent()){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Splash.this,HomePage.class);
                    startActivity(intent);
                    finish();
                }
            },1500);
        }
    }

    private boolean isWorkingInternetPresent(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if(info != null)
                for(NetworkInfo net: info){
                    if(net.getState() == NetworkInfo.State.CONNECTED){
                        return true;
                    }
                }
        }
        return false;
    }
}