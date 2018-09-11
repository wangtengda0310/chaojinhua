package com.igame.work.quest.dao;



import com.igame.core.db.AbsDao;
import com.igame.work.quest.QuestDataManager;
import com.igame.work.quest.data.QuestTemplate;
import com.igame.work.quest.dto.TaskDayInfo;
import com.igame.work.user.dto.Player;
import org.mongodb.morphia.query.UpdateOperations;

import java.util.List;

/**
 * 
 * @author Marcus.Z
 *
 */
public class QuestDAO extends AbsDao {

    /**
     * 查询
     */
    public void getByPlayer(Player player){
    	
    	List<TaskDayInfo> ls = getDatastore().find(TaskDayInfo.class, "playerId", player.getPlayerId()).asList();
    	for(TaskDayInfo tk :ls){
    		QuestTemplate qt = QuestDataManager.QuestData.getTemplate(tk.getQuestId());
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
     */
    public TaskDayInfo save(TaskDayInfo m){
    	getDatastore().save(m);
    	return m;
    }
    
    /**
     * 更新
     */
    public void update(TaskDayInfo m){
    	UpdateOperations<TaskDayInfo> up = getDatastore().createUpdateOperations(TaskDayInfo.class);
    	up.set("status", m.getStatus());
    	up.set("vars", m.getVars());
    	getDatastore().update(m, up);
    }
    
    /**
     * 删除
     */
    public void remove(TaskDayInfo m){
    	getDatastore().delete(m);
    }
    
    /**
     * 更新玩家
     */
    public void updatePlayer(Player player){
    	
    	for(TaskDayInfo m : player.getAchievement().values()){
    		if(m.getDtate() == 1){
    			save(m);
    		}else if(m.getDtate() == 2){
    			update(m);
    		}else if(m.getDtate() == 3){
    			remove(m);
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
