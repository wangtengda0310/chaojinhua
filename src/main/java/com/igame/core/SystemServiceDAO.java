package com.igame.core;




import com.igame.core.db.AbsDao;
import org.mongodb.morphia.query.UpdateOperations;

/**
 * 
 * @author Marcus.Z
 *
 */
public class SystemServiceDAO extends AbsDao {

	@Override
	public String getTableName() {
		return "SystemServiceDto";
	}
	
    private static final SystemServiceDAO domain = new SystemServiceDAO();

    public static final SystemServiceDAO ins() {
        return domain;
    }
    

    /**
     * 查询
     */
    public SystemServiceDto loadData(){
    	
    	return getDatastore("accounts").find(SystemServiceDto.class).get();
    }

    /**
     * 更新
     */
    public void update(SystemServiceDto m){
    	SystemServiceDto sys = getDatastore("accounts").find(SystemServiceDto.class).get();
		if(sys == null){
    		getDatastore("accounts").save(m);
    	}else{
	    	UpdateOperations<SystemServiceDto> up = getDatastore("accounts").createUpdateOperations(SystemServiceDto.class);
	    	up.set("clock", m.getClock());
	    	getDatastore("accounts").update(m, up);
    	}
//    	GoldLog.info("SystemServiceDAO save:" + m.getClock().size());
    }


    

}
