package com.example.happygps;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{
    Button create;
    TextView nolocationTriggers;
    ListView messagelist;
    messageAdapter messageAdapter;
    ArrayList<keywords> messageItemList = new ArrayList<>();
    ArrayList<String>signalItemList=new ArrayList<>();
    ArrayList<String>batteryItemList=new ArrayList<>();
    SharedPreferences sharedPreferences,sharedPreferencesFirst;
    private static final int BACK_PRESS_DELAY = 2000;
    private boolean doubleBackToExitPressedOnce = false;
    private final Handler handler = new Handler();
    View coordinatorLayout;
    private AdView adView_bottom;
    Intent serviceIntent;
    LocationListener locationListener;
    LocationManager locationManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firstTime();

        if(forgroundServiceRunning()){
            serviceIntent = new Intent(this, foreground_Activity.class);
        }


        create = findViewById(R.id.button_Creat);
        messagelist = findViewById(R.id.listView_msg);
        nolocationTriggers=findViewById(R.id.txt_nolo);
        showMessages();
        showBannerad();

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(forgroundServiceRunning()){
                    try {
                        stopService(serviceIntent);
                        Intent intent_create=new Intent(MainActivity.this, com.example.happygps.create.class);
                        startActivity(intent_create);
                        finish();
                    }catch (Exception e){
                        Toast.makeText(MainActivity.this, String.valueOf(e), Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Intent intent_create=new Intent(MainActivity.this, com.example.happygps.create.class);
                    startActivity(intent_create);
                    finish();
                }
            }
        });

        messagelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(forgroundServiceRunning()){
                    try {
                        stopService(serviceIntent);
                        Intent intent_editCreate=new Intent(MainActivity.this,editCreate.class);
                        intent_editCreate.putExtra("value",i+1);
                        startActivity(intent_editCreate);
                        finish();
                    }catch (Exception e){
                        Toast.makeText(MainActivity.this, String.valueOf(e), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }

    public boolean forgroundServiceRunning(){
        ActivityManager activityManager=(ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service: activityManager.getRunningServices(Integer.MAX_VALUE)){
            if(foreground_Activity.class.getName().equals(service.service.getClassName())){
                return true;
            }
        }
        return false;
    }

    private void checkPermission(){
        ComponentName cn = new ComponentName("com.example.happygps", "com.example.happygps.NotificationListenerservice");
        String enabledListeners = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        boolean isEnabled = enabledListeners != null && enabledListeners.contains(cn.flattenToString());

        if(!isEnabled){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            alertDialogBuilder.setTitle("Notification listner service permission.");
            alertDialogBuilder.setMessage("This app requires notification access in order to read notifications and reply accordingly.");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("Give Permission Access", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    getApplicationContext().startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            alertDialogBuilder.setTitle("User permissions.");
            alertDialogBuilder.setMessage("In order for the app to work properly,You must allow the following permissions.\n\nLocation permission (Allow all the time)\nContatcts permission (Allow)\nPhone (Allow)\nSMS (Allow)");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("Give Permission Access", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.fromParts("package", getPackageName(), null));
                    startActivity(intent);
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }


    public void showMessages(){
        String message = "",battery,signal;
        int count;
        sharedPreferences=getSharedPreferences("sendMessage", Context.MODE_PRIVATE);
        count=sharedPreferences.getInt("count",0);

        if(count>0){

            for(int x=1;x<=count;x++){
                message=sharedPreferences.getString(x+"message","");
                battery=sharedPreferences.getString(x+"battery_level","");
                signal=sharedPreferences.getString(x+"signal_level","");
                messageItemList.add(new keywords(message));
                batteryItemList.add(battery);
                signalItemList.add(signal);
            }
            if(count==1) nolocationTriggers.setText("Happy GPS is ready!\n\nNow,Send the trigger word:\n"+message+"\nto this device through WhatsApp to get this device location!");
            else nolocationTriggers.setVisibility(View.INVISIBLE);

            messageAdapter=new messageAdapter(this,R.layout.list_row,messageItemList);
            messagelist.setAdapter(messageAdapter);

            if(!forgroundServiceRunning()){
                serviceIntent = new Intent(this, foreground_Activity.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent);
                }
            }
            locationProviderEnabled();
        }
        else nolocationTriggers.setText("No location triggers");
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
        }else
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();

        this.doubleBackToExitPressedOnce = true;

        handler.postDelayed(() -> doubleBackToExitPressedOnce = false, BACK_PRESS_DELAY);
    }

    @Override
    protected void onResume() {
        checkPermission();
        super.onResume();
    }

    private void locationProviderEnabled(){
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                LocationListener.super.onStatusChanged(provider, status, extras);
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                try {
                    if(forgroundServiceRunning()){
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                        alertDialogBuilder.setTitle("Turn ON Location");
                        alertDialogBuilder.setMessage("Seems like the device location is turned OFF. In order for the app to work properly, location must be turned ON");
                        alertDialogBuilder.setCancelable(false);
                        alertDialogBuilder.setPositiveButton("I Will Do That", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                }catch (Exception e){
                    Log.d("onProviderChange",String.valueOf(e));
                }
            }
        };
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    500, (float) 0.01, locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void firstTime(){
        coordinatorLayout =findViewById(R.id.main_layout);
        sharedPreferencesFirst=getSharedPreferences("first", Context.MODE_PRIVATE);

        String firstTime=sharedPreferencesFirst.getString("firstTime","true");
        if(firstTime.equals("true")){
            Snackbar snackbar1 = Snackbar.make(coordinatorLayout, "Welcome to Happy GPS! Click NEXT to proceed", Snackbar.LENGTH_INDEFINITE)
                    .setAction("NEXT", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SharedPreferences.Editor editor=sharedPreferencesFirst.edit();
                            editor.putString("firstTime","false");
                            editor.commit();
                            Snackbar snackbar2=Snackbar.make(coordinatorLayout,"First save a WhatsApp trigger by clicking \n(+ CREATE)",Snackbar.LENGTH_INDEFINITE)
                                    .setAction("OK", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                        }
                                    });
                            snackbar2.show();
                        }
                    });
            snackbar1.show();
        }
    }

    private void showBannerad(){
        adView_bottom=findViewById(R.id.adView_bottom);
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