package com.igame.work.system;




import com.igame.core.db.AbsDao;
import org.mongodb.morphia.query.UpdateOperations;

/**
 * 
 * @author Marcus.Z
 *
 */
public class RankServiceDAO extends AbsDao {

	@Override
	public String getTableName() {
		return "RankServiceDto";
	}
	
    private static final RankServiceDAO domain = new RankServiceDAO();

    public static final RankServiceDAO ins() {
        return domain;
    }
    

    /**
     * 查询
     */
    public  RankServiceDto loadData(){
    	return getDatastore("accounts").find(RankServiceDto.class).get();
    }

    /**
     * 更新
     */
    public void update(RankServiceDto m){
    	RankServiceDto sys = getDatastore("accounts").find(RankServiceDto.class).get();
    	if(sys == null){
    		getDatastore("accounts").save(m);
    	}else{
	    	UpdateOperations<RankServiceDto> up = getDatastore("accounts").createUpdateOperations(RankServiceDto.class);
	    	up.set("rankMap", m.getRankMap());
//	    	for(Map<Long, Ranker> rr : m.getRankMap().values()){
//	    		int ss = rr.size();
//	    	}
	    	getDatastore("accounts").update(m, up);
    	}
//    	GoldLog.info("RankServiceDAO save:" + m.getRankMaps().size());
    }


    

}
