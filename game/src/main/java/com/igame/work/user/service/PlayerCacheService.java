package com.igame.work.user.service;


import com.google.common.collect.Maps;
import com.igame.core.ISFSModule;
import com.igame.core.SessionManager;
import com.igame.core.di.Inject;
import com.igame.core.event.EventService;
import com.igame.core.event.RemoveOnLogout;
import com.igame.core.log.ExceptionLog;
import com.igame.core.quartz.TimeListener;
import com.igame.util.LoginOutReason;
import com.igame.work.PlayerEvents;
import com.igame.work.fight.service.PVPFightService;
import com.igame.work.friend.dao.FriendDAO;
import com.igame.work.friend.service.FriendService;
import com.igame.work.user.dao.PlayerDAO;
import com.igame.work.user.dto.Player;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 
 * @author Marcus.Z
 *
 */
public class PlayerCacheService extends EventService implements ISFSModule, TimeListener {
	@Inject private SessionManager sessionManager;
	@Inject private FriendDAO dao;
	@Inject private PVPFightService pvpFightService;
	@Inject private PlayerDAO playerDAO;
	@Inject private FriendService friendService;

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
    public Player getPlayerById(long playerId){

		Player player  = sessionManager.getSessionByPlayerId(playerId);
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
    public Player getPlayerByNickName(String nickName){

		Player player  = sessionManager.getSession(nickName);
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

		List<Player> allPlayer = playerDAO.getALLPlayer();
		allPlayer.forEach(PlayerCacheService::cachePlayer);

		allPlayer.forEach(player ->
				//加载好友
				friendService.setFriends(player,dao.getFriendInfoByPlayerId(player.getPlayerId()))
		);
	}

	public static List<Player> getPlayers(int serverId) {
		return pcd.values().stream().filter(player -> player.getSeverId() == serverId).collect(Collectors.toList());
	}

	@RemoveOnLogout() private Map<Long, Long> heartTime = new ConcurrentHashMap<>();//心跳刷新时间

	private long getHeartTime(long playerId) {
		return heartTime.containsKey(playerId)?heartTime.get(playerId):0;
	}

	public void setHeartTime(long playerId, long heartTime) {
		this.heartTime.put(playerId, heartTime);
	}

	private void checkPlayer(){
    	for(Player player : sessionManager.getSessions().values()){
			long heartTime = getHeartTime(player.getPlayerId());
			if(heartTime >0 && System.currentTimeMillis() - heartTime > 5 * 60 * 1000){
				try{
					fireEvent(player, PlayerEvents.OFF_LINE,System.currentTimeMillis());
				}catch(Exception e){
					ExceptionLog.error("palyer leave save error----- :",e);
				}
				
				if(pvpFightService.palyers.containsKey(player.getPlayerId())){
					pvpFightService.chancelFight(player);
				}
				pvpFightService.fights.remove(player.getPlayerId());
				player.getFateData().setTodayFateLevel(1);
				player.getFateData().setTodayBoxCount(0);
				player.getFateData().setTempBoxCount(-1);
				player.getFateData().setTempSpecialCount(0);
				player.getFateData().setAddRate(0);
				player.getUser().getZone().removeUser(player.getUser());
				player.getUser().disconnect(new LoginOutReason());
				sessionManager.removeSession(Long.parseLong(player.getUser().getName()));
				this.heartTime.remove(player.getPlayerId());
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
