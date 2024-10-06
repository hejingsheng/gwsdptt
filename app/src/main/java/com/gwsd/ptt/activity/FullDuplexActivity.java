package com.gwsd.ptt.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.gwsd.bean.GWCurrentGroupNotifyBean;
import com.gwsd.bean.GWDuplexBean;
import com.gwsd.bean.GWMemberInfoBean;
import com.gwsd.bean.GWRequestSpeakBean;
import com.gwsd.bean.GWSpeakNotifyBean;
import com.gwsd.bean.GWTempGroupBean;
import com.gwsd.bean.GWTempGroupNotifyBean;
import com.gwsd.bean.GWType;
import com.gwsd.ptt.R;
import com.gwsd.ptt.adapter.MemberAdapter;
import com.gwsd.ptt.manager.GWSDKManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FullDuplexActivity extends BaseActivity{
    private static final String TAG = "GW_FullDuplexActivity";

    EditText editRemoteid;
    Button btnCall;
    Button btnAccept;
    Button btnHangup;

    Toolbar toolbar;

    String remoteid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_duplex);

        initData();
        initView();
        initEvent();

        if (!gwsdkManager.hasDuplexCallPermission()) {
            showAlert("this account do not have duplex call permission!!!");
            finish();
        }
    }

    private void initView() {

        editRemoteid = findViewById(R.id.callRemoteId);
        btnCall = findViewById(R.id.btnCall);
        btnAccept = findViewById(R.id.btnAccept);
        btnHangup = findViewById(R.id.btnHangup);

        toolbar = findViewById(R.id.toolbar);

    }
    private void initEvent() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btnCall.setOnClickListener(v ->{
            remoteid = editRemoteid.getText().toString();
            if (TextUtils.isEmpty(remoteid)){
                showAlert("please input user id");
            }else {
                gwsdkManager.fullDuplex(Integer.parseInt(remoteid),GWType.GW_DUPLEX_TYPE.GW_PTT_DUPLEX_START);
            }
        });
        btnAccept.setOnClickListener(v -> {
           gwsdkManager.fullDuplex(Integer.parseInt(remoteid), GWType.GW_DUPLEX_TYPE.GW_PTT_DUPLEX_ACCEPT);
        });
        btnHangup.setOnClickListener(v -> {
            gwsdkManager.fullDuplex(Integer.parseInt(remoteid), GWType.GW_DUPLEX_TYPE.GW_PTT_DUPLEX_HANGUP);
        });

    }

    private void initData() {
        gwsdkManager = GWSDKManager.INSTANCE(this);
        gwsdkManager.registerPttObserver(new GWSDKManager.GWSDKPttEngineObserver() {
            @Override
            public void onPttEvent(int event, String data, int var3) {
                runOnUiThread(()->{
                    if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_DUPLEX) {
                        GWDuplexBean gwDuplexBean = JSON.parseObject(data, GWDuplexBean.class);
                        if (gwDuplexBean.getResult() == 0) {
                            if (gwDuplexBean.getStatus() == GWType.GW_DUPLEX_STATUS.GW_PTT_DUPLEX_STATUS_START) {
                                if (gwDuplexBean.getUid() == 0) {
                                    showToast("call success wait remote accept");
                                } else {
                                    remoteid = String.valueOf(gwDuplexBean.getUid());
                                    showToast("recv user:"+remoteid+" call request");
                                }
                            } else if (gwDuplexBean.getStatus() == GWType.GW_DUPLEX_STATUS.GW_PTT_DUPLEX_STATUS_ACCEPTED) {
                                showToast("call establish!!!");
                            } else if (gwDuplexBean.getStatus() == GWType.GW_DUPLEX_STATUS.GW_PTT_DUPLEX_STATUS_END) {
                                showToast("call end");
                            } else {
                                showToast("call buse:"+gwDuplexBean.getStatus());
                            }
                        } else {
                            showAlert("duplex call error");
                        }
                    }
                });
            }

            @Override
            public void onMsgEvent(int var1, String var2) {

            }
        });
    }

}
