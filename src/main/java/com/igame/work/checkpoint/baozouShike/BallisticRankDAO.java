package com.igame.work.checkpoint.baozouShike;


import com.igame.core.db.AbsDao;
import com.igame.core.db.AccountDbDao;
import com.igame.core.di.Inject;
import org.mongodb.morphia.query.UpdateOperations;

/**
 * 
 * @author Marcus.Z
 *
 */
public class BallisticRankDAO extends AbsDao {
	@Inject private AccountDbDao accountDbDao;

    private static final BallisticRankDAO domain = new BallisticRankDAO();

    public static BallisticRankDAO ins() {
        return domain;
    }
    

    /**
     * 查询
     */
    public BallisticRank get(){

    	return accountDbDao.getAccountDatastore().find(BallisticRank.class).get();
    }

	/**
	 * 保存
	 */
	public BallisticRank save(BallisticRank m){

		accountDbDao.getAccountDatastore().save(m);

		return m;
	}

    /**
     * 更新
     */
    public void update(BallisticRank m){
		UpdateOperations<BallisticRank> up = accountDbDao.getAccountDatastore().createUpdateOperations(BallisticRank.class)
				.set("rankMap", m.getRankMap())
				.set("buffMap", m.getBuffMap());
		accountDbDao.getAccountDatastore().update(m, up);
    }

}
