package com.igame.core;




import com.igame.core.db.AbsDao;
import org.mongodb.morphia.query.UpdateOperations;

/**
 * 
 * @author Marcus.Z
 *
 */
public class SystemServiceDAO extends AbsDao {

    /**
     * 查询
     */
    public SystemServiceDto loadData(){
    	
    	return getDatastore().find(SystemServiceDto.class).get();
    }

    /**
     * 更新
     */
    public void update(SystemServiceDto m){
    	SystemServiceDto sys = getDatastore().find(SystemServiceDto.class).get();
		if(sys == null){
			getDatastore().save(m);
    	}else{
	    	UpdateOperations<SystemServiceDto> up = getDatastore().createUpdateOperations(SystemServiceDto.class);
	    	up.set("clock", m.getClock());
			getDatastore().update(m, up);
    	}
//    	GoldLog.info("SystemServiceDAO save:" + m.getClock().size());
    }


    

}
