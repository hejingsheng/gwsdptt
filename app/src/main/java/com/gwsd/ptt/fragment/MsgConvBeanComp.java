package com.gwsd.ptt.fragment;

import com.gwsd.ptt.dao.pojo.MsgConversationPojo;

import java.util.Comparator;

public class MsgConvBeanComp implements Comparator<MsgConversationPojo> {
    @Override
    public int compare(MsgConversationPojo o1, MsgConversationPojo o2) {
        return Long.compare(o2.getLastMsgTime(),o1.getLastMsgTime());
    }
}
