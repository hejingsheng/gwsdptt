package com.gwsd.ptt.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.gwsd.ptt.R;
import com.gwsd.ptt.broadcast.KeyReceiver;

public class MainService extends Service {

    public static final String CHANNEL_ID_STRING = "service_01";

    public static void startServer(Context context){
        Intent intent=new Intent(context,MainService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        }else{
            context.startService(intent);
        }
    }
    public static void stopServer(Context context){
        Intent intent=new Intent(context,MainService.class);
        context.stopService(intent);
    }

    private void log(String message){
        Log.d("MainService:", message);
    }

    private KeyReceiver keyReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID_STRING, getString(R.string.app_name), NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(mChannel);
            Notification notification = new Notification.Builder(getApplicationContext(), CHANNEL_ID_STRING).build();
            startForeground(1,notification);
        }
        init();
    }

    private void release(){
        log("call release");
    }
    private void init(){
        log("call init");
        keyReceiver = new KeyReceiver();
        registerReceiver(keyReceiver, getIntentFilter());
    }

    private IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(KeyReceiver.PTT_KEY_DOWN);
        intentFilter.addAction(KeyReceiver.PTT_KEY_UP);
        return intentFilter;
    }


}
