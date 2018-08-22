package com.igame.work.shop.data;

import com.igame.work.shop.ShopConstants;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Arrays;

/**
 * @author xym
 *
 * shoptype = 2 时的商品模板
 */
@XmlRootElement(name = "low")
@XmlAccessorType(XmlAccessType.NONE)
public class ShopOutPutTemplate {

    @XmlAttribute(name = "shop_id", required = true)
    private int shopId;//商店ID

    @XmlAttribute(name = "item")
    private String item;//道具

    @XmlAttribute(name = "rate")
    private String rate;//道具概率

    @XmlAttribute(name = "buy")
    private String buy;//数量

    @XmlAttribute(name = "item_price")
    private String itemPrice;//价格


    //根据商品ID 获取价格
    public int getPrice(int itemId) {
        String[] items = item.split(ShopConstants.SPLIT_ONE);
        String[] prices = itemPrice.split(ShopConstants.SPLIT_ONE);

        int i = Arrays.asList(items).indexOf(""+itemId);
        return Integer.parseInt(prices[i]);
    }



    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getBuy() {
        return buy;
    }

    public void setBuy(String buy) {
        this.buy = buy;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }
}
