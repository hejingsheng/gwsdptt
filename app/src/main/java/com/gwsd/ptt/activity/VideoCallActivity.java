package com.gwsd.ptt.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.gwsd.GWVideoEngine;
import com.gwsd.ptt.R;
import com.gwsd.ptt.bean.VideoStateParam;
import com.gwsd.ptt.manager.GWSDKManager;
import com.gwsd.ptt.utils.Utils;
import com.gwsd.ptt.utils.VideoWinSwitchUtil;
import com.gwsd.ptt.view.ChatVideoContentView;
import com.gwsd.ptt.view.ChatVideoViewContracts;
import com.gwsd.rtc.view.GWRtcSurfaceVideoRender;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class VideoCallActivity extends BaseActivity implements ChatVideoViewContracts.OnVideoBtnCallback {

    protected RelativeLayout viewSurfaceGroup;
    protected GWRtcSurfaceVideoRender viewRenderLocal;
    protected GWRtcSurfaceVideoRender viewRenderRemote;
    protected FrameLayout viewFrameLayout;
    ChatVideoContentView videoContentView=null;
    VideoWinSwitchUtil videoWinSwitchUtil;

    private String remoteid;
    private String remoteNm;
    private boolean caller;
    private boolean record;
    private VideoStateParam videoStateParam;
    Disposable disposable;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                acceptVideo();
            } else {
                log("exit activity");
                //normal confirm
                finish();
            }
        }
    };

    public static void startAct(Context context, String remoteid, String remotenm, boolean caller, boolean record) {
        Intent intent = new Intent(context, VideoCallActivity.class);
        intent.putExtra("remoteid", remoteid);
        intent.putExtra("remotenm", remotenm);
        intent.putExtra("caller", caller);
        intent.putExtra("record", record);
        context.startActivity(intent);
    }

    @Override
    protected int getViewId() {
        return R.layout.activity_video;
    }

    @Override
    protected void initEvent() {
        videoContentView.setOnVideoBtnClick(this);
    }

    @Override
    protected void initView() {
        viewSurfaceGroup = findViewById(R.id.viewSurfaceGroup);
        viewRenderLocal = findViewById(R.id.viewRenderLocal);
        viewRenderRemote = findViewById(R.id.viewRenderRemote);
        viewFrameLayout = findViewById(R.id.viewFrameLayout);

        videoContentView=new ChatVideoContentView(getContext());
        viewFrameLayout.addView(videoContentView);
        videoWinSwitchUtil = new VideoWinSwitchUtil(this);
        videoWinSwitchUtil.setVideoView(viewSurfaceGroup, viewRenderRemote, viewRenderLocal);
        videoWinSwitchUtil.addClickListener();
        videoWinSwitchUtil.changeLocalSmall();
        if (caller) {
            GWSDKManager.getSdkManager().callVideo(remoteid, record, GWVideoEngine.GWVideoResolution.GW_VIDEO_RESOLUTION_SMOOTH);
        } else {
            log("recv user "+remoteNm+" video call request");
        }
        videoContentView.setUpdateVideoVState(videoStateParam);
        startTimer();
    }

    @Override
    protected void initData() {
        Bundle bundle = getIntent().getExtras();
        remoteid = bundle.getString("remoteid");
        remoteNm = bundle.getString("remotenm");
        caller = bundle.getBoolean("caller");
        record = bundle.getBoolean("record");
        videoStateParam = new VideoStateParam();
        videoStateParam.setRemoteName(remoteNm)
                .setRemoteUID(remoteid)
                .setRecord(record)
                .setDuplex(true);
        if (caller) {
            videoStateParam.setVideoStatus(ChatVideoViewContracts.VIDEO_View_send_Call);
        } else {
            videoStateParam.setVideoStatus(ChatVideoViewContracts.VIDEO_View_Receive_OnCall);
        }
        GWSDKManager.getSdkManager().registerVideoObserver(new GWSDKManager.GWSDKVideoEngineObserver() {
            @Override
            public void onVideoPull(String s, String s1, int i, boolean b) {

            }

            @Override
            public void onVideoCall(String s, String s1) {

            }

            @Override
            public void onVideoMeetingInvite(String s, String s1) {

            }

            @Override
            public void onVideoMeetingCancel() {

            }

            @Override
            public void onVideoMeetingSpeak() {

            }

            @Override
            public void onVideoMeetingMute() {

            }

            @Override
            public void onVideoMeetingUserJoin(long videoId, String id, String name, boolean video) {

            }

            @Override
            public void onVideoMeetingSelfJoin() {

            }

            @Override
            public void onVideoMeetingUserLeave(long l) {

            }

            @Override
            public void onVideoMeetingKickout() {

            }

            @Override
            public void onLocalStreamReady() {
                runOnUiThread(()->{
                    GWSDKManager.getSdkManager().attachLocalVideoView(viewRenderLocal);
                });
            }

            @Override
            public void onRemoteStreamReady(boolean video, long uid) {
                runOnUiThread(()->{
                    if (video) {
                        videoStateParam.setVideoStatus(ChatVideoViewContracts.VIDEO_View_send_Accept);
                        videoContentView.setUpdateVideoVState(videoStateParam);
                        GWSDKManager.getSdkManager().attachRemoteVideoView(viewRenderRemote, uid);
                    } else {
                        //showToast("remote stream ready not have video");
                    }
                });
            }

            @Override
            public void onRemoteStreamRemove() {
                runOnUiThread(()->{
                    GWSDKManager.getSdkManager().clearVideoView(viewRenderRemote);
                });
            }

            @Override
            public void onLocalStreamRemove() {
                runOnUiThread(()->{
                    GWSDKManager.getSdkManager().clearVideoView(viewRenderLocal);
                });
            }

            @Override
            public void onVideoData(byte[] bytes, int i, int i1, int i2, int i3) {

            }

            @Override
            public void onHangup(String remoteid) {
                runOnUiThread(()->{
                    receiveVideoHangup();
                });
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    private void readyExit() {
        // delay some time
        handler.sendEmptyMessageDelayed(0, 2000);
    }

    private void acceptVideo() {
        GWSDKManager.getSdkManager().acceptCallVideo();
    }

    private void sendVideoHangup() {
        GWSDKManager.getSdkManager().hangupVideo(remoteid);
        stopTimer();
        videoContentView.setUpdateVideoVState(new VideoStateParam().setVideoStatus(ChatVideoViewContracts.VIDEO_View_send_Hangup));
        readyExit();
    }

    private void receiveVideoHangup() {
        stopTimer();
        videoContentView.setUpdateVideoVState(new VideoStateParam().setVideoStatus(ChatVideoViewContracts.VIDEO_View_Receive_Hangup));
        readyExit();
    }

    @Override
    protected void release() {
        super.release();
        videoContentView = null;
        disposable = null;
        videoStateParam = null;
        GWSDKManager.getSdkManager().registerVideoObserver(null);
    }

    @Override
    public void onVideoBtnAccept() {
        handler.sendEmptyMessageDelayed(1, 300);
    }

    @Override
    public void onVideoBtnHangup() {
        sendVideoHangup();
    }

    @Override
    public void onChangeCamera() {
        GWSDKManager.getSdkManager().switchCamera();
    }

    @Override
    public void onMute(boolean mute, boolean local) {
        if (local){
            if (mute) {//1、down, open，0、up,close
                GWSDKManager.getSdkManager().muteMic(true);
            } else {
                GWSDKManager.getSdkManager().muteMic(false);
            }
        }else {
            if (mute) {//1、down, open，0、up,close
                GWSDKManager.getSdkManager().muteSpk(true);
            } else {
                GWSDKManager.getSdkManager().muteSpk(false);
            }
        }
    }

    private void startTimer() {
        log("==startTimer==");
        disposable = Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if(videoContentView!=null){
                        videoContentView.setUpdateVideoVTime(Utils.intToTimer(aLong.intValue()));
                    }
                });
    }

    private void stopTimer() {
        if (disposable != null && !disposable.isDisposed()) {
            log("==stopTimer=dispose");
            disposable.dispose();
            disposable = null;
        }
    }
}
