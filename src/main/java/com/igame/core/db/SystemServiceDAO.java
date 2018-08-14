package com.igame.core.db;




import java.util.Set;

import org.mongodb.morphia.query.UpdateOperations;

import com.google.common.collect.Sets;
import com.igame.core.db.AbsDao;
import com.igame.core.log.GoldLog;
import com.igame.util.SystemService;

/**
 * 
 * @author Marcus.Z
 *
 */
public class SystemServiceDAO extends AbsDao {

	@Override
	public String getTableName() {
		return "SystemService";
	}
	
    private static final SystemServiceDAO domain = new SystemServiceDAO();

    public static final SystemServiceDAO ins() {
        return domain;
    }
    

    /**
     * 查询
     * @return
     */
    public Set<Long> loadData(){
    	
    	Set<Long> clock = Sets.newHashSet();
    	
    	SystemService sys = getDatastore("accounts").find(SystemService.class).get();
    	if(sys != null){
    		return sys.getClock();
    	}  	
    	return clock;
    }

    /**
     * 更新
     * @param m
     */
    public void update(SystemService m){
    	SystemService sys = getDatastore("accounts").find(SystemService.class).get();
		if(sys == null){
    		getDatastore("accounts").save(m);
    	}else{
	    	UpdateOperations<SystemService> up = getDatastore("accounts").createUpdateOperations(SystemService.class);
	    	up.set("clock", m.getClock());
	    	getDatastore("accounts").update(m, up);
    	}
//    	GoldLog.info("SystemServiceDAO save:" + m.getClock().size());
    }


    

}
