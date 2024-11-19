package com.gwsd.open_ptt.activity;

import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.gwsd.bean.GWJoinGroupBean;
import com.gwsd.bean.GWRequestSpeakBean;
import com.gwsd.bean.GWSpeakNotifyBean;
import com.gwsd.bean.GWType;
import com.gwsd.open_ptt.R;
import com.gwsd.open_ptt.manager.GWSDKManager;
import com.gwsd.open_ptt.view.AppTopView;
import com.gwsd.open_ptt.view.SpeakerVoiceDBAnimView;
import com.gwsd.open_ptt.view.VoiceSendingView;

public class PttCallActivity extends BaseActivity implements View.OnTouchListener{

    AppTopView aTHalfDuplex;

    TextView tVSpeaker;

    SpeakerVoiceDBAnimView viewSpeakerAnim;
    TextView tVgid;
    TextView tVgName;
    VoiceSendingView viewVoiceSendingView;

    private long gid;
    private String gname;
    private int gtype;

    public static void startAct(Context context,long gid,String name, int type) {
        Intent intent = new Intent(context, PttCallActivity.class);
        intent.putExtra("pttGroup", gid);
        intent.putExtra("pttGroupName",name);
        intent.putExtra("pttGroupType", type);
        context.startActivity(intent);
    }


    @Override
    protected int getViewId() {
        return R.layout.activity_pttcall;
    }

    @Override
    protected void initData() {
        gid = getIntent().getLongExtra("pttGroup", -1);
        gname = getIntent().getStringExtra("pttGroupName");
        gtype = getIntent().getIntExtra("pttGroupType", 0);
        if (gtype == GWType.GW_MSG_RECV_TYPE.GW_PTT_MSG_RECV_TYPE_GROUP
                || gtype == GWType.GW_MSG_RECV_TYPE.GW_PTT_MSG_RECV_TYPE_SELFGROUP) {
            log("ptt call group");
        } else if (gtype == GWType.GW_MSG_RECV_TYPE.GW_PTT_MSG_RECV_TYPE_USER) {
            log("ptt call user");
        }
        GWSDKManager.getSdkManager().registerPttObserver(new GWSDKManager.GWSDKPttEngineObserver() {
            @Override
            public void onPttEvent(int event, String data, int data1) {
                if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_JOIN_GROUP){
                    runOnUiThread(() -> {
                        GWJoinGroupBean gwJoinGroupBean = JSON.parseObject(data, GWJoinGroupBean.class);
                        if (gwJoinGroupBean.getResult() == 0) {
                           log("jonin group success");
                        } else {
                            showToast("join group fail");
                            finish();
                        }
                    });
                }else if(event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_SPEAK){
                    runOnUiThread(()->{
                        GWSpeakNotifyBean gwSpeakNotifyBean = JSON.parseObject(data, GWSpeakNotifyBean.class);
                        if (gwSpeakNotifyBean.getUid() != 0) {
                            tVSpeaker.setText(gwSpeakNotifyBean.getName());
                            viewSpeakerAnim.startSpeakerAnim();
                        } else {
                            tVSpeaker.setText("");
                            viewSpeakerAnim.stopSpeakerAnim();
                        }
                    });
                }else if(event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_REQUEST_MIC){
                    runOnUiThread(()->{
                        GWRequestSpeakBean gwRequestSpeakBean = JSON.parseObject(data, GWRequestSpeakBean.class);
                        if (gwRequestSpeakBean.getResult() != 0) {
                            log("request speak fail");
                        }
                    });
                }
            }

            @Override
            public void onMsgEvent(int var1, String var2) {

            }
        });
    }

    @Override
    protected void initView() {
        aTHalfDuplex = findViewById(R.id.viewHalfDuplexTopView);
        tVSpeaker = findViewById(R.id.speaker);
        viewSpeakerAnim = findViewById(R.id.viewSpeakerAnim);
        aTHalfDuplex.setTopTitle(R.string.group_voice_intercom);
        tVSpeaker.setText("");
        tVgid = findViewById(R.id.groupId);
        tVgName = findViewById(R.id.groupName);
        tVgName.setText(gname);
        tVgid.setText(String.valueOf(gid));
        viewVoiceSendingView = findViewById(R.id.view_VoiceSendingView);
        viewVoiceSendingView.cancalText();
    }

    @Override
    protected void initEvent() {
        GWSDKManager.getSdkManager().joinGroup(gid,gtype);
        viewVoiceSendingView.setOnTouchListener(this);
        aTHalfDuplex.setLeftClick(v ->{
            finish();
        });

    }
            @Override
            public boolean onTouch(View v, MotionEvent event) {
        touchHandler(event.getAction());
        return true;
    }
    private void touchHandler(int action) {
        if (action == MotionEvent.ACTION_DOWN) {
                        GWSDKManager.getSdkManager().startSpeak();
            runOnUiThread(()->{
                tVSpeaker.setText(getString(R.string.local_equipment_speak));
                viewSpeakerAnim.startSpeakerAnim();
                viewVoiceSendingView.showRecording();
            });
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
            viewSpeakerAnim.stopSpeakerAnim();
                        GWSDKManager.getSdkManager().stopSpeak();
            runOnUiThread(()->{
                tVSpeaker.setText("");
                viewSpeakerAnim.stopSpeakerAnim();
                viewVoiceSendingView.showCancel();
            });
            }

    }

}
