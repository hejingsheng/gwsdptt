package com.gwsd.ptt.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gwsd.bean.GWType;
import com.gwsd.ptt.R;
import com.gwsd.ptt.bean.ChatParam;
import com.gwsd.ptt.view.AppTopView;

public class GroupDetailActivity extends BaseActivity{

    AppTopView topView;
    TextView tVGrpName;
    TextView tVGid;
    LinearLayout lLmsg;
    LinearLayout lLHalfDuplex;

    private long gid;
    private String gname;

    public static void startAct(Context context,long gid,String name) {
        Intent intent = new Intent(context, GroupDetailActivity.class);
        intent.putExtra("selectGid", gid);
        intent.putExtra("selectName",name);
        context.startActivity(intent);
    }

    @Override
    protected int getViewId() {
        return R.layout.activity_group_opt;
    }

    @Override
    protected void initData() {
        gid = getIntent().getLongExtra("selectGid", -1);
        gname = getIntent().getStringExtra("selectName");
    }

    @Override
    protected void initView() {
        topView = findViewById(R.id.viewTopView);
        tVGrpName = findViewById(R.id.groupName);
        tVGid = findViewById(R.id.groupId);
        lLmsg = findViewById(R.id.sendMsgLL);
        lLHalfDuplex = findViewById(R.id.halfDuplexLL);

        topView.setTopTitle(R.string.group_detail);
        tVGrpName.setText(gname);
        tVGid.setText(String.valueOf(gid));

    }

    @Override
    protected void initEvent() {
        topView.setLeftClick(v->{
            finish();
        });
        topView.setTopRightImg(R.mipmap.ic_more);
        topView.setRightClick(v->{
            MemberListActivity.startAct(getContext(),gid);
        });
        lLmsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatParam chatParam=new ChatParam();
                chatParam.setConvId((int)gid);
                chatParam.setConvName(gname);
                chatParam.setConvType(GWType.GW_MSG_RECV_TYPE.GW_PTT_MSG_RECV_TYPE_GROUP);
                ChatActivity.startAct(getContext(), chatParam);
            }
        });
        lLHalfDuplex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HalfDuplexActivity.startAct(getContext(),gid, gname);
            }
        });

    }

}
