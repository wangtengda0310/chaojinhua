package com.igame.work.user.service;


import com.google.common.collect.Maps;
import com.igame.core.ISFSModule;
import com.igame.core.SessionManager;
import com.igame.core.di.Inject;
import com.igame.core.event.EventService;
import com.igame.core.log.ExceptionLog;
import com.igame.core.quartz.TimeListener;
import com.igame.util.LoginOutReason;
import com.igame.work.PlayerEvents;
import com.igame.work.fight.service.PVPFightService;
import com.igame.work.friend.dao.FriendDAO;
import com.igame.work.user.dao.PlayerDAO;
import com.igame.work.user.dto.Player;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 
 * @author Marcus.Z
 *
 */
public class PlayerCacheService extends EventService implements ISFSModule, TimeListener {
	@Inject private FriendDAO dao;

	@Override
	public void minute() {
		checkPlayer();
	}

	private static Map<Long,Player> pcd = Maps.newHashMap();
    
    private static Map<String,Player> pcn = Maps.newHashMap();


    /**
     * 更新玩家
     */
    public static void cachePlayer(Player player){

		pcd.put(player.getPlayerId(), player);
    	pcn.put(player.getNickname(), player);
    	
    }
    
    /**
     * 根据玩家ID获取
     */
    public static Player getPlayerById(long playerId){

		Player player  = SessionManager.ins().getSessionByPlayerId(playerId);
		if(player != null){	//如果玩家在线
			pcd.remove(player.getPlayerId());
			return player;
		}else {	//如果玩家不在线
			return pcd.get(playerId);
		}

    }
    
    /**
     * 根据玩家昵称
     */
    public static Player getPlayerByNickName(String nickName){

		Player player  = SessionManager.ins().getSession(nickName);
		if(player != null){	//如果玩家在线
			pcn.remove(player.getNickname());
			return player;
		}else {	//如果玩家不在线
			return pcn.get(nickName);
		}
    }

	/**
	 * 加载所有玩家
	 */
	@Override
	public void init() {

		List<Player> allPlayer = PlayerDAO.ins().getALLPlayer();
		allPlayer.forEach(PlayerCacheService::cachePlayer);

		allPlayer.forEach(player ->
				//加载好友
				player.setFriends(dao.getFriendInfoByPlayerId(player.getPlayerId()))
		);
	}

	public static List<Player> getPlayers(int serverId) {
		return pcd.values().stream().filter(player -> player.getSeverId() == serverId).collect(Collectors.toList());
	}
	
	public void checkPlayer(){
    	for(Player player : SessionManager.ins().getSessions().values()){
			if(player.getHeartTime()>0 && System.currentTimeMillis() - player.getHeartTime() > 5 * 60 * 1000){
				try{
					fireEvent(player, PlayerEvents.OFF_LINE,System.currentTimeMillis());
				}catch(Exception e){
					ExceptionLog.error("palyer leave save error----- :",e);
				}
				
				if(PVPFightService.ins().palyers.containsKey(player.getPlayerId())){
					PVPFightService.ins().chancelFight(player);
				}
				PVPFightService.ins().fights.remove(player.getPlayerId());
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

	public static void remove(Player player) {
		if (player != null) {
			pcd.remove(player.getPlayerId());
			pcn.remove(player.getNickname());
		}
	}
}
