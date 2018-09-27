package com.igame.work.quest.service;

import com.google.common.collect.Lists;
import com.igame.core.ISFSModule;
import com.igame.core.di.Inject;
import com.igame.core.di.LoadXml;
import com.igame.core.event.RemoveOnLogout;
import com.igame.core.log.GoldLog;
import com.igame.util.MyUtil;
import com.igame.work.MessageUtil;
import com.igame.work.checkpoint.guanqia.CheckPointService;
import com.igame.work.checkpoint.guanqia.RewardDto;
import com.igame.work.monster.dto.Gods;
import com.igame.work.quest.dao.QuestDAO;
import com.igame.work.quest.data.QuestData;
import com.igame.work.quest.data.QuestTemplate;
import com.igame.work.quest.dto.TaskDayInfo;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 
 * @author Marcus.Z
 *
 */
public class QuestService implements ISFSModule {
	/**
	 * 任务成就数据
	 */
	@LoadXml("questdata.xml") public QuestData questData;
	
	@Inject private QuestDAO questDAO;
	@Inject private ResourceService resourceService;
	@Inject private QuestService questService;
	@Inject private CheckPointService checkPointService;

	@RemoveOnLogout() private Map<Long, Map<Integer, TaskDayInfo>> achievement = new ConcurrentHashMap<>();//成就任务

	/**
	 * 检测玩家任务
	 */
	public void afterPlayerLogin(Player player){
			
		for(TaskDayInfo td :getAchievement(player.getPlayerId()).values()){
			if(questData.getTemplate(td.getQuestId()) == null){
				td.setDtate(3);
			}
		}
		for(QuestTemplate qt : questData.getAll()){
			
			//成就
			TaskDayInfo tf = achievement.get(player.getPlayerId()).get(qt.getQuestId());
			if(tf == null){
				if(canOpenQuest(player,achievement.get(player.getPlayerId()),qt)){
					TaskDayInfo value = new TaskDayInfo(player, qt.getQuestId());
					if(qt.getQuestType()==2 && qt.getClaim()>1 && qt.getClaim()<=25){
						questService.processTaskDetail(player, Lists.newArrayList(), value, qt.getClaim(), 0);
					}
					achievement.get(player.getPlayerId()).put(qt.getQuestId(), value);//添加新任务
				}
			}else{
//				if((qt.getClaim() == 17 && tf.getVars() == qt.getFinish() 
//						|| qt.getClaim() == 18 && tf.getVars() <= qt.getFinish()) && tf.getStatus() == 1){
//					tf.setStatus(2);
//					tf.setDtate(2);
//				}else{
					if(tf.getVars() >= qt.getFinish() && tf.getStatus() == 1){//可领取
						tf.setStatus(2);
						tf.setDtate(2);
					}
					if(qt.getClaim() == 17||qt.getClaim() == 19||qt.getClaim() == 21
							||qt.getClaim() == 23||qt.getClaim() == 24|qt.getClaim() == 25){
						processTaskDetail(player, Lists.newArrayList(), tf, qt.getClaim(), 0);
					}
//				}

			}
		}
		
	}
	
	public boolean canOpenQuest(Player player,Map<Integer, TaskDayInfo> qs,QuestTemplate qt){
		if(MyUtil.isNullOrEmpty(qt.getUnlock())){
			return true;
		}
		String[] uc = qt.getUnlock().split(",");
		if("1".equals(uc[0])){
			if(player.getPlayerLevel() >= Integer.parseInt(uc[1])){
				return true;
			}
		}else if("2".equals(uc[0])){
			if("-1".equals(uc[1]) || qs.get(Integer.parseInt(uc[1])) != null && qs.get(Integer.parseInt(uc[1])).getStatus() == 3){
				return true;
			}
		}
		
		return false;
		
	}
	
	/**
	 * 领取任务奖励
	 */
	public String getReward(Player player,TaskDayInfo td){
		
		QuestTemplate qt = questData.getTemplate(td.getQuestId());
		RewardDto reward = resourceService.getRewardDto(qt.getReward(), "100");
		resourceService.addRewarToPlayer(player, reward);
		td.setStatus(3);
		td.setDtate(2);
		td.setAction(2);
		List<TaskDayInfo> qList = Lists.newArrayList();
		qList.add(td);
		qList.addAll(endQuest(player, td));//判断是否有新开启任务
		MessageUtil.notifyQuestChange(player, qList);
		GoldLog.info(player.getSeverId(), player.getUserId(), player.getPlayerId(), GoldLog.QUESTREWARD, "#questId:" + td.getQuestId());
		return resourceService.getRewardString(reward);
				
	}
	
