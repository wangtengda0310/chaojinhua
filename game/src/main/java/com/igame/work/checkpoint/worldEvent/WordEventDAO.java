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

    /**
     * 查询
     */
    public Map<Integer, WorldEventDto> getByPlayer(long playerId){
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
    public WorldEventDto save(WorldEventDto m){
    	getDatastore().save(m);
    	return m;
    }
    
    /**
     * 更新
     */
    public void update(WorldEventDto m){
    	UpdateOperations<WorldEventDto> up = getDatastore().createUpdateOperations(WorldEventDto.class)
        		.set("count", m.getCount())
        		.set("level", m.getLevel())
        		;
    	getDatastore().update(m, up);
    }
    
    /**
     * 删除
     */
    public void remove(WorldEventDto m){
    	getDatastore().delete(m);
    }
    
    /**
     * 更新玩家
     */
    public void updatePlayer(Player player){
    	
    	for(WorldEventDto m : player.getWordEvent().values()){
    		if(m.getDtate() == 1){
    			save(m);
    		}else if(m.getDtate() == 2){
    			update(m);
    		}else if(m.getDtate() == 3){
    			remove(m);
    		}
    		m.setDtate(0);
    	}

	
    }
    

}
