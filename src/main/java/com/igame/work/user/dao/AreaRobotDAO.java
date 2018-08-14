package com.igame.work.user.dao;



import java.util.List;
import java.util.Map;

import org.mongodb.morphia.query.UpdateOperations;

import com.google.common.collect.Maps;
import com.igame.core.db.AbsDao;
import com.igame.work.fight.service.ArenaService;
import com.igame.work.system.RankService;
import com.igame.work.system.Ranker;
import com.igame.work.user.dto.RobotDto;

/**
 * 
 * @author Marcus.Z
 *
 */
public class AreaRobotDAO extends AbsDao {

	@Override
	public String getTableName() {
		return "Robot";
	}
	
    private static final AreaRobotDAO domain = new AreaRobotDAO();

    public static final AreaRobotDAO ins() {
        return domain;
    }
    

    /**
     * 查询
     * @param serverId
     * @param playerId
     * @return
     */
    public Map<Long,RobotDto> loadData(int serverId){
    	Map<Long,RobotDto> all = Maps.newHashMap();
    	
    	List<RobotDto> ls = getDatastore(serverId).find(RobotDto.class,"type",1).asList();
    	if(ls != null){
    		for(RobotDto mm : ls){
    			all.put(mm.getPlayerId(), mm);
    		}
    	}   	
    	return all;
    }
    
    
    /**
     * 保存
     * @param serverId
     * @param userId
     * @return
     */
    public RobotDto saveRobotDto(RobotDto m){
    	getDatastore(m.getSeverId()).save(m);
    	return m;
    }
    
    /**
     * 更新
     * @param serverId
     * @param m
     */
    public void updateRobotDto(RobotDto m){
    	UpdateOperations<RobotDto> up = getDatastore(m.getSeverId()).createUpdateOperations(RobotDto.class);
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
    	getDatastore(m.getSeverId()).update(m,up);
    }
    
    /**
     * 删除
     * @param serverId
     * @param m
     */
    public void removeRobotDto(RobotDto m){
    	getDatastore(m.getSeverId()).delete(m);
    }
    
    /**
     * 更新玩家
     * @param player
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
     * 查询
     * @return
     */
    public  void loadRank(ArenaService as){
    	
    	
    	ArenaService sys = getDatastore("accounts").find(ArenaService.class).get();
    	if(sys != null){
    		as.setRank1(sys.getRank1());
    		as.setRank2(sys.getRank2());
    		as.setRank3(sys.getRank3());
    		as.setRank4(sys.getRank4());
    		as.setRank5(sys.getRank5());
    		as.setRank6(sys.getRank6());
    		as.setRank7(sys.getRank7());
    		as.setRank8(sys.getRank8());
    	}  	
    }

    /**
     * 更新
     * @param serverId
     * @param m
     */
    public void updateRank(ArenaService m){
    	ArenaService sys = getDatastore("accounts").find(ArenaService.class).get();
    	if(sys == null){
    		getDatastore("accounts").save(m);
    	}else{
	    	UpdateOperations<ArenaService> up = getDatastore("accounts").createUpdateOperations(ArenaService.class);
	    	up.set("rank1", m.getRank1());
	    	up.set("rank2", m.getRank2());
	    	up.set("rank3", m.getRank3());
	    	up.set("rank4", m.getRank4());
	    	up.set("rank5", m.getRank5());
	    	up.set("rank6", m.getRank6());
	    	up.set("rank7", m.getRank7());
	    	up.set("rank8", m.getRank8());

	    	getDatastore("accounts").update(m, up);
    	}
    }


    

}