	/**
	 * 完成任务
	 */
	public List<TaskDayInfo> endQuest(Player player,TaskDayInfo td){
		
		List<TaskDayInfo> qs = Lists.newArrayList();
		for(QuestTemplate qt : questData.getAll()){
			if(!MyUtil.isNullOrEmpty(qt.getUnlock())){
				String[] uc = qt.getUnlock().split(",");
				if("2".equals(uc[0])){
					if(td.getQuestId() == Integer.parseInt(uc[1]) && achievement.get(player.getPlayerId()).get(Integer.parseInt(uc[1])) == null){
						TaskDayInfo e = new TaskDayInfo(player, Integer.parseInt(uc[1]));
						QuestTemplate qtt = questData.getTemplate(e.getQuestId());
						if(qtt.getQuestType()==2 && qtt.getClaim()>1 && qtt.getClaim()<=25){
							questService.processTaskDetail(player, Lists.newArrayList(), e, qtt.getClaim(), 0);
						}
						qs.add(e);
					}
				}
			}
		}
		return qs;
		
	}
	
	/**
	 * 人物升级
	 */
	public void onLevelUp(Player player){
		
		List<TaskDayInfo> qList = Lists.newArrayList();
		for(QuestTemplate qt : questData.getAll()){
			
			TaskDayInfo tf = achievement.get(player.getPlayerId()).get(qt.getQuestId());
			if(tf == null){
				if(!MyUtil.isNullOrEmpty(qt.getUnlock())){
					String[] uc = qt.getUnlock().split(",");
					if("1".equals(uc[0])){
						if(player.getPlayerLevel() >= Integer.parseInt(uc[1])){
							TaskDayInfo tt = new TaskDayInfo(player,qt.getQuestId());
							if(qt.getQuestType()==2 && qt.getClaim()>1 && qt.getClaim()<=25){
								questService.processTaskDetail(player, Lists.newArrayList(), tt, qt.getClaim(), 0);
							}
							qList.add(tt);
							achievement.get(player.getPlayerId()).put(tt.getQuestId(), tt);
						}
					}
				}
			}
		}
		MessageUtil.notifyQuestChange(player, qList);
	}
	
	/**
	 * 任务埋点处理
	 */
	public List<TaskDayInfo> processTask(Player player,int claim,int count){
		
		List<TaskDayInfo> qList = Lists.newArrayList();
		for(TaskDayInfo td : achievement.get(player.getPlayerId()).values()){
			processTaskDetail(player,qList,td,claim,count);
		}
		MessageUtil.notifyQuestChange(player, qList);
		return qList;
		
		
	}
	
