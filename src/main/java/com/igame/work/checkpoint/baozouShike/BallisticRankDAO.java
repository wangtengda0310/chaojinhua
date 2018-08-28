package com.igame.work.checkpoint.baozouShike;


import com.igame.core.db.AbsDao;
import org.mongodb.morphia.query.UpdateOperations;

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

}
