package com.gwsd.ptt.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gwsd.bean.GWMsgBean;
import com.gwsd.ptt.MyApp;
import com.gwsd.ptt.bean.FileSendParam;
import com.gwsd.ptt.bean.FileUploadResBean;
import com.gwsd.ptt.dao.MsgDaoHelp;
import com.gwsd.ptt.dao.pojo.MsgContentPojo;
import com.gwsd.ptt.dao.pojo.MsgConversationPojo;
import com.gwsd.ptt.manager.GWSDKManager;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FileSendService extends Service {

    private void log(String msg) {
        Log.i(MyApp.TAG, this.getClass().getSimpleName()+"="+msg);
    }

    public static void startFileSend(Context context, FileSendParam fileSendParam){
        Intent intent=new Intent(context,FileSendService.class);
        intent.putExtra("file",fileSendParam);
        context.startService(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getExtras() != null) {
            log("request upload file");
            Bundle bundle = intent.getExtras();
            FileSendParam fileSendParam = (FileSendParam) bundle.getSerializable("file");
            log(fileSendParam.getGwMsgBean().toString());
            processFileSend(fileSendParam);
        }
        return START_STICKY;
    }

//    public final static String ACTION = "com.gwsd.ptt.action.msg.uploadfile";
//    public final static String PARAM_KEY = "msg";
//    private void sendFileMsgBroadcast(GWMsgBean msg){
//        Intent intent=new Intent();
//        intent.setAction(ACTION);
//        intent.putExtra(PARAM_KEY, msg);
//        sendBroadcast(intent);
//    }

    private String getUid() {
        String uid = String.valueOf(GWSDKManager.getSdkManager().getUserInfo().getId());
        return uid;
    }

    private void processFileSend(FileSendParam fileSendParam) {
        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType mediaType;
        File mainfile = new File(fileSendParam.getFilepath());
        File thumbfile = null;
        if (fileSendParam.getFiletype() == FileSendParam.VIDEO_FILE_TYPE) {
            mediaType = MediaType.parse("video/mp4");
            thumbfile = new File(fileSendParam.getFilepathThumb());
        } else if (fileSendParam.getFiletype() == FileSendParam.VOICE_FILE_TYPE) {
            mediaType = MediaType.parse("audio/mp3");
        } else if (fileSendParam.getFiletype() == FileSendParam.PHOTO_FILE_TYPE) {
            mediaType = MediaType.parse("image/jpg");
        } else {
            mediaType = MediaType.parse("image/jpg");
        }
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .addFormDataPart("file1", mainfile.getName(), RequestBody.create(mediaType, mainfile));
        builder.setType(MultipartBody.FORM);
        if (thumbfile != null) {
            builder.addFormDataPart("file2", thumbfile.getName(), RequestBody.create(MediaType.parse("image/jpg"), thumbfile));
        }
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url("http://123.249.38.46:5001/app/chat/msg/upload2")
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();
                log("data="+data);
                FileUploadResBean fileUploadResBean = JSON.parseObject(data, FileUploadResBean.class);
                if (fileUploadResBean.getStatus() == 200) {
                    String fileurl = fileUploadResBean.getData().getFile1().getUrl();
                    GWMsgBean msg = fileSendParam.getGwMsgBean();
                    msg.getData().setUrl(fileurl);
                    if (fileUploadResBean.getData().getFile2() != null) {
                        String thumburl = fileUploadResBean.getData().getFile2().getUrl();
                        msg.getData().setThumbUrl(thumburl);
                    }
                    log(msg.toString());
                    GWSDKManager.getSdkManager().sendMsg(msg);
                    // use local path insert to database
                    msg.getData().setUrl(fileSendParam.getFilepath());
                    msg.getData().setThumbUrl(fileSendParam.getFilepathThumb());
                    MsgContentPojo msgContentPojo = MsgDaoHelp.saveMsgContent(getUid(), msg);
                    MsgConversationPojo msgConversationPojo = MsgDaoHelp.saveOrUpdateConv(msgContentPojo);
                    EventBus.getDefault().post(msgContentPojo);
                    EventBus.getDefault().post(msgConversationPojo);
                }
            }
        });
    }

}