	public void processTaskDetail(Player player,List<TaskDayInfo> qList,TaskDayInfo td,int claim,int count){
		if(td.getStatus() != 3){
			QuestTemplate qt = questData.getTemplate(td.getQuestId());
			if(qt != null && qt.getClaim() == claim){
				switch (claim){
					case 1://怪物提升一级
						td.setVars(count);
						processInfoStatus(player,qList, td, qt);
						break;
					case 2://冒险关卡获得X次胜利
						processVars(player, td, qt, count);
						processInfoStatus(player,qList, td, qt);
						break;
					case 3://世界事件获得X次胜利
						processVars(player, td, qt, count);
						processInfoStatus(player,qList, td, qt);
						break;
					case 4://击杀心魔x次
						processVars(player, td, qt, count);					
						processInfoStatus(player,qList, td, qt);
						break;
					case 5://赠送好友体力x次
						processVars(player, td, qt, count);
						processInfoStatus(player,qList, td, qt);
						break;
					case 6://怪物技能提升X次
						processVars(player, td, qt, count);							
						processInfoStatus(player,qList, td, qt);
						break;
					case 7://怪物强化X次
						processVars(player, td, qt, count);							
						processInfoStatus(player,qList, td, qt);
					case 8://怪物进化X次
						processVars(player, td, qt, count);							
						processInfoStatus(player,qList, td, qt);
						break;
					case 9://纹章合成x次
						processVars(player, td, qt, count);							
						processInfoStatus(player,qList, td, qt);
						break;
					case 10://挑战X次命运之门
						processVars(player, td, qt, count);							
						processInfoStatus(player,qList, td, qt);
						break;
					case 11://挑战X次无尽之森
						processVars(player, td, qt, count);							
						processInfoStatus(player,qList, td, qt);
						break;
					case 12://挑战X次星核之眼
						processVars(player, td, qt, count);							
						processInfoStatus(player,qList, td, qt);
						break;
					case 13://购买x次体力
						processVars(player, td, qt, count);							
						processInfoStatus(player,qList, td, qt);
						break;
					case 14://挑战x次竞技场
						processVars(player, td, qt, count);							
						processInfoStatus(player,qList, td, qt);
						break;
					case 15://进行x次探索
						processVars(player, td, qt, count);							
						processInfoStatus(player,qList, td, qt);
						break;
					case 16://收集X只怪物
						processVars(player, td, qt, count);							
						processInfoStatus(player,qList, td, qt);
						break;
					case 17://击败第x个BOSS关卡
						if(!MyUtil.isNullOrEmpty(player.getCheckPoint())){
							for(String cc : player.getCheckPoint().split(",")){
								if(Integer.parseInt(cc) == qt.getFinish()){
									td.setVars(Integer.parseInt(cc));
									td.setDtate(2);
									td.setAction(2);
									td.setStatus(2);
									qList.add(td);
									break;
								}
							}
						}
						break;
					case 18://竞技场达到前X名
						break;
					case 19://物种同化等级达到X级
						td.setVars(player.getTongAdd().getTongLevel());
						processInfoStatus(player, qList, td, qt);
						break;
					case 20://起源殿达到青铜段位
						break;
					case 21://神灵达到x阶
						int max = 0;
						for(Gods god : player.getGods().values()){
							if(god.getGodsLevel() > max){
								max = god.getGodsLevel();
							}
						}
						if(max > td.getVars()){
							td.setVars(max);
							processInfoStatus(player, qList, td, qt);
						}
						break;
					case 22://暴走时刻单次击杀怪物达到X只
						processVars(player, td, qt, count);							
						processInfoStatus(player,qList, td, qt);
						break;
					case 23://占领x个金矿
						int total = 0;
						if(!MyUtil.isNullOrEmpty(player.getCheckPoint())){
							for(String cc : player.getCheckPoint().split(",")){
								if(checkPointService.checkPointData.getTemplate(Integer.parseInt(cc)).getChapterType() == 2){
									total++;
								}
							}
						}
						if(total > td.getVars()){
							td.setVars(total);
							processInfoStatus(player, qList, td, qt);
						}
						break;
					case 24://占领X个洞穴
						break;
					case 25://城市占领度达到x%
						int tal = 0;
						if(!MyUtil.isNullOrEmpty(player.getCheckPoint())){
							tal = (int)(player.getCheckPoint().split(",").length/(checkPointService.checkPointData.size()+.0) * 100);
						}
						if(tal > td.getVars()){
							td.setVars(tal);
							processInfoStatus(player, qList, td, qt);
						}
						break;
					default:
						break;
				}
			}
		}
	}
	
	
	private void processVars(Player player,TaskDayInfo td,QuestTemplate qt,int count){
		if(qt.getQuestType() == 1){
			td.setVars(td.getVars() + count);
		}else if(qt.getQuestType() == 2){
			if(count > 0){
				player.addCountMap(qt.getClaim(), count);
			}		
			td.setVars(player.getCountdByType(qt.getClaim()));
		}	
	}
		
	private void processInfoStatus(Player player,List<TaskDayInfo> qList,TaskDayInfo td,QuestTemplate qt){
		
		td.setDtate(2);
		td.setAction(2);
		if(td.getVars() >= qt.getFinish()){
			td.setStatus(2);
		}
		qList.add(td);

	}


	public void loadPlayer(Player player) {
		Map<Integer, TaskDayInfo> byPlayer = questDAO.getByPlayer(player);
		for(TaskDayInfo tk :byPlayer.values()){
			QuestTemplate qt = questData.getTemplate(tk.getQuestId());
			if(qt!= null){
//    			if(qt.getQuestType() == 1){
//    				player.getDayTask().put(tk.getQuestId(), tk);
//    			}else if(qt.getQuestType() == 2){
//    			}
			}
		}
		achievement.put(player.getPlayerId(), byPlayer);
	}

	public Map<Integer, TaskDayInfo> getAchievement(long playerId) {
		return achievement.computeIfAbsent(playerId, pid->new HashMap<>());
	}

	public List<TaskDayInfo> reset(Player player) {
		List<TaskDayInfo> qList = Lists.newArrayList();
		for(TaskDayInfo td : achievement.get(player.getPlayerId()).values()){
			if(questData.getTemplate(td.getQuestId()) != null && questData.getTemplate(td.getQuestId()).getQuestType() == 1){
				if(td.getVars() > 0){
					td.setVars(0);
					td.setStatus(1);
					td.setAction(2);
					td.setDtate(2);
					qList.add(td);
				}
			}
		}
		return qList;
	}

	public void updatePlayer(Player player) {
		questDAO.updatePlayer(achievement.get(player.getPlayerId()));
	}
}
