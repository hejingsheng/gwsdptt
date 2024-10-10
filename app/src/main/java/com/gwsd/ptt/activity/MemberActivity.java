package com.gwsd.ptt.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.gwsd.bean.GWGroupListBean;
import com.gwsd.bean.GWJoinGroupBean;
import com.gwsd.bean.GWMemberInfoBean;
import com.gwsd.bean.GWRequestSpeakBean;
import com.gwsd.bean.GWSpeakNotifyBean;
import com.gwsd.bean.GWTempGroupBean;
import com.gwsd.bean.GWTempGroupNotifyBean;
import com.gwsd.bean.GWType;
import com.gwsd.ptt.R;
import com.gwsd.ptt.adapter.GroupAdapter;
import com.gwsd.ptt.adapter.MemberAdapter;
import com.gwsd.ptt.manager.GWSDKManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemberActivity extends BaseActivity {

    private static final String TAG = "GW_MemberActivity";

    TextView tVSpeaker;
    Button btnQueryMember;
    Button btnTempGroup;
    Button btnExitTempGroup;
    Button btnSpeak;

    Toolbar toolbar;
    MemberAdapter adapter;

    private RecyclerView recyclerView;

    private boolean speak = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        initData();
        initView();
        initEvent();
    }

    private void initView() {
        tVSpeaker = findViewById(R.id.speaker);
        btnQueryMember = findViewById(R.id.queryMember);
        btnTempGroup = findViewById(R.id.tempGroup);
        btnSpeak = findViewById(R.id.speak);
        btnExitTempGroup = findViewById(R.id.exitTempGroup);

        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.memberRecycleView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
    private void initEvent() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btnQueryMember.setOnClickListener(v -> {
            gwsdkManager.queryMember(gwsdkManager.getUserInfo().getCurrentGroupGid(), gwsdkManager.getUserInfo().getCurrentGroupType());
        });
        toolbar.setNavigationOnClickListener(v -> finish());

        btnTempGroup.setOnClickListener(v -> {
            int uid = adapter.getSelectedUid();
            int[] memberList = new int [] {uid};
            gwsdkManager.tempGroup(memberList,1);
        });
        btnSpeak.setOnClickListener(v -> {
            if (speak) {
                gwsdkManager.stopSpeak();
                btnSpeak.setText("speak");
            } else {
                gwsdkManager.startSpeak();
                btnSpeak.setText("stop");
            }
            speak = !speak;
        });
        btnExitTempGroup.setOnClickListener(v -> {
            int[] ids = new int [] {0};
            gwsdkManager.tempGroup(ids, 1);
        });
    }

    private void initData() {
        adapter = new MemberAdapter();
        gwsdkManager = GWSDKManager.INSTANCE(getApplicationContext());
        gwsdkManager.registerPttObserver(new GWSDKManager.GWSDKPttEngineObserver() {
            @Override
            public void onPttEvent(int event, String data, int var3) {
                if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_QUERY_MEMBER) {
                    runOnUiThread(()->{
                        GWMemberInfoBean gwMemberInfoBean = JSON.parseObject(data, GWMemberInfoBean.class);
                        if (gwMemberInfoBean.getResult() == 0 && gwMemberInfoBean.getMembers().size() > 0) {
                            Map<Integer, String> membersMap = new HashMap<>();
                            for (GWMemberInfoBean.MemberInfo member : gwMemberInfoBean.getMembers()) {
                                membersMap.put(member.getUid(), member.getName());
                            }
                            adapter.setMembers(membersMap);
                        } else {
                            showAlert("not have online users");
                        }
                    });
                } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_TMP_GROUP_ACTIVE) {
                    runOnUiThread(()->{
                        GWTempGroupBean gwTempGroupBean = JSON.parseObject(data, GWTempGroupBean.class);
                        if (gwTempGroupBean.getResult() == 0) {
                            showAlert("invite " +gwTempGroupBean.getUids()+" to temp group");
                        } else if (gwTempGroupBean.getResult() == 1) {
                            showAlert("exit temp group");
                        }
                    });
                } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_TMP_GROUP_PASSIVE){
                    runOnUiThread(()->{
                        GWTempGroupNotifyBean gwTempGroupNotifyBean = JSON.parseObject(data, GWTempGroupNotifyBean.class);
                        if (gwTempGroupNotifyBean.getName() != null) {
                            showAlert("invite temp group by user " + gwTempGroupNotifyBean.getName());
                        } else {
                            showAlert("exit temp group");
                        }
                    });
                } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_SPEAK) {
                    runOnUiThread(()->{
                        GWSpeakNotifyBean gwSpeakNotifyBean = JSON.parseObject(data, GWSpeakNotifyBean.class);
                        if (gwSpeakNotifyBean.getUid() != 0) {
                            tVSpeaker.setText("tmp group have user speak:"+gwSpeakNotifyBean.getUid()+"/"+gwSpeakNotifyBean.getName());
                        } else {
                            tVSpeaker.setText("no user speak");
                        }
                    });
                } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_REQUEST_MIC) {
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

}
