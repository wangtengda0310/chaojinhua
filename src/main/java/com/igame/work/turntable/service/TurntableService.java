package com.igame.work.turntable.service;

import com.igame.core.SessionManager;
import com.igame.core.di.Inject;
import com.igame.core.handler.RetVO;
import com.igame.core.quartz.TimeListener;
import com.igame.util.DateUtil;
import com.igame.util.GameMath;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.work.turntable.LuckTableDataManager;
import com.igame.work.turntable.dao.TurntableDAO;
import com.igame.work.turntable.data.LuckTableTemplate;
import com.igame.work.turntable.dto.Turntable;
import com.igame.work.user.dto.Player;

import java.util.*;

/**
 * @author xym
 *
 * 幸运大转盘服务
 */
public class TurntableService implements TimeListener {
    @Inject
    TurntableDAO dao;
    @Inject private SessionManager sessionManager;

    public Map<Long, Turntable> turntables = new HashMap<>();//幸运大转盘
    public Turntable getTurntable(Player player) {
        return turntables.computeIfAbsent(player.getPlayerId(), playerId -> initTurntable(player));
    }

    public void setTurntable(Player player, Turntable turntable) {
        turntables.put(player.getPlayerId(), turntable);
    }

    public Turntable transTurntableVo(Player player) {
        Turntable turntable = turntables.get(player.getPlayerId());
        if (turntable != null) {
            turntable.setRewardsStr();
        }

        return turntable;
    }

    /**
     * 初始化大转盘
     */
    public Turntable initTurntable(Player player){

        if (player.getPlayerLevel() < 15)
            return null;

        Turntable turntable = new Turntable();
        turntable.setPlayerId(player.getPlayerId());
        turntable.setDtate(1);

        setTurntable(player,turntable);

        //刷新道具
        reloadTurntable(player);

        //推送更新
        notifyTurntableChange(player);

        return turntable;
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
     */
    public void reloadTurntable(Player player){

        Turntable turntable = getTurntable(player);
        int playerLevel = player.getPlayerLevel();

        Map<Integer, String> rewards = turntable.getRewards();
        List<Integer> results = turntable.getResults();

        //清除之前的商品与结果
        rewards.clear();
        results.clear();

        //随机十二个位置的商品
        List<LuckTableTemplate> templates = LuckTableDataManager.luckTableData.getTemplate(playerLevel);
        for (LuckTableTemplate template : templates) {

            int site = template.getSite();
            String[] items = template.getItem().split(";");
            String[] showRates = template.getShowRate().split(";");

            List<Float> rates = new ArrayList<>();
            for (String showRate : showRates) {
                rates.add(Float.parseFloat(showRate));
            }

            int index = GameMath.getRate(rates);

            if (index != -1)
                rewards.put(site,items[index]);
        }

        //记录刷新时间
        turntable.setLastUpdate(new Date());

    }


    /**
     * 抽奖
     */
    public int lottery(Player player){

        Turntable turntable = getTurntable(player);
        int playerLevel = player.getPlayerLevel();

        Map<Integer, String> rewards = turntable.getRewards();
        List<Integer> results = turntable.getResults();

        //计算概率
        Float[] floats = new Float[rewards.size()];
        List<LuckTableTemplate> templates = LuckTableDataManager.luckTableData.getTemplate(playerLevel);
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

    /**
     * 推送玩家大转盘更新
     */
    private void notifyTurntableChange(Player player) {

        //推送
        RetVO vo = new RetVO();
        vo.addData("turntables", transTurntableVo(player));

        MessageUtil.sendMessageToPlayer(player, MProtrol.TURNTABLE_UPDATE, vo);
    }

    public void updatePlayer(Player player) {

        if(getTurntable(player).getDtate() == 1){
            dao.saveTurntable(getTurntable(player));
        }else if(getTurntable(player).getDtate() == 2){
            dao.updateTurntable(getTurntable(player));
        }
    }

    @Override
    public void minute180() {

        for(Player player : sessionManager.getSessions().values()){

            if (getTurntable(player) != null){
                //更新大转盘
                reloadTurntable(player);
                //推送更新
                notifyTurntableChange(player);
            }
        }

    }
}
