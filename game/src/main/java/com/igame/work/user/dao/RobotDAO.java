package com.igame.work.user.dao;



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
public class RobotDAO extends AbsDao {

    /**
     * 查询
     */
    public Map<String,RobotDto> loadData(){
    	Map<String,RobotDto> all = Maps.newHashMap();
    	
    	List<RobotDto> ls = getDatastore().find(RobotDto.class,"type", 0).asList();
    	
//    	List<RobotDto> ls = getAccountDatastore(serverId).find(RobotDto.class).asList();
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


    

}
