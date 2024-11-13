package com.gwsd.ptt.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gwsd.bean.GWMsgBean;
import com.gwsd.bean.GWType;
import com.gwsd.ptt.MyApp;
import com.gwsd.ptt.R;
import com.gwsd.ptt.adapter.ChatAdapter;
import com.gwsd.ptt.bean.ChatParam;
import com.gwsd.ptt.dao.MsgDaoHelp;
import com.gwsd.ptt.dao.pojo.MsgContentPojo;
import com.gwsd.ptt.manager.GWSDKManager;
import com.gwsd.ptt.view.AppTopView;
import com.gwsd.ptt.view.ChatInputView;
import com.gwsd.ptt.view.VoiceSendingView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class ChatActivity extends BaseActivity implements ChatInputView.OnInputViewLisenter {

    AppTopView viewChatTopView;
    ChatInputView viewChatInputView;

    RecyclerView viewRecyclerView;
    VoiceSendingView viewVoiceAnimPanel;

    ChatParam chatParam;
    ChatAdapter mAdapter;
    protected List<MsgContentPojo> mData;

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
        Bundle bundle = getIntent().getExtras();
        chatParam = (ChatParam) bundle.getSerializable("param");
        mAdapter = new ChatAdapter(getUid());
        mData=new ArrayList<>();
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
            Observable.timer(500, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> {
                        viewRecyclerView.scrollToPosition(mAdapter.getItemCount()-1);
                    });
        }
    }

    @Override
    public void onSendTxt(String str) {
        GWMsgBean gwMsgBean = null;
        gwMsgBean = GWSDKManager.getSdkManager().sendMsg(chatParam.getConvType(),  chatParam.getConvId(), chatParam.getConvName(), GWType.GW_MSG_TYPE.GW_PTT_MSG_TYPE_TEXT, str);
        MsgContentPojo msgContentPojo = MsgDaoHelp.saveMsgContent(getUid(), gwMsgBean);
        MsgDaoHelp.saveOrUpdateConv(msgContentPojo);
        mAdapter.addMessage(msgContentPojo);
    }

    @Override
    public void onStartVoice() {

    }

    @Override
    public void onStopVoice() {

    }

    @Override
    public void onCancelVoice() {

    }

    @Override
    public void onBtnPhoto() {

    }

    @Override
    public void onBtnCamera() {

    }

    @Override
    public void onBtnVideo() {

    }

    @Override
    public void onBtnFile() {

    }

    @Override
    public void onBtnLoc() {

    }
}
