package com.gwsd.ptt.manager;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.gwsd.GWPttApi;
import com.gwsd.GWPttEngine;
import com.gwsd.GWVideoEngine;
import com.gwsd.bean.GWChatGroupDetailBean;
import com.gwsd.bean.GWCurrentGroupNotifyBean;
import com.gwsd.bean.GWDuplexBean;
import com.gwsd.bean.GWGroupListBean;
import com.gwsd.bean.GWGroupOperateBean;
import com.gwsd.bean.GWJoinGroupBean;
import com.gwsd.bean.GWKickoutNotifyBean;
import com.gwsd.bean.GWLoginResultBean;
import com.gwsd.bean.GWMemberInfoBean;
import com.gwsd.bean.GWRequestSpeakBean;
import com.gwsd.bean.GWSpeakNotifyBean;
import com.gwsd.bean.GWTempGroupBean;
import com.gwsd.bean.GWTempGroupNotifyBean;
import com.gwsd.bean.GWType;
import com.gwsd.ptt.bean.GWPttUserInfo;

public class GWSDKManager implements GWPttApi.GWPttObserver, GWVideoEngine.GWVideoEventHandler {

    private final String TAG = "GWSDKManager";

    public interface GWSDKPttEngineObserver {
        void onPttEvent(int var1, String var2, int var3);
        void onMsgEvent(int var1, String var2);
    }

    public interface GWSDKVideoEngineObserver extends GWVideoEngine.GWVideoEventHandler {

    }

    private static GWSDKManager gwsdkManager;
    public static GWSDKManager INSTANCE(Context context) {
        if (gwsdkManager == null) {
            synchronized (GWSDKManager.class) {
                if (gwsdkManager == null) {
                    gwsdkManager = new GWSDKManager(context);
                }
            }
        }
        return gwsdkManager;
    }

    private void log(String message) {
        Log.i(TAG, message);
    }

    private Context context;
    private GWPttEngine gwPttEngine;
    private GWVideoEngine gwVideoEngine;

    private GWSDKPttEngineObserver pttObserver;
    private GWSDKVideoEngineObserver videoObserver;

    private GWPttUserInfo userInfo;

    private GWSDKManager(Context context) {
        this.context = context;
        gwPttEngine = GWPttEngine.INSTANCE(context);
        gwVideoEngine = GWVideoEngine.INSTANCE();
        log("current sdk version:"+gwPttEngine.pttGetVersion());
        gwPttEngine.pttInit(this, this, null);
        gwPttEngine.pttConfigServer(0,"43.250.33.13", 23003);
        gwPttEngine.pttConfigServer(1,"43.250.33.13", 51883);
        gwPttEngine.pttConfigServer(2,"43.250.33.13", 50001);
        gwPttEngine.pttConfigServer(3,"43.250.33.13", 8188);
        userInfo = new GWPttUserInfo();
    }

    public void registerPttObserver(GWSDKPttEngineObserver observer) {
        pttObserver = observer;
    }

    public void registerVideoObserver(GWSDKVideoEngineObserver observer) {
        videoObserver = observer;
    }

    public GWPttUserInfo getUserInfo() {
        return userInfo;
    }

    public void login(String account, String password, String imei, String iccid) {
        gwPttEngine.pttLogin(account, password, imei, iccid);
        userInfo.setAccount(account);
        userInfo.setPassword(password);
        userInfo.setOnline(false);
    }

    public void joinGroup(long gid, int type) {
        gwPttEngine.pttJoinGroup(gid, type);
    }

    public void queryGroup() {
        gwPttEngine.pttQueryGroup();
    }

