package com.gwsd.ptt;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.gwsd.bean.GWJoinGroupBean;
import com.gwsd.bean.GWLoginResultBean;
import com.gwsd.bean.GWType;
import com.gwsd.ptt.manager.GWSDKManager;

public class MainActivity extends AppCompatActivity {

    private GWSDKManager gwsdkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();

    }

    private void initData() {
        gwsdkManager = GWSDKManager.INSTANCE(this);
        gwsdkManager.registerPttObserver(new GWSDKManager.GWSDKPttEngineObserver() {
            @Override
            public void onPttEvent(int event, String data, int data1) {
                if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_LOGIN) {
                    GWLoginResultBean gwLoginResultBean = JSON.parseObject(data, GWLoginResultBean.class);
                    if (gwLoginResultBean.getResult() == 0) {
                        //Log.d(TAG, ": ");
                        runOnUiThread(()->{

                        });
                    }
                } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_JOIN_GROUP) {
                    GWJoinGroupBean gwJoinGroupBean = JSON.parseObject(data, GWJoinGroupBean.class);
                    if (gwJoinGroupBean.getResult() == 0) {
                        //
                    }
                }
            }

            @Override
            public void onMsgEvent(int var1, String var2) {

            }
        });

    }

}
