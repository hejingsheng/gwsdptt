package com.gwsd.ptt.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.gwsd.ptt.MyApp;
import com.gwsd.ptt.manager.GWSDKManager;

public class KeyReceiver extends BroadcastReceiver {

    public static final String PTT_KEY_DOWN = "android.intent.action.side_key.keydown.PTT";
    public static final String PTT_KEY_UP = "android.intent.action.side_key.keyup.PTT";

    private void log(String message){
        Log.d("KeyReceiver", message);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Bundle bundle = intent.getExtras();
        log("recv action="+action);
        if (action.equals(PTT_KEY_DOWN)) {
            if (GWSDKManager.getSdkManager().isOnline()) {
                GWSDKManager.getSdkManager().startSpeak();
            }
        } else if (action.equals(PTT_KEY_UP)) {
            if (GWSDKManager.getSdkManager().isOnline()) {
                GWSDKManager.getSdkManager().stopSpeak();
            }
        } else {
            // you can process other broadcast
        }
    }
}
