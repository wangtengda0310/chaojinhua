package com.igame.work.activity.QitianDenglu;


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
public class QitianDengluDAO extends AbsDao {

	@Override
	public String getTableName() {
		return "QitianDengluActivity";
	}
	
    private static final QitianDengluDAO domain = new QitianDengluDAO();

    public static QitianDengluDAO ins() {
        return domain;
    }
    

    /**
     * 查询
     */
    public Map<Integer,QitianDengluDto> getByPlayer(int serverId,long playerId){
    	Map<Integer,QitianDengluDto> all = Maps.newHashMap();
    	
    	List<QitianDengluDto> ls = getDatastore(serverId).find(QitianDengluDto.class, "playerId", playerId).asList();
    	if(ls != null){
    		for(QitianDengluDto mm : ls){
    			all.put(mm.getActivityId(), mm);
    		}
    	}   	
    	return all;
    }

    
    /**
     * 保存
     */
    public QitianDengluDto save(int serverId,QitianDengluDto m){
    	getDatastore(serverId).save(m);
    	return m;
    }
    
    /**
     * 更新
     */
    public void update(int serverId,QitianDengluDto m){
    	UpdateOperations<QitianDengluDto> up = getDatastore(serverId).createUpdateOperations(QitianDengluDto.class);
    	up.set("activityId", m.getActivityId());
    	up.set("playerId", m.getPlayerId());
    	up.set("loginTimes", m.getLoginTimes());
    	getDatastore(serverId).update(m, up);
    }
    
    /**
     * 删除
     */
    public void remove(int serverId,QitianDengluDto m){
    	getDatastore(serverId).delete(m);
    }

}
