package com.example.happygps;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.telephony.SignalStrength;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;

public class foreground_Activity extends Service {

    NotificationListenerservice notificationListenerservice;
    ReciverBroadcastReceiver imagebrc;
    ArrayList<String> messageItemList = new ArrayList<>();
    ArrayList<String>signalItemList=new ArrayList<>();
    ArrayList<String>batteryItemList=new ArrayList<>();
    SharedPreferences sharedPreferences;
    LocationManager locationManager;
    android.location.LocationListener locationListener;
    double latitude;
    double longitude;
    boolean isGPSEnabled;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        imagebrc = new ReciverBroadcastReceiver();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("com.example.happygps");
        registerReceiver(imagebrc,intentFilter);
        readMessages();
        startLocationUpdates();

        final String CHANNELID="Foreground Service ID";
        NotificationChannel channel= null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel(
                    CHANNELID,
                    CHANNELID,
                    NotificationManager.IMPORTANCE_LOW
            );
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
            Notification.Builder notification=new Notification.Builder(this,CHANNELID)
                    .setContentText("Happy GPS is running")
                    .setSmallIcon(R.drawable.app_icon);
            startForeground(1001,notification.build());
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        try {
            unregisterReceiver(imagebrc);
            locationManager.removeUpdates(locationListener);
            Log.d("serviceStatus","stoped");
        }catch (Exception e){
            Log.d("serviceStatus",String.valueOf(e));
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class ReciverBroadcastReceiver extends BroadcastReceiver {
        SmsManager smsManager = SmsManager.getDefault();
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            String From = intent.getStringExtra("from");
            String message_text = intent.getStringExtra("message");
            Log.e("recieved",From+" msg "+message_text);

            for(int x=0;x<messageItemList.size();x++){
                if(message_text.equals(messageItemList.get(x))){
                    if(isGPSEnabled){
                        if(batteryItemList.get(x).equals("true") && signalItemList.get(x).equals("true")){
                            int batLevel = batteryLevel();
                            String signalLevel=signalStatus();
                            String secure_msg="Location is: https://www.google.com/maps/place/"+String.valueOf(latitude)+","+String.valueOf(longitude)+"\n\n"+"Battery Level is: "+String.valueOf(batLevel)+"%\n\n"+"Signal Level is: "+String.valueOf(signalLevel)+"\n\nSent by Happy GPS!";

                            if(From.contains("+")){
                                smsManager.sendTextMessage(From, null, secure_msg, null, null);
                            }
                            else{
                                String phone=getContactNumberFromName(From);
                                smsManager.sendTextMessage(phone, null, secure_msg, null, null);
                            }
                            break;
                        }

                        else if(batteryItemList.get(x).equals("true") && signalItemList.get(x).equals("false")){
                            int batLevel = batteryLevel();
                            String secure_msg="Location is: https://www.google.com/maps/place/"+String.valueOf(latitude)+","+String.valueOf(longitude)+"\n\n"+"Battery Level is: "+String.valueOf(batLevel)+"%\n\nSent by Happy GPS!";

                            if(From.contains("+")){
                                smsManager.sendTextMessage(From, null, secure_msg, null, null);
                            }
                            else{
                                String phone=getContactNumberFromName(From);
                                smsManager.sendTextMessage(phone, null, secure_msg, null, null);
                            }
                            break;
                        }

                        else if(signalItemList.get(x).equals("true") && batteryItemList.get(x).equals("false") ){
                            String signalLevel=signalStatus();
                            String secure_msg="Location is: https://www.google.com/maps/place/"+String.valueOf(latitude)+","+String.valueOf(longitude)+"\n\n"+"Signal Level is: "+String.valueOf(signalLevel)+"\n\nSent by Happy GPS!";

                            if(From.contains("+")){
                                smsManager.sendTextMessage(From, null, secure_msg, null, null);
                            }
                            else{
                                String phone=getContactNumberFromName(From);
                                smsManager.sendTextMessage(phone, null, secure_msg, null, null);
                            }
                            break;
                        }

                        else if(batteryItemList.get(x).equals("false") && signalItemList.get(x).equals("false")){
                            String secure_msg="Location is: https://www.google.com/maps/place/"+String.valueOf(latitude)+","+String.valueOf(longitude)+"\n\nSent by Happy GPS!";

                            if(From.contains("+")){
                                smsManager.sendTextMessage(From, null, secure_msg, null, null);
                            }
                            else{
                                String phone=getContactNumberFromName(From);
                                smsManager.sendTextMessage(phone, null, secure_msg, null, null);
                            }
                            break;
                        }
                    }

                    else {
                        String secure_msg="Happy GPS is unable to send the location because Location is turned OFF on the target device.\n\nSent by Happy GPS!";

                        if(From.contains("+")){
                            smsManager.sendTextMessage(From, null, secure_msg, null, null);
                        }
                        else{
                            String phone=getContactNumberFromName(From);
                            smsManager.sendTextMessage(phone, null, secure_msg, null, null);
                        }
                        break;
                    }
                }
            }
        }
    }

    private String getContactNumberFromName(String name) {
        String number = null;
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + "=?", new String[]{name}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int phoneNumberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            number = cursor.getString(phoneNumberIndex);
            cursor.close();
        }
        return number;
    }

    public int batteryLevel() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = this.registerReceiver(null, filter);
        int batteryPercent = 0;

        if (batteryStatus != null) {
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            batteryPercent = (int) ((level / (float) scale) * 100);
        }

        return batteryPercent;
    }

    private void startLocationUpdates(){
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        locationListener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                isGPSEnabled=true;
                Log.e("Locationlongi",String.valueOf(longitude));
            }
            public void onStatusChanged(@NonNull String provider, int status, Bundle extras) {

            }

            public void onProviderEnabled(@NonNull String provider) {
            }


            public void onProviderDisabled(@NonNull String provider) {
                isGPSEnabled=false;
                Log.e("Locationlongi",String.valueOf(longitude));
            }
        };

        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    1000,(float)0.01,locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public String signalStatus(){
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        SignalStrength signalStrength = null;
        String netQuality = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            signalStrength = telephonyManager.getSignalStrength();
        }
        if (signalStrength != null) {
            int signalLevelAsNumber = signalStrength.getLevel();
            if(signalLevelAsNumber>=4){
                netQuality="Excelent";
            }
            else if(signalLevelAsNumber==3){
                netQuality="Good";
            }
            else if(signalLevelAsNumber==2){
                netQuality="Normal";
            }
            else if(signalLevelAsNumber==1){
                netQuality="Bad";
            }
            return netQuality;
        }
        return "0";
    }

    public void readMessages(){
        String message,battery,signal;
        int count;
        sharedPreferences=getSharedPreferences("sendMessage", Context.MODE_PRIVATE);
        count=sharedPreferences.getInt("count",0);

        if(count>0){
            for(int x=1;x<=count;x++){
                message=sharedPreferences.getString(x+"message","");
                battery=sharedPreferences.getString(x+"battery_level","");
                signal=sharedPreferences.getString(x+"signal_level","");
                messageItemList.add(message);
                batteryItemList.add(battery);
                signalItemList.add(String.valueOf(signal));
            }
        }
    }


}
