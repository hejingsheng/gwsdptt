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
import com.gwsd.bean.GWLoginResultBean;
import com.gwsd.bean.GWRequestSpeakBean;
import com.gwsd.bean.GWSpeakNotifyBean;
import com.gwsd.bean.GWType;
import com.gwsd.ptt.R;
import com.gwsd.ptt.adapter.GroupAdapter;
import com.gwsd.ptt.manager.GWSDKManager;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupActivity extends BaseActivity {

    private static final String TAG = "GW_GroupActivity";

    TextView tVGroupName;
    TextView tVGroupId;
    TextView tVSpeaker;

    Button btnQueryGroup;
    Button btnJoinGroup;
    Button btnSpeak;

    Toolbar toolbar;

    private RecyclerView recyclerView;

    private GroupAdapter adapter;

    Map<Long, String> groupMap;

    private boolean speak = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

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
        tVGroupName = findViewById(R.id.groupName);
        tVGroupId = findViewById(R.id.groupId);
        tVSpeaker = findViewById(R.id.speaker);
        btnQueryGroup = findViewById(R.id.btnQueryGroup);
        btnJoinGroup = findViewById(R.id.btnJoinGroup);
        btnSpeak = findViewById(R.id.btnSpeak);
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.groupRecycleView);
        //tVGroupId.setText(String.valueOf(gwsdkManager.getUserInfo().getCurrentGroupGid()));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
    private void initEvent() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btnQueryGroup.setOnClickListener(v -> gwsdkManager.queryGroup());
        toolbar.setNavigationOnClickListener(v -> finish());
        btnJoinGroup.setOnClickListener(v -> {
            gwsdkManager.joinGroup(adapter.getSelectedGid(),adapter.getSelectedType());
        });
        btnSpeak.setOnClickListener(v -> {
            if (!speak) {
                gwsdkManager.startSpeak();
            } else {
                gwsdkManager.stopSpeak();
            }
            speak = !speak;
        });
    }

    private void initData() {
        adapter = new GroupAdapter();
        gwsdkManager = GWSDKManager.INSTANCE(getApplicationContext());
        gwsdkManager.registerPttObserver(new GWSDKManager.GWSDKPttEngineObserver() {
            @Override
            public void onPttEvent(int event, String data, int var3) {
                if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_QUERY_GROUP) {
                    GWGroupListBean gwGroupListBean = JSON.parseObject(data, GWGroupListBean.class);

                    if (gwGroupListBean.getResult() == 0) {
                        List<GWGroupListBean.GWGroupBean> groups = gwGroupListBean.getGroups();
                        groupMap = new HashMap<>();

                        for (GWGroupListBean.GWGroupBean group : groups) {
                            groupMap.put(group.getGid(), group.getName());
                        }
                        String groupName = groupMap.get(gwsdkManager.getUserInfo().getCurrentGroupGid());
                        runOnUiThread(() -> {
                            tVGroupName.setText(groupName);
                            adapter.setGroups(groups);

                        });
                    }
                } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_JOIN_GROUP) {
                    runOnUiThread(() -> {
                        GWJoinGroupBean gwJoinGroupBean = JSON.parseObject(data, GWJoinGroupBean.class);
                        if (gwJoinGroupBean.getResult() == 0) {
                            showAlert("join group success");
                            tVGroupName.setText(groupMap.get(gwsdkManager.getUserInfo().getCurrentGroupGid()));
                            tVGroupId.setText(String.valueOf(gwsdkManager.getUserInfo().getCurrentGroupGid()));
                        } else {
                            showAlert("join group fail");
                        }
                    });
                } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_SPEAK) {
                    runOnUiThread(()->{
                        GWSpeakNotifyBean gwSpeakNotifyBean = JSON.parseObject(data, GWSpeakNotifyBean.class);
                        if (gwSpeakNotifyBean.getUid() != 0) {
                            tVSpeaker.setText("speaker id:"+gwSpeakNotifyBean.getUid()+" name:"+gwSpeakNotifyBean.getName());
                        } else {
                            tVSpeaker.setText("no speaker");
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