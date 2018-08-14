package com.igame.work.monster.dao;



import java.util.List;
import java.util.Map;

import org.mongodb.morphia.query.UpdateOperations;

import com.google.common.collect.Maps;
import com.igame.core.db.AbsDao;
import com.igame.work.monster.dto.Gods;
import com.igame.work.user.dto.Player;

/**
 * 
 * @author Marcus.Z
 *
 */
public class GodsDAO extends AbsDao {

	@Override
	public String getTableName() {
		return "Gods";
	}
	
    private static final GodsDAO domain = new GodsDAO();

    public static final GodsDAO ins() {
        return domain;
    }
    

    /**
     * 查询
     * @param serverId
     * @param playerId
     * @return
     */
    public Map<Integer,Gods> getByPlayer(int serverId,long playerId){
    	Map<Integer,Gods> all = Maps.newHashMap();
    	
    	List<Gods> ls = getDatastore(serverId).find(Gods.class, "playerId", playerId).asList();
    	if(ls != null){
    		for(Gods mm : ls){
    			all.put(mm.getGodsType(), mm);
    		}
    	}   	
    	return all;
    }
    
    /**
     * 保存
     * @param serverId
     * @param userId
     * @return
     */
    public Gods save(int serverId,Gods m){
    	getDatastore(serverId).save(m);
    	return m;
    }
    
    /**
     * 更新
     * @param serverId
     * @param m
     */
    public void update(int serverId,Gods m){
    	UpdateOperations<Gods> up = getDatastore(serverId).createUpdateOperations(Gods.class);
    	up.set("godsLevel", m.getGodsLevel());
    	getDatastore(serverId).update(m, up);
    }
    
    /**
     * 删除
     * @param serverId
     * @param m
     */
    public void remove(int serverId,Gods m){
    	getDatastore(serverId).delete(m);
    }
    
    /**
     * 更新玩家
     * @param player
     */
    public void updatePlayer(Player player){
    	
    	for(Gods m : player.getGods().values()){
    		if(m.getDtate() == 1){
    			save(player.getSeverId(), m);
    		}else if(m.getDtate() == 2){
    			update(player.getSeverId(), m);
    		}else if(m.getDtate() == 3){
    			remove(player.getSeverId(), m);
    		}
    		m.setDtate(0);
    	}

	
    }
    

}
