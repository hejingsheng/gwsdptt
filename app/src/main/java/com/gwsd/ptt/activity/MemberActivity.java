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
import com.gwsd.bean.GWSpeakNotifyBean;
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

    TextView tVselectMember;
    TextView tVcurrentGroupGid;
    TextView viewSpeaker;
    Button btnQueryMember;
    Button btnTempGroup;
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
        tVcurrentGroupGid = findViewById(R.id.groupId);
        viewSpeaker = findViewById(R.id.speaker);
        btnQueryMember = findViewById(R.id.queryMember);
        btnTempGroup = findViewById(R.id.tempGroup);
        btnSpeak = findViewById(R.id.speak);

        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.memberRecycleView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
    private void initEvent() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        btnQueryMember.setOnClickListener(v -> gwsdkManager.queryMember(gwsdkManager.getUserInfo().getCurrentGroupGid(),0));
        toolbar.setNavigationOnClickListener(v -> finish());
        int[] memberList = new int [] {1,2,3};
        btnTempGroup.setOnClickListener(v -> gwsdkManager.tempGroup(memberList,1));
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
        adapter = new MemberAdapter();
        gwsdkManager = GWSDKManager.INSTANCE(getApplicationContext());
        gwsdkManager.registerPttObserver(new GWSDKManager.GWSDKPttEngineObserver() {
            @Override
            public void onPttEvent(int event, String data, int var3) {
                if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_QUERY_MEMBER) {

                } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_TMP_GROUP_ACTIVE) {

                } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_TMP_GROUP_PASSIVE){

                } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_SPEAK) {
                    runOnUiThread(()->{
                        GWSpeakNotifyBean gwSpeakNotifyBean = JSON.parseObject(data, GWSpeakNotifyBean.class);
                        if (gwSpeakNotifyBean.getUid() != 0) {
                            viewSpeaker.setText("tmp group have user speak:"+gwSpeakNotifyBean.getUid()+"/"+gwSpeakNotifyBean.getName());
                        } else {
                            viewSpeaker.setText("no user speak");
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
