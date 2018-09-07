package com.igame.work.monster.dao;



import com.google.common.collect.Maps;
import com.igame.core.db.AbsDao;
import com.igame.work.monster.dto.Gods;
import com.igame.work.user.dto.Player;
import org.mongodb.morphia.query.UpdateOperations;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author Marcus.Z
 *
 */
public class GodsDAO extends AbsDao {

    private static final GodsDAO domain = new GodsDAO();

    public static final GodsDAO ins() {
        return domain;
    }
    

    /**
     * 查询
     */
    public Map<Integer,Gods> getByPlayer(long playerId){
    	Map<Integer,Gods> all = Maps.newHashMap();
    	
    	List<Gods> ls = getDatastore().find(Gods.class, "playerId", playerId).asList();
    	if(ls != null){
    		for(Gods mm : ls){
    			all.put(mm.getGodsType(), mm);
    		}
    	}   	
    	return all;
    }
    
    /**
     * 保存
     */
    public Gods save(Gods m){
    	getDatastore().save(m);
    	return m;
    }
    
    /**
     * 更新
     */
    public void update(Gods m){
    	UpdateOperations<Gods> up = getDatastore().createUpdateOperations(Gods.class);
    	up.set("godsLevel", m.getGodsLevel());
    	getDatastore().update(m, up);
    }
    
    /**
     * 删除
     */
    public void remove(Gods m){
    	getDatastore().delete(m);
    }
    
    /**
     * 更新玩家
     */
    public void updatePlayer(Player player){
    	
    	for(Gods m : player.getGods().values()){
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
