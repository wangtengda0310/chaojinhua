package com.igame.work.fight.arena;



import com.google.common.collect.Maps;
import com.igame.core.db.AbsDao;
import com.igame.work.user.dto.RobotDto;
import org.mongodb.morphia.query.UpdateOperations;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author Marcus.Z
 *
 */
public class ArenaRobotDAO extends AbsDao {

    /**
     * 查询
     */
    public Map<Long,RobotDto> loadData(){
    	Map<Long,RobotDto> all = Maps.newHashMap();
    	
    	List<RobotDto> ls = getDatastore().find(RobotDto.class,"type",1).asList();
    	if(ls != null){
    		for(RobotDto mm : ls){
    			all.put(mm.getPlayerId(), mm);
    		}
    	}   	
    	return all;
    }
    
    
    /**
     * 保存
     */
    public RobotDto saveRobotDto(RobotDto m){
    	getDatastore().save(m);
    	return m;
    }
    
    /**
     * 更新
     */
    public void updateRobotDto(RobotDto m){
    	UpdateOperations<RobotDto> up = getDatastore().createUpdateOperations(RobotDto.class);
    	up.set("severId", m.getSeverId())
          .set("playerId", m.getPlayerId())
          .set("name", m.getName())
          .set("level", m.getLevel())
          .set("playerFrameId", m.getPlayerFrameId())
          .set("playerHeadId", m.getPlayerHeadId())
          .set("gods", m.getGods())
          .set("mon", m.getMon())
          .set("type", m.getType())
          .set("fightValue", m.getFightValue())
          ;
    	getDatastore().update(m,up);
    }
    
    /**
     * 删除
     */
    public void removeRobotDto(RobotDto m){
    	getDatastore().delete(m);
    }
    
    /**
     * 更新玩家
     */
    public void update(RobotDto m){
    	
		if(m.getDtate() == 1){
			saveRobotDto(m);
		}else if(m.getDtate() == 2){
			updateRobotDto(m);
		}else if(m.getDtate() == 3){
			removeRobotDto(m);
		}
		m.setDtate(0);

	
    }
    
    

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
