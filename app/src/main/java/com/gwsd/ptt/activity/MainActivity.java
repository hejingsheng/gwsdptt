package com.gwsd.ptt.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.gwsd.bean.GWLoginResultBean;
import com.gwsd.bean.GWType;
import com.gwsd.ptt.R;
import com.gwsd.ptt.manager.GWSDKManager;

public class MainActivity extends BaseActivity {

    private static final String TAG = "GW_MainActivity";
    private static final String VERSION = "V_0.0.1";

    TextView sdkVersion;
    TextView demoVersion;

    EditText eTuserAccount;
    EditText eTuserPassword;

    Button btnLogin;
    Button btnGroup;
    Button btnMember;
    Button btnMsg;
    Button btnFullDuplex;
    Button btnVideo;
    Button btnOther;
    Button btnLoginOut;

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

        sdkVersion = findViewById(R.id.sdkVersion);
        demoVersion = findViewById(R.id.demoVersion);

        eTuserAccount = findViewById(R.id.loginUserName);
        eTuserPassword = findViewById(R.id.loginUserPass);

        btnLogin = findViewById(R.id.btnlogin);
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
            //String account = eTuserAccount.getText().toString();
            //String password = eTuserPassword.getText().toString();
            String account = "gwsd03";
            String password = "111111";
            gwsdkManager.login(account,password,"12345","54321");
        });
        btnGroup.setOnClickListener(v ->{
            Intent intent = new Intent(MainActivity.this, GroupActivity.class);
            startActivity(intent);
        });
        btnMember.setOnClickListener(v->{
            Intent intent = new Intent(MainActivity.this, MemberActivity.class);
            startActivity(intent);
        });
        btnMsg.setOnClickListener(v -> {
            MsgActivity.startAct(this);
        });
        btnVideo.setOnClickListener(v -> {
            VideoActivity.startAct(this);
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
                        });
                    }
                } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_LOGOUT) {
                    GWLoginResultBean gwLoginOutResultBean = JSON.parseObject(data, GWLoginResultBean.class);
                    if (gwLoginOutResultBean.getResult() == 0) {
                        runOnUiThread(() -> {

                        });
                    }
                }
            }
            @Override
            public void onMsgEvent(int var1, String var2) {

            }
        });
    }


}