    @Override
    public void onPttEvent(int event, String data, int data1) {
        log("recv ptt event="+event+" data="+data);
        if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_LOGIN) {
            GWLoginResultBean gwLoginResultBean = JSON.parseObject(data, GWLoginResultBean.class);
            if (gwLoginResultBean.getResult() == 0) {
                log(gwLoginResultBean.getName()+" login success");
                userInfo.setName(gwLoginResultBean.getName());
                userInfo.setId(gwLoginResultBean.getUid());
                userInfo.setOnline(true);
                userInfo.setDefaultGid(gwLoginResultBean.getDefaultGid());
                joinGroup(gwLoginResultBean.getDefaultGid(), 0);
            }
        } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_QUERY_GROUP) {
            GWGroupListBean gwGroupListBean = JSON.parseObject(data, GWGroupListBean.class);
        } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_JOIN_GROUP) {
            GWJoinGroupBean gwJoinGroupBean = JSON.parseObject(data, GWJoinGroupBean.class);
            if (gwJoinGroupBean.getResult() == 0) {
                userInfo.setCurrentGroupGid(gwJoinGroupBean.getGid());
                userInfo.setCurrentGroupPriority(gwJoinGroupBean.getPriority());
            }
        } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_QUERY_MEMBER) {
            GWMemberInfoBean gwMemberInfoBean = JSON.parseObject(data, GWMemberInfoBean.class);
        } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_REQUEST_MIC) {
            GWRequestSpeakBean gwRequestSpeakBean = JSON.parseObject(data, GWRequestSpeakBean.class);
        } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_TMP_GROUP_ACTIVE) {
            GWTempGroupBean gwTempGroupBean = JSON.parseObject(data, GWTempGroupBean.class);
        } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_TMP_GROUP_PASSIVE) {
            GWTempGroupNotifyBean gwTempGroupNotifyBean = JSON.parseObject(data, GWTempGroupNotifyBean.class);
        } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_DUPLEX) {
            GWDuplexBean gwDuplexBean = JSON.parseObject(data, GWDuplexBean.class);
        } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_LOGOUT) {
            userInfo.setOnline(false);
            log("logout");
        } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_KICKOUT) {
            GWKickoutNotifyBean gwKickoutNotifyBean = JSON.parseObject(data, GWKickoutNotifyBean.class);
            userInfo.setOnline(false);
        } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_CURRENT_GROUP) {
            GWCurrentGroupNotifyBean gwCurrentGroupNotifyBean = JSON.parseObject(data, GWCurrentGroupNotifyBean.class);
            if (gwCurrentGroupNotifyBean.getResult() == 0) {
                userInfo.setCurrentGroupGid(gwCurrentGroupNotifyBean.getGid());
                userInfo.setCurrentGroupName(gwCurrentGroupNotifyBean.getName());
                if (gwCurrentGroupNotifyBean.getReason().equals("return")) {
                    userInfo.setCurrentGroupPriority(userInfo.getLastpriority());
                } else {
                    userInfo.setLastpriority(userInfo.getCurrentGroupPriority());
                    userInfo.setCurrentGroupPriority(-1);
                }
            }
        } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_SPEAK) {
            GWSpeakNotifyBean gwSpeakNotifyBean = JSON.parseObject(data, GWSpeakNotifyBean.class);
        } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_PLAY_DATA) {
            log("recv pcm data="+data1);
            return;
        } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_LOSTMIC) {
            Log.d(TAG, "speak too long time stop record");
        } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_GROUP_OPERATE) {
            GWGroupOperateBean gwGroupOperateBean = JSON.parseObject(data, GWGroupOperateBean.class);
        } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_QUERY_DISPATCH) {
            GWMemberInfoBean gwMemberInfoBean = JSON.parseObject(data, GWMemberInfoBean.class);
        } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_FRIEND_OPERATE) {
            GWMemberInfoBean gwMemberInfoBean = JSON.parseObject(data, GWMemberInfoBean.class);
        } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_QUERY_CHAT_GRP) {
            GWGroupListBean gwGroupListBean = JSON.parseObject(data, GWGroupListBean.class);
        } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_CHAT_GRP_DETAIL) {
            GWChatGroupDetailBean gwChatGroupDetailBean = JSON.parseObject(data, GWChatGroupDetailBean.class);
        } else {
            log("error happen");
            userInfo.setOnline(false);
        }
        if (pttObserver != null) {
            pttObserver.onPttEvent(event, data, data1);
        }
    }

    @Override
    public void onMsgEvent(int i, String s) {
        log("recv msg event="+i+" data="+s);
        if (pttObserver != null) {
            pttObserver.onMsgEvent(i, s);
        }
    }

    @Override
    public void onVideoEvent(String s) {

    }

    @Override
    public void onVideoPull(String s, String s1, int i, boolean b) {

    }

    @Override
    public void onVideoCall(String s, String s1) {

    }

    @Override
    public void onVideoMeetingInvite(String s, String s1) {

    }

    @Override
    public void onVideoMeetingCancel() {

    }

    @Override
    public void onVideoMeetingSpeak() {

    }

    @Override
    public void onVideoMeetingMute() {

    }

    @Override
    public void onVideoMeetingUserJoin(long l, String s, String s1, boolean b) {

    }

    @Override
    public void onVideoMeetingSelfJoin() {

    }

    @Override
    public void onVideoMeetingUserLeave(long l) {

    }

    @Override
    public void onVideoMeetingKickout() {

    }

    @Override
    public void onLocalStreamReady() {

    }

    @Override
    public void onRemoteStreamReady(boolean b, long l) {

    }

    @Override
    public void onRemoteStreamRemove() {

    }

    @Override
    public void onLocalStreamRemove() {

    }

    @Override
    public void onVideoData(byte[] bytes, int i, int i1, int i2, int i3) {

    }

    @Override
    public void onHangup(String s) {

    }

    @Override
    public void onError(int i, String s) {

    }
}
