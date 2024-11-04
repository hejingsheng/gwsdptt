package com.gwsd.ptt.activity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.gwsd.bean.GWLoginResultBean;
import com.gwsd.bean.GWSpeakNotifyBean;
import com.gwsd.bean.GWType;
import com.gwsd.ptt.MyApp;
import com.gwsd.ptt.R;
import com.gwsd.ptt.manager.AppManager;
import com.gwsd.ptt.manager.GWSDKManager;
import com.gwsd.ptt.service.MainService;

public class MainActivity extends BaseActivity {

    private static final String TAG = "GW_MainActivity";
    private static final String VERSION = "v_0.0.2";

    TextView sdkVersion;
    TextView demoVersion;
    TextView power;
    TextView info;
    TextView speaker;

    EditText eTuserAccount;
    EditText eTuserPassword;

    Button btnLogin;
    Button btnSpeak;
    Button btnGroup;
    Button btnMember;
    Button btnMsg;
    Button btnFullDuplex;
    Button btnVideo;
    Button btnOther;
    Button btnLoginOut;

    boolean speak = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initEvent();
    }

    @Override
    protected void onStart() {
        super.onStart();
        String str = "name:"+gwsdkManager.getUserInfo().getName()+" group:"+gwsdkManager.getUserInfo().getCurrentGroupName();
        info.setText(str);
    }

    private void initView() {

        sdkVersion = findViewById(R.id.sdkVersion);
        demoVersion = findViewById(R.id.demoVersion);
        power = findViewById(R.id.viewPower);
        info = findViewById(R.id.viewCurrentInfo);
        speaker = findViewById(R.id.speaker);

        eTuserAccount = findViewById(R.id.loginUserName);
        eTuserPassword = findViewById(R.id.loginUserPass);

        btnLogin = findViewById(R.id.btnlogin);
        btnSpeak = findViewById(R.id.btnspeak);
        btnGroup = findViewById(R.id.btngroup);
        btnMember = findViewById(R.id.btnmember);
        btnMsg = findViewById(R.id.btnmsg);
        btnFullDuplex = findViewById(R.id.btnFullDuplexCall);
        btnVideo = findViewById(R.id.btnVideo);
        btnOther = findViewById(R.id.btnOther);
        btnLoginOut = findViewById(R.id.btnlogout);

    }
    private void initEvent(){
        sdkVersion.setText("sdkVersion" + gwsdkManager.getVersion());
        demoVersion.setText("demoVersion" + VERSION);
        btnLogin.setOnClickListener(v->{
            String account = eTuserAccount.getText().toString();
            String password = eTuserPassword.getText().toString();
            String imei = "12345"; // you should call android api get device imei
            String iccid = "54321"; // you should call android api get sim card iccid
            gwsdkManager.login(account,password,imei,iccid);
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
        btnGroup.setOnClickListener(v ->{
            Intent intent = new Intent(MainActivity.this, GroupActivity.class);
            startActivity(intent);
        });
        btnMember.setOnClickListener(v->{
            Intent intent = new Intent(MainActivity.this, MemberActivity.class);
            startActivity(intent);
        });
        btnFullDuplex.setOnClickListener(v ->{
            Intent intent = new Intent(MainActivity.this, FullDuplexActivity.class);
            startActivity(intent);
        });
        btnMsg.setOnClickListener(v -> {
            MsgActivity.startAct(this);
        });
        btnVideo.setOnClickListener(v -> {
            VideoActivity.startAct(this);
        });
        btnOther.setOnClickListener(v -> {
            OtherActivity.startAct(this);
        });
		btnLoginOut.setOnClickListener(v -> {
            gwsdkManager.loginOut();
            //MyApp.exitApp();
        });
    }

    private void initData(){
        gwsdkManager = GWSDKManager.INSTANCE(getApplicationContext());
        gwsdkManager.registerPttObserver(new GWSDKManager.GWSDKPttEngineObserver() {
            @Override
            public void onPttEvent(int event, String data, int data1) {
                if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_LOGIN) {
                    GWLoginResultBean gwLoginResultBean = JSON.parseObject(data, GWLoginResultBean.class);
                    if (gwLoginResultBean.getResult() == 0) {
                        runOnUiThread(() -> {
                            showToast("user:"+gwLoginResultBean.getName()+" login success");
                            String powerstr = "msg:"+gwLoginResultBean.isMessage()+" call:"+gwLoginResultBean.isCall()+" video:"+gwLoginResultBean.isVideo()+" silent:"+gwLoginResultBean.isSilent();
                            power.setText(powerstr);
                        });
                    }
                } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_LOGOUT) {
                    GWLoginResultBean gwLoginOutResultBean = JSON.parseObject(data, GWLoginResultBean.class);
                    if (gwLoginOutResultBean.getResult() == 0) {
                        runOnUiThread(() -> {
                            showAlert("user offline");
                        });
                    }
                } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_JOIN_GROUP) {
                    runOnUiThread(()->{
                        String msg = "user "+gwsdkManager.getUserInfo().getName()+" login success\njoin group "+gwsdkManager.getUserInfo().getCurrentGroupName();
                        showAlert(msg);
                        String str = "name:"+gwsdkManager.getUserInfo().getName()+" group:"+gwsdkManager.getUserInfo().getCurrentGroupName();
                        info.setText(str);
                    });
                } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_SPEAK) {
                    runOnUiThread(()->{
                        GWSpeakNotifyBean gwSpeakNotifyBean = JSON.parseObject(data, GWSpeakNotifyBean.class);
                        if (gwSpeakNotifyBean.getUid() != 0) {
                            Log.i(TAG, "have speaker");
                            speaker.setText("speaker id:"+gwSpeakNotifyBean.getUid()+" name:"+gwSpeakNotifyBean.getName());
                            AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                            audioManager.setMode(AudioManager.MODE_NORMAL);
                            audioManager.stopBluetoothSco();
                            audioManager.setBluetoothScoOn(false);
                            audioManager.setSpeakerphoneOn(true);
                        } else {
                            Log.i(TAG, "no speaker");
                            speaker.setText("no speaker");
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
