package com.igame.work.checkpoint.worldEvent;



import java.util.List;
import java.util.Map;

import org.mongodb.morphia.query.UpdateOperations;

import com.google.common.collect.Maps;
import com.igame.core.db.AbsDao;
import com.igame.work.user.dto.Player;

/**
 * 
 * @author Marcus.Z
 *
 */
public class WordEventDAO extends AbsDao {

	@Override
	public String getTableName() {
		return "WorldEventDto";
	}
	
    private static final WordEventDAO domain = new WordEventDAO();

    public static final WordEventDAO ins() {
        return domain;
    }
    

    /**
     * 查询
     * @param serverId
     * @param playerId
     * @return
     */
    public Map<Integer, WorldEventDto> getByPlayer(int serverId, long playerId){
    	Map<Integer, WorldEventDto> all = Maps.newHashMap();
    	
    	List<WorldEventDto> ls = getDatastore(serverId).find(WorldEventDto.class, "playerId", playerId).asList();
    	if(ls != null){
    		for(WorldEventDto mm : ls){
    			all.put(mm.getEventType(), mm);
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
    public WorldEventDto save(int serverId, WorldEventDto m){
    	getDatastore(serverId).save(m);
    	return m;
    }
    
    /**
     * 更新
     * @param serverId
     * @param m
     */
    public void update(int serverId, WorldEventDto m){
    	UpdateOperations<WorldEventDto> up = getDatastore(serverId).createUpdateOperations(WorldEventDto.class)
        		.set("count", m.getCount())
        		.set("level", m.getLevel())
        		;
    	getDatastore(serverId).update(m, up);
    }
    
    /**
     * 删除
     * @param serverId
     * @param m
     */
    public void remove(int serverId, WorldEventDto m){
    	getDatastore(serverId).delete(m);
    }
    
    /**
     * 更新玩家
     * @param player
     */
    public void updatePlayer(Player player){
    	
    	for(WorldEventDto m : player.getWordEvent().values()){
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
