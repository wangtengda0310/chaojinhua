package com.igame.core;


import com.google.common.collect.Maps;
import com.igame.core.log.DebugLog;
import com.igame.work.user.dto.Player;

import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

/**
 * 
 * @author Marcus.Z
 *
 */
public class SessionManager {

    private ConcurrentMap<Long,Player> sessions = Maps.newConcurrentMap();
    private Object lock = new Object();

    public Player getSessionByPlayerId(long playerId){
    	for(Player pp : sessions.values()){
    		if(pp.getPlayerId() == playerId){
    			return pp;
    		}
    	}
    	return null;
    }
    public Player getSession(long userId){
//        synchronized (lock) {
            return sessions.get(userId);
//        }
    }
    
    public Player getSession(String username){
    	for(Player pp : sessions.values()){
    		if(pp.getUsername().equalsIgnoreCase(username)){
    			return pp;
    		}
    	}
    	return null;
    }
    

    
    public boolean isOnline(long playerId){
    	Player session = getSessionByPlayerId(playerId);
        return !(session == null );
    }


    public boolean removeSession(long userId){
//        synchronized (lock) {
            Player player = sessions.remove(userId);
            if(player!=null){
                DebugLog.debug("removeSession---- serverId:" + player.getSeverId() + "," +"userId:" + userId + "," +"playerId:" + player.getPlayerId() + "," +"playerName:" + player.getNickname());
//              player.disconnect();
                return true;
            }
//        }
        return false;
    }

    public void disconnectAll(){
        sessions.keySet().forEach(e->removeSession(e));
    }

    public void addSession(Player player){
        synchronized (lock) {
            long userId = player.getUserId();
            if(sessions.keySet().contains(userId)){
//              getSession(userId).getUser().getSession().close();
            }else {
                DebugLog.debug("addSession---- serverId:" + player.getSeverId() + "," +"userId:" + userId + "," +"playerId:" + player.getPlayerId() + "," +"playerName:" + player.getNickname());
                sessions.put(userId, player);
            }
        }
    }
    public Collection<Long> getPids(){
        return sessions.keySet();
    }
    public ConcurrentMap<Long, Player> getSessions() {
        return sessions;
    }

}
