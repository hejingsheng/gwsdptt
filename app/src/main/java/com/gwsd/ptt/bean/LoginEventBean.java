package com.gwsd.ptt.bean;

public class LoginEventBean {

    private int loginResult;

    public LoginEventBean(int loginResult) {
        this.loginResult = loginResult;
    }

    public int getLoginResult() {
        return loginResult;
    }

    public void setLoginResult(int loginResult) {
        this.loginResult = loginResult;
    }
}
