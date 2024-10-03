package com.gwsd.ptt.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.gwsd.bean.GWCurrentGroupNotifyBean;
import com.gwsd.bean.GWJoinGroupBean;
import com.gwsd.bean.GWMemberInfoBean;
import com.gwsd.bean.GWRequestSpeakBean;
import com.gwsd.bean.GWSpeakNotifyBean;
import com.gwsd.bean.GWTempGroupBean;
import com.gwsd.bean.GWTempGroupNotifyBean;
import com.gwsd.bean.GWType;
import com.gwsd.ptt.R;
import com.gwsd.ptt.adapter.MemberAdapter;
import com.gwsd.ptt.manager.GWSDKManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemberActivity extends BaseActivity {

    private static final String TAG = "GW_MemberActivity";

    TextView tVselectMember;
    TextView tVcurrentGroupName;
    TextView tVSpeaker;
    Button btnQueryMember;
    Button btnTemCall;
    Button btnExitTemCall;
    Button btnPttDown;
    Button btnPttUp;

    Toolbar toolbar;
    MemberAdapter adapter;
    Drawable originalPttDown;
    Drawable originalPttUp;
    Drawable originalTempCall;
    Drawable originalExitTempCall;

    Map<Integer, String> membersMap;
    GWSDKManager gwsdkManager;
    private RecyclerView recyclerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        initData();
        initView();
        initEvent();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart=");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume=");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    private void initView() {
        tVselectMember = findViewById(R.id.memId);
        tVcurrentGroupName = findViewById(R.id.groupName);
        tVSpeaker = findViewById(R.id.speaker);
        btnQueryMember = findViewById(R.id.queryMember);
        btnTemCall = findViewById(R.id.temCall);
        btnTemCall.setClickable(false);
        btnExitTemCall = findViewById(R.id.exitTemCall);
        btnExitTemCall.setClickable(false);
        btnPttDown = findViewById(R.id.pttDown);
        btnPttUp = findViewById(R.id.pttUp);

        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.groupRecycleView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        originalPttDown = btnPttDown.getBackground();
        originalPttUp = btnPttDown.getBackground();
        originalTempCall = btnTemCall.getBackground();
        originalExitTempCall = btnExitTemCall.getBackground();
        btnTemCall.setBackgroundColor(ContextCompat.getColor(MemberActivity.this, R.color.gray));
        btnExitTemCall.setBackgroundColor(ContextCompat.getColor(MemberActivity.this, R.color.gray));
        tVcurrentGroupName.setText(gwsdkManager.getUserInfo().getCurrentGroupName());
    }
    private void initEvent() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        btnQueryMember.setOnClickListener(v -> {
            gwsdkManager.queryMember(1,1);
            btnTemCall.setClickable(true);
            btnExitTemCall.setClickable(true);
            btnTemCall.setBackground(originalTempCall);
            btnExitTemCall.setBackground(originalExitTempCall);
        });
        toolbar.setNavigationOnClickListener(v -> finish());
        btnTemCall.setOnClickListener(v -> {
            int selectedUid = adapter.getSelectedUid();
            int[] memberList = new int[]{selectedUid};
            gwsdkManager.temCall(memberList,1);
            changePttButton(true,true,false,true);
        });
        btnExitTemCall.setOnClickListener(v->{
            int[] exitCall = new int[]{0};
            gwsdkManager.temCall(exitCall,0);
            changePttButton(true,true,true,true);
        });
        btnPttDown.setOnClickListener(v -> {
            gwsdkManager.pttDown();
            changePttButton(false,true,false,false);
            tVSpeaker.setText("tempcall  wait " + adapter.getSelectedName() + "speak");
        });

        btnPttUp.setOnClickListener(v ->{
            gwsdkManager.pttUp();
            changePttButton(true,true,true,false);
            tVSpeaker.setText("tempcall  wait " + adapter.getSelectedName() + "speak");
        });
    }

    private void initData() {
        adapter = new MemberAdapter();
        gwsdkManager = GWSDKManager.INSTANCE(this);
        gwsdkManager.registerPttObserver(new GWSDKManager.GWSDKPttEngineObserver() {
            @Override
            public void onPttEvent(int event, String data, int var3) {
                if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_QUERY_MEMBER) {
                    GWMemberInfoBean gwMemberInfoBean = JSON.parseObject(data, GWMemberInfoBean.class);
                    if (gwMemberInfoBean.getResult() == 0) {
                        List<GWMemberInfoBean.MemberInfo> members = gwMemberInfoBean.getMembers();
                        if (members != null) {
                            membersMap = new HashMap<>();
                            for (GWMemberInfoBean.MemberInfo member : members) {
                                int uid = member.getUid();
                                int gid = member.getGid();
                                String name = member.getName();
                                if (gid != 0) {
                                    membersMap.put(uid, name);
                                }
                            }
                            adapter.setMembers(membersMap);
                        }else{
                            runOnUiThread(() -> {
                                showAlert("There are no other members in the group");
                            });
                        }

                    }
                } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_TMP_GROUP_ACTIVE) {
                    GWTempGroupBean gwTempGroupBean = JSON.parseObject(data, GWTempGroupBean.class);
                    if (gwTempGroupBean.getResult() == 0) {
                        runOnUiThread(() -> {
                            showAlert("join temp group success");
                            tVSpeaker.setText("tempcall  wait " + adapter.getSelectedName() + "speak");
                            tVselectMember.setText(adapter.getSelectedName());
                        });
                    }else if(gwTempGroupBean.getResult() == 1){
                        runOnUiThread(() -> {
                            showAlert("exit temp group success");
                            tVSpeaker.setText("tempcall  wait " + adapter.getSelectedName() + "speak");
                            tVselectMember.setText("");
                        });
                    }else{
                        runOnUiThread(() -> {
                            showAlert("join temp group fail");
                        });
                    }
                } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_TMP_GROUP_PASSIVE){
                    GWTempGroupNotifyBean gwTempGroupNotifyBean = JSON.parseObject(data,GWTempGroupNotifyBean.class);
                    if (gwTempGroupNotifyBean.getResult() == 0){
                        if (gwTempGroupNotifyBean.getUid() == 0){
                            runOnUiThread(() -> {
                                showAlert("exit temp group");
                                tVSpeaker.setText("wait for speak...");
                            });
                        }else{
                            String callMemName = gwTempGroupNotifyBean.getName();
                            runOnUiThread(() -> {
                                showAlert("join temp group");
                                tVSpeaker.setText("tempcall  wait " + callMemName + "speak");
                            });
                        }

                    }
                } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_SPEAK) {
                    GWRequestSpeakBean gwRequestSpeakBean = JSON.parseObject(data,GWRequestSpeakBean.class);
                    if (gwRequestSpeakBean.getResult() == 0){
                        showAlert("temp group call success");
                        changePttButton(false,false,true,false);
                    }
                }else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_REQUEST_MIC){
                        GWSpeakNotifyBean gwSpeakNotifyBean = JSON.parseObject(data, GWSpeakNotifyBean.class);
                    if (gwSpeakNotifyBean.getUid() == 0){
                        runOnUiThread(()->{
                            if (adapter.getSelectedName() != null){
                                tVSpeaker.setText("tempcall  wait " + adapter.getSelectedName() + "speak");
                        } else {
                                tVSpeaker.setText("wait for speak...");
                        }
                            changePttButton(true,true,true,true);
                    });
                    }else{
                        runOnUiThread(()->{
                            tVSpeaker.setText(gwSpeakNotifyBean.getName());
                            changePttButton(false,false,false,false);
                        });
                    }
                }else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_CURRENT_GROUP){
                    GWCurrentGroupNotifyBean gwCurrentGroupNotifyBean = JSON.parseObject(data,GWCurrentGroupNotifyBean.class);
                    if (gwCurrentGroupNotifyBean.getResult() == 0){
                        runOnUiThread(()->{
                           tVcurrentGroupName.setText(gwCurrentGroupNotifyBean.getName());
                        });
                    }
                }
            }

            @Override
            public void onMsgEvent(int var1, String var2) {

            }
        });

    }
    public void changePttButton(boolean pttDown,boolean pttUp,boolean tempCall,boolean exitTempCall){
        btnPttUp.setClickable(pttUp);
        btnPttDown.setClickable(pttDown);
        btnTemCall.setClickable(tempCall);
        btnExitTemCall.setClickable(exitTempCall);
        if (pttDown){
            btnPttDown.setBackground(originalPttDown);
        }else{
            btnPttDown.setBackgroundColor(ContextCompat.getColor(MemberActivity.this, R.color.gray));
        }
        if (pttUp){
            btnPttUp.setBackground(originalPttUp);
        }else{
            btnPttUp.setBackgroundColor(ContextCompat.getColor(MemberActivity.this, R.color.gray));
        }
        if (tempCall){
            btnTemCall.setBackground(originalTempCall);
        }else{
            btnTemCall.setBackgroundColor(ContextCompat.getColor(MemberActivity.this, R.color.gray));
        }
        if (exitTempCall){
            btnExitTemCall.setBackground(originalExitTempCall);
        }else{
            btnExitTemCall.setBackgroundColor(ContextCompat.getColor(MemberActivity.this, R.color.gray));
        }
    }

}
