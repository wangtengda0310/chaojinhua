package com.igame.work.checkpoint.dao;



import java.util.List;
import java.util.Map;

import org.mongodb.morphia.query.UpdateOperations;

import com.google.common.collect.Maps;
import com.igame.core.db.AbsDao;
import com.igame.work.checkpoint.dto.WordEventDto;
import com.igame.work.user.dto.Player;

/**
 * 
 * @author Marcus.Z
 *
 */
public class WordEventDAO extends AbsDao {

	@Override
	public String getTableName() {
		return "WordEventDto";
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
    public Map<Integer,WordEventDto> getByPlayer(int serverId,long playerId){
    	Map<Integer,WordEventDto> all = Maps.newHashMap();
    	
    	List<WordEventDto> ls = getDatastore(serverId).find(WordEventDto.class, "playerId", playerId).asList();
    	if(ls != null){
    		for(WordEventDto mm : ls){
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
    public WordEventDto save(int serverId,WordEventDto m){
    	getDatastore(serverId).save(m);
    	return m;
    }
    
    /**
     * 更新
     * @param serverId
     * @param m
     */
    public void update(int serverId,WordEventDto m){
    	UpdateOperations<WordEventDto> up = getDatastore(serverId).createUpdateOperations(WordEventDto.class)
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
    public void remove(int serverId,WordEventDto m){
    	getDatastore(serverId).delete(m);
    }
    
    /**
     * 更新玩家
     * @param player
     */
    public void updatePlayer(Player player){
    	
    	for(WordEventDto m : player.getWordEvent().values()){
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
