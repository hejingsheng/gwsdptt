package com.gwsd.open_ptt.bean;

import java.io.Serializable;

public class ChatParam implements Serializable {

    int convId;
    int convType;
    String convName;

    public int getConvId() {
        return convId;
    }

    public void setConvId(int convId) {
        this.convId = convId;
    }

    public String getConvName() {
        return convName;
    }

    public void setConvName(String convName) {
        this.convName = convName;
    }

    public int getConvType() {
        return convType;
    }

    public void setConvType(int convType) {
        this.convType = convType;
    }

}
