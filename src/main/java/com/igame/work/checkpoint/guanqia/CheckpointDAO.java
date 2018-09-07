package com.igame.work.checkpoint.guanqia;



import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.igame.core.db.AbsDao;
import com.igame.work.user.dto.Player;

/**
 * 
 * @author Marcus.Z
 *
 */
public class CheckpointDAO extends AbsDao {

    private static final CheckpointDAO domain = new CheckpointDAO();

    public static final CheckpointDAO ins() {
        return domain;
    }
    

    /**
     * 查询
     */
    public Map<Integer,Checkpoint> getByPlayer(int serverId,long playerId){
    	Map<Integer,Checkpoint> all = Maps.newHashMap();
    	
    	List<Checkpoint> ls = getDatastore().find(Checkpoint.class, "playerId", playerId).asList();
    	if(ls != null){
    		for(Checkpoint mm : ls){
    			all.put(mm.getChapterId(), mm);
    		}
    	}   	
    	return all;
    }
    
    /**
     * 保存
     */
    public Checkpoint save(int serverId,Checkpoint m){
    	getDatastore().save(m);
    	return m;
    }
    
    /**
     * 更新
     */
    public void update(int serverId,Checkpoint m){
//    	UpdateOperations<Checkpoint> up = getAccountDatastore(serverId).createUpdateOperations(Checkpoint.class)
//        		.set("count", m.getCount())
//        		;
//    	getAccountDatastore(serverId).update(m, up);
    }
    
    /**
     * 删除
     */
    public void remove(int serverId,Checkpoint m){
    	getDatastore().delete(m);
    }
    
    /**
     * 更新玩家
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
