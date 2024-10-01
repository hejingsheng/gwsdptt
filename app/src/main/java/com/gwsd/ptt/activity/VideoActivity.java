package com.gwsd.ptt.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gwsd.GWVideoEngine;
import com.gwsd.ptt.R;
import com.gwsd.ptt.manager.GWSDKManager;
import com.gwsd.rtc.view.GWRtcSurfaceVideoRender;

public class VideoActivity extends BaseActivity {

    private GWSDKManager gwsdkManager;

    GWRtcSurfaceVideoRender gwRtcSurfaceVideoRenderLocal;
    GWRtcSurfaceVideoRender gwRtcSurfaceVideoRenderRemote;

    Button btnCall;
    Button btnPull;
    Button btnAccept;
    Button btnHangup;

    EditText editRemoteId;

    boolean isVideoCall = false;

    public static void startAct(Context context) {
        Intent intent = new Intent(context, VideoActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        initData();
        initView();
        initEvent();
    }

    private void initEvent() {
        btnPull.setOnClickListener(v -> {
            String remoteid = editRemoteId.getText().toString();
            if (remoteid == null || "".equals(remoteid)) {
                showToast("please input remote user id");
            }
            gwsdkManager.pullVideo(remoteid, false, false,
                    GWVideoEngine.GWVideoPriority.GW_VIDEO_PRIORITY_SMOOTH,
                    GWVideoEngine.GWVideoResolution.GW_VIDEO_RESOLUTION_NORMAL);
        });
        btnCall.setOnClickListener(v -> {
            String remoteid = editRemoteId.getText().toString();
            if (remoteid == null || "".equals(remoteid)) {
                showToast("please input remote user id");
            }
            gwsdkManager.callVideo(remoteid, false,
                    GWVideoEngine.GWVideoResolution.GW_VIDEO_RESOLUTION_NORMAL);
        });
        btnAccept.setOnClickListener(v -> {
            if (isVideoCall) {
                gwsdkManager.acceptCallVideo();
            } else {
                gwsdkManager.acceptPullVideo(2, false);
            }
        });
        btnHangup.setOnClickListener(v -> {
            gwsdkManager.hangupVideo();
        });
    }

    private void initView() {
        gwRtcSurfaceVideoRenderLocal = findViewById(R.id.videoviewlocal);
        gwRtcSurfaceVideoRenderRemote = findViewById(R.id.videoviewremote);

        btnPull = findViewById(R.id.videopull);
        btnCall = findViewById(R.id.videocall);
        btnAccept = findViewById(R.id.videoaccept);
        btnHangup = findViewById(R.id.videohangup);
        editRemoteId = findViewById(R.id.videoRemoteId);
    }

    private void initData() {
        gwsdkManager = GWSDKManager.INSTANCE(getApplicationContext());
        int[] a=new int[0];
        int[] b=new int[0];
        gwsdkManager.startMsgService(a, b, 0);
        gwsdkManager.registerVideoObserver(new GWSDKManager.GWSDKVideoEngineObserver() {
            @Override
            public void onVideoPull(String s, String s1, int i, boolean b) {
                isVideoCall = false;
                runOnUiThread(()->{
                    String msg = "recv user "+s1+" video pull request";
                    showToast(msg);
                });
            }

            @Override
            public void onVideoCall(String s, String s1) {
                isVideoCall = true;
                runOnUiThread(()->{
                    String msg = "recv user "+s1+" video call request";
                    showToast(msg);
                });
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
            public void onVideoMeetingUserJoin(long l, String s, String s1, boolean b) {

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
                    gwsdkManager.attachLocalVideoView(gwRtcSurfaceVideoRenderLocal);
                });
            }

            @Override
            public void onRemoteStreamReady(boolean video, long uid) {
                runOnUiThread(()->{
                    if (video) {
                        gwsdkManager.attachRemoteVideoView(gwRtcSurfaceVideoRenderRemote, uid);
                    } else {
                        showToast("remote stream ready not have video");
                    }
                });
            }

            @Override
            public void onRemoteStreamRemove() {
                runOnUiThread(()->{
                    gwsdkManager.clearVideoView(gwRtcSurfaceVideoRenderRemote);
                });
            }

            @Override
            public void onLocalStreamRemove() {
                runOnUiThread(()->{
                    gwsdkManager.attachLocalVideoView(gwRtcSurfaceVideoRenderLocal);
                });
            }

            @Override
            public void onVideoData(byte[] bytes, int i, int i1, int i2, int i3) {

            }

            @Override
            public void onHangup(String remoteid) {
                runOnUiThread(()->{
                    showToast("remote user hangup video:"+remoteid);
                });
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

}
