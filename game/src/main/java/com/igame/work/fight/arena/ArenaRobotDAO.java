package com.igame.work.fight.arena;



import com.igame.core.db.AbsDao;
import org.mongodb.morphia.query.UpdateOperations;

/**
 * 
 * @author Marcus.Z
 *
 */
public class ArenaRobotDAO extends AbsDao {

    /**
     * 更新
     */
    public void updateRank(ArenaServiceDto m){
    	ArenaServiceDto sys = getDatastore().find(ArenaServiceDto.class).get();
    	if(sys == null){
			getDatastore().save(m);
    	}else{
	    	UpdateOperations<ArenaServiceDto> up = getDatastore().createUpdateOperations(ArenaServiceDto.class);
	    	up.set("rank1", m.getRank1());
	    	up.set("rank2", m.getRank2());
	    	up.set("rank3", m.getRank3());
	    	up.set("rank4", m.getRank4());
	    	up.set("rank5", m.getRank5());
	    	up.set("rank6", m.getRank6());
	    	up.set("rank7", m.getRank7());
	    	up.set("rank8", m.getRank8());

			getDatastore().update(m, up);
    	}
    }


    

}
