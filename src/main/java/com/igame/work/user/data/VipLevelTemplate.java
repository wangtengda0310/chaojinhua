package com.igame.work.user.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author xym
 *
 * 会员等级
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "low")
public class VipLevelTemplate {

    @XmlAttribute(name = "vip_level", required = true)
    private int vipLevel;//vip等级

    @XmlAttribute(name = "vip_exp")
    private double vipExp;//vip升级经验

    @XmlAttribute(name = "vip_text")
    private String vipText;//vip描述文本

    @XmlAttribute(name = "text_value")
    private String textValue;//vip文本值

    public int getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(int vipLevel) {
        this.vipLevel = vipLevel;
    }

    public double getVipExp() {
        return vipExp;
    }

    public void setVipExp(double vipExp) {
        this.vipExp = vipExp;
    }

    public String getVipText() {
        return vipText;
    }

    public void setVipText(String vipText) {
        this.vipText = vipText;
    }

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }
}
