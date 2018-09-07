package com.igame.work.system;




import com.igame.core.db.AbsDao;
import org.mongodb.morphia.query.UpdateOperations;

/**
 * 
 * @author Marcus.Z
 *
 */
public class RankServiceDAO extends AbsDao {

    private static final RankServiceDAO domain = new RankServiceDAO();

    public static RankServiceDAO ins() {
        return domain;
    }
    

    /**
     * 查询
     */
    public  RankServiceDto loadData(){
    	return getDatastore().find(RankServiceDto.class).get();
    }

    /**
     * 更新
     */
    public void update(RankServiceDto m){
    	RankServiceDto sys = getDatastore().find(RankServiceDto.class).get();
    	if(sys == null){
			getDatastore().save(m);
    	}else{
	    	UpdateOperations<RankServiceDto> up = getDatastore().createUpdateOperations(RankServiceDto.class);
	    	up.set("rankMap", m.getRankMap());
//	    	for(Map<Long, Ranker> rr : m.getRankDto().values()){
//	    		int ss = rr.size();
//	    	}
			getDatastore().update(m, up);
    	}
//    	GoldLog.info("RankServiceDAO save:" + m.getRankMaps().size());
    }


    

}
