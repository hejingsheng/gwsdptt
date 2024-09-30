package com.gwsd.ptt.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gwsd.bean.GWGroupListBean;
import com.gwsd.ptt.R;

import java.util.ArrayList;
import java.util.List;


public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {
    private List<GWGroupListBean.GWGroupBean> groups = new ArrayList<>();
    private int selectedPosition = -1; // 当前选中的位置

    public void setGroups(List<GWGroupListBean.GWGroupBean> groups) {
        this.groups = groups;
        Log.d("GroupAdapter", "Group size: " + groups.size());
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_recyclelist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GWGroupListBean.GWGroupBean group = groups.get(position);

        // 设置 CheckBox 状态
        holder.checkBox.setChecked(position == selectedPosition);
        holder.count.setText(String.valueOf(position + 1));
        holder.groupName.setText(group.getName());
        holder.groupId.setText(String.valueOf(group.getGid()));

        // 设置 CheckBox 的点击事件
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedPosition = position;
                notifyDataSetChanged();
                Log.d("GroupAdapter", "Selected gid: " + group.getGid());
            } else {
                if (selectedPosition == position) {
                    selectedPosition = -1;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public long getSelectedGid() {
        if (selectedPosition != -1) {
            return groups.get(selectedPosition).getGid(); // 返回选中项的 gid
        }
        return -1;
    }

    public int getSelectedType() {
        if (selectedPosition != -1) {
            return groups.get(selectedPosition).getType(); // 返回选中项的 gid
        }
        return -1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView count;
        TextView groupName;
        TextView groupId;

        public ViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBox);
            count = itemView.findViewById(R.id.count);
            groupName = itemView.findViewById(R.id.groupName);
            groupId = itemView.findViewById(R.id.groupId);
        }
    }
}


