package com.igame.core.db;


import com.google.common.collect.Maps;
import com.igame.work.system.BallisticRank;
import com.igame.work.system.BallisticRanker;
import org.mongodb.morphia.query.UpdateOperations;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Marcus.Z
 *
 */
public class BallisticRankDAO extends AbsDao {

	@Override
	public String getTableName() {
		return "ballisticRank";
	}
	
    private static final BallisticRankDAO domain = new BallisticRankDAO();

    public static final BallisticRankDAO ins() {
        return domain;
    }
    

    /**
     * 查询
     */
    public BallisticRank get(){

    	return getDatastore("accounts").find(BallisticRank.class).get();
    }

	/**
	 * 保存
	 */
	public BallisticRank save(BallisticRank m){

		getDatastore("accounts").save(m);

		return m;
	}

    /**
     * 更新
     * @param m
     */
    public void update(BallisticRank m){
		UpdateOperations<BallisticRank> up = getDatastore("accounts").createUpdateOperations(BallisticRank.class)
				.set("rankMap", m.getRankMap())
				.set("buffMap", m.getBuffMap());
		getDatastore("accounts").update(m, up);
    }
    
    public void updateTest(){


		BallisticRank rr = BallisticRank.ins();

		BallisticRanker rank = new BallisticRanker();
    	rank.setScore(1);
    	rank.setName("舒适度");
    	rank.setPlayerId(1l);
    	Map<Long, BallisticRanker> lm = new HashMap<Long, BallisticRanker>();
    	lm.put(1l, rank);
    	rr.getRankMap().put(1, lm);
    	
//    	rr.getMap().put(1, 1);
//    	rr.getMap().put(2, 2);

		BallisticRank sys = getDatastore("accounts").find(BallisticRank.class).get();
    	if(sys == null){
    		getDatastore("accounts").save(rr);
    	}else{
	    	UpdateOperations<BallisticRank> up = getDatastore("accounts").createUpdateOperations(BallisticRank.class);
	    	up.set("rankMap", rr.getRankMap());
//	    	up.set("map", 1);
	    	getDatastore("accounts").update(rr, up);
    	}
//    	GoldLog.info("RankServiceDAO save:" + m.getRankMaps().size());
    }


    

}
