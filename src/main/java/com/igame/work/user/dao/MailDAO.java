package com.igame.work.user.dao;



import com.google.common.collect.Maps;
import com.igame.core.db.AbsDao;
import com.igame.work.user.dto.Mail;
import com.igame.work.user.dto.Player;
import org.mongodb.morphia.query.UpdateOperations;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author Marcus.Z
 *
 */
public class MailDAO extends AbsDao {

    private static final MailDAO domain = new MailDAO();

    public static final MailDAO ins() {
        return domain;
    }
    

    /**
     * 查询
     */
    public Map<Integer,Mail> getByPlayer(long playerId){
    	Map<Integer,Mail> all = Maps.newHashMap();
    	
    	List<Mail> ls = getDatastore().find(Mail.class, "playerId", playerId).asList();
    	if(ls != null){
    		for(Mail mm : ls){
    			all.put(mm.getId(), mm);
    		}
    	}   	
    	return all;
    }

    
    /**
     * 保存
     */
    public Mail save(Mail m){
    	getDatastore().save(m);
    	return m;
    }
    
    /**
     * 更新
     */
    public void update(Mail m){
    	UpdateOperations<Mail> up = getDatastore().createUpdateOperations(Mail.class);
    	up.set("state", m.getState());
    	getDatastore().update(m, up);
    }
    
    /**
     * 删除
     */
    public void remove(Mail m){
    	getDatastore().delete(m);
    }
    
    /**
     * 更新玩家
     */
    public void updatePlayer(Player player){
    	
    	for(Mail m : player.getMail().values()){
    		if(m.getDtate() == 1){
    			save(m);
    		}else if(m.getDtate() == 2){
    			update(m);
    		}else if(m.getDtate() == 3){
    			remove(m);
    		}
    		m.setDtate(0);
    	}

	
    }
    

}
