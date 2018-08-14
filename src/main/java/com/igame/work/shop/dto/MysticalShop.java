package com.igame.work.shop.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.igame.work.shop.ShopConstants;
import org.mongodb.morphia.annotations.Embedded;

import java.util.Date;

/**
 * @author xym
 *
 * 神秘商店
 */
@Embedded
public class MysticalShop {

    private int shopId;  //商店ID

    private int shopLv;    //商店等级

    private int itemType;    //2 = 怪兽, 3 = 道具

    private int id;      //怪兽或者道具ID

    private int count;   //剩余可购买个数

    private int price;   //原价

    private int sale;   //折扣

    private int discountedPrice;   //折后价

    @JsonIgnore
    private Date lastReload;    //最近刷新时间    定时任务和手动刷新维护

    @JsonIgnore
    private int reloadCount;    //当日累计刷新次数

    private int exp;    //经验


    //清除商品项
    public void clearItems() {

        this.itemType = 0;    //2 = 怪兽, 3 = 道具
        this.id = 0;      //怪兽或者道具ID
        this.count = 0;   //剩余可购买个数
        this.sale = 10;   //折扣
        this.price = 0;   //价格
        this.lastReload = null;    //最近刷新时间    定时任务和手动刷新维护
    }

    //2(类型),1001(商品ID),1(数量);
    public void parseShops(String shops) {

        String[] split = shops.split(ShopConstants.SPLIT_ONE);

        this.itemType = Integer.parseInt(split[0]);    //2 = 怪兽, 3 = 道具
        this.id = Integer.parseInt(split[1]);      //怪兽或者道具ID
        this.count = Integer.parseInt(split[2]);   //剩余可购买个数
    }

    public synchronized int addReloadCount() {
        this.reloadCount++;
        return this.reloadCount;
    }




    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Date getLastReload() {
        return lastReload;
    }

    public void setLastReload(Date lastReload) {
        this.lastReload = lastReload;
    }

    public int getSale() {
        return sale;
    }

    public void setSale(int sale) {
        this.sale = sale;
    }

    public int getReloadCount() {
        return reloadCount;
    }

    public void setReloadCount(int reloadCount) {
        this.reloadCount = reloadCount;
    }

    public int getShopLv() {
        return shopLv;
    }

    public void setShopLv(int shopLv) {
        this.shopLv = shopLv;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(int discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public synchronized int addCount(int count) {
        return this.count += count;
    }
}
