package com.example.happygps;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.widget.Toast;

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
        if (!sbn.getPackageName().equals(WA_PACKAGE)) return;

        else{
            Notification notification = sbn.getNotification();
            Bundle bundle = notification.extras;

            String from = bundle.getString("android.title");
            String message = bundle.getString("android.text");

            if(!(from.equals("WhatsApp")||from.contains(":")||message.contains("new messages")||msgCount>1)){
                Intent intent=new Intent("com.example.happygps");
                intent.putExtra("from",from);
                intent.putExtra("message",message);

                sendBroadcast(intent);
                msgCount+=1;
                handler.postDelayed(() -> msgCount=1, 2000);
            }
        }
    }
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

    }
}
