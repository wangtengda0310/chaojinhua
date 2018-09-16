package com.igame.work.monster.dao;


import com.google.common.collect.Maps;
import com.igame.core.db.AbsDao;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dto.Player;
import org.mongodb.morphia.query.UpdateOperations;

import java.util.List;
import java.util.Map;

/**
 * 怪物DAO
 * @author Marcus.Z
 *
 */
public class MonsterDAO extends AbsDao {

    private static final MonsterDAO domain = new MonsterDAO();

    public static MonsterDAO ins() {
        return domain;
    }

    public List<Monster> getALLMonster(){
    	return getDatastore().find(Monster.class).asList();
    }

	public Monster getMonsterByOid(long objectId){
		return getDatastore().find(Monster.class,"objectId", objectId).get();
	}
    

    /**
     * 根据角色ID查询所有怪物列表
     */
    public Map<Long,Monster> getMonsterByPlayer(Player player, long playerId){
    	Map<Long,Monster> all = Maps.newHashMap();
    	
    	List<Monster> ls = getDatastore().find(Monster.class, "playerId", playerId).asList();
    	if(ls != null){
    		for(Monster mm : ls){
    			all.put(mm.getObjectId(), mm);
    		}
    	}   	
    	return all;
    }
    
    /**
     * 保存新的怪物
     */
    public Monster saveNewMonster(Monster m){
    	getDatastore().save(m);
    	return m;
    }
    
    public void updateMonster(Monster m){
    	UpdateOperations<Monster> up = getDatastore().createUpdateOperations(Monster.class)
        		.set("hp", m.getHp())
        		.set("isLock", m.getIsLock())
        		.set("level", m.getLevel())
        		.set("monsterId", m.getMonsterId())
        		.set("exp", m.getExp())
        		.set("breaklv", m.getBreaklv())
        		.set("skillMap", m.getSkillMap())
        		.set("equip", m.getEquip())
        		.set("skillExp", m.getSkillExp())		
        		;
    	getDatastore().update(m, up);
    }
    
    public void removeMonster(Monster m){
    	getDatastore().delete(m);
    }
    
    /**
     * 更新玩家
     */
    public void updatePlayer(Player player){
    	
    	for(Monster m : player.getMonsters().values()){
    		if(m.getDtate() == 1){
    			saveNewMonster(m);
    		}else if(m.getDtate() == 2){
    			updateMonster(m);
    		}else if(m.getDtate() == 3){
    			removeMonster(m);
    		}
    		m.setDtate(0);
    	}
	
    }
    

}
