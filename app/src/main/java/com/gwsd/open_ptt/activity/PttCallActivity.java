package com.gwsd.open_ptt.activity;

import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.gwsd.bean.GWJoinGroupBean;
import com.gwsd.bean.GWRequestSpeakBean;
import com.gwsd.bean.GWSpeakNotifyBean;
import com.gwsd.bean.GWType;
import com.gwsd.open_ptt.R;
import com.gwsd.open_ptt.manager.GWSDKManager;
import com.gwsd.open_ptt.view.AppTopView;

public class PttCallActivity extends BaseActivity{

    AppTopView aTHalfDuplex;

    TextView tVSpeaker;

    ImageView iVPtt;

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
        return R.layout.activity_half_duplex;
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
                            showAlert("join group success");
                        } else {
                            showAlert("join group fail");
                        }
                    });
                }else if(event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_SPEAK){
                    runOnUiThread(()->{
                        GWSpeakNotifyBean gwSpeakNotifyBean = JSON.parseObject(data, GWSpeakNotifyBean.class);
                        if (gwSpeakNotifyBean.getUid() != 0) {
                            tVSpeaker.setText("speaker id:"+gwSpeakNotifyBean.getUid()+" name:"+gwSpeakNotifyBean.getName());
                        } else {
                            tVSpeaker.setText("wait for speak...");
                        }
                    });
                }else if(event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_REQUEST_MIC){
                    runOnUiThread(()->{
                        GWRequestSpeakBean gwRequestSpeakBean = JSON.parseObject(data, GWRequestSpeakBean.class);
                        if (gwRequestSpeakBean.getResult() != 0) {
                            showAlert("request speak fail");
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
        iVPtt = findViewById(R.id.ptt);
        aTHalfDuplex.setTopTitle(gname);
        tVSpeaker.setText("wait for speak...");
    }

    @Override
    protected void initEvent() {
        GWSDKManager.getSdkManager().joinGroup(gid,gtype);
        aTHalfDuplex.setLeftClick(v ->{
            finish();
        });
        iVPtt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        GWSDKManager.getSdkManager().startSpeak();
                        iVPtt.setPressed(true);
                        return true;
                    case MotionEvent.ACTION_UP:
                        iVPtt.setPressed(false);
                        GWSDKManager.getSdkManager().stopSpeak();
                        return true;
                    default:
                        return false;
                }
            }
        });

    }

}
