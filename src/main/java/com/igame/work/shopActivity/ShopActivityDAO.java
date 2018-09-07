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

	public List<ShopActivityDto> listByPlayer(long playerId){
		return getDatastore().find(ShopActivityDto.class, "playerId", playerId).asList();
	}
	public List<ShopActivityDto> listAll(){
		return getDatastore().find(ShopActivityDto.class).asList();
	}
    /**
     * 查询
     */
    public Map<Integer,ShopActivityDto> getByPlayer(long playerId){
    	Map<Integer,ShopActivityDto> all = Maps.newHashMap();

		List<ShopActivityDto> ls = listByPlayer(playerId);
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
    public ShopActivityDto save(ShopActivityDto m){
    	getDatastore().save(m);
    	return m;
    }
    
    /**
     * 更新
     */
    public void update(ShopActivityDto m){
//    	UpdateOperations<ShopActivityDto> up = getAccountDatastore(serverId).createUpdateOperations(ShopActivityDto.class);
//    	up.set("playerId", m.getActivityId());
//    	up.set("activityId", m.getActivityId());
//    	up.set("record", m.getRecord());
//    	getAccountDatastore(serverId).update(m, up);
    }
    
    /**
     * 删除
     */
    public void remove(ShopActivityDto m){
    	getDatastore().delete(m);
    }

}
