package com.igame.work.turntable.service;

import com.igame.core.MessageUtil;
import com.igame.core.data.DataManager;
import com.igame.core.data.template.LuckTableTemplate;
import com.igame.util.DateUtil;
import com.igame.util.GameMath;
import com.igame.work.turntable.dto.Turntable;
import com.igame.work.user.dto.Player;

import java.util.*;

/**
 * @author xym
 *
 * 幸运大转盘服务
 */
public class TurntableService {


    private static final TurntableService domain = new TurntableService();

    public static final TurntableService ins() {
        return domain;
    }

    /**
     * 初始化大转盘
     * @param player
     */
    public void initTurntable(Player player){

        if (player.getPlayerLevel() < 15)
            return;

        Turntable turntable = new Turntable();
        turntable.setPlayerId(player.getPlayerId());
        turntable.setDtate(1);

        player.setTurntable(turntable);

        //刷新道具
        reloadTurntable(player);

        //推送更新
        MessageUtil.notiyTurntableChange(player);
    }

    /**
     * 判断是否需要刷新
     * @param lastReload 上次刷新时间
     * @return true = 需要刷新
     */
    public boolean needRealod(Date lastReload){

        if (lastReload == null)
            return true;

        int[] resestTimes = new int[]{0,3,6,9,12,15,18,21,24};

        int curHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int tempHour = 0;
        for (int resestTime : resestTimes) {
            if (tempHour <= resestTime && resestTime <= curHour)
                tempHour = resestTime;
        }

        return DateUtil.isNeedFushDate(lastReload, tempHour);
    }


    /**
     * 刷新大转盘
     * @param player
     */
    public void reloadTurntable(Player player){

        Turntable turntable = player.getTurntable();
        int playerLevel = player.getPlayerLevel();

        Map<Integer, String> rewards = turntable.getRewards();
        List<Integer> results = turntable.getResults();

        //清除之前的商品与结果
        rewards.clear();
        results.clear();

        //随机十二个位置的商品
        List<LuckTableTemplate> templates = DataManager.luckTableData.getTemplate(playerLevel);
        for (LuckTableTemplate template : templates) {

            int site = template.getSite();
            String[] items = template.getItem().split(";");
            String[] showRates = template.getShowRate().split(";");

            List<Float> rates = new ArrayList<>();
            for (String showRate : showRates) {
                rates.add(Float.parseFloat(showRate));
            }

            Integer index = GameMath.getRate(rates);

            if (index != -1)
                rewards.put(site,items[index]);
        }

        //记录刷新时间
        turntable.setLastUpdate(new Date());

    }


    /**
     * 抽奖
     * @param player
     */
    public int lottery(Player player){

        Turntable turntable = player.getTurntable();
        int playerLevel = player.getPlayerLevel();

        Map<Integer, String> rewards = turntable.getRewards();
        List<Integer> results = turntable.getResults();

        //计算概率
        Float[] floats = new Float[rewards.size()];
        List<LuckTableTemplate> templates = DataManager.luckTableData.getTemplate(playerLevel);
        for (LuckTableTemplate template : templates) {

            int site = template.getSite();
            float getRate = template.getGetRate();
            Float upRate = template.getUpRate();

            if (upRate != null){
                getRate += upRate * results.size();
            }

            if (template.getTableType() == 1 && results.contains(site)){
                getRate = 0;
            }

            floats[site-1] = getRate;
        }

        //随机抽奖
        int index = GameMath.getRate(Arrays.asList(floats));

        if (index != -1){

            int site = index+1;

            results.add(site);
            return site;
        }else {
            return -1;
        }

    }

}
