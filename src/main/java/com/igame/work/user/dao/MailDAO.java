package com.igame.work.user.dao;



import java.util.List;
import java.util.Map;

import org.mongodb.morphia.query.UpdateOperations;

import com.google.common.collect.Maps;
import com.igame.core.db.AbsDao;
import com.igame.work.user.dto.Mail;
import com.igame.work.user.dto.Player;

/**
 * 
 * @author Marcus.Z
 *
 */
public class MailDAO extends AbsDao {

	@Override
	public String getTableName() {
		return "Mail";
	}
	
    private static final MailDAO domain = new MailDAO();

    public static final MailDAO ins() {
        return domain;
    }
    

    /**
     * 查询
     * @param serverId
     * @param playerId
     * @return
     */
    public Map<Integer,Mail> getByPlayer(int serverId,long playerId){
    	Map<Integer,Mail> all = Maps.newHashMap();
    	
    	List<Mail> ls = getDatastore(serverId).find(Mail.class, "playerId", playerId).asList();
    	if(ls != null){
    		for(Mail mm : ls){
    			all.put(mm.getId(), mm);
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
    public Mail save(int serverId,Mail m){
    	getDatastore(serverId).save(m);
    	return m;
    }
    
    /**
     * 更新
     * @param serverId
     * @param m
     */
    public void update(int serverId,Mail m){
    	UpdateOperations<Mail> up = getDatastore(serverId).createUpdateOperations(Mail.class);
    	up.set("state", m.getState());
    	getDatastore(serverId).update(m, up);
    }
    
    /**
     * 删除
     * @param serverId
     * @param m
     */
    public void remove(int serverId,Mail m){
    	getDatastore(serverId).delete(m);
    }
    
    /**
     * 更新玩家
     * @param player
     */
    public void updatePlayer(Player player){
    	
    	for(Mail m : player.getMail().values()){
    		if(m.getDtate() == 1){
    			save(player.getSeverId(), m);
    		}else if(m.getDtate() == 2){
    			update(player.getSeverId(), m);
    		}else if(m.getDtate() == 3){
    			remove(player.getSeverId(), m);
    		}
    		m.setDtate(0);
    	}

	
    }
    

}
