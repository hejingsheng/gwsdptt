package com.gwsd.ptt.activity;

import android.content.Context;
import android.content.Intent;
import android.widget.FrameLayout;

import androidx.fragment.app.FragmentTransaction;

import com.gwsd.ptt.R;
import com.gwsd.ptt.fragment.ChatListFragment;
import com.gwsd.ptt.view.AppTopView;

public class ChatListActivity extends BaseActivity{

    private AppTopView viewTop;
    private FrameLayout viewFLayoutChatList;

    public static void startAct(Context context) {
        Intent intent = new Intent(context, ChatListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getViewId() {
        return R.layout.activity_chatlist;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        viewTop = (AppTopView) findViewById(R.id.viewTop);
        viewFLayoutChatList = (FrameLayout) findViewById(R.id.viewFLayoutChatList);

        viewTop.setTopTitle(R.string.item_talkback_menu_item_chat);

        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.viewFLayoutChatList, ChatListFragment.build());

        fragmentTransaction.commit();
    }

    @Override
    protected void initEvent() {
        viewTop.setLeftClick(v->{
            finish();
        });
    }
}
