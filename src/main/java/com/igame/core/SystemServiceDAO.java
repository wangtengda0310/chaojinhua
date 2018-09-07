package com.igame.core;




import com.igame.core.db.AbsDao;
import com.igame.core.db.AccountDbDao;
import com.igame.core.di.Inject;
import org.mongodb.morphia.query.UpdateOperations;

/**
 * 
 * @author Marcus.Z
 *
 */
public class SystemServiceDAO extends AbsDao {
	@Inject
	private AccountDbDao accountDbDao;

    private static final SystemServiceDAO domain = new SystemServiceDAO();

    public static SystemServiceDAO ins() {
        return domain;
    }
    

    /**
     * 查询
     */
    public SystemServiceDto loadData(){
    	
    	return accountDbDao.getAccountDatastore().find(SystemServiceDto.class).get();
    }

    /**
     * 更新
     */
    public void update(SystemServiceDto m){
    	SystemServiceDto sys = accountDbDao.getAccountDatastore().find(SystemServiceDto.class).get();
		if(sys == null){
			accountDbDao.getAccountDatastore().save(m);
    	}else{
	    	UpdateOperations<SystemServiceDto> up = accountDbDao.getAccountDatastore().createUpdateOperations(SystemServiceDto.class);
	    	up.set("clock", m.getClock());
			accountDbDao.getAccountDatastore().update(m, up);
    	}
//    	GoldLog.info("SystemServiceDAO save:" + m.getClock().size());
    }


    

}
