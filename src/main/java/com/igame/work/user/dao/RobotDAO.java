package com.igame.work.user.dao;



import java.util.List;
import java.util.Map;

import org.mongodb.morphia.query.UpdateOperations;

import com.google.common.collect.Maps;
import com.igame.core.db.AbsDao;
import com.igame.core.log.GoldLog;
import com.igame.work.user.dto.RobotDto;

/**
 * 
 * @author Marcus.Z
 *
 */
public class RobotDAO extends AbsDao {

	@Override
	public String getTableName() {
		return "Robot";
	}
	
    private static final RobotDAO domain = new RobotDAO();

    public static final RobotDAO ins() {
        return domain;
    }
    

    /**
     * 查询
     * @param serverId
     * @param playerId
     * @return
     */
    public Map<String,RobotDto> loadData(int serverId){
    	Map<String,RobotDto> all = Maps.newHashMap();
    	
    	List<RobotDto> ls = getDatastore(serverId).find(RobotDto.class,"type", 0).asList();
    	
//    	List<RobotDto> ls = getDatastore(serverId).find(RobotDto.class).asList();
//    	GoldLog.info("loadDataloadData:"+ls.size());
    	if(ls != null){
    		for(RobotDto mm : ls){
    			all.put(mm.getName(), mm);
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


    

}
