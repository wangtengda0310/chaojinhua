package com.igame.work.checkpoint.worldEvent;



import com.google.common.collect.Maps;
import com.igame.core.db.AbsDao;
import com.igame.work.user.dto.Player;
import org.mongodb.morphia.query.UpdateOperations;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author Marcus.Z
 *
 */
public class WordEventDAO extends AbsDao {

    private static final WordEventDAO domain = new WordEventDAO();

    public static WordEventDAO ins() {
        return domain;
    }
    

    /**
     * 查询
     */
    public Map<Integer, WorldEventDto> getByPlayer(int serverId, long playerId){
    	Map<Integer, WorldEventDto> all = Maps.newHashMap();
    	
    	List<WorldEventDto> ls = getDatastore().find(WorldEventDto.class, "playerId", playerId).asList();
    	if(ls != null){
    		for(WorldEventDto mm : ls){
    			all.put(mm.getEventType(), mm);
    		}
    	}   	
    	return all;
    }
    
    /**
     * 保存
     */
    public WorldEventDto save(int serverId, WorldEventDto m){
    	getDatastore().save(m);
    	return m;
    }
    
    /**
     * 更新
     */
    public void update(int serverId, WorldEventDto m){
    	UpdateOperations<WorldEventDto> up = getDatastore().createUpdateOperations(WorldEventDto.class)
        		.set("count", m.getCount())
        		.set("level", m.getLevel())
        		;
    	getDatastore().update(m, up);
    }
    
    /**
     * 删除
     */
    public void remove(int serverId, WorldEventDto m){
    	getDatastore().delete(m);
    }
    
    /**
     * 更新玩家
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
