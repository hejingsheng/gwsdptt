package com.gwsd.ptt.activity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;
import com.gwsd.ptt.R;
import com.gwsd.ptt.fragment.BaseFragment;
import com.gwsd.ptt.fragment.ChatListFragment;
import com.gwsd.ptt.fragment.GroupListFragment;
import com.gwsd.ptt.fragment.MeFragment;
import com.gwsd.ptt.view.AppTopView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    AppTopView viewAppTop;
    FrameLayout viewFrameLayout;
    TabLayout viewTablayout;

    List<BaseFragment> fragmentList;

    public static void startAct(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        viewAppTop = findViewById(R.id.viewTop);
        viewFrameLayout = findViewById(R.id.viewFrameLayout);
        viewTablayout = findViewById(R.id.viewTablayout);
    }

    @Override
    protected void initEvent() {
        onTabLayouSelectedListener();
        fragmentList=new ArrayList<>();
        initTab(getSupportFragmentManager(), fragmentList,this,viewTablayout);
        viewTablayout.getTabAt(0).select();
    }

    private void initTab(FragmentManager fragmentManager, List<BaseFragment> fragmentList, Context context, TabLayout viewTablayout) {

        TabLayout tabLayout = viewTablayout;
        List<String> fragmentTitleList = new ArrayList<>();
        List<String> fragmentFlagList = new ArrayList<>();
        List<Integer> titleImgsList = new ArrayList<>();

        fragmentList.add(ChatListFragment.build());
        fragmentTitleList.add(getString(R.string.main_tab_chat));
        fragmentFlagList.add("ChatListFragment");
        titleImgsList.add(R.drawable.main_tab_chat);

        fragmentList.add(GroupListFragment.build());
        fragmentTitleList.add(getString(R.string.main_tab_grp));
        fragmentFlagList.add("GroupFragment");
        titleImgsList.add(R.drawable.main_tab_grp);

        fragmentList.add(MeFragment.build());
        fragmentTitleList.add(getString(R.string.main_tab_my));
        fragmentFlagList.add("MeFragment");
        titleImgsList.add(R.drawable.main_tab_me);

        for (int i = 0; i < fragmentTitleList.size(); i++) {
            tabLayout.addTab(tabLayout.newTab());
        }
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(getTabView(context, i, fragmentTitleList, titleImgsList));
            }
        }
    }

    private View getTabView(Context context, int position, List<String> fragmentTitleList, List<Integer> titleImgsList) {
        View view;

        view = LayoutInflater.from(context).inflate(R.layout.view_main_tab, null);
        TextView textview = (TextView) view.findViewById(R.id.tab_tv);
        textview.setTextAppearance(context, R.style.mainRadioBtn);
        ImageView imageview = (ImageView) view.findViewById(R.id.tab_imgview);
        View view_msg_red = view.findViewById(R.id.view_msg_red);
        view_msg_red.setVisibility(View.GONE);
        textview.setText(fragmentTitleList.get(position));
        imageview.setImageResource(titleImgsList.get(position).intValue());
        return view;
    }

    private void onTabLayouSelectedListener(){
        viewTablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    viewAppTop.setVisibility(View.VISIBLE);
                    viewAppTop.setTopTitle(R.string.title_ChatActivity);
                } else if (position == 1) {
                    viewAppTop.setVisibility(View.VISIBLE);
                    viewAppTop.setTopTitle(R.string.title_GroupFragment);
                } else {
                    viewAppTop.setVisibility(View.GONE);
                }
                BaseFragment baseFragment = fragmentList.get(position);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                if (!baseFragment.isAdded()) {
                    fragmentTransaction.add(R.id.viewFrameLayout, baseFragment);
                }
                fragmentTransaction.show(baseFragment);
                for (BaseFragment fragment : fragmentList) {
                    if (fragment != baseFragment) {
                        fragmentTransaction.hide(fragment);
                    }
                }
                fragmentTransaction.commitAllowingStateLoss();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

}
