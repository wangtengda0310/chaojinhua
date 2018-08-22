package com.igame.work.shop;

import com.igame.work.shop.data.ShopData;
import com.igame.work.shop.data.ShopOutPutData;
import com.igame.work.shop.data.ShopRandomData;
import com.igame.work.shop.data.ShopRandomLvData;

public class ShopDataManager {
    /**
     * 商店基础信息
     */
    public static ShopData shopData;
    /**
     * shoptype = 2 时商品数据
     */
    public static ShopOutPutData shopOutPutData;
    /**
     * shoptype = 1 时商品数据
     */
    public static ShopRandomData shopRandomData;
    /**
     * 神秘商店等级数据
     */
    public static ShopRandomLvData shopRandomLvData;
}
