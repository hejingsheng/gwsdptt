package com.gwsd.ptt;

import android.app.Application;

import com.gwsd.ptt.manager.AppManager;
import com.gwsd.ptt.service.MainService;

public class MyApp extends Application {

    static MyApp myApp;

    @Override
    public void onCreate() {
        super.onCreate();
        myApp = this;
        init();
    }

    private void init() {
        AppManager.getInstance().init(this, true);
    }

    public static void exitApp(){
        MainService.stopServer(myApp);
        int myPid = android.os.Process.myPid();
        AppManager.getInstance().exit();
        android.os.Process.killProcess(myPid);
        System.exit(0);
        System.gc();
    }

}
