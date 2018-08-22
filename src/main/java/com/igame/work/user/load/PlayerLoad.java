package com.igame.work.user.load;

import com.google.common.collect.Maps;
import com.igame.core.log.DebugLog;
import com.igame.util.MyUtil;
import com.igame.util.PlayerTimeResCalUtil;
import com.igame.util.SystemService;
import com.igame.work.activity.ActivityService;
import com.igame.work.chat.dao.PlayerMessageDAO;
import com.igame.work.chat.service.MessageBoardService;
import com.igame.work.checkpoint.GuanQiaDataManager;
import com.igame.work.checkpoint.dao.WordEventDAO;
import com.igame.work.checkpoint.data.CheckPointTemplate;
import com.igame.work.checkpoint.service.CheckPointService;
import com.igame.work.fight.FightDataManager;
import com.igame.work.fight.data.GodsdataTemplate;
import com.igame.work.fight.service.ComputeFightService;
import com.igame.work.friend.dao.FriendDAO;
import com.igame.work.friend.service.FriendService;
import com.igame.work.item.dao.ItemDAO;
import com.igame.work.item.service.ItemService;
import com.igame.work.monster.dao.GodsDAO;
import com.igame.work.monster.dao.MonsterDAO;
import com.igame.work.monster.dto.Gods;
import com.igame.work.monster.dto.Monster;
import com.igame.work.monster.service.MonsterService;
import com.igame.work.quest.dao.QuestDAO;
import com.igame.work.quest.service.QuestService;
import com.igame.work.shop.dao.ShopDAO;
import com.igame.work.shop.service.ShopService;
import com.igame.work.turntable.service.TurntableService;
import com.igame.work.user.dao.MailDAO;
import com.igame.work.user.dao.PlayerDAO;
import com.igame.work.user.dto.Player;
import com.igame.work.user.dto.PlayerTop;
import com.igame.work.user.dto.Team;
import com.igame.work.user.service.HeadService;
import com.igame.work.user.service.MailService;
import com.igame.work.user.service.PlayerCacheService;
import com.igame.work.user.service.VIPService;
import org.apache.commons.beanutils.BeanUtils;

import java.util.Date;

/**
 * 
 * @author Marcus.Z
 *
 */
public class PlayerLoad {
	
    private static final PlayerLoad domain = new PlayerLoad();

    public static PlayerLoad ins() {
        return domain;
    }
    
    public Player loadPlayer(Player player,int serverId,long userId){
		ItemService.ins().loadPlayer(player, serverId);

    	player.setGods(GodsDAO.ins().getByPlayer(serverId, player.getPlayerId()));

    	MonsterService.loadPlayer(player,serverId);

    	player.setWordEvent(WordEventDAO.ins().getByPlayer(serverId, player.getPlayerId()));

		MailService.ins().loadPlayer(player, serverId);
		ShopService.ins().loadPlayer(player, serverId);
		FriendService.ins().loadPlayer(player);

		player.setPrivateMessages(PlayerMessageDAO.ins().getMessageByPlayerId(player.getSeverId(),player.getPlayerId()).getMessages());
		player.initMessageBoard();

    	QuestService.loadPlayer(player, serverId);
		ActivityService.loadPlayer(player);

		PlayerCacheService.ins().remove(player);
    	return null;
    }
    
