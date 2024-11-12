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

import java.util.Collections;
import java.util.List;

public class ChatListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private TextView viewNoData;

    private MsgConvAdapter mAdapter;

    public static ChatListFragment build() {
        ChatListFragment chatListFragment = new ChatListFragment();
        return chatListFragment;
    }

    @Override
    protected int getViewId() {
        return R.layout.fragment_chatlist;
    }

    private String getUid() {
        String uid = String.valueOf(GWSDKManager.INSTANCE(getContext()).getUserInfo().getId());
        return uid;
    }

    @Override
    protected void initData() {
        mAdapter = new MsgConvAdapter(getContext(), getUid());
    }

    @Override
    protected void initView() {
        swipeRefreshLayout = (SwipeRefreshLayout) contentView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RecyclerView) contentView.findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        viewNoData = (TextView) contentView.findViewById(R.id.viewNoData);
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
        refreshData();
    }

    @Override
    protected void initEvent() {
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        refreshData();
    }

    private synchronized void refreshData(){
        List<MsgConversationPojo> convList= MsgDaoHelp.queryConvList(getUid());
        if(convList==null)return;
        if(convList.size()>2){
            Collections.sort(convList,new MsgConvBeanComp());
        }
        mAdapter.setData(convList);
        //dissmissLoadingDig();
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

}
