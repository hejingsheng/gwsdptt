package com.gwsd.ptt.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.gwsd.bean.GWType;
import com.gwsd.ptt.R;
import com.gwsd.ptt.manager.GWSDKManager;

public class MsgActivity extends BaseActivity {

    RadioButton radioBtnUser;
    RadioButton radioBtnGroup;
    RadioGroup recvTypeRadio;

    RadioButton radioBtnText;
    RadioButton radioBtnPhoto;
    RadioButton radioBtnVoice;
    RadioButton radioBtnVideo;
    RadioGroup msgTypeRadio;

    Button btnSend;

    TextView textRecvView;
    EditText editRemoteid;
    EditText editContentOrUrl;

    int recvType;
    int msgType;

    public static void startAct(Context context) {
        Intent intent = new Intent(context, MsgActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg);

        initData();
        initView();
        initEvent();

        if (!gwsdkManager.hasMsgPermission()) {
            showAlert("this account do not have message permission!!!");
            finish();
        }
    }

    private void initData() {
        gwsdkManager = GWSDKManager.INSTANCE(getApplicationContext());
        gwsdkManager.registerPttObserver(new GWSDKManager.GWSDKPttEngineObserver() {
            @Override
            public void onPttEvent(int var1, String var2, int var3) {

            }

            @Override
            public void onMsgEvent(int status, String data) {
                runOnUiThread(()->{
                    String tmp = "recv msg status:"+status+" data:"+data;
                    textRecvView.setText(tmp);
                });
            }
        });
        int[] a=new int[0];
        int[] b=new int[0];
        gwsdkManager.startMsgService(a, b, 0);
    }

    private void initView() {
        radioBtnUser = findViewById(R.id.radio_btn_user);
        radioBtnGroup = findViewById(R.id.radio_btn_group);
        recvTypeRadio = findViewById(R.id.recvTypeRadio);

        radioBtnText = findViewById(R.id.radio_btn_text);
        radioBtnPhoto = findViewById(R.id.radio_btn_photo);
        radioBtnVoice = findViewById(R.id.radio_btn_voice);
        radioBtnVideo = findViewById(R.id.radio_btn_video);
        msgTypeRadio = findViewById(R.id.msgTypeRadio);

        btnSend = findViewById(R.id.btnSend);

        textRecvView = findViewById(R.id.viewRecvMsg);
        editRemoteid = findViewById(R.id.viewRemoteId);
        editContentOrUrl = findViewById(R.id.editContentView);

        radioBtnUser.setChecked(true);
        recvType = GWType.GW_MSG_RECV_TYPE.GW_PTT_MSG_RECV_TYPE_USER;
        radioBtnText.setChecked(true);
        msgType = GWType.GW_MSG_TYPE.GW_PTT_MSG_TYPE_TEXT;
    }

    private void initEvent() {
        recvTypeRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_btn_user) {
                    recvType = GWType.GW_MSG_RECV_TYPE.GW_PTT_MSG_RECV_TYPE_USER;
                } else if (checkedId == R.id.radio_btn_group) {
                    recvType = GWType.GW_MSG_RECV_TYPE.GW_PTT_MSG_RECV_TYPE_GROUP;
                }
            }
        });

        msgTypeRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_btn_text) {
                    msgType = GWType.GW_MSG_TYPE.GW_PTT_MSG_TYPE_TEXT;
                } else if (checkedId == R.id.radio_btn_photo) {
                    msgType = GWType.GW_MSG_TYPE.GW_PTT_MSG_TYPE_PHOTO;
                } else if (checkedId == R.id.radio_btn_voice) {
                    msgType = GWType.GW_MSG_TYPE.GW_PTT_MSG_TYPE_VOICE;
                } else if (checkedId == R.id.radio_btn_video) {
                    msgType = GWType.GW_MSG_TYPE.GW_PTT_MSG_TYPE_VIDEO;
                }
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMsg();
            }
        });
    }

    private void sendMsg() {
        String remoteid = editRemoteid.getText().toString();
        String content = editContentOrUrl.getText().toString();
        if (TextUtils.isEmpty(remoteid) || TextUtils.isEmpty(content)) {
            showToast("please input id and content");
            return;
        }
        gwsdkManager.sendMsg(recvType, Integer.valueOf(remoteid), msgType, content);
    }

}
