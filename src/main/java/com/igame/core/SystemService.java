package com.igame.core;

import com.igame.core.di.Inject;
import com.igame.core.event.EventService;
import com.igame.core.event.EventType;
import com.igame.core.event.PlayerEventObserver;
import com.igame.core.log.ExceptionLog;
import com.igame.core.quartz.TimeListener;
import com.igame.work.MessageUtil;
import com.igame.work.PlayerEvents;
import com.igame.work.checkpoint.worldEvent.WorldEventDto;
import com.igame.work.friend.service.FriendService;
import com.igame.work.quest.service.QuestService;
import com.igame.work.shop.service.ShopService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.service.VIPService;
import org.apache.commons.beanutils.BeanUtils;

public class SystemService extends EventService implements ISFSModule, TimeListener {
    @Inject private FriendService friendService;
    @Inject private VIPService vipService;
    @Inject private SessionManager sessionManager;
    @Inject private SystemServiceDAO systemServiceDAO;
    @Inject private ShopService shopService;
    @Inject private QuestService questService;

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
        sessionManager.getSessions().values()
                .forEach(player->resetOnce(player,true));
    }

    public SystemServiceDto dto;

    @Override
    protected PlayerEventObserver playerObserver() {
        return new PlayerEventObserver() {
            @Override
            public EventType interestedType() {
                return PlayerEvents.RESET_ONCE; // 换成监听上线事件？
            }

            @Override
            public void observe(Player player, EventType eventType, Object event) {
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

        shopService.resetShopInfo(player);

        //重置暴走时刻刷新次数
        player.setBallisticCount(0);

        //重置好友 体力领取次数与探索加速次数
        friendService.zero(player);

        //重置玩家会员特权
        vipService.zero(player);

        //重置玩家当日剩余挑战次数
        try {
            BeanUtils.copyProperties(player.getPlayerCount(),player.getPlayerTop());
        } catch (Exception e) {
            ExceptionLog.error("systemServiceDto.resetOnce.resetCount failed");
            e.printStackTrace();
        }

        if(notify){
            MessageUtil.notifyQuestChange(player, questService.reset(player));
            MessageUtil.notifyWuResetChange(player);
            MessageUtil.notifyDeInfoChange(player);
            MessageUtil.notifyVipPrivilegesChange(player);
            MessageUtil.notifyFriendInfo(player);
        }
    }

    @Override
    public void init(){
        dto = systemServiceDAO.loadData();
        if (dto == null) {
            dto = new SystemServiceDto();
        }
    }

    private void saveData(){
        systemServiceDAO.update(dto);
    }

    public void destroy() {
        saveData();
    }
}
