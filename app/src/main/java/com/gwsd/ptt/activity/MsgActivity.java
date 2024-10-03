package com.gwsd.ptt.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.alibaba.fastjson.JSON;
import com.gwsd.bean.GWDuplexBean;
import com.gwsd.bean.GWMsgBean;
import com.gwsd.bean.GWMsgNoticeBean;
import com.gwsd.bean.GWType;
import com.gwsd.ptt.R;
import com.gwsd.ptt.manager.GWSDKManager;

public class MsgActivity extends BaseActivity {
    private static final String TAG = "GWMsg_Activity";

    EditText eTreceiveId;
    EditText eTType;
    EditText eTregisterId;

    Button btnSend;
    Button btnPhoto;
    Button btnVoice;
    Button btnVideo;
    Button btnRegister;

    TextView tVsendMsg;
    TextView tVreciveMsg;

    Toolbar toolbar;

    GWSDKManager gwsdkManager;
    private static final int IMAGE_REQUEST_CODE = 100;
    private static final int VIDEO_REQUEST_CODE = 101;
    private static final int AUDIO_REQUEST_CODE = 102;
    int msgType = 1103;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg);

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
        eTreceiveId = findViewById(R.id.receiveId);
        eTType = findViewById(R.id.sendType);

        eTregisterId = findViewById(R.id.registerId);
        btnSend = findViewById(R.id.send);
        btnPhoto = findViewById(R.id.selectPhoto);
        btnVoice = findViewById(R.id.selectVoice);
        btnVideo = findViewById(R.id.btnVideo);

        btnRegister = findViewById(R.id.registerMsg);

        tVsendMsg = findViewById(R.id.sendMsg);
        tVreciveMsg = findViewById(R.id.receivedMsg);
        toolbar = findViewById(R.id.toolbar);

    }

    private void initEvent() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        btnSend.setOnClickListener(v ->{
            int sendId = gwsdkManager.getUserInfo().getId();
            String name = gwsdkManager.getUserInfo().getName();
            int type = Integer.parseInt(eTType.getText().toString());
            String content = tVsendMsg.getText().toString();
            String msg = content.replace("send content:\n", "").trim();
            String var7 = "";
            int receiveId = Integer.parseInt(eTreceiveId.getText().toString());
            gwsdkManager.sendMsg(sendId,name,type,receiveId,msgType,msg,var7,(char)1);
            msgType =1103;
            uri = null;
        });
        btnPhoto.setOnClickListener(v ->{
            selectImage();
        });
        btnVoice.setOnClickListener(v ->{
            selectAudio();
        });
        btnVideo.setOnClickListener(v ->{
            selectVideo();
        });
        btnRegister.setOnClickListener(v->{
        });
                }
    private void initData() {
        gwsdkManager = GWSDKManager.INSTANCE(this);
        gwsdkManager.registerPttObserver(new GWSDKManager.GWSDKPttEngineObserver() {
            @Override
            public void onPttEvent(int event, String data, int var3) {
            }

            @Override
            public void onMsgEvent(int status, String data) {
                Log.d(TAG, "msg status=" + status +  "data=" + data);
                if (status == GWType.GW_MSG_STATUS.GW_MSG_STATUS_ERROR){
                    showAlert("send error");
                }else if (status == GWType.GW_MSG_STATUS.GW_MSG_STATUS_SUCC){
                    GWMsgBean gwMsgBean = JSON.parseObject(data,GWMsgBean.class);
                    if (gwMsgBean.getData().getContent() != null){
                        showAlert("send success");
                        tVsendMsg.setText("send content:\n" + gwMsgBean.getData().getContent());
                    }
                }else if (status == GWType.GW_MSG_STATUS.GW_MSG_STATUS_DATA){
                    GWMsgNoticeBean gwMsgBean = JSON.parseObject(data,GWMsgNoticeBean.class);
                    if (gwMsgBean.getData().getContent() != null){
                        showAlert("send success");
                        tVreciveMsg.setText("receive content:\n" + gwMsgBean.getData().getContent());
                    }
                }
            }
        });

    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_REQUEST_CODE);
    }
    private void selectVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, VIDEO_REQUEST_CODE);
            }

    private void selectAudio() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, AUDIO_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            uri = data.getData();
            switch (requestCode) {
                case IMAGE_REQUEST_CODE:
                    // ¥¶¿ÌÕº∆¨ URI
                    Log.d(TAG, "Selected Image URI: " + uri);
                    msgType = 1104;
                    break;
                case VIDEO_REQUEST_CODE:
                    Log.d(TAG, "Selected Video URI: " + uri);
                    msgType = 1105;
                    break;
                case AUDIO_REQUEST_CODE:
                    Log.d(TAG, "Selected Audio URI: " + uri);
                    msgType = 1106;
                    break;
            }
        }
    }

}
