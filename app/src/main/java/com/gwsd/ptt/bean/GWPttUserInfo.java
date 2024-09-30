package com.gwsd.ptt.bean;

public class GWPttUserInfo {

    private String account;
    private String password;
    private String name;
    private int id;
    private long defaultGid;
    private CurrentGroup currentGroup;
    private long lastpriority;
    private boolean online;

    public GWPttUserInfo() {
        currentGroup = new CurrentGroup();
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getDefaultGid() {
        return defaultGid;
    }

    public void setDefaultGid(long defaultGid) {
        this.defaultGid = defaultGid;
    }

    public String getCurrentGroupName() {
        return currentGroup.getGname();
    }

    public void setCurrentGroupName(String gname) {
        currentGroup.setGname(gname);
    }

    public long getCurrentGroupGid() {
        return currentGroup.getGid();
    }

    public void setCurrentGroupGid(long currentGid) {
        currentGroup.setGid(currentGid);
    }

    public void setCurrentGroupPriority(long priority) {
        currentGroup.setPriority(priority);
    }

    public long getCurrentGroupPriority() {
        return currentGroup.getPriority();
    }

    public long getLastpriority() {
        return lastpriority;
    }

    public void setLastpriority(long lastpriority) {
        this.lastpriority = lastpriority;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public static class CurrentGroup {
        private long gid;
        private String gname;
        private long priority;

        public long getGid() {
            return gid;
        }

        public void setGid(long gid) {
            this.gid = gid;
        }

        public String getGname() {
            return gname;
        }

        public void setGname(String gname) {
            this.gname = gname;
        }

        public long getPriority() {
            return priority;
        }

        public void setPriority(long priority) {
            this.priority = priority;
        }
    }

}
