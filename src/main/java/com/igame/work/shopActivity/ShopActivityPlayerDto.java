package com.igame.work.shopActivity;

import java.util.HashMap;
import java.util.Map;

class ShopActivityPlayerDto {
    long openMillis;
    /**时间戳:金额*/
    Map<Long,Long> goldActivityInfo = new HashMap<>();
    /**itemId:count*/
    Map<Integer, Integer> itemActivityInfo = new HashMap<>();

}
