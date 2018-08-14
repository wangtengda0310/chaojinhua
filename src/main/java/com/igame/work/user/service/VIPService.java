package com.igame.work.user.service;

import com.igame.core.data.DataManager;
import com.igame.core.data.template.VipLevelTemplate;
import com.igame.core.log.ExceptionLog;
import com.igame.work.user.dto.Player;

import java.util.Map;

import static com.igame.work.user.VIPConstants.*;

/**
 * @author xym
 *
 * 会员服务
 */
public class VIPService {

    private static final VIPService domain = new VIPService();

    public static final VIPService ins() {

        return domain;
    }

    /**
     * 校验是否可升级
     */
    private boolean isUp(Player player){

        int vip = player.getVip();
        double totalMoney = player.getTotalMoney();

        VipLevelTemplate template = DataManager.vipLevelData.getTemplate(vip);
        double vipExp = template.getVipExp();

        return vipExp != -1 && totalMoney >= vipExp;
    }

    /**
     * 增加VIP等级
     */
    public boolean addVipLv(Player player){

        if (!isUp(player))
            return false;

        while (isUp(player)){
            int vip = player.getVip();

            resetPrivileges(vip,++vip,player.getVipPrivileges());

            player.setVip(vip);
        }
        return true;
    }

    /**
     * 重新计算vip特权
     */
    private Map<String,Object> resetPrivileges(int curVipLv,int addVipLv,Map<String,Object> vipPrivileges){

        if (curVipLv >= addVipLv || curVipLv > 11 || addVipLv > 12){
            ExceptionLog.error("reset privileges error----- : curVipLv - "+curVipLv+"  addVipLv - "+addVipLv);
            return vipPrivileges;
        }

        if (curVipLv == 0 || vipPrivileges.size() <= 0){
            initPrivileges(vipPrivileges);
        }

        curVipLv++;
        for (int i = curVipLv; i <= addVipLv; i++) {
            Map<String, Integer> vipValue = VIPConfig.ins().getVipValue(i);
            for (Map.Entry<String, Integer> entry : vipValue.entrySet()) {
                String key = entry.getKey();
                Integer value = entry.getValue();

                int newValue = (int) vipPrivileges.get(key) + value;
                vipPrivileges.put(key,newValue);
            }
        }
        return vipPrivileges;
    }

    /**
     * 重置玩家vip特权
     */
    public void zero(Player player){

        resetPrivileges(0,player.getVip(),player.getVipPrivileges());
    }

    /**
     * 初始化VIP特权
     */
    public void initPrivileges(Map<String, Object> vipPrivileges){

        vipPrivileges.put( KEY_SPEED_TOP , DEFVALUE_SPEED_TOP);
        vipPrivileges.put( KEY_FRIEND_TOP , DEFVALUE_FRIEND_TOP);
        vipPrivileges.put( KEY_PHYSICAL_RECEIVE_TOP , DEFVALUE_PHYSICAL_RECEIVE_TOP);
        vipPrivileges.put( KEY_EXPLORE_HELP_TOP , DEFVALUE_EXPLORE_HELP_TOP);
        vipPrivileges.put( KEY_WORLD_EVENT_BUY , DEFVALUE_WORLD_EVENT_BUY);
        vipPrivileges.put( KEY_PHYSICAL_BUY , DEFVALUE_PHYSICAL_BUY);
        vipPrivileges.put( KEY_GOLD_BUY , DEFVALUE_GOLD_BUY);
        vipPrivileges.put( KEY_GATE_BUY , DEFVALUE_GATE_BUY);
        vipPrivileges.put( KEY_ASSIMILATION_TOP , DEFVALUE_ASSIMILATION_TOP);
        vipPrivileges.put( KEY_ASSIMILATION_BUY , DEFVALUE_ASSIMILATION_BUY);
        vipPrivileges.put( KEY_EXPLORE_TOP , DEFVALUE_EXPLORE_TOP);
        vipPrivileges.put( KEY_RESOURCE_POINT_TOP , DEFVALUE_RESOURCE_POINT_TOP);
        vipPrivileges.put( KEY_ORIGIN_TOP , DEFVALUE_ORIGIN_TOP);
        vipPrivileges.put( KEY_ARENA_BUY , DEFVALUE_ARENA_BUY);
        vipPrivileges.put( KEY_BALLISTIC_TOP , DEFVALUE_BALLISTIC_TOP);
        vipPrivileges.put( KEY_BALLISTIC_BUY , DEFVALUE_BALLISTIC_BUY);
        vipPrivileges.put( KEY_NUCLEUS_TOP , DEFVALUE_NUCLEUS_TOP);
        vipPrivileges.put( KEY_NUCLEUS_BUY , DEFVALUE_NUCLEUS_BUY);
        vipPrivileges.put( KEY_BOARSE_BUY , DEFVALUE_BOARSE_BUY);
        vipPrivileges.put( KEY_FIRST_PACK , DEFVALUE_FIRST_PACK);
        vipPrivileges.put( KEY_DAY_PACK , DEFVALUE_DAY_PACK);

    }
}
