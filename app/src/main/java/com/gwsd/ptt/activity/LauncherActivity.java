package com.gwsd.ptt.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gwsd.ptt.R;
import com.gwsd.ptt.manager.AppManager;
import com.gwsd.ptt.service.MainService;

import java.util.Iterator;
import java.util.List;

public class LauncherActivity extends AppCompatActivity {

    private static final String TAG = "GW_LanucherActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        init();
    }

    private Handler mhandle = new Handler();

    private void init() {
        if(checkAndHandleRepeatStartApp()){
            Log.i(TAG, "app have started");
            return;
        }
        Log.i(TAG, "start app request permissions");
        requestPermissions();
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        startMain();
    }

    private void startMain() {
        MainService.startServer(this);

        mhandle.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(LauncherActivity.this, MainActivity.class);
                LauncherActivity.this.startActivity(intent);
                finish();
            }
        }, 1000);
    }

    public boolean appBringToFront(Context context) {
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = activityManager.getRunningTasks(100);
        ActivityManager.RunningTaskInfo taskInfoCur = null;
        Iterator var4 = list.iterator();

        while(var4.hasNext()) {
            ActivityManager.RunningTaskInfo taskInfo = (ActivityManager.RunningTaskInfo)var4.next();
            if (taskInfo.topActivity.getPackageName().equals(context.getPackageName())) {
                taskInfoCur = taskInfo;
                break;
            }
        }

        if (taskInfoCur != null) {
            activityManager.moveTaskToFront(taskInfoCur.id, ActivityManager.MOVE_TASK_WITH_HOME);
            return true;
        } else {
            return false;
        }
    }

    private boolean checkAndHandleRepeatStartApp(){
        if(checkHasRepeatStartApp()){
            AppManager.getInstance().finshAct(android.app.LauncherActivity.class);
            finish();
            appBringToFront(this);
            return true;
        }
        return false;
    }
    private boolean checkHasRepeatStartApp(){
        boolean hasRepeatStartApp=false;
        if(AppManager.getActList().size()>0){
            for (Activity activity : AppManager.getActList()) {
                if( activity != this){
                    hasRepeatStartApp=true;
                }else {
                }
            }
        }
        return hasRepeatStartApp;
    }
}
