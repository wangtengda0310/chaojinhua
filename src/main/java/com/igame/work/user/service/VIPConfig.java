package com.igame.work.user.service;

import java.util.HashMap;
import java.util.Map;

import static com.igame.work.user.VIPConstants.*;

/**
 * @author xym
 *
 * 会员配置
 */
public class VIPConfig {

    public VIPConfig() {
        initVipValue();
    }

    //值
    private Map<Integer,Map<String,Integer>> vipValue = new HashMap<>();

    public Map<String,Integer> getVipValue(Integer vipLv){
        return vipValue.get(vipLv);
    }

    private void initVipValue() {

        //立即开通双倍速，兑换体力与金币次数+1，好友上限与领取上限+2
        HashMap<String, Integer> vip1 = new HashMap<>();
        vip1.put(KEY_SPEED_TOP,1);
        vip1.put(KEY_PHYSICAL_BUY,1);
        vip1.put(KEY_GOLD_BUY,1);
        vip1.put(KEY_FRIEND_TOP,2);
        vip1.put(KEY_PHYSICAL_RECEIVE_TOP,2);
        vipValue.put(1,vip1);

        //兑换体力与金币次数+1，好友上限与领取上限+2，荒野探索的次数上限为8，酒吧与餐厅的累计时间上限为4小时
        HashMap<String, Integer> vip2 = new HashMap<>();
        vip2.put(KEY_PHYSICAL_BUY,1);
        vip2.put(KEY_GOLD_BUY,1);
        vip2.put(KEY_FRIEND_TOP,2);
        vip2.put(KEY_PHYSICAL_RECEIVE_TOP,2);
        vip2.put(KEY_ASSIMILATION_TOP,3);
        vip2.put(KEY_RESOURCE_POINT_TOP,2);
        vipValue.put(2,vip2);

        //兑换体力/金币次数+1，好友上限与领取上限+2，酒吧/餐厅的累计时间上限为6小时
        HashMap<String, Integer> vip3 = new HashMap<>();
        vip3.put(KEY_PHYSICAL_BUY,1);
        vip3.put(KEY_GOLD_BUY,1);
        vip3.put(KEY_FRIEND_TOP,2);
        vip3.put(KEY_PHYSICAL_RECEIVE_TOP,2);
        vip3.put(KEY_RESOURCE_POINT_TOP,2);
        vipValue.put(3,vip3);

        //兑换体力/金币次数+1，好友上限与领取上限+2，荒野探索的次数上限为10，酒吧/餐厅的累计时间上限为8小时，开放幸运转盘十连抽，
        HashMap<String, Integer> vip4 = new HashMap<>();
        vip4.put(KEY_PHYSICAL_BUY,1);
        vip4.put(KEY_GOLD_BUY,1);
        vip4.put(KEY_FRIEND_TOP,2);
        vip4.put(KEY_PHYSICAL_RECEIVE_TOP,2);
        vip4.put(KEY_ASSIMILATION_TOP,2);
        vip4.put(KEY_RESOURCE_POINT_TOP,2);
        vipValue.put(4,vip4);

        //兑换体力/金币次数+1，好友上限与领取上限+2，重大事件的购买次数+1，酒吧/餐厅的累计时间上限为10小时
        HashMap<String, Integer> vip5 = new HashMap<>();
        vip5.put(KEY_PHYSICAL_BUY,1);
        vip5.put(KEY_GOLD_BUY,1);
        vip5.put(KEY_FRIEND_TOP,2);
        vip5.put(KEY_PHYSICAL_RECEIVE_TOP,2);
        vip5.put(KEY_WORLD_EVENT_BUY,1);
        vip5.put(KEY_RESOURCE_POINT_TOP,2);
        vipValue.put(5,vip5);

        //兑换体力/金币次数+1，好友上限与领取上限+2，荒野探索购买次数+1，酒吧/餐厅的累计时间上限为12小时，街区挑战的次数上限变为10
        HashMap<String, Integer> vip6 = new HashMap<>();
        vip6.put(KEY_PHYSICAL_BUY,1);
        vip6.put(KEY_GOLD_BUY,1);
        vip6.put(KEY_FRIEND_TOP,2);
        vip6.put(KEY_PHYSICAL_RECEIVE_TOP,2);
        vip6.put(KEY_ASSIMILATION_BUY,1);
        vip6.put(KEY_RESOURCE_POINT_TOP,2);
        vip6.put(KEY_NUCLEUS_TOP,5);
        vipValue.put(6,vip6);

        //兑换体力/金币次数+1，好友上限与领取上限+2，街区挑战的购买次数+1，
        HashMap<String, Integer> vip7 = new HashMap<>();
        vip7.put(KEY_PHYSICAL_BUY,1);
        vip7.put(KEY_GOLD_BUY,1);
        vip7.put(KEY_FRIEND_TOP,2);
        vip7.put(KEY_PHYSICAL_RECEIVE_TOP,2);
        vip7.put(KEY_NUCLEUS_BUY,1);
        vipValue.put(7,vip7);

        //兑换体力/金币次数+1，好友上限与领取上限+2，狂野赛道购买次数+1
        HashMap<String, Integer> vip8 = new HashMap<>();
        vip8.put(KEY_PHYSICAL_BUY,1);
        vip8.put(KEY_GOLD_BUY,1);
        vip8.put(KEY_FRIEND_TOP,2);
        vip8.put(KEY_PHYSICAL_RECEIVE_TOP,2);
        vip8.put(KEY_BOARSE_BUY,1);
        vipValue.put(8,vip8);

        //兑换体力/金币次数+1，好友上限与领取上限+2，斗技场的挑战购买次数+1
        HashMap<String, Integer> vip9 = new HashMap<>();
        vip9.put(KEY_PHYSICAL_BUY,1);
        vip9.put(KEY_GOLD_BUY,1);
        vip9.put(KEY_FRIEND_TOP,2);
        vip9.put(KEY_PHYSICAL_RECEIVE_TOP,2);
        vip9.put(KEY_ARENA_BUY,1);
        vipValue.put(9,vip9);

        //兑换体力/金币次数+1，好友上限与领取上限+2，命运之路的购买次数+1，，荒野探索购买次数+1，
        HashMap<String, Integer> vip10 = new HashMap<>();
        vip10.put(KEY_PHYSICAL_BUY,1);
        vip10.put(KEY_GOLD_BUY,1);
        vip10.put(KEY_FRIEND_TOP,2);
        vip10.put(KEY_PHYSICAL_RECEIVE_TOP,2);
        vip10.put(KEY_GATE_BUY,1);
        vip10.put(KEY_ASSIMILATION_BUY,1);
        vipValue.put(10,vip10);

        //暴走街区的购买次数+1，街区挑战的次数上限变为15
        HashMap<String, Integer> vip11 = new HashMap<>();
        vip11.put(KEY_BALLISTIC_BUY,1);
        vip11.put(KEY_NUCLEUS_TOP,5);
        vipValue.put(11,vip11);

        //重大事件的购买次数+1，，荒野探索购买次数+1
        HashMap<String, Integer> vip12 = new HashMap<>();
        vip12.put(KEY_WORLD_EVENT_BUY,1);
        vip12.put(KEY_ASSIMILATION_BUY,1);
        vipValue.put(12,vip12);
    }
}
