package com.igame.core;

import com.google.common.collect.Lists;
import com.igame.core.event.EventService;
import com.igame.core.event.EventType;
import com.igame.core.event.PlayerEventObserver;
import com.igame.core.log.ExceptionLog;
import com.igame.core.quartz.TimeListener;
import com.igame.work.MessageUtil;
import com.igame.work.PlayerEvents;
import com.igame.work.checkpoint.worldEvent.WorldEventDto;
import com.igame.work.friend.service.FriendService;
import com.igame.work.quest.QuestDataManager;
import com.igame.work.quest.dto.TaskDayInfo;
import com.igame.work.user.dto.Player;
import com.igame.work.user.service.VIPService;
import org.apache.commons.beanutils.BeanUtils;

import java.util.List;

public class SystemService extends EventService implements ISFSModule, TimeListener {
    @Override
    public void zero() {

        dto.getClock().clear();
        resetOnline();

    }

    @Override
    public void minute() {
        saveData();
    }

    private void resetOnline(){
        SessionManager.ins().getSessions().values()
                .forEach(player->resetOnce(player,true));
    }

    public SystemServiceDto dto;

    @Override
    protected PlayerEventObserver observeEvent() {
        return new PlayerEventObserver() {
            @Override
            public EventType interestedType() {
                return PlayerEvents.RESET_ONCE;
            }

            @Override
            public void observe(Player player, Object event) {
                resetOnce(player,false);//0点执行
            }
        };
    }

    private void resetOnce(Player player, boolean notify){
        if(dto.getClock().contains(player.getPlayerId())){
            return;
        }
        dto.getClock().add(player.getPlayerId());

        player.getTongAdd().setTongBuyCount(0);
        player.setPhBuyCount(0);
        player.setXinBuyCount(0);
        player.setGoldBuyCount(0);
        player.setOpenId(0);
        player.setOreCount(0);
        player.setWuReset(0);

//      player.getFateData().setFateLevel(1);
        player.getFateData().setTodayFateLevel(1);
        player.getFateData().setTodayBoxCount(0);
        player.getFateData().setGetReward(0);
        player.getFateData().setTempBoxCount(-1);
        player.getFateData().setTempSpecialCount(0);//更新临时特殊门刷数
        player.getFateData().setAddRate(0);//更新临时特殊门几率增长
        player.setAreaCount(0);

        for(WorldEventDto wt : player.getWordEvent().values()){
            wt.setCount(0);
            wt.setDtate(2);
        }
        List<TaskDayInfo> qList = Lists.newArrayList();
        for(TaskDayInfo td : player.getAchievement().values()){
            if(QuestDataManager.QuestData.getTemplate(td.getQuestId()) != null && QuestDataManager.QuestData.getTemplate(td.getQuestId()).getQuestType() == 1){
                if(td.getVars() > 0){
                    td.setVars(0);
                    td.setStatus(1);
                    td.setAction(2);
                    td.setDtate(2);
                    qList.add(td);
                }
            }
        }

        //重置商店刷新次数
        if (player.getShopInfo() != null){
            if (player.getShopInfo().getMysticalShop().getShopId() != 0)
                player.getShopInfo().getMysticalShop().setReloadCount(0);
            player.getShopInfo().getWujinShop().setReloadCount(0);
            player.getShopInfo().getDoujiShop().setReloadCount(0);
            player.getShopInfo().getQiyuanShop().setReloadCount(0);
            player.getShopInfo().getBuluoShop().setReloadCount(0);
        }

        //重置暴走时刻刷新次数
        player.setBallisticCount(0);

        //重置好友 体力领取次数与探索加速次数
        FriendService.ins().zero(player);

        //重置玩家会员特权
        VIPService.ins().zero(player);

        //重置玩家当日剩余挑战次数
        try {
            BeanUtils.copyProperties(player.getPlayerCount(),player.getPlayerTop());
        } catch (Exception e) {
            ExceptionLog.error("SystemServiceDto.ins().resetOnce.resetCount failed");
            e.printStackTrace();
        }

        if(notify){
            MessageUtil.notifyQuestChange(player, qList);
            MessageUtil.notifyWuResetChange(player);
            MessageUtil.notifyDeInfoChange(player);
            MessageUtil.notifyVipPrivilegesChange(player);
            MessageUtil.notifyFriendInfo(player);
        }
    }

    public void init(){
        dto = SystemServiceDAO.ins().loadData();
    }

    private void saveData(){
        SystemServiceDAO.ins().update(dto);
    }

    public void destroy() {
        saveData();
    }
}
