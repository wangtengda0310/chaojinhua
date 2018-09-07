package com.igame.work.shopActivity;


import com.google.common.collect.Maps;
import com.igame.core.db.AbsDao;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author Marcus.Z
 *
 */
public class ShopActivityDAO extends AbsDao {

	@Override
	public String getTableName() {
		return "ShopActivity";
	}


	public List<ShopActivityDto> listByPlayer(int serverId,long playerId){
		return getDatastore(serverId).find(ShopActivityDto.class, "playerId", playerId).asList();
	}
	public List<ShopActivityDto> listAll(int serverId){
		return getDatastore(serverId).find(ShopActivityDto.class).asList();
	}
    /**
     * 查询
     */
    public Map<Integer,ShopActivityDto> getByPlayer(int serverId,long playerId, int activityId){
    	Map<Integer,ShopActivityDto> all = Maps.newHashMap();

		List<ShopActivityDto> ls = listByPlayer(serverId, playerId);
    	if(ls != null){
    		for(ShopActivityDto mm : ls){
    			all.put(mm.getActivityId(), mm);
    		}
    	}   	
    	return all;
    }

    
    /**
     * 保存
     */
    public ShopActivityDto save(int serverId,ShopActivityDto m){
    	getDatastore(serverId).save(m);
    	return m;
    }
    
    /**
     * 更新
     */
    public void update(int serverId,ShopActivityDto m){
//    	UpdateOperations<ShopActivityDto> up = getDatastore(serverId).createUpdateOperations(ShopActivityDto.class);
//    	up.set("playerId", m.getActivityId());
//    	up.set("activityId", m.getActivityId());
//    	up.set("record", m.getRecord());
//    	getDatastore(serverId).update(m, up);
    }
    
    /**
     * 删除
     */
    public void remove(int serverId,ShopActivityDto m){
    	getDatastore(serverId).delete(m);
    }

}
