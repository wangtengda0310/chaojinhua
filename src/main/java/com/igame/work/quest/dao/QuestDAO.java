package com.igame.work.quest.dao;



import java.util.List;
import java.util.Map;

import org.mongodb.morphia.query.UpdateOperations;

import com.google.common.collect.Maps;
import com.igame.core.data.DataManager;
import com.igame.core.data.template.QuestTemplate;
import com.igame.core.db.AbsDao;
import com.igame.work.quest.dto.TaskDayInfo;
import com.igame.work.user.dto.Mail;
import com.igame.work.user.dto.Player;

/**
 * 
 * @author Marcus.Z
 *
 */
public class QuestDAO extends AbsDao {

	@Override
	public String getTableName() {
		return "Quest";
	}
	
    private static final QuestDAO domain = new QuestDAO();

    public static final QuestDAO ins() {
        return domain;
    }
    

    /**
     * 查询
     * @param serverId
     * @param playerId
     * @return
     */
    public void getByPlayer(int serverId,Player player){
    	
    	List<TaskDayInfo> ls = getDatastore(serverId).find(TaskDayInfo.class, "playerId", player.getPlayerId()).asList();
    	for(TaskDayInfo tk :ls){
    		QuestTemplate qt = DataManager.ins().QuestData.getTemplate(tk.getQuestId());
    		if(qt!= null){
//    			if(qt.getQuestType() == 1){
//    				player.getDayTask().put(tk.getQuestId(), tk);
//    			}else if(qt.getQuestType() == 2){
    				player.getAchievement().put(tk.getQuestId(), tk);
//    			}
    		}
    	}
    	
    }

    
    /**
     * 保存
     * @param serverId
     * @param userId
     * @return
     */
    public TaskDayInfo save(int serverId,TaskDayInfo m){
    	getDatastore(serverId).save(m);
    	return m;
    }
    
    /**
     * 更新
     * @param serverId
     * @param m
     */
    public void update(int serverId,TaskDayInfo m){
    	UpdateOperations<TaskDayInfo> up = getDatastore(serverId).createUpdateOperations(TaskDayInfo.class);
    	up.set("status", m.getStatus());
    	up.set("vars", m.getVars());
    	getDatastore(serverId).update(m, up);
    }
    
    /**
     * 删除
     * @param serverId
     * @param m
     */
    public void remove(int serverId,TaskDayInfo m){
    	getDatastore(serverId).delete(m);
    }
    
    /**
     * 更新玩家
     * @param player
     */
    public void updatePlayer(Player player){
    	
    	for(TaskDayInfo m : player.getAchievement().values()){
    		if(m.getDtate() == 1){
    			save(player.getSeverId(), m);
    		}else if(m.getDtate() == 2){
    			update(player.getSeverId(), m);
    		}else if(m.getDtate() == 3){
    			remove(player.getSeverId(), m);
    		}
    		m.setDtate(0);
    	}
//    	for(TaskDayInfo m : player.getDayTask().values()){
//    		if(m.getDtate() == 1){
//    			save(player.getSeverId(), m);
//    		}else if(m.getDtate() == 2){
//    			update(player.getSeverId(), m);
//    		}else if(m.getDtate() == 3){
//    			remove(player.getSeverId(), m);
//    		}
//    		m.setDtate(0);
//    	}

	
    }
    

}
