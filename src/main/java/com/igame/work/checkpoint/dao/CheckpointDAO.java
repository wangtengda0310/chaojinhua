package com.igame.work.checkpoint.dao;



import java.util.List;
import java.util.Map;

import org.mongodb.morphia.query.UpdateOperations;

import com.google.common.collect.Maps;
import com.igame.core.db.AbsDao;
import com.igame.work.checkpoint.dto.Checkpoint;
import com.igame.work.user.dto.Player;

/**
 * 
 * @author Marcus.Z
 *
 */
public class CheckpointDAO extends AbsDao {

	@Override
	public String getTableName() {
		return "Checkpoint";
	}
	
    private static final CheckpointDAO domain = new CheckpointDAO();

    public static final CheckpointDAO ins() {
        return domain;
    }
    

    /**
     * 查询
     * @param serverId
     * @param playerId
     * @return
     */
    public Map<Integer,Checkpoint> getByPlayer(int serverId,long playerId){
    	Map<Integer,Checkpoint> all = Maps.newHashMap();
    	
    	List<Checkpoint> ls = getDatastore(serverId).find(Checkpoint.class, "playerId", playerId).asList();
    	if(ls != null){
    		for(Checkpoint mm : ls){
    			all.put(mm.getChapterId(), mm);
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
    public Checkpoint save(int serverId,Checkpoint m){
    	getDatastore(serverId).save(m);
    	return m;
    }
    
    /**
     * 更新
     * @param serverId
     * @param m
     */
    public void update(int serverId,Checkpoint m){
//    	UpdateOperations<Checkpoint> up = getDatastore(serverId).createUpdateOperations(Checkpoint.class)
//        		.set("count", m.getCount())
//        		;
//    	getDatastore(serverId).update(m, up);
    }
    
    /**
     * 删除
     * @param serverId
     * @param m
     */
    public void remove(int serverId,Checkpoint m){
    	getDatastore(serverId).delete(m);
    }
    
    /**
     * 更新玩家
     * @param player
     */
    public void updatePlayer(Player player){
    	
//    	for(Checkpoint m : player.getCheckpoint().values()){
//    		if(m.getDtate() == 1){
//    			save(player.getSeverId(), m);
//    		}else if(m.getDtate() == 2){
//    			update(player.getSeverId(), m);
//    		}else if(m.getDtate() == 3){
//    			remove(player.getSeverId(), m);
//    		}
//    		m.setDtate(0);
//    	}
    	
	
    }
    

}
