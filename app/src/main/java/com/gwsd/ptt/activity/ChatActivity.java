package com.gwsd.ptt.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gwsd.ptt.R;
import com.gwsd.ptt.bean.ChatParam;
import com.gwsd.ptt.view.AppTopView;
import com.gwsd.ptt.view.ChatInputView;
import com.gwsd.ptt.view.VoiceSendingView;

public class ChatActivity extends BaseActivity implements ChatInputView.OnInputViewLisenter {

    AppTopView viewChatTopView;
    ChatInputView viewChatInputView;

    //GWChatAdapter mAdapter;
    RecyclerView viewRecyclerView;
    VoiceSendingView viewVoiceAnimPanel;

    ChatParam chatParam;

    public static void startAct(Context context, ChatParam chatParam) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("param", chatParam);
        context.startActivity(intent);
    }

    @Override
    protected int getViewId() {
        return R.layout.activity_chat;
    }

    @Override
    protected void initData() {
        Bundle bundle = getIntent().getExtras();
        chatParam = (ChatParam) bundle.getSerializable("param");
    }

    @Override
    protected void initView() {
        viewChatTopView = findViewById(R.id.viewChatTopView);
        viewChatTopView.setTopTitle(chatParam.getConvName());

        viewChatInputView = findViewById(R.id.viewChatInputView);

        viewRecyclerView = findViewById(R.id.viewRecyclerView);
        viewRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        viewVoiceAnimPanel = findViewById(R.id.viewVoiceAnimPanel);
        viewVoiceAnimPanel.setVisibility(View.GONE);
        viewVoiceAnimPanel.showCancel();
    }

    @Override
    protected void initEvent() {
        viewChatTopView.setLeftClick(v -> {
            finish();
        });
        viewChatInputView.setOnInputVewCLisenter(this);
    }

    @Override
    public void onSendTxt(String str) {

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
