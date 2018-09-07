package com.igame.work.activity.denglu;


import com.google.common.collect.Maps;
import com.igame.core.db.AbsDao;
import org.mongodb.morphia.query.UpdateOperations;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author Marcus.Z
 *
 */
public class DengluDAO extends AbsDao {

    private static final DengluDAO domain = new DengluDAO();

    public static DengluDAO ins() {
        return domain;
    }


	public List<DengluDto> listByPlayer(int serverId,long playerId){
		return getDatastore().find(DengluDto.class, "playerId", playerId).asList();
	}
    /**
     * 查询
     */
    public Map<Integer,DengluDto> getByPlayer(int serverId,long playerId){
    	Map<Integer,DengluDto> all = Maps.newHashMap();

		List<DengluDto> ls = listByPlayer(serverId, playerId);
    	if(ls != null){
    		for(DengluDto mm : ls){
    			all.put(mm.getActivityId(), mm);
    		}
    	}   	
    	return all;
    }

    
    /**
     * 保存
     */
    public DengluDto save(int serverId,DengluDto m){
    	getDatastore().save(m);
    	return m;
    }
    
    /**
     * 更新
     */
    public void update(int serverId,DengluDto m){
    	UpdateOperations<DengluDto> up = getDatastore().createUpdateOperations(DengluDto.class);
    	up.set("playerId", m.getActivityId());
    	up.set("activityId", m.getActivityId());
    	up.set("record", m.getRecord());
    	getDatastore().save(m);
    }
    
    /**
     * 删除
     */
    public void remove(int serverId,DengluDto m){
    	getDatastore().delete(m);
    }

}
