package com.igame.work.system;




import java.util.HashMap;
import java.util.Map;

import org.mongodb.morphia.query.UpdateOperations;

import com.google.common.collect.Maps;
import com.igame.core.db.AbsDao;

/**
 * 
 * @author Marcus.Z
 *
 */
public class RankServiceDAO extends AbsDao {

	@Override
	public String getTableName() {
		return "RankService";
	}
	
    private static final RankServiceDAO domain = new RankServiceDAO();

    public static final RankServiceDAO ins() {
        return domain;
    }
    

    /**
     * 查询
     * @return
     */
    public  Map<Integer,Map<Long,Ranker>> loadData(){
    	
    	
    	RankService sys = getDatastore("accounts").find(RankService.class).get();
    	if(sys != null){
    		return sys.getRankMap();
    	}  	
    	return Maps.newHashMap();
    }

    /**
     * 更新
     * @param serverId
     * @param m
     */
    public void update(RankService m){
    	RankService sys = getDatastore("accounts").find(RankService.class).get();
    	if(sys == null){
    		getDatastore("accounts").save(m);
    	}else{
	    	UpdateOperations<RankService> up = getDatastore("accounts").createUpdateOperations(RankService.class);
	    	up.set("rankMap", m.getRankMap());
//	    	for(Map<Long, Ranker> rr : m.getRankMap().values()){
//	    		int ss = rr.size();
//	    	}
	    	getDatastore("accounts").update(m, up);
    	}
//    	GoldLog.info("RankServiceDAO save:" + m.getRankMaps().size());
    }
    
    public void updateTest(){
    	
    	
    	RankService rr = RankService.ins();
    	
    	Ranker rank = new Ranker();
    	rank.setScore(1);
    	rank.setName("舒适度");
    	rank.setPlayerId(1l);
    	Map<Long, Ranker> lm = new HashMap<Long, Ranker>();
    	lm.put(1l, rank);
    	rr.getRankMap().put(1, lm);
    	
//    	rr.getMap().put(1, 1);
//    	rr.getMap().put(2, 2);
    	
    	RankService sys = getDatastore("accounts").find(RankService.class).get();
    	if(sys == null){
    		getDatastore("accounts").save(rr);
    	}else{
	    	UpdateOperations<RankService> up = getDatastore("accounts").createUpdateOperations(RankService.class);
	    	up.set("rankMap", rr.getRankMap());
//	    	up.set("map", 1);
	    	getDatastore("accounts").update(rr, up);
    	}
//    	GoldLog.info("RankServiceDAO save:" + m.getRankMaps().size());
    }


    

}
