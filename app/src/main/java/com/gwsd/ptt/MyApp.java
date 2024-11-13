package com.gwsd.ptt;

import android.app.Application;

import com.gwsd.ptt.dao.DBManager;
import com.gwsd.ptt.dao.greendao.DaoSession;
import com.gwsd.ptt.manager.AppManager;
import com.gwsd.ptt.manager.GWSDKManager;
import com.gwsd.ptt.service.MainService;

public class MyApp extends Application {

    static MyApp myApp;
    public static MyApp getInstance(){
        return myApp;
    }

    DaoSession daoSession;
    @Override
    public void onCreate() {
        super.onCreate();
        myApp = this;
        init();
    }

    private void init() {
        AppManager.getInstance().init(this, true);
        this.daoSession = DBManager.initDB(this);
        GWSDKManager.INSTANCE(this);
    }

    public DaoSession getDaoSession() {
        return daoSession;
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
