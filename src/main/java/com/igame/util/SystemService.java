package com.igame.util;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.work.checkpoint.GuanQiaDataManager;
import com.igame.work.checkpoint.data.FatedataTemplate;
import com.igame.core.db.BasicDto;
import com.igame.work.system.SystemServiceDAO;
import com.igame.core.log.ExceptionLog;
import com.igame.work.checkpoint.dto.WordEventDto;
import com.igame.work.checkpoint.service.CheckPointService;
import com.igame.work.friend.service.FriendService;
import com.igame.work.monster.dto.Monster;
import com.igame.work.quest.QuestDataManager;
import com.igame.work.quest.dto.TaskDayInfo;
import com.igame.work.user.dto.Player;
import com.igame.work.user.service.VIPService;
import org.apache.commons.beanutils.BeanUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Transient;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 
 * @author Marcus.Z
 *
 */
@Entity(value = "SystemService", noClassnameStored = true)
public class SystemService extends BasicDto {
	
    private Set<Long> clock = Sets.newHashSet();//0clock
    
    
    @Transient
    private Map<Integer,List<Map<Long,Monster>>> fateMap = Maps.newHashMap();//命运之门怪物配置
    
    public void resetOnline(){
    	for(Player player : SessionManager.ins().getSessions().values()){
            resetOnce(player,true);
        }
    }
    
    public void resetOnce(Player player,boolean notiy){
        if(clock.contains(player.getPlayerId())){
            return;
        }
        clock.add(player.getPlayerId());
        
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
		
        for(WordEventDto wt : player.getWordEvent().values()){
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
			ExceptionLog.error("SystemService.ins().resetOnce.resetCount failed");
			e.printStackTrace();
		}

		if(notiy){
			MessageUtil.notiyQuestChange(player, qList);
			MessageUtil.notiyWuResetChange(player);
			MessageUtil.notiyDeInfoChange(player);
			MessageUtil.notiyVipPrivilegesChange(player);
			MessageUtil.notiyFriendInfo(player);
		}
	}

	public Set<Long> getClock() {
		return clock;
	}

	public void setClock(Set<Long> clock) {
		this.clock = clock;
	}
	
	
	public Map<Integer, List<Map<Long, Monster>>> getFateMap() {
		return fateMap;
	}

	public void setFateMap(Map<Integer, List<Map<Long, Monster>>> fateMap) {
		this.fateMap = fateMap;
	}

	public void loadData(){
		refFateMap();
		this.clock = SystemServiceDAO.ins().loadData();
	}
	
	public void refFateMap(){
		
		this.fateMap.clear();
		for(FatedataTemplate ft : GuanQiaDataManager.FateData.getAll()){
			List<Map<Long,Monster>> ls = Lists.newArrayList();
			for(int i = 1;i <= 3;i++){
				ls.add(CheckPointService.getNormalFateMonster(ft.getFloorNum()));
			}
			this.fateMap.put(ft.getFloorNum(), ls);
		}
	}
	
	public void saveData(){
		SystemServiceDAO.ins().update(this);
	}
    
	@Transient
    private static final SystemService domain = new SystemService();
	
    public static final SystemService ins(){
    	domain.set_id(new ObjectId("5ad950a8f9745d21386f1a18"));
        return domain;
    }
    

}
