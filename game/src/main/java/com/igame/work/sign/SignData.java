package com.igame.work.sign;

import org.mongodb.morphia.annotations.Entity;


@Entity(noClassnameStored = true)
public class SignData {
    private String signData;    // round,signedDays,signDate
    private String totalSign;   // "0,0,0,0" （1 已经领取 其他值 未领取）

    public SignData() {
        signData = "1,0,null";
        totalSign = "0,0,0,0";
    }

    public void setSignData(String signData) {
        this.signData = signData;
    }

    public String getSignData() {
        return this.signData;
    }

    public String getTotalSign() {
        return totalSign;
    }

    public void setTotalSign(String totalSign) {
        this.totalSign = totalSign;
    }

}
