package com.example.happygps;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

import androidx.work.ForegroundInfo;
import androidx.work.ForegroundUpdater;

public class NotificationListenerservice extends NotificationListenerService {

    Context context;
    int msgCount=1;
    private static final String WA_PACKAGE = "com.whatsapp";
    private final Handler handler = new Handler();


    @Override

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (!sbn.getPackageName().equals(WA_PACKAGE)) {
            Log.d("SBN Package","Package cannot found");
        }

        else{
            Notification notification = sbn.getNotification();
            Bundle bundle = notification.extras;

            String from = bundle.getString("android.title");
            String message = bundle.getString("android.text");
            Log.d("notification_class_recieved",from+" before "+message);

            try {
                if(!(from.contains("WhatsApp")||from.contains(":")||message.contains("messages")||msgCount>1)){
                    Log.d("notification_class_recieved",from+" after "+message);
                    Intent intent=new Intent("com.example.happygps");
                    intent.putExtra("from",from);
                    intent.putExtra("message",message);

                    sendBroadcast(intent);
                    msgCount+=1;
                    try {
                        handler.postDelayed(() -> msgCount=1, 2000);
                    }catch (Exception e){
                        Log.d("handle_timer",String.valueOf(e));
                    }
                }
            }catch (Exception e){
                Log.d("notification_class_recieved_Exception",String.valueOf(e));
            }

        }
    }
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

    }
}
