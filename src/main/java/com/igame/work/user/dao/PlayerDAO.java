package com.igame.work.user.dao;

import com.google.common.collect.Maps;
import com.igame.core.db.AbsDao;
import com.igame.core.di.Inject;
import com.igame.core.log.ExceptionLog;
import com.igame.work.serverList.ServerInfo;
import com.igame.work.serverList.ServerManager;
import com.igame.work.user.dto.Player;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.UpdateOperations;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class PlayerDAO extends AbsDao {
	@Inject private ServerManager serverManager;

    /**
     * 获取角色对象
     */
    public Player getPlayerByUserId(long userId){
		return getDatastore().find(Player.class, "userId", userId).get();
    }
    
    public Player getPlayerByPlayerId(long playerId){
		return getDatastore().find(Player.class, "playerId", playerId).get();
    }
    
    public Player getPlayerByPlayerNickName(String nickname){
		return getDatastore().find(Player.class, "nickname", nickname).get();
    }
    
    public Map<Integer,Player> getAllUser(long userId){
    	Map<Integer,Player> all = Maps.newHashMap();
    	
    	for(ServerInfo serverInfo :serverManager.servers){
			String serverId = serverInfo.getServerId();
    		Player p = null;
    		try{
    			p = getDatastore().find(Player.class, "userId", userId).get();
    		} catch (Exception e){
    			ExceptionLog.error("PlayerDAO.getAllUser ERROR,serverId:" + serverId+",userId:" + userId);
    		}
    		if(p != null){
    			all.put(Integer.parseInt(serverId), p);
    		}
    	}
    	
    	return all;
    }
    
    /**
     * 默认创建一个新玩家
     */
    public Player savePlayer(Player player){
		getDatastore().save(player);
    	return player;
    }
    
    public List<Player> getALLPlayer(){
    	return getDatastore().find(Player.class).asList();
    }
    
    /**
     * 更新玩家
     */
    public void updatePlayer(Player player,boolean loginOutTime){
    	Datastore ds = getDatastore();
    	UpdateOperations<Player> up = ds.createUpdateOperations(Player.class)
    		.set("nickname", player.getNickname())
    		.set("lastNickname", player.getLastNickname())
    		.set("playerLevel", player.getPlayerLevel())
    		.set("exp", player.getExp())
    		.set("vip", player.getVip())
    		.set("bagSpace", player.getBagSpace())
    		.set("physical", player.getPhysical())
    		.set("gold", player.getGold())
    		.set("diamond", player.getDiamond())
    		.set("teams", player.getTeams())
    		.set("battleSpace", player.getBattleSpace())
    		.set("lastCheckpointId", player.getLastCheckpointId())
    		.set("checkPoint", player.getCheckPoint() == null ? "" : player.getCheckPoint())
    		.set("timeResCheck", player.getTimeResCheck())
    		.set("xinMo", player.getXinMo())
    		.set("xinMoMinuts", player.getXinMoMinuts())
    		.set("tangSuo", player.getTangSuo())
    		.set("tongRes", player.getTongRes())
    		.set("tongAdd", player.getTongAdd())
    		.set("sao", player.getSao())
    		.set("xing", player.getXing())
    		.set("resMintues", player.getResMintues())
    		.set("phBuyCount", player.getPhBuyCount())
    		.set("xinBuyCount", player.getXinBuyCount())
    		.set("goldBuyCount", player.getGoldBuyCount())
    		.set("meetM", player.getMeetM())
    		.set("draw", player.getDraw())
    		.set("countMap", player.getCountMap())
    		.set("towerId", player.getTowerId())
    		.set("oreCount", player.getOreCount())
    		.set("wuMap", player.getWuMap())
    		.set("wuZheng", player.getWuZheng())
    		.set("wuGods", player.getWuGods())
    		.set("wuNai", player.getWuNai())
    		.set("wuEffect", player.getWuEffect())
    		.set("wuReset", player.getWuReset())
    		.set("wuScore", player.getWuScore())
    		.set("fateData", player.getFateData())
			.set("doujiScore", player.getDoujiScore())
			.set("qiyuanScore", player.getQiyuanScore())
			.set("buluoScore", player.getBuluoScore())
			.set("yuanzhengScore", player.getYuanzhengScore())
			.set("xuanshangScore", player.getXuanshangScore())
			.set("unlockHead", player.getUnlockHead())
			.set("unlockFrame", player.getUnlockFrame())
			.set("playerFrameId", player.getPlayerFrameId())
			.set("playerHeadId", player.getPlayerHeadId())
			.set("ballisticCount", player.getBallisticCount())
			.set("isBanStrangers", player.getIsBanStrangers())
			.set("lastMessageBoard", player.getLastMessageBoard())
			.set("curTeam", player.getCurTeam())
			.set("totalMoney", player.getTotalMoney())
			.set("vipPrivileges", player.getVipPrivileges())
			.set("round", player.getRound())
			.set("areaCount", player.getAreaCount())
			.set("playerCount", player.getPlayerCount())
			.set("playerTop", player.getPlayerTop())
			.set("fightValue", player.getFightValue())
			.set("sign", player.getSign())
    		;
    	if(loginOutTime){
    		up.set("loginoutTime", new Date())
    			.set("loginTime", player.getLoginTime());
    	}
    	if(player.getTonghua() != null){
    		up.set("tonghua", player.getTonghua());
    	}
    	
    	ds.update(player, up);
    }
    
    

    public void updatePlayerTest(Player player){
    	Datastore ds = getDatastore();
    	UpdateOperations<Player> up = ds.createUpdateOperations(Player.class);
    	up.set("wuMap", player.getWuMap());
    	ds.update(player, up);
    }
    
    

}
