package com.gwsd.ptt.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.gwsd.ptt.R;
import com.gwsd.ptt.dao.pojo.MsgContentPojo;

import java.util.ArrayList;

public class ChatFromPhotoView extends ChatBaseFromMsgView {
    public ChatFromPhotoView(Context context) {
        super(context);
    }

    public ChatFromPhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChatFromPhotoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    ImageView viewPhoto;
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        viewPhoto=findViewById(R.id.viewPhoto);
    }

    @Override
    public void displayMessage(int position, MsgContentPojo dbBean, String userId) {
        super.displayMessage(position,dbBean, userId);
        String imgUrl=dbBean.getUrl();
        loadImg(getContext(),imgUrl,viewPhoto);
        setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        ArrayList<String> arrayList=new ArrayList<>();
        arrayList.add(bean.getUrl());
        //PhotoImagePagerActivity.navToImagePagerActivity(view.getContext(),arrayList,0);
    }
}
