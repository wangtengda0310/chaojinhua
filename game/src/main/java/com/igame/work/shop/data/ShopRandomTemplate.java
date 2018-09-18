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
 * shoptype = 1 时的商品模板
 */
@XmlRootElement(name = "low")
@XmlAccessorType(XmlAccessType.NONE)
public class ShopRandomTemplate {

    @XmlAttribute(name = "shop_id", required = true)
    private int shopId;//商店ID

    @XmlAttribute(name = "lv")
    private int lv;//商店等级

    //3(类型),200004(商品ID),30(数量);
    @XmlAttribute(name = "item")
    private String item;//道具

    @XmlAttribute(name = "item_price")
    private String itemPrice;//道具价格

    //2(类型),1001(商品ID),1(数量);
    @XmlAttribute(name = "monster")
    private String monster;//怪兽

    @XmlAttribute(name = "monster_price")
    private String monsterPrice;//怪兽价格

    @XmlAttribute(name = "sale")
    private String sale;//折扣

    @XmlAttribute(name = "rate")
    private String rate;//折扣概率


    //根据商品类型和商品ID 获取单价
    public int getPrice(int type, int itemId) {

        if (type == 3){

            String[] items = item.split(ShopConstants.SPLIT_TWO);
            String[] itemPrices = itemPrice.split(ShopConstants.SPLIT_TWO);

            for (int i = 0; i < items.length; i++) {
                if (items[i].contains(""+itemId))
                    return Integer.parseInt(itemPrices[i]);
            }
        }

        if (type == 2){

            String[] monsters = monster.split(ShopConstants.SPLIT_TWO);
            String[] monsterPrices = monsterPrice.split(ShopConstants.SPLIT_TWO);

            for (int i = 0; i < monsters.length; i++) {
                if (monsters[i].contains(""+itemId))
                    return Integer.parseInt(monsterPrices[i]);
            }
        }

        return 0;
    }



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

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getMonster() {
        return monster;
    }

    public void setMonster(String monster) {
        this.monster = monster;
    }

    public String getSale() {
        return sale;
    }

    public void setSale(String sale) {
        this.sale = sale;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}
