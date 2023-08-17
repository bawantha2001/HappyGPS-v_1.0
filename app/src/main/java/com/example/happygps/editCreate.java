package com.example.happygps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class editCreate extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    EditText msg;
    CheckBox sendBatterylevel,sendSignallevel;
    Button done,delete;

    private AdView adView_bottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_create);
        showBannerad();

        msg=findViewById(R.id.txt_msg);
        done=findViewById(R.id.btn_done);
        delete=findViewById(R.id.btn_delete);
        sendSignallevel=findViewById(R.id.chk_signalLevel);
        sendBatterylevel=findViewById(R.id.chk_batteryLevel);

        Intent intent=getIntent();
        int x=intent.getIntExtra("value",0);
        show_details(x);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData(x);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete(x);
            }
        });
    }

    public void onBackPressed() {
        Intent intent=new Intent(editCreate.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void show_details(int x){
        String message,battery,signal;
        sharedPreferences=getSharedPreferences("sendMessage", Context.MODE_PRIVATE);

        message=sharedPreferences.getString(x+"message","");
        battery=sharedPreferences.getString(x+"battery_level","");
        signal=sharedPreferences.getString(x+"signal_level","");

        msg.setText(message);
        if(battery.equals("true")){
            sendBatterylevel.setChecked(true);
        }
        if (signal.equals("true")){
            sendSignallevel.setChecked(true);
        }
    }

    private void saveData(int count){
        sharedPreferences=getSharedPreferences("sendMessage", Context.MODE_PRIVATE);

        if(!msg.getText().toString().equals("")){
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString(count+"message",msg.getText().toString());

            if(sendBatterylevel.isChecked()){
                editor.putString(count+"battery_level","true");
            }
            else {
                editor.putString(count+"battery_level","false");
            }
            if(sendSignallevel.isChecked()){
                editor.putString(count+"signal_level","true");
            }
            else {
                editor.putString(count+"signal_level","false");
            }
            editor.commit();
            Intent intent=new Intent(editCreate.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        else if(msg.getText().toString().equals("")){
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
        }
    }

    private void delete(int number){
        String message,battery,signal;
        int count;
        sharedPreferences=getSharedPreferences("sendMessage", Context.MODE_PRIVATE);
        count=sharedPreferences.getInt("count",0);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        for(int x=1;x<=count;x++){
            if(number!=x){
                message=sharedPreferences.getString(x+"message","");
                battery=sharedPreferences.getString(x+"battery_level","");
                signal=sharedPreferences.getString(x+"signal_level","");
                editor.putString((x-1)+"message",message);
                editor.putString((x-1)+"battery_level",battery);
                editor.putString((x-1)+"signal_level",signal);
            }
        }
        editor.putInt("count",count-=1);
        editor.commit();
        Intent intent=new Intent(editCreate.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void showBannerad(){
        adView_bottom=findViewById(R.id.adView_top);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        adView_bottom.loadAd(adRequest);

        adView_bottom.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdSwipeGestureClicked() {
                super.onAdSwipeGestureClicked();
            }
        });
    }
}