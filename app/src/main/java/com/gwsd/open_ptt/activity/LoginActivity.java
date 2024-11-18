package com.gwsd.open_ptt.activity;

import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.gwsd.bean.GWLoginResultBean;
import com.gwsd.bean.GWType;
import com.gwsd.open_ptt.R;
import com.gwsd.open_ptt.manager.GWSDKManager;

public class LoginActivity extends BaseActivity {

    EditText viewInputUm;
    EditText viewInputPsw;
    TextView viewLogin;
    TextView viewMore;
    ImageView viewLogo;
    TextView viewUpdateApp;
    TextView viewLoginWelcome;

    @Override
    protected int getViewId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        viewInputUm = findViewById(R.id.view_input_username);
        viewInputPsw = findViewById(R.id.view_input_psw);
        viewLogin = findViewById(R.id.view_login);
        viewMore = findViewById(R.id.view_more);
        viewLogo = findViewById(R.id.view_login_logflag);
        viewUpdateApp = findViewById(R.id.viewUpdateApp);
        viewLoginWelcome = findViewById(R.id.view_login_welcome);
    }

    @Override
    protected void initEvent(){
        viewLogin.setOnClickListener(v->{
//            String account = viewInputUm.getText().toString();
//            String password = viewInputPsw.getText().toString();
            String account = "gwsd03";
            String password = "123456";
            String imei = "12345"; // you should call android api get device imei
            String iccid = "54321"; // you should call android api get sim card iccid
            GWSDKManager.getSdkManager().login(account,password,imei,iccid);
        });
    }

    @Override
    protected void initData(){
        GWSDKManager.getSdkManager().registerPttObserver(new GWSDKManager.GWSDKPttEngineObserver() {
            @Override
            public void onPttEvent(int event, String data, int data1) {
                if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_LOGIN) {
                    GWLoginResultBean gwLoginResultBean = JSON.parseObject(data, GWLoginResultBean.class);
                    if (gwLoginResultBean.getResult() == 0) {
                        runOnUiThread(() -> {
                            MainActivity.startAct(getContext());
                            finish();
                        });
                    } else {
                        runOnUiThread(()->{
                            showToast(getContext().getString(R.string.hint_login_failure));
                        });
                    }
                } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_ERROR) {
                    runOnUiThread(()->{
                        showToast(getContext().getString(R.string.hint_network_err));
                    });
                }
            }
            @Override
            public void onMsgEvent(int var1, String var2) {

            }
        });
    }

    @Override
    protected void release() {
        super.release();
        GWSDKManager.getSdkManager().registerPttObserver(null);
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
