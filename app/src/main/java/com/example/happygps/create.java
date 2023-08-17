package com.example.happygps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class create extends AppCompatActivity {
    EditText msg;
    CheckBox sendBatterylevel,sendSignallevel;
    Button done;
    SharedPreferences sharedPreferences;
    private AdView adView_bottom;

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        msg=findViewById(R.id.txt_msg);
        done=findViewById(R.id.btn_done);
        sendSignallevel=findViewById(R.id.chk_signalLevel);
        sendBatterylevel=findViewById(R.id.chk_batteryLevel);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInterstitialAd();
                saveData();
            }
        });

        showBannerad();
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(create.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void saveData(){
        int count;
        sharedPreferences=getSharedPreferences("sendMessage", Context.MODE_PRIVATE);
        count=sharedPreferences.getInt("count",0);

        if(count>0 && !msg.getText().toString().equals("")){
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putInt("count",count+=1);
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
            Intent intent=new Intent(create.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        else if (count==0 && !msg.getText().toString().equals("")){

            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putInt("count",1);
            editor.putString(1+"message",msg.getText().toString());

            if(sendBatterylevel.isChecked()){
                editor.putString(1+"battery_level","true");
            }
            else {
                editor.putString(1+"battery_level","false");
            }
            if(sendSignallevel.isChecked()){
                editor.putString(1+"signal_level","true");
            }
            else {
                editor.putString(1+"signal_level","false");
            }
            editor.commit();
            Intent intent=new Intent(create.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        else if(msg.getText().toString().equals("")){
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
        }
    }

    private void showInterstitialAd(){
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, "ca-app-pub-8280404068654201/5192317278", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                mInterstitialAd = interstitialAd;
                Log.i("add", "onAdLoaded");
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdClicked() {
                        // Called when a click is recorded for an ad.
                        Log.d("addvertise", "Ad was clicked.");
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        // Set the ad reference to null so you don't show the ad a second time.
                        Log.d("addvertise", "Ad dismissed fullscreen content.");
                        mInterstitialAd = null;
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when ad fails to show.
                        Log.e("addvertise", "Ad failed to show fullscreen content.");
                        mInterstitialAd = null;
                    }

                    @Override
                    public void onAdImpression() {
                        // Called when an impression is recorded for an ad.
                        Log.d("addvertise", "Ad recorded an impression.");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when ad is shown.
                        Log.d("addvertise", "Ad showed fullscreen content.");
                    }
                });
            }
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Log.d("addvertise", loadAdError.toString());
                mInterstitialAd = null;
            }
        });

        if (mInterstitialAd!=null){
            mInterstitialAd.show(this);
        }
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