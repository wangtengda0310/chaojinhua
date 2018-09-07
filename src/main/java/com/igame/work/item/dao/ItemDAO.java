package com.igame.work.item.dao;



import com.google.common.collect.Maps;
import com.igame.core.db.AbsDao;
import com.igame.work.item.dto.Item;
import com.igame.work.user.dto.Player;
import org.mongodb.morphia.query.UpdateOperations;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author Marcus.Z
 *
 */
public class ItemDAO extends AbsDao {

    private static final ItemDAO domain = new ItemDAO();

    public static ItemDAO ins() {
        return domain;
    }
    

    /**
     * 查询
     */
    public Map<Integer,Item> getItemByPlayer(long playerId){
    	Map<Integer,Item> all = Maps.newHashMap();
    	
    	List<Item> ls = getDatastore().find(Item.class, "playerId", playerId).asList();
    	if(ls != null){
    		for(Item mm : ls){
    			all.put(mm.getItemId(), mm);
    		}
    	}   	
    	return all;
    }
    
    /**
     * 保存
     */
    public Item saveItem(Item m){
    	getDatastore().save(m);
    	return m;
    }
    
    /**
     * 更新
     */
    public void updateItem(Item m){
    	UpdateOperations<Item> up = getDatastore().createUpdateOperations(Item.class)
        		.set("count", m.getCount())
        		.set("equipCounts", m.getEquipCounts())
        		;
    	getDatastore().update(m, up);
    }
    
    /**
     * 删除
     */
    public void removeItem(Item m){
    	getDatastore().delete(m);
    }
    
    /**
     * 更新玩家
     */
    public void updatePlayer(Player player){
    	
    	for(Item m : player.getItems().values()){
    		if(m.getDtate() == 1){
    			saveItem(m);
    		}else if(m.getDtate() == 2){
    			updateItem(m);
    		}else if(m.getDtate() == 3){
    			removeItem(m);
    		}
    		m.setDtate(0);
    	}
    	
    	for(Item m : player.getRemoves()){
    		removeItem(m);
    		m.setDtate(0);
    	}
	
    }
    

}
