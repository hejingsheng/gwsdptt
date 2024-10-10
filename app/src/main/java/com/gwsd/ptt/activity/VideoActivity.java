package com.gwsd.ptt.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.gwsd.GWVideoEngine;
import com.gwsd.bean.GWMemberInfoBean;
import com.gwsd.bean.GWType;
import com.gwsd.ptt.R;
import com.gwsd.ptt.manager.GWSDKManager;
import com.gwsd.rtc.view.GWRtcSurfaceVideoRender;

public class VideoActivity extends BaseActivity {

    GWRtcSurfaceVideoRender gwRtcSurfaceVideoRenderLocal;
    GWRtcSurfaceVideoRender gwRtcSurfaceVideoRenderRemote;

    Button btnCall;
    Button btnPull;
    Button btnAccept;
    Button btnHangup;

    Button btnMuteMic;
    Button btnMuteSpk;
    Button btnSwitchCamera;

    Button btnSelectMember;

    int videoMode = -1;  // 0 video pull  1 video call  2 video meeting
    String remoteid;
    String creater;
    boolean isJoinMeeting = false;

    boolean micMute = false;
    boolean spkMute = false;

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

        if (!gwsdkManager.hasVideoPermission()) {
            showAlert("this account do not have video permission!!!");
            finish();
        }
    }

    private void initEvent() {
        btnSelectMember.setOnClickListener(v -> {
            gwsdkManager.queryMember(gwsdkManager.getUserInfo().getCurrentGroupGid(), gwsdkManager.getUserInfo().getCurrentGroupType());
        });
        btnPull.setOnClickListener(v -> {
            if (TextUtils.isEmpty(remoteid)) {
                showToast("please select remote user");
                return;
            }
            gwsdkManager.pullVideo(remoteid, false, false,
                    GWVideoEngine.GWVideoPriority.GW_VIDEO_PRIORITY_SMOOTH,
                    GWVideoEngine.GWVideoResolution.GW_VIDEO_RESOLUTION_NORMAL);
        });
        btnCall.setOnClickListener(v -> {
            if (TextUtils.isEmpty(remoteid)) {
                showToast("please select remote user");
                return;
            }
            gwsdkManager.callVideo(remoteid, false,
                    GWVideoEngine.GWVideoResolution.GW_VIDEO_RESOLUTION_NORMAL);
        });
        btnAccept.setOnClickListener(v -> {
            if (videoMode == 0) {
                gwsdkManager.acceptPullVideo(2, false);
            } else if (videoMode == 1) {
                gwsdkManager.acceptCallVideo();
            } else if (videoMode == 2) {
                gwsdkManager.joinVideoMeeting();
            } else {
                showAlert("error video mode");
            }
        });
        btnHangup.setOnClickListener(v -> {
            if (videoMode == 2) {
                if (isJoinMeeting) {
                    gwsdkManager.leaveVideoMeeting();
                } else {
                    gwsdkManager.rejectVideoMeeting(creater, "user");
                }
            } else {
                gwsdkManager.hangupVideo(remoteid);
            }
        });
        btnMuteMic.setOnClickListener(v -> {
            gwsdkManager.muteMic(!micMute);
            micMute = !micMute;
        });
        btnMuteSpk.setOnClickListener(v -> {
            gwsdkManager.muteSpk(!spkMute);
            spkMute = !spkMute;
        });
        btnSwitchCamera.setOnClickListener(v -> {
            gwsdkManager.switchCamera();
        });
    }

    private void initView() {
        gwRtcSurfaceVideoRenderLocal = findViewById(R.id.videoviewlocal);
        gwRtcSurfaceVideoRenderRemote = findViewById(R.id.videoviewremote);

        btnPull = findViewById(R.id.videopull);
        btnCall = findViewById(R.id.videocall);
        btnAccept = findViewById(R.id.videoaccept);
        btnHangup = findViewById(R.id.videohangup);

        btnMuteMic = findViewById(R.id.btnMuteMic);
        btnMuteSpk = findViewById(R.id.btnMuteSpk);
        btnSwitchCamera = findViewById(R.id.btnSwitchCamera);

        btnSelectMember = findViewById(R.id.btnVideoRemoteId);
    }

    private void initData() {
        gwsdkManager = GWSDKManager.INSTANCE(getApplicationContext());
        gwsdkManager.registerPttObserver(new GWSDKManager.GWSDKPttEngineObserver() {
            @Override
            public void onPttEvent(int event, String data, int var3) {
                if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_QUERY_MEMBER) {
                    runOnUiThread(()->{
                        GWMemberInfoBean gwMemberInfoBean = JSON.parseObject(data, GWMemberInfoBean.class);
                        if (gwMemberInfoBean.getResult() == 0 && gwMemberInfoBean.getMembers().size() > 0) {
                            GWMemberInfoBean.MemberInfo member = gwMemberInfoBean.getMembers().get(0);
                            showAlert("select member:"+member.getName()+",you can start video call or video pull");
                            remoteid = String.valueOf(member.getUid());
                        } else {
                            showAlert("not have online users");
                        }
                    });
                }
            }

            @Override
            public void onMsgEvent(int var1, String var2) {

            }
        });
        gwsdkManager.registerVideoObserver(new GWSDKManager.GWSDKVideoEngineObserver() {
            @Override
            public void onVideoPull(String s, String s1, int i, boolean b) {
                videoMode = 0;
                remoteid = s;
                runOnUiThread(()->{
                    String msg = "recv user "+s1+" video pull request";
                    showToast(msg);
                });
            }

            @Override
            public void onVideoCall(String s, String s1) {
                videoMode = 1;
                remoteid = s;
                runOnUiThread(()->{
                    String msg = "recv user "+s1+" video call request";
                    showToast(msg);
                });
            }

            @Override
            public void onVideoMeetingInvite(String s, String s1) {
                videoMode = 2;
                remoteid = "";
                runOnUiThread(()->{
                    String msg = "recv user "+s+" create video meeting invite";
                    showToast(msg);
                });
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
                runOnUiThread(()->{
                    String data = name+ "join meeting"+" "+video;
                    showToast(data);
                    if (video) {
                        gwsdkManager.attachRemoteVideoView(gwRtcSurfaceVideoRenderRemote, videoId);
                    }
                });
            }

            @Override
            public void onVideoMeetingSelfJoin() {
                isJoinMeeting = true;
                runOnUiThread(()->{
                    String data = "I join meeting";
                    showToast(data);
                    gwsdkManager.attachLocalVideoView(gwRtcSurfaceVideoRenderLocal);
                });
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
                    gwsdkManager.clearVideoView(gwRtcSurfaceVideoRenderLocal);
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
