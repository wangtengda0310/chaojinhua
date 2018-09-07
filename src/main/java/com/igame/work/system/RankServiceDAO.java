package com.igame.work.system;




import com.igame.core.db.AbsDao;
import com.igame.core.db.AccountDbDao;
import com.igame.core.di.Inject;
import org.mongodb.morphia.query.UpdateOperations;

/**
 * 
 * @author Marcus.Z
 *
 */
public class RankServiceDAO extends AbsDao {
	@Inject
	private AccountDbDao accountDbDao;

    private static final RankServiceDAO domain = new RankServiceDAO();

    public static RankServiceDAO ins() {
        return domain;
    }
    

    /**
     * 查询
     */
    public  RankServiceDto loadData(){
    	return accountDbDao.getAccountDatastore().find(RankServiceDto.class).get();
    }

    /**
     * 更新
     */
    public void update(RankServiceDto m){
    	RankServiceDto sys = accountDbDao.getAccountDatastore().find(RankServiceDto.class).get();
    	if(sys == null){
			accountDbDao.getAccountDatastore().save(m);
    	}else{
	    	UpdateOperations<RankServiceDto> up = accountDbDao.getAccountDatastore().createUpdateOperations(RankServiceDto.class);
	    	up.set("rankMap", m.getRankMap());
//	    	for(Map<Long, Ranker> rr : m.getRankMap().values()){
//	    		int ss = rr.size();
//	    	}
			accountDbDao.getAccountDatastore().update(m, up);
    	}
//    	GoldLog.info("RankServiceDAO save:" + m.getRankMaps().size());
    }


    

}
