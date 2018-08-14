package com.igame.work.user.service;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.igame.core.SessionManager;
import com.igame.core.db.DBManager;
import com.igame.core.log.ExceptionLog;
import com.igame.util.LoginOutReason;
import com.igame.work.fight.service.PVPFightService;
import com.igame.work.friend.dao.FriendDAO;
import com.igame.work.friend.dto.FriendInfo;
import com.igame.work.user.dao.PlayerDAO;
import com.igame.work.user.dto.Player;
import com.igame.work.user.dto.PlayerCacheDto;
import com.igame.work.user.load.PlayerLoad;

/**
 * 
 * @author Marcus.Z
 *
 */
public class PlayerCacheService {
	
    private static final PlayerCacheService domain = new PlayerCacheService();

    public static final PlayerCacheService ins() {
        return domain;
    }
    
    private Map<Long,PlayerCacheDto> pcd = Maps.newHashMap();
    
    private Map<String,PlayerCacheDto> pcn = Maps.newHashMap();


    /**
     * 更新玩家
     * @param player
     */
    public void updatePlayer(Player player){
    	
    	PlayerCacheDto pdo = pcd.get(player.getPlayerId());
    	if(pdo == null){
    		pdo = new PlayerCacheDto();
    	}
    	pdo.updatePlayer(player);
    	pcd.put(player.getPlayerId(), pdo);
    	pcn.put(player.getNickname(), pdo);
    	
    }
    
    /**
     * 根据玩家ID获取
     * @param serverId
     * @param playerId
     * @return
     */
    public PlayerCacheDto getPlayerById(int serverId,long playerId){

		PlayerCacheDto pdo;
		Player player  = SessionManager.ins().getSessionByPlayerId(playerId);
		if(player != null){	//如果玩家在线
			pdo = new PlayerCacheDto();
			pdo.updatePlayer(player);
		}else {	//如果玩家不在线
			pdo = pcd.get(playerId);
		}

		//以防外一
		if(pdo == null){
			player = PlayerDAO.ins().getPlayerByPlayerId(serverId, playerId);
			if(player != null){
				pdo = new PlayerCacheDto();
				pdo.updatePlayer(player);
				pcd.put(player.getPlayerId(), pdo);
				pcn.put(player.getNickname(), pdo);
			}
		}
    	return pdo;
    }
    
    /**
     * 根据玩家昵称
     * @param serverId
     * @param nickName
     * @return
     */
    public PlayerCacheDto getPlayerByNickName(int serverId,String nickName){

		PlayerCacheDto pdo;
		Player player  = SessionManager.ins().getSession(nickName);
		if(player != null){	//如果玩家在线
			pdo = new PlayerCacheDto();
			pdo.updatePlayer(player);
		}else {	//如果玩家不在线
			pdo = pcn.get(nickName);
		}

		//以防外一
		if(pdo == null){
			player = PlayerDAO.ins().getPlayerByPlayerNickName(serverId, nickName);
			if(player != null){
				pdo = new PlayerCacheDto();
				pdo.updatePlayer(player);
				pcd.put(player.getPlayerId(), pdo);
				pcn.put(player.getNickname(), pdo);
			}
		}
    	return pdo;
    }


	/**
	 * 加载所有玩家
	 */
	public void loadData() {

		String DBName = DBManager.getInstance().p.getProperty("DBName");
		String[] DBNames = DBName.split(",");
		for(String db : DBNames){
			int serverId=Integer.parseInt(db.substring(5));

			List<Player> allPlayer = PlayerDAO.ins().getALLPlayer(serverId);
			for (Player player : allPlayer) {

                //加载好友
                player.setFriends(FriendDAO.ins().getFriendInfoByPlayerId(serverId, player.getPlayerId()));

                updatePlayer(player);

            }
		}

	}

	/**
	 * 获取
	 * @param severId 服务器ID
	 */
	public List<PlayerCacheDto> getPlayers(int severId) {

		List<PlayerCacheDto> playerCacheDtos = new ArrayList<>();

		for (PlayerCacheDto cacheDto : pcd.values()) {
			if (cacheDto.getSeverId() == severId)
				playerCacheDtos.add(cacheDto);
		}

		return playerCacheDtos;

	}
	
	public static void checkPlayer(){
    	for(Player player : SessionManager.ins().getSessions().values()){
			if(player.getHeartTime()>0 && System.currentTimeMillis() - player.getHeartTime() > 5 * 60 * 1000){
				try{
					PlayerLoad.ins().savePlayer(player,true);
				}catch(Exception e){
					ExceptionLog.error("palyer leave save error----- :",e);
				}
				
				if(PVPFightService.ins().palyers.containsKey(player.getPlayerId())){
					PVPFightService.ins().chancelFight(player);
				}
				if(PVPFightService.ins().fights.containsKey(player.getPlayerId())){
					PVPFightService.ins().fights.remove(player.getPlayerId());
				}
				player.getFateData().setTodayFateLevel(1);
				player.getFateData().setTodayBoxCount(0);
				player.getFateData().setTempBoxCount(-1);
				player.getFateData().setTempSpecialCount(0);
				player.getFateData().setAddRate(0);
				player.getUser().getZone().removeUser(player.getUser());
				player.getUser().disconnect(new LoginOutReason());
				SessionManager.ins().removeSession(Long.parseLong(player.getUser().getName()));
			}
		}
	}
}