    /**
     * 玩家登录成功后的操作
     */
    public void afterPlayerLogin(Player player) throws Exception {

    	if (player.getPrivateMessages().size() <= 0)
			VIPService.ins().initPrivileges(player.getVipPrivileges());

		//初始化商店或者刷新
		if (player.getShopInfo() == null)
			ShopService.ins().initShop(player);
		else
			ShopService.ins().reloadAll(player.getShopInfo());

		//初始化头像和头像框
		if (player.getUnlockHead().size() == 0)
			HeadService.ins().initHead(player);
		if (player.getUnlockFrame().size() == 0)
			HeadService.ins().initFrame(player);


		//初始化角色挑战次数上限与剩余挑战次数
		if (player.getPlayerTop() == null){
			player.setPlayerTop(new PlayerTop().init());
			BeanUtils.copyProperties(player.getPlayerCount(),player.getPlayerTop());
		}

    	if(player.getTimeResCheck() == null){
    		player.setTimeResCheck(Maps.newHashMap());
    	}
    	if(player.getPlayerLevel() >= 30 && player.getTonghua() == null){
    		player.setTonghua(PlayerService.getRandomTongHuaDto());
    		player.getTonghua().setStartRefTime(System.currentTimeMillis());
    	}
    	for(int i = 3;i<=7;i++){
    		if( i == 5){
    			continue;
    		}
			player.getResMintues().putIfAbsent(i, 0);
    	}
    	for(Integer type : FightDataManager.GodsData.getSets()){
    		GodsdataTemplate gt = FightDataManager.GodsData.getTemplate(type+"_0");
    		if(gt != null && player.getPlayerLevel() >= gt.getUnlockLv() && player.getGods().get(type) == null){
    			Gods gods = new Gods(player.getPlayerId(), type, 0, 1);
    			player.getGods().put(gods.getGodsType(), gods);
    		}
    	}
    	player.setLoginTime(new Date());
    	
    	if(!MyUtil.isNullOrEmpty(player.getCheckPoint())){
    		for(String cc :player.getCheckPoint().split(",")){//已过关卡有，但是资源关卡时间计数器没有添加
    			Integer cid = Integer.parseInt(cc);
    			CheckPointTemplate ct = GuanQiaDataManager.CheckPointData.getTemplate(cid);
    			if(ct != null && ct.getChapterType() == 2 && !MyUtil.isNullOrEmpty(ct.getDropPoint())
    					&& !player.getTimeResCheck().containsKey(cid)){
    				player.getTimeResCheck().put(cid, ct.getMaxTime() * 60);
    			}
    		}
			for(Integer id : player.getTimeResCheck().keySet()){//已过关卡里没有的，资源关卡时间计数器有的删除
				if(!MyUtil.hasCheckPoint(player.getCheckPoint(), String.valueOf(id))){
					player.getTimeResCheck().remove(id);
				}
			}
    	}

    	SystemService.ins().resetOnce(player,false);//0点执行
    	PlayerTimeResCalUtil.ins().calPlayerTimeRes(player);//计算金币关卡的获得数量 和心魔 以及各货币资源的定时更新
    	player.calLeftTime();//算每个心魔剩余时间    	
    	MonsterService.reCalMonsterExtPre(player,true);//计算图鉴增加属性
		ComputeFightService.ins().computePlayerFight(player);
    	//player.reCalFightValue();//计算战斗力
    	PlayerService.checkDrawData(player, false);//检测造物台数据
    	QuestService.checkPlayerQuest(player);//检测玩家任务
    	if(player.getWuMap().isEmpty()){
    		CheckPointService.refEndlessRef(player);
    	}
    	
		player.getFateData().setTodayFateLevel(1);
		player.getFateData().setTodayBoxCount(0);
		player.getFateData().setTempBoxCount(-1);
		player.getFateData().setTempSpecialCount(0);
		player.getFateData().setAddRate(0);
		
		if(player.getTeams().get(6) == null){
			long id1 = -1;
			long id2 = -1;
			int count = 0;
			for(Monster mm : player.getMonsters().values()){
				if(count == 0){
					id1 = mm.getObjectId();
				}else{
					id2 = mm.getObjectId();
				}		
				count++;
				if(count >= 2){
					break;
				}
			}
			player.getTeams().put(6,new Team(6,"竞技场防守阵容",id1,id2));
		}

		if (player.getTurntable() != null && TurntableService.ins().needRealod(player.getTurntable().getLastUpdate()))
			TurntableService.ins().reloadTurntable(player);

    }
    
    /**
     * 保存角色相关数据
     */
    public void savePlayer(Player player,boolean loginOutTime){
    	synchronized(player.dbLock){
			DebugLog.debug("palyer leave save---- serverId:" + player.getSeverId() + "," +"userId:" + player.getUserId() + "," +"playerId:" + player.getPlayerId() + "," +"playerName:" + player.getNickname());
			PlayerDAO.ins().updatePlayer(player,loginOutTime);
        	MonsterDAO.ins().updatePlayer(player);
        	ItemDAO.ins().updatePlayer(player);
        	WordEventDAO.ins().updatePlayer(player);
        	GodsDAO.ins().updatePlayer(player);
        	MailDAO.ins().updatePlayer(player);
        	QuestDAO.ins().updatePlayer(player);
        	ShopDAO.ins().updatePlayer(player);
        	FriendDAO.ins().updatePlayer(player);
        	PlayerMessageDAO.ins().updatePlayer(player);
			MessageBoardService.ins().saveMessageBoard(player);
			PlayerCacheService.ins().cachePlayer(player);

			PlayerCacheService.ins().cachePlayer(player);
    	}
    }

}
