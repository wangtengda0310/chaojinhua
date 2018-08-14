package com.igame.core.data.template;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author xym
 *
 * 神秘商店等级模板
 */
@XmlRootElement(name = "low")
@XmlAccessorType(XmlAccessType.NONE)
public class ShopRandomLvTemplate {

    @XmlAttribute(name = "shop_id", required = true)
    private int shopId;//商店ID

    @XmlAttribute(name = "lv")
    private int lv;//商店等级

    @XmlAttribute(name = "exp")
    private int exp;//经验

    @XmlAttribute(name = "diamon")
    private int diamon;//钻石数

    @XmlAttribute(name = "get_exp")
    private int getExp;//消耗钻石数获得经验

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public int getLv() {
        return lv;
    }

    public void setLv(int lv) {
        this.lv = lv;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getDiamon() {
        return diamon;
    }

    public void setDiamon(int diamon) {
        this.diamon = diamon;
    }

    public int getGetExp() {
        return getExp;
    }

    public void setGetExp(int getExp) {
        this.getExp = getExp;
    }

    public int getUnitExp() {
        return this.getExp/this.diamon;
    }
}
