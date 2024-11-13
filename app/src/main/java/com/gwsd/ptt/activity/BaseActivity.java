package com.gwsd.ptt.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gwsd.ptt.R;
import com.gwsd.ptt.manager.GWSDKManager;

public abstract class BaseActivity extends AppCompatActivity {

    protected GWSDKManager gwsdkManager;

    private static final String TAG = "GWAPP";

    protected void log(String msg) {
        Log.i(TAG, msg);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Gw_Phone);
        setContentView(getViewId());

        initData();
        initView();
        initEvent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        release();
    }

    protected Context getContext() {
        return this;
    }

    protected abstract int getViewId();

    protected abstract void initData();

    protected abstract void initView();

    protected abstract void initEvent();

    protected void release() {

    }

    protected void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    protected void showAlert(String message) {
        new AlertDialog.Builder(this)
                .setTitle("pointer")
                .setMessage(message)
                .setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // 关闭对话框
                    }
                })
                .setNegativeButton("cancel", null)
                .show();
    }

}
