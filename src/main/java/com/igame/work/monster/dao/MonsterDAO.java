package com.igame.work.monster.dao;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.igame.core.db.AbsDao;
import com.igame.work.monster.MonsterDataManager;
import com.igame.work.monster.data.MonsterTemplate;
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

	public Monster getMonsterByOid(int serverId,long objectId){
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
    			MonsterTemplate mt = MonsterDataManager.MONSTER_DATA.getMonsterTemplate(mm.getMonsterId());
    			if(mt != null && mt.getSkill() != null){
    				String[] skills = mt.getSkill().split(",");
    				if(skills != null){
    					if(mm.getSkillMap().isEmpty()){
    						mm.setDtate(2);
    					}
    					for(String skill : skills){
    						if(!mm.getSkillMap().containsKey(Integer.parseInt(skill))){
    							mm.getSkillMap().put(Integer.parseInt(skill), 1);
    						}
    					}
    	    			List<Integer> temp = Lists.newArrayList();
    	    			for(Integer skill : mm.getSkillMap().keySet()){
    	    				if(mt.getSkill().indexOf(String.valueOf(skill)) == -1){
    	    					temp.add(skill);
    	    				}
    	    			}
    	    			for(Integer rem : temp){
    	    				 mm.getSkillMap().remove(rem);
    	    			}
    				}
    			}
    			mm.initSkillString();//初始化技能列表字符串
    			mm.reCalculate(player,true);//计算值
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
