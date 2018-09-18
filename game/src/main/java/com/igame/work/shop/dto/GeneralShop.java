package com.igame.work.shop.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import org.mongodb.morphia.annotations.Embedded;

import java.util.Date;
import java.util.List;

/**
 * @author xym
 *
 * shoptype = 2 时通用model
 */
@Embedded
public class GeneralShop {

    private int shopId;  //商店ID

    private List<ShopItem> items = Lists.newArrayList();   //商品列表

    @JsonIgnore
    private Date lastReload;    //最近刷新时间    定时任务和手动刷新维护

    @JsonIgnore
    private int reloadCount;    //当日剩余刷新次数   每日十二点重置



    //获取某个商品项的剩余可购买个数
    public int getItemCount(int itemId){
        for (ShopItem item : items) {
            if (item.getItemId() == itemId)
                return item.getCount();
        }
        return 0;
    }

    //设置某个商品项的剩余可购买个数
    public void addItemCount(int itemId, int count){
        for (ShopItem item : items) {
            if (item.getItemId() == itemId)
                item.addCount(count);
        }
    }

    //添加商品
    public void addItem(int itemId, int count, int price){
        items.add(new ShopItem(itemId,count,price));
    }

    //清除商品项
    public void clearItems() {
        this.lastReload = null;
        items.clear();
    }



    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public List<ShopItem> getItems() {
        return items;
    }

    public void setItems(List<ShopItem> items) {
        this.items = items;
    }

    public Date getLastReload() {
        return lastReload;
    }

    public void setLastReload(Date lastReload) {
        this.lastReload = lastReload;
    }

    public int getReloadCount() {
        return reloadCount;
    }

    public void setReloadCount(int reloadCount) {
        this.reloadCount = reloadCount;
    }

    public synchronized int addReloadCount() {
        this.reloadCount++;
        return this.reloadCount;
    }

    /**
     * 商品
     */
    @Embedded
    static class ShopItem {

        private int itemId; //道具ID

        private int count;  //剩余可购买个数

        private int price;  //价格

        public ShopItem() {
        }

        public ShopItem(int itemId, int count, int price) {
            this.itemId = itemId;
            this.count = count;
            this.price = price;
        }

        public int getItemId() {
            return itemId;
        }

        public void setItemId(int itemId) {
            this.itemId = itemId;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public synchronized int addCount(int count) {
            return this.count += count;
        }
    }
}
