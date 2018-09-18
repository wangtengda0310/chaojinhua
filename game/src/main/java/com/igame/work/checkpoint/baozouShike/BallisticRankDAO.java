package com.igame.work.checkpoint.baozouShike;


import com.igame.core.db.AbsDao;
import org.mongodb.morphia.query.UpdateOperations;

/**
 * 
 * @author Marcus.Z
 *
 */
public class BallisticRankDAO extends AbsDao {

    /**
     * 查询
     */
    public BallisticRankDto get(){

    	return getDatastore().find(BallisticRankDto.class).get();
    }

	/**
	 * 保存
	 */
	public BallisticRankDto save(BallisticRankDto m){

		getDatastore().save(m);

		return m;
	}

    /**
     * 更新
     */
    public void update(BallisticRankDto m){
		UpdateOperations<BallisticRankDto> up = getDatastore().createUpdateOperations(BallisticRankDto.class)
				.set("rank", m.getRank())
				.set("buff", m.getBuff());
		getDatastore().update(m, up);
    }

}
