package com.gwsd.ptt.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gwsd.bean.GWMsgBean;
import com.gwsd.bean.GWType;
import com.gwsd.ptt.R;
import com.gwsd.ptt.adapter.ChatAdapter;
import com.gwsd.ptt.bean.ChatParam;
import com.gwsd.ptt.bean.FileSendParam;
import com.gwsd.ptt.dao.MsgDaoHelp;
import com.gwsd.ptt.dao.pojo.MsgContentPojo;
import com.gwsd.ptt.manager.GWSDKManager;
import com.gwsd.ptt.service.FileSendService;
import com.gwsd.ptt.utils.RecorderUtil;
import com.gwsd.ptt.video_ui.video_record.VideoRecordActivity;
import com.gwsd.ptt.video_ui.video_record.help.VideoRecordParam1;
import com.gwsd.ptt.view.AppTopView;
import com.gwsd.ptt.view.ChatInputView;
import com.gwsd.ptt.view.VoiceSendingView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class ChatActivity extends BaseActivity implements ChatInputView.OnInputViewLisenter {

    private static final int MAX_VIDEO_RECORD_TIME = 15;

    AppTopView viewChatTopView;
    ChatInputView viewChatInputView;

    RecyclerView viewRecyclerView;
    VoiceSendingView viewVoiceAnimPanel;

    ChatParam chatParam;
    ChatAdapter mAdapter;
    protected List<MsgContentPojo> mData;

    private static class FileSelectData {
        public int type;
        public String path;
    }

    public static void startAct(Context context, ChatParam chatParam) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("param", chatParam);
        context.startActivity(intent);
    }

    private String getUid() {
        String uid = String.valueOf(GWSDKManager.getSdkManager().getUserInfo().getId());
        return uid;
    }

    @Override
    protected int getViewId() {
        return R.layout.activity_chat;
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        Bundle bundle = getIntent().getExtras();
        chatParam = (ChatParam) bundle.getSerializable("param");
        mAdapter = new ChatAdapter(getUid());
        mData=new ArrayList<>();
    }

    @Override
    protected void release() {
        super.release();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initView() {
        viewChatTopView = findViewById(R.id.viewChatTopView);
        viewChatTopView.setTopTitle(chatParam.getConvName());

        viewChatInputView = findViewById(R.id.viewChatInputView);

        viewRecyclerView = findViewById(R.id.viewRecyclerView);
        viewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        viewRecyclerView.setAdapter(mAdapter);

        viewVoiceAnimPanel = findViewById(R.id.viewVoiceAnimPanel);
        viewVoiceAnimPanel.setVisibility(View.GONE);
        viewVoiceAnimPanel.showCancel();

        setConvUnReadNone();
        refreshData();
    }

    @Override
    protected void initEvent() {
        viewChatTopView.setLeftClick(v -> {
            finish();
        });
        viewChatInputView.setOnInputVewCLisenter(this);
    }

    private void setConvUnReadNone() {
        String userId=getUid();
        MsgDaoHelp.updateConvRead(userId, chatParam.getConvId(), chatParam.getConvType());
    }

    private void refreshData() {
        String userId=getUid();
        List<MsgContentPojo> chatMsgBasePojoList = MsgDaoHelp.queryChatRecord(userId, chatParam.getConvId(), chatParam.getConvType(),0,30);
        mData.clear();
        mData.addAll(chatMsgBasePojoList);
        mAdapter.addAllMessage(mData);
        if(mData.size()>0){
            Observable.timer(200, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> {
                        viewRecyclerView.scrollToPosition(mAdapter.getItemCount()-1);
                    });
        }
    }

    @Override
    public void onSendTxt(String str) {
        GWMsgBean gwMsgBean = GWSDKManager.getSdkManager().createMsgBean(chatParam.getConvType(),  chatParam.getConvId(), chatParam.getConvName(), GWType.GW_MSG_TYPE.GW_PTT_MSG_TYPE_TEXT);
        gwMsgBean.getData().setContent(str);
        GWSDKManager.getSdkManager().sendMsg(gwMsgBean);
        MsgContentPojo msgContentPojo = MsgDaoHelp.saveMsgContent(getUid(), gwMsgBean);
        MsgDaoHelp.saveOrUpdateConv(msgContentPojo);
        mAdapter.addMessage(msgContentPojo);
    }

    RecorderUtil recorderUtil;
    @Override
    public void onStartVoice() {
        log("start voice");
        if (recorderUtil == null) {
            recorderUtil = new RecorderUtil();
        }
        recorderUtil.generalFileName();
        recorderUtil.startRecording();
    }

    @Override
    public void onStopVoice() {
        log("stop voice");
        if(recorderUtil!=null){
            recorderUtil.stopRecording();
            long timeInterval=recorderUtil.getTimeInterval();
            String filePath=recorderUtil.getFilePath();
            File file=new File(filePath);
            if(timeInterval<3){
                showToast(R.string.hint_camera_time_small);
                if(file.exists()){
                    file.delete();
                }
                recorderUtil.release();
            }else {
                recorderUtil.release();
                FileSelectData filedata = new FileSelectData();
                filedata.type = FileSendParam.VOICE_FILE_TYPE;
                filedata.path = filePath;
                handleActivityResult(filedata);
            }
        }
        recorderUtil=null;
    }

    @Override
    public void onCancelVoice() {
        log("cancel voice");
        if(recorderUtil!=null) {
            recorderUtil.stopRecording();
            recorderUtil.release();
            String filePath=recorderUtil.getFilePath();
            File file=new File(filePath);
            if(file.exists()) {
                file.delete();
            }
        }
        recorderUtil=null;
    }

    @Override
    public void onBtnPhoto() {
        log("send photo");
        if (GWSDKManager.getSdkManager().hasMsgPermission()) {
            startPhoto.launch("image/*");
        } else {
            showToast(R.string.hint_notPermission);
        }
    }

    @Override
    public void onBtnVideo() {
        VideoRecordParam1.RecordParam recordParam=new VideoRecordParam1.RecordParam();
        recordParam.setMaxTime(MAX_VIDEO_RECORD_TIME);
        VideoRecordParam1 videoRecordParam1 =new VideoRecordParam1();
        videoRecordParam1.setPlayParam(null);
        videoRecordParam1.setRecordParam(recordParam);
        startVideo.launch(videoRecordParam1);
    }

    @Override
    public void onBtnFile() {

    }

    @Override
    public void onBtnLoc() {
        showToast(R.string.hint_exploit_ing);
    }

    @Override
    public void onBtnPttCall() {

    }

    @Override
    public void onBtnVoiceCall() {

    }

    @Override
    public void onBtnVideoCall() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRecvMsg(MsgContentPojo data) {
        if (data.getConvId() == chatParam.getConvId()) {
            setConvUnReadNone();
            if (mData != null) {
                mData.add(data);
            }
            mAdapter.addMessage(data);
            Observable.timer(200, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong ->  viewRecyclerView.scrollToPosition(mAdapter.getItemCount()-1));
        }
    }

    ActivityResultLauncher<String> startPhoto = registerForActivityResult(new ActivityResultContract<String, FileSelectData>() {
        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, String input) {
            Intent intent = new Intent(Intent.ACTION_PICK, null);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI , input);
            return intent;
        }

        @Override
        public FileSelectData parseResult(int resultCode, @Nullable Intent intent) {
            Uri uri = intent.getData();
            String scheme = uri.getScheme();
            String filepath = "";
            if (scheme.startsWith("file")) {
                filepath = uri.getPath();
            } else if (scheme.startsWith("content")) {
                Cursor cursor = getContentResolver().query(uri, null,null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    int count = cursor.getCount();
                    if (count > 0) {
                        filepath = cursor.getString(index);
                    }
                    cursor.close();
                }
            }
            FileSelectData fileSelectData = new FileSelectData();
            fileSelectData.type = FileSendParam.PHOTO_FILE_TYPE;
            fileSelectData.path = filepath;
            return fileSelectData;
        }
    }, this::handleActivityResult);

    ActivityResultLauncher<VideoRecordParam1> startVideo = registerForActivityResult(new ActivityResultContract<VideoRecordParam1, FileSelectData>() {
        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, VideoRecordParam1 input) {
            Intent intent=new Intent(context,VideoRecordActivity.class);
            intent.putExtra("VideoRecordParam1", input);
            return intent;
        }

        @Override
        public FileSelectData parseResult(int resultCode, @Nullable Intent intent) {
            Bundle bundle=intent.getExtras();
            String filepath = "";
            if(bundle!=null){
                String path=bundle.getString("filePath");
                if(TextUtils.isEmpty(path) || !path.endsWith(".mp4")){
                    showToast(R.string.hint_video_only_mp4);
                    filepath = "";
                } else {
                    filepath = path;
                }
            } else {
                filepath = "";
            }
            FileSelectData fileSelectData = new FileSelectData();
            fileSelectData.type = FileSendParam.VIDEO_FILE_TYPE;
            fileSelectData.path = filepath;
            return fileSelectData;
        }
    }, this::handleActivityResult);

    private void handleActivityResult(FileSelectData filedata) {
        log("select file="+filedata.type+" path="+filedata.path);
        int msgtype;
        FileSendParam fileSendParam = new FileSendParam();
        fileSendParam.setFilepath(filedata.path);
        fileSendParam.setFiletype(filedata.type);
        msgtype = GWType.GW_MSG_TYPE.GW_PTT_MSG_TYPE_PHOTO;
        if (filedata.type == FileSendParam.VIDEO_FILE_TYPE) {
            String thumburl = GWSDKManager.getSdkManager().createThumb(filedata.path);
            log("video thumburl="+thumburl);
            fileSendParam.setFilepathThumb(thumburl);
            msgtype = GWType.GW_MSG_TYPE.GW_PTT_MSG_TYPE_VIDEO;
        } else if (filedata.type == FileSendParam.VOICE_FILE_TYPE) {
            msgtype = GWType.GW_MSG_TYPE.GW_PTT_MSG_TYPE_VOICE;
        }
        GWMsgBean gwMsgBean = GWSDKManager.getSdkManager().createMsgBean(chatParam.getConvType(),  chatParam.getConvId(), chatParam.getConvName(), msgtype);
        fileSendParam.setGwMsgBean(gwMsgBean);
        FileSendService.startFileSend(getContext(), fileSendParam);
    }
}