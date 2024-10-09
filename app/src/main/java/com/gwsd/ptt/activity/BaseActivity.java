package com.gwsd.ptt.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gwsd.ptt.manager.GWSDKManager;

public class BaseActivity extends AppCompatActivity {

    protected GWSDKManager gwsdkManager;

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
