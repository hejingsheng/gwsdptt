package com.gwsd.ptt.dao;

import com.gwsd.bean.GWMsgBean;
import com.gwsd.bean.GWType;
import com.gwsd.ptt.MyApp;
import com.gwsd.ptt.dao.greendao.DaoSession;
import com.gwsd.ptt.dao.greendao.MsgContentPojoDao;
import com.gwsd.ptt.dao.greendao.MsgConversationPojoDao;
import com.gwsd.ptt.dao.pojo.MsgContentPojo;
import com.gwsd.ptt.dao.pojo.MsgConversationPojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MsgDaoHelp {

    private static MsgConversationPojoDao getConvDao() {
        MsgConversationPojoDao msgConversationPojoDao = null;
        DaoSession daoSession = MyApp.getInstance().getDaoSession();
        if (daoSession != null) {
            msgConversationPojoDao = daoSession.getMsgConversationPojoDao();
        }
        return msgConversationPojoDao;
    }

    private static MsgContentPojoDao getMsgDao() {
        MsgContentPojoDao msgContentPojoDao = null;
        DaoSession daoSession = MyApp.getInstance().getDaoSession();
        if (daoSession != null) {
            msgContentPojoDao = daoSession.getMsgContentPojoDao();
        }
        return msgContentPojoDao;
    }

    public static List<MsgConversationPojo> queryConvList(String loginUId) {
        MsgConversationPojoDao convDao = getConvDao();
        if (convDao == null) return new ArrayList<>();
        List<MsgConversationPojo> convBeanList = convDao
                .queryBuilder()
                .where(MsgConversationPojoDao.Properties.LoginUId.eq(loginUId))
                .orderDesc(MsgConversationPojoDao.Properties.LastMsgTime)
                .list();
        return convBeanList;
    }

    public static MsgContentPojo saveMsgContent(String uid, GWMsgBean bean) {
        MsgContentPojoDao dao = getMsgDao();
        if (dao == null) return null;
        MsgContentPojo msgContentPojo = new MsgContentPojo();
        msgContentPojo.setLoginUId(uid);
        msgContentPojo.setSenderId(bean.getData().getSendId());
        msgContentPojo.setSenderNm(bean.getData().getSendName());
        msgContentPojo.setSenderType(bean.getData().getSendUType());
        msgContentPojo.setRecvId(bean.getData().getReceiveId());
        msgContentPojo.setRecvNm(bean.getData().getReceiveName());
        msgContentPojo.setRecvType(bean.getData().getReceiveUType());
        int msgtype = bean.getData().getMsgType();
        msgContentPojo.setMsgType(msgtype);
        if (msgtype == GWType.GW_MSG_TYPE.GW_PTT_MSG_TYPE_TEXT) {
            msgContentPojo.setContent(bean.getData().getContent());
            msgContentPojo.setUrl("");
            msgContentPojo.setThumburl("");
        } else {
            msgContentPojo.setContent("");
            msgContentPojo.setUrl(bean.getData().getUrl());
            if (msgtype == GWType.GW_MSG_TYPE.GW_PTT_MSG_TYPE_VIDEO) {
                msgContentPojo.setThumburl(bean.getData().getThumbUrl());
            } else {
                msgContentPojo.setThumburl("");
            }
        }
        msgContentPojo.setTime(Long.valueOf(bean.getTime()));
        if (bean.getData().getReceiveUType() == GWType.GW_MSG_RECV_TYPE.GW_PTT_MSG_RECV_TYPE_USER
            ||bean.getData().getReceiveUType() == GWType.GW_MSG_RECV_TYPE.GW_PTT_MSG_RECV_TYPE_DISPATCH) {
            if (uid.equals(bean.getData().getSendId())) {
                msgContentPojo.setConvId(Integer.parseInt(bean.getData().getReceiveId()));
            } else {
                msgContentPojo.setConvId(Integer.parseInt(bean.getData().getSendId()));
            }
        } else {
            msgContentPojo.setConvId(Integer.parseInt(bean.getData().getReceiveId()));
        }

//        if (bean.getData().getReceiveUType() == GWType.GW_MSG_RECV_TYPE.GW_PTT_MSG_RECV_TYPE_USER
//            ||bean.getData().getReceiveUType() == GWType.GW_MSG_RECV_TYPE.GW_PTT_MSG_RECV_TYPE_DISPATCH) {
//            if (uid.equals(bean.getData().getSendId())) {
//                msgContentPojo.setConvType(bean.getData().getReceiveUType());
//            } else {
//                msgContentPojo.setConvType(bean.getData().getSendUType());
//            }
//        } else {
//            msgContentPojo.setConvType(bean.getData().getReceiveUType());
//        }
        msgContentPojo.setConvType(bean.getData().getReceiveUType());
        msgContentPojo.setPlaytime(0);
        long id = dao.insertOrReplace(msgContentPojo);
        //msgContentPojo.setTab_Id(id);
        return msgContentPojo;
    }

    public static MsgConversationPojo saveOrUpdateConv(MsgContentPojo msgContent) {
        String uid=msgContent.getLoginUId();
        Integer cid =msgContent.getConvId();
        Integer ctype = msgContent.getConvType();
        MsgConversationPojoDao convDao = getConvDao();
        if (convDao == null)
            return null;
        MsgConversationPojo msgConversationPojo;
        msgConversationPojo = convDao.queryBuilder()
                .where(MsgConversationPojoDao.Properties.LoginUId.eq(uid),MsgConversationPojoDao.Properties.ConvId.eq(cid),MsgConversationPojoDao.Properties.ConvType.eq(ctype))
                .unique();
        if (msgConversationPojo == null) {
            msgConversationPojo = new MsgConversationPojo();
            msgConversationPojo.setMsgUnReadCnt(0);
        }
        msgConversationPojo.setLoginUId(uid);
        msgConversationPojo.setConvId(cid);
//        if (ctype == GWType.GW_MSG_RECV_TYPE.GW_PTT_MSG_RECV_TYPE_USER) {
//            if (uid.equals(msgContent.getSenderId())) {
//
//            }
//        }
        msgConversationPojo.setConvType(ctype);
        if (ctype == GWType.GW_MSG_RECV_TYPE.GW_PTT_MSG_RECV_TYPE_USER
            || ctype == GWType.GW_MSG_RECV_TYPE.GW_PTT_MSG_RECV_TYPE_DISPATCH) {
            if (uid.equals(msgContent.getSenderId())) {
                msgConversationPojo.setConvNm(msgContent.getRecvNm());
            } else {
                msgConversationPojo.setConvNm(msgContent.getSenderNm());
            }
        } else {
            msgConversationPojo.setConvNm(msgContent.getRecvNm());
        }
        msgConversationPojo.setLastMsgId(msgContent.getTab_Id());
        msgConversationPojo.setLastMsgSenderNm(msgContent.getSenderNm());
        msgConversationPojo.setLastMsgType(msgContent.getMsgType());
        msgConversationPojo.setLastMsgTime(msgContent.getTime());
        msgConversationPojo.setLastMsgContent(msgContent.getContent());
        if (uid.equals(msgContent.getSenderId())) {
            msgConversationPojo.setMsgUnReadCnt(0);
        } else {
            int unread = msgConversationPojo.getMsgUnReadCnt();
            msgConversationPojo.setMsgUnReadCnt(unread+1);
        }
        convDao.insertOrReplace(msgConversationPojo);
        return msgConversationPojo;
    }

    public static void updateConvRead(String loginUId,int convId, int convtype){
        MsgConversationPojoDao convDao = getConvDao();
        if (convDao == null) return;
        MsgConversationPojo convBean=convDao.queryBuilder()
                .where(MsgConversationPojoDao.Properties.LoginUId.eq(loginUId),MsgConversationPojoDao.Properties.ConvId.eq(convId),MsgConversationPojoDao.Properties.ConvType.eq(convtype))
                .unique();
        if(convBean!=null){
            convBean.setMsgUnReadCnt(0);
            convDao.update(convBean);
        }
    }

    public static List<MsgContentPojo> queryChatRecord(String loginUId, int cId,int ctype, int pageNum,int pagrSize) {
        MsgContentPojoDao dao = getMsgDao();
        if (dao == null) return new ArrayList<>();
        List<MsgContentPojo> msgBeanList = dao
                .queryBuilder()
                .where(MsgContentPojoDao.Properties.LoginUId.eq(loginUId),MsgContentPojoDao.Properties.ConvId.eq(cId),MsgContentPojoDao.Properties.ConvType.eq(ctype))
                .orderDesc(MsgContentPojoDao.Properties.Time)
                .offset(pageNum*pagrSize)
                .limit(pagrSize)
                .list();
        Collections.reverse(msgBeanList);
        return msgBeanList;
    }

}