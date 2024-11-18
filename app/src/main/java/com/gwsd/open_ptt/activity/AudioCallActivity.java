package com.gwsd.open_ptt.activity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.gwsd.bean.GWDuplexBean;
import com.gwsd.bean.GWType;
import com.gwsd.open_ptt.R;
import com.gwsd.open_ptt.manager.GWSDKManager;
import com.gwsd.open_ptt.utils.Utils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;


public class AudioCallActivity extends BaseActivity{

    TextView tVCallUser;
    TextView tVTimer;
    ImageView iVMic;
    ImageView iVBullhorn;
    ImageView iVHungUp;
    ImageView iVAccept;

    int remoteid;
    String remoteNm;
    boolean caller;

    public static void startAct(Context context, int remoteid, String remotenm, boolean caller) {
        Intent intent = new Intent(context, AudioCallActivity.class);
        intent.putExtra("remoteid", remoteid);
        intent.putExtra("remotenm", remotenm);
        intent.putExtra("caller", caller);
        context.startActivity(intent);
    }

    @Override
    protected int getViewId() {
        return R.layout.activity_full_dulexing;
    }

    protected void initView(){
        tVCallUser = findViewById(R.id.viewAudioCallNm);
        tVTimer = findViewById(R.id.viewAudioCallTime);
        iVMic = findViewById(R.id.viewAudioMuteLocal);
        iVBullhorn = findViewById(R.id.viewAudioHandFree);
        iVHungUp = findViewById(R.id.viewAudioHangup);
        iVAccept = findViewById(R.id.viewAudioAccept);

        iVMic.setVisibility(View.INVISIBLE);
        iVBullhorn.setVisibility(View.INVISIBLE);
        if (caller) {
            iVAccept.setVisibility(View.GONE);
        }
        startTimer(1000);
        tVCallUser.setText(remoteNm);
    }

    protected void initData(){
        Bundle bundle = getIntent().getExtras();
        remoteid = bundle.getInt("remoteid");
        remoteNm = bundle.getString("remotenm");
        caller = bundle.getBoolean("caller");
        GWSDKManager.getSdkManager().registerPttObserver(new GWSDKManager.GWSDKPttEngineObserver() {
            @Override
            public void onPttEvent(int event, String data, int var3) {
                if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_DUPLEX){
                    GWDuplexBean gwDuplexBean = JSON.parseObject(data, GWDuplexBean.class);
                    if (gwDuplexBean.getResult() == 0){
                        if (gwDuplexBean.getStatus() == GWType.GW_DUPLEX_STATUS.GW_PTT_DUPLEX_STATUS_ACCEPTED){
                            runOnUiThread(()->{
                                iVMic.setVisibility(View.VISIBLE);
                                iVBullhorn.setVisibility(View.VISIBLE);
                                iVAccept.setVisibility(View.GONE);
                            });
                        }else if (gwDuplexBean.getStatus() == GWType.GW_DUPLEX_STATUS.GW_PTT_DUPLEX_STATUS_END){
                            runOnUiThread(()->{
                                log("call end finish");
                                finish();
                            });
                        }else if (gwDuplexBean.getStatus() == GWType.GW_DUPLEX_STATUS.GW_PTT_DUPLEX_STATUS_START) {
                            log("call success");
                        }
                    }
                }
            }

            @Override
            public void onMsgEvent(int var1, String var2) {

            }
        });
        if (caller) {
            GWSDKManager.getSdkManager().fullDuplex(remoteid, GWType.GW_DUPLEX_TYPE.GW_PTT_DUPLEX_ACTION_CREATE);
        } else {
            log("recv user:"+remoteNm+" voice call request");
        }
    }

    protected void initEvent(){
        iVMic.setOnClickListener(v ->{
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (iVMic.isSelected()) {
                audioManager.setMicrophoneMute(false);
                boolean isMicrophoneMuted = audioManager.isMicrophoneMute();
                log("isMicrophoneMuted:" + isMicrophoneMuted);
                iVMic.setSelected(false);
            }else{
                audioManager.setMicrophoneMute(true);//禁音
                boolean isMicrophoneMuted = audioManager.isMicrophoneMute();
                log("isMicrophoneMuted:" + isMicrophoneMuted);
                iVMic.setSelected(true);
            }
        });
        iVBullhorn.setOnClickListener(v->{
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (iVBullhorn.isSelected()) {
                audioManager.setSpeakerphoneOn(false);
                boolean isSpeakerOn = audioManager.isSpeakerphoneOn();
                log("isSpeakerOn:"+ isSpeakerOn);
                iVBullhorn.setSelected(false);
            }else{
                audioManager.setSpeakerphoneOn(true);// 打开扬声器
                boolean isSpeakerOn = audioManager.isSpeakerphoneOn();
                log("isSpeakerOn:"+ isSpeakerOn);
                iVBullhorn.setSelected(true);
            }
        });
        iVHungUp.setOnClickListener(v->{
            GWSDKManager.getSdkManager().fullDuplex(remoteid, GWType.GW_DUPLEX_TYPE.GW_PTT_DUPLEX_ACTION_HANGUP);
        });
        iVAccept.setOnClickListener(v -> {
            GWSDKManager.getSdkManager().fullDuplex(remoteid, GWType.GW_DUPLEX_TYPE.GW_PTT_DUPLEX_ACTION_ACCEPT);
        });
    }

    @Override
    protected void release() {
        super.release();
        stopTimer();
    }

    @Override
    protected void onTimer(int ts) {
        if(tVTimer!=null){
            tVTimer.setText(Utils.intToTimer(ts));
        }
    }

}

