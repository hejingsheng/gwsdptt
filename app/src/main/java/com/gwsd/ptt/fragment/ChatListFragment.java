package com.gwsd.ptt.fragment;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gwsd.ptt.R;
import com.gwsd.ptt.activity.ChatActivity;
import com.gwsd.ptt.adapter.MsgConvAdapter;
import com.gwsd.ptt.bean.ChatParam;
import com.gwsd.ptt.dao.MsgDaoHelp;
import com.gwsd.ptt.dao.pojo.MsgConversationPojo;
import com.gwsd.ptt.manager.GWSDKManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class ChatListFragment extends ListFragment{

    private MsgConvAdapter mAdapter;
    private Disposable mDisposable;

    public static ChatListFragment build() {
        ChatListFragment chatListFragment = new ChatListFragment();
        return chatListFragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshData();
    }


    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        mAdapter = new MsgConvAdapter(getContext(), getUid());
    }

    @Override
    protected void release() {
        super.release();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void setAdapter() {
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClick((MsgConvAdapter.OnItemClick<MsgConversationPojo>) (view, o, longclick) -> {
            if (longclick) {
                // not process
            } else {
                ChatParam chatParam=new ChatParam();
                chatParam.setConvId(o.getConvId());
                chatParam.setConvName(o.getConvNm());
                chatParam.setConvType(o.getConvType());
                ChatActivity.startAct(getContext(), chatParam);
            }
        });
    }

    @Override
    protected void loadData() {
        refreshData();
    }

    private synchronized void refreshData(){
        List<MsgConversationPojo> convList= MsgDaoHelp.queryConvList(getUid());
        if(convList==null)return;
        if(convList.size()>2){
            Collections.sort(convList,new MsgConvBeanComp());
        }
        mAdapter.setData(convList);
        swipeRefreshLayout.setRefreshing(false);
        checkDataEmpty();
    }

    private void checkDataEmpty(){
        if(mAdapter.getMsgCount()>0){
            viewNoData.setVisibility(View.GONE);
        }else {
            viewNoData.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventUpdateConvList(MsgConversationPojo data) {
        if (data == null) {
            return;
        }
        cancelDisposable();
        mDisposable= Observable.timer( 300, TimeUnit.MILLISECONDS )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    refreshData();
                });
    }

    private void cancelDisposable(){
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
            mDisposable=null;
        }
    }

}