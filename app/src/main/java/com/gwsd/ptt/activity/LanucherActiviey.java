package com.gwsd.ptt.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gwsd.ptt.R;
import com.gwsd.ptt.service.MainService;

public class LanucherActiviey extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        init();
    }

    private Handler mhandle = new Handler();

    private void init() {
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
                Intent intent=new Intent(LanucherActiviey.this, MainActivity.class);
                LanucherActiviey.this.startActivity(intent);
                finish();
            }
        }, 1000);
    }
}
