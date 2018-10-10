package com.igame.work.shop.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author xym
 *
 * 商店模板
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "item")
public class ShopTemplate {


    @XmlAttribute(name = "shop_id", required = true)
    private int shopId;//商店ID

    @XmlAttribute(name = "shop_name")
    private String shopName;//商店名

    @XmlAttribute(name = "point_type")
    private int pointType;//购买货币类型

    @XmlAttribute(name = "shop_type")
    private int shopType;//商店类型

    @XmlAttribute(name = "unlock")
    private int unlock;//解锁条件

    @XmlAttribute(name = "resest_gem")
    private String resestGem;//刷新钻石

    @XmlAttribute(name = "resest_max")
    private int resestMax;//刷新次数上限

    @XmlAttribute(name = "resest_time")
    private String resestTime;//自动刷新时间

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public int getPointType() {
        return pointType;
    }

    public void setPointType(int pointType) {
        this.pointType = pointType;
    }

    public int getShopType() {
        return shopType;
    }

    public void setShopType(int shopType) {
        this.shopType = shopType;
    }

    public int getUnlock() {
        return unlock;
    }

    public void setUnlock(int unlock) {
        this.unlock = unlock;
    }

    public String getResestGem() {
        return resestGem;
    }

    public void setResestGem(String resestGem) {
        this.resestGem = resestGem;
    }

    public int getResestMax() {
        return resestMax;
    }

    public void setResestMax(int resestMax) {
        this.resestMax = resestMax;
    }

    public String getResestTime() {
        return resestTime;
    }

    public void setResestTime(String resestTime) {
        this.resestTime = resestTime;
    }
}
