package com.gwsd.ptt.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

    EditText eTSelectMember;
    TextView tVSpeaker;
    Button btnFullCall;

    Toolbar toolbar;

    GWSDKManager gwsdkManager;

    String selectMemId;
    int receiveMemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_duplex);

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

        eTSelectMember = findViewById(R.id.selectMem);
        tVSpeaker = findViewById(R.id.speaker);
        btnFullCall = findViewById(R.id.fullCall);

        toolbar = findViewById(R.id.toolbar);

    }
    private void initEvent() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        btnFullCall.setOnClickListener(v ->{
            selectMemId = eTSelectMember.getText().toString();
            eTSelectMember.setText(selectMemId);
            if (selectMemId.isEmpty()){
                showAlert("please input uid");
            }else {
                gwsdkManager.fullDuplex(Integer.parseInt(selectMemId),GWType.GW_DUPLEX_TYPE.GW_PTT_DUPLEX_START);
            }

        });

    }

    private void initData() {
        gwsdkManager = GWSDKManager.INSTANCE(this);
        gwsdkManager.registerPttObserver(new GWSDKManager.GWSDKPttEngineObserver() {
            @Override
            public void onPttEvent(int event, String data, int var3) {

                if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_DUPLEX) {
                    GWDuplexBean gwDuplexBean = JSON.parseObject(data, GWDuplexBean.class);
                    if (gwDuplexBean.getResult() == 0) {
                        if (gwDuplexBean.getStatus() == GWType.GW_DUPLEX_STATUS.GW_PTT_DUPLEX_STATUS_ACCEPTED)
                        {
                            receiveMemId = gwDuplexBean.getUid();
                            showFullDuplexCallAlert("FullDuplex call coming");
                            runOnUiThread(() -> {
                                eTSelectMember.setText(receiveMemId);
                                tVSpeaker.setText("join FullDuplex Call");
                            });
                        }else if (gwDuplexBean.getStatus() == GWType.GW_DUPLEX_STATUS.GW_PTT_DUPLEX_STATUS_END){
                            receiveMemId = gwDuplexBean.getUid();
                            runOnUiThread(() -> {
                                eTSelectMember.setText(receiveMemId);
                                tVSpeaker.setText("wait for speak...");
                                eTSelectMember.setText("");
                            });
                        }else if (gwDuplexBean.getStatus() == GWType.GW_DUPLEX_STATUS.GW_PTT_DUPLEX_STATUS_START){
                            showAlert("FullDuplex success");
                        }

                    }else{
                        if (gwDuplexBean.getStatus() == GWType.GW_DUPLEX_STATUS.GW_PTT_DUPLEX_STATUS_BUSY){
                            runOnUiThread(() -> {
                                showAlert("FuDuplex call failed.The other party is currently making a call");
                            });
                        }else if (gwDuplexBean.getStatus() == GWType.GW_DUPLEX_STATUS.GW_PTT_DUPLEX_STATUS_OTHRE_INVITE) {
                            runOnUiThread(() -> {
                                showAlert("FuDuplex call failed.The other party is inviting");
                            });
                        }

                    }
                }
            }

            @Override
            public void onMsgEvent(int var1, String var2) {

            }
        });

    }

    public void showFullDuplexCallAlert(String message) {
        new AlertDialog.Builder(this)
                .setTitle("pointer")
                .setMessage(message)
                .setPositiveButton("Answer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gwsdkManager.fullDuplex(gwsdkManager.getUserInfo().getId(),GWType.GW_DUPLEX_TYPE.GW_PTT_DUPLEX_ACCEPT);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Reject", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gwsdkManager.fullDuplex(receiveMemId,GWType.GW_DUPLEX_TYPE.GW_PTT_DUPLEX_HANGUP);
                        dialog.dismiss();
                    }

                })
                .show();
    }

}
