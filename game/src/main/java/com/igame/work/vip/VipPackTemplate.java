package com.igame.work.vip;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author xym
 *
 * 会员礼包
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "item")
public class VipPackTemplate {

    @XmlAttribute(name = "vip_lv", required = true)
    private int vipLv;//vip等级

    @XmlAttribute(name = "day_pack")
    private String dayPack;//每日领取礼包

    @XmlAttribute(name = "first_pack")
    private String firstPack;//特权礼包

    @XmlAttribute(name = "gem")
    private int gem;//特权礼包消耗钻石

    public int getVipLv() {
        return vipLv;
    }

    public void setVipLv(int vipLv) {
        this.vipLv = vipLv;
    }

    public String getDayPack() {
        return dayPack;
    }

    public void setDayPack(String dayPack) {
        this.dayPack = dayPack;
    }

    public String getFirstPack() {
        return firstPack;
    }

    public void setFirstPack(String firstPack) {
        this.firstPack = firstPack;
    }

    public int getGem() {
        return gem;
    }

    public void setGem(int gem) {
        this.gem = gem;
    }
}
