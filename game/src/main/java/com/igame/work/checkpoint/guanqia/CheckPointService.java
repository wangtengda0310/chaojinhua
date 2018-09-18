package com.igame.work.checkpoint.guanqia;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.igame.core.ISFSModule;
import com.igame.core.SessionManager;
import com.igame.core.di.Inject;
import com.igame.core.di.LoadXml;
import com.igame.core.log.GoldLog;
import com.igame.core.quartz.TimeListener;
import com.igame.util.GameMath;
import com.igame.util.MyUtil;
import com.igame.work.MessageUtil;
import com.igame.work.checkpoint.guanqia.data.CheckPointData;
import com.igame.work.checkpoint.guanqia.data.CheckPointTemplate;
import com.igame.work.checkpoint.guanqia.data.DropData;
import com.igame.work.checkpoint.guanqia.data.DropDataTemplate;
import com.igame.work.checkpoint.mingyunZhiMen.GateService;
import com.igame.work.checkpoint.tansuo.TansuoData;
import com.igame.work.checkpoint.worldEvent.WorldEventData;
import com.igame.work.checkpoint.wujinZhiSen.EndlessService;
import com.igame.work.checkpoint.wujinZhiSen.WuZhengDto;
import com.igame.work.checkpoint.xinmo.XingMoDto;
import com.igame.work.fight.dto.MatchMonsterDto;
import com.igame.work.monster.dto.JiyinType;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dto.Player;
import com.igame.work.user.dto.ResCdto;
import com.igame.work.user.dto.RobotDto;
import com.igame.work.user.load.ResourceService;
import com.igame.work.user.service.RobotService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 
 * @author Marcus.Z
 *
 */
public class CheckPointService implements ISFSModule, TimeListener {
	/**
	 * 关卡列表
	 */
	@LoadXml("chapterdata.xml") public CheckPointData checkPointData;
	/**
	 * 掉落包
	 */
	@LoadXml("dropdata.xml") public DropData dropData;
	@LoadXml("questteam.xml") public TansuoData tansuoData;
	@LoadXml("worldevent.xml")public WorldEventData worldEventData;

	@Inject private GateService gateService;
	@Inject private RobotService robotService;
	@Inject private ResourceService resourceService;
	@Inject private SessionManager sessionManager;
	@Inject private EndlessService endlessService;

	private Map<Long, Long> enterCheckPointTime = new ConcurrentHashMap<>();//进入的关卡时间
	private Map<Long, Long> enterWordEventTime = new ConcurrentHashMap<>();//进入世界事件关卡时间
	private Map<Long, Integer> enterCheckpointId = new ConcurrentHashMap<>();//进入的关卡ID



	private Map<Long, Map<String,Object>> lastBattleParam = new ConcurrentHashMap<>();//上次战斗的关卡参数

	public Map<String, Object> getLastBattleParam(long playerId) {
		return lastBattleParam.get(playerId);
	}

	public void setLastBattleParam(long playerId, Map<String,Object> param) {
		lastBattleParam.put(playerId,param);
	}

	public String getString(Player player, RewardDto reward, List<Monster> ll, String monsterExpStr) {
		StringBuilder monsterExpStrBuilder = new StringBuilder(monsterExpStr);
		for(long mid : player.getTeams().get(player.getCurTeam()).getTeamMonster()){
			if(-1 != mid){
				Monster mm = player.getMonsters().get(mid);
				if(mm != null){
					int mmExp = getTotalExp(mm, reward.getExp());
					monsterExpStrBuilder.append(mid);
					if(resourceService.addMonsterExp(player, mid, mmExp, false) == 0){
						ll.add(mm);
						monsterExpStrBuilder.append(",").append(mmExp).append(";");
					}else{
						monsterExpStrBuilder.append(",0;");
					}
				}
			}
		}
		monsterExpStr = monsterExpStrBuilder.toString();
		return monsterExpStr;
	}

	@Override
	public void minute() {

		calPlayerTimeRes();
	}

	private Map<Long,Object> timeLock = new ConcurrentHashMap<>();//定时同步锁

	public Object getTimeLock(Player player) {
		return timeLock.computeIfAbsent(player.getPlayerId(), pid->new Object());
	}
	//定时执行玩家金币关卡计算
	private void calPlayerTimeRes(){	// todo 这逻辑停复杂 全服计算还有锁 会阻塞了定时器线程
		for(Player player : sessionManager.getSessions().values()){
			synchronized (getTimeLock(player)) {
				if(!player.getTimeResCheck().isEmpty()){
					for(Map.Entry<Integer, Integer> m : player.getTimeResCheck().entrySet()){
						CheckPointTemplate ct = checkPointData.getTemplate(m.getKey());
						if(ct != null && !MyUtil.isNullOrEmpty(ct.getDropPoint())){
							if(m.getValue() < ct.getMaxTime() * 60){//没到上限
								player.getTimeResCheck().put(m.getKey(), m.getValue()+ 1);
								if(m.getValue() >= 60 && m.getValue() % 60 == 0){//到了一小时更新，推送金币数
									RewardDto dto = resourceService.getResRewardDto(ct.getDropPoint(), m.getValue(), ct.getMaxTime() * 60);
									MessageUtil.notifyTimeResToPlayer(player, m.getKey(), dto);
								}

								//零时测试
//        						RewardDto dto = resourceService.getResRewardDto(ct.getDropPoint(), m.getValue(), ct.getMaxTime() * 60);
//        						MessageUtil.notifyTimeResToPlayer(player, m.getKey(), dto);
							}
						}
					}
				}
				processXinMo(player);
				processRes(player);
			}
		}
	}

	private void processRes(Player player){
		for(Map.Entry<Integer, Integer> m : player.getResMintues().entrySet()){
			if(m.getKey() == 6 && player.getTongRes() >= 15 || m.getKey() == 7 && player.getXing() >= 10){
				continue;
			}else{
				player.getResMintues().put(m.getKey(), m.getValue() + 1);
			}

		}
		if(player.getResMintues().get(3) != null){
			if(player.getResMintues().get(3) >=6){
				resourceService.addPhysica(player, 1);
				MessageUtil.notifyCDDown(player, 3);
				player.getResMintues().put(3, 0);
			}

		}
		if(player.getResMintues().get(4) != null){
			if(player.getResMintues().get(4) >=60){
				resourceService.addSao(player, 1);
				player.getResMintues().put(4, 0);
			}

		}
		if(player.getResMintues().get(6) != null){
			if(player.getResMintues().get(6) >=120 && player.getTongRes() < 15){
				resourceService.addTongRes(player, 1);
				player.getResMintues().put(6, 0);
			}

		}
		if(player.getResMintues().get(7) != null){
			if(player.getResMintues().get(7) >=120  && player.getXing() < 10){
				resourceService.addXing(player, 1);
				player.getResMintues().put(7, 0);
			}

		}

	}

	private void processXinMo(Player player){

		boolean change = false;
		StringBuilder removeId = new StringBuilder();
		List<XingMoDto> lx = Lists.newArrayList();
		if(!MyUtil.isNullOrEmpty(player.getCheckPoint()) && player.getCheckPoint().split(",").length >= 30){

			long now = System.currentTimeMillis();
			//先是删掉24小时没打的
			List<Integer> remove = Lists.newArrayList();
			for(XingMoDto xin :player.getXinMo().values()){
				if(xin.getStatTime() == 0 || now - xin.getStatTime() >= 24 * 3600 * 1000){
					remove.add(xin.getCheckPiontId());
					removeId.append(",").append(xin.getCheckPiontId());
				}
			}
			if(!remove.isEmpty()){
				for(Integer id :remove){
					player.getXinMo().remove(id);
					GoldLog.info("remove xinMo:" + id);
				}
				change = true;
			}
			//判断是否刷新新的
			if(player.getXinMoMinuts() < 60){
				player.setXinMoMinuts(player.getXinMoMinuts() + 1);
			}else{
				if(player.getXinMoMinuts() >= 60){//到了一小时  可能刷新
					String[] ccs =  player.getCheckPoint().split(",");
					List<Integer> ls = Lists.newArrayList();
					for(String cc : ccs){
						if(!player.getXinMo().containsKey(Integer.parseInt(cc)) && checkPointData.getTemplate(Integer.parseInt(cc)).getChapterType() != 2 && Integer.parseInt(cc) <=140){//有心魔的关卡不在生成列表中
							ls.add(Integer.parseInt(cc));
						}
					}
					if(!ls.isEmpty() && player.getXinMo().size() < 25){
						Integer cid = ls.get(GameMath.getRandInt(ls.size()));
						XingMoDto xx = new XingMoDto();
						xx.setCheckPiontId(cid);
						Map<String, RobotDto> robs = robotService.getRobot();
						if(!robs.isEmpty()){
							RobotDto rb = new ArrayList<>(robs.values()).get(GameMath.getRandInt(robs.size()));
							xx.setPlayerId(rb.getPlayerId());
							xx.setMid(rb.getName());
							xx.setPlayerFrameId(rb.getPlayerFrameId());
							xx.setPlayerHeadId(rb.getPlayerHeadId());
							xx.setStatTime(now);
							player.getXinMo().put(cid, xx);
							player.setXinMoMinuts(0);//重新累算
							change = true;
							lx.add(xx);
							GoldLog.info("add xinMo:" + cid);
						}else{
							xx.setMid("");
						}

					}
				}
			}
		}
		if(change){
			if(removeId.length() > 0){
				removeId = new StringBuilder(removeId.substring(1));
			}
			MessageUtil.notifyXingMoChange(player, removeId.toString(),lx);//推送更新
		}

	}

	/**
	 * 获取奖励
	 * @param chapterId 关卡ID
	 * @param win 是否胜利
	 * @param type 可能的活动ID
	 */
	public RewardDto getReward(Player player,int chapterId,int win,boolean first,int type){
		
		RewardDto reward = new RewardDto();
		int ret = 0;
		CheckPointTemplate ct = checkPointData.getTemplate(chapterId);
		DropDataTemplate dt = dropData.getTemplate(ct.getDropId());

		if(win == 1){//赢了
			reward.setExp(ct.getExp());
			if(dt != null){
				reward.setGold(dt.getGoldDrop());
				RewardDto other = null;
				if(first){
					other = resourceService.getRewardDto(dt.getFirstDrop(), "100");
				}else{
					other = resourceService.getRewardDto(dt.getItemDrop(), dt.getRate());
				}
				reward.setGold(reward.getGold() + other.getGold());
				reward.setDiamond(other.getDiamond());
				reward.items = other.items;
				reward.monsters = other.monsters;
			}
		}
		reward.setRet(ret);
		return reward;
	}
	
	
	public static int getTotalExp(Monster mm,int exp){
		
		int ret = exp;
		double add = 0.0;
		String[] vrl = mm.getBreaklv().split(",");
		for(String vl : vrl){
			if(JiyinType.TYPE_013.equals(vl)){//经验加成
				add += 0.2;
			}
		}
		ret = ret + (int)(exp * add) + mm.getExpAadd();
		return ret;
		
	}
	

	public static WuZhengDto parsePlayer(Player player){
		WuZhengDto wd = new WuZhengDto();
		wd.setWuGods(player.getWuGods().getGodsType() + ","+player.getWuGods().getGodsLevel());
		for(MatchMonsterDto wt : player.getWuZheng().values()){
			String str = String.valueOf(wt.getMonsterId());
			str += ";" + wt.getLevel();
			str += ";" + wt.getHp();
			str += ";" + wt.getHpInit();
			str += ";" + wt.getBreaklv();
			wd.getWuMons().add(str);
		}
		return wd;
	}
	
	public static boolean isFullWuHp(Player player){
		boolean is = true;
		if(player.getWuZheng().isEmpty()){
			return true;
		}
		for(MatchMonsterDto mto : player.getWuZheng().values()){
			if(mto.getHp() < mto.getHpInit()){
				is = false;
				return is;
			}
		}
		return is;
	}


	public void setEnterCheckPointTime(Player player) {
		enterCheckPointTime.put(player.getPlayerId(), System.currentTimeMillis());
	}

	public void setEnterWordEventTime(Player player) {
		enterWordEventTime.put(player.getPlayerId(), System.currentTimeMillis());
	}

	public int getEnterCheckpointId(Player player) {
		return enterCheckpointId.get(player.getPlayerId());
	}

	public void setEnterCheckpointId(Player player, int chapterId) {
		enterCheckpointId.put(player.getPlayerId(), chapterId);
	}

	public void afterPlayerLogin(Player player) {
        for(int i = 3;i<=7;i++){    // todo enum
            if( i == 5){
                continue;
            }
            player.getResMintues().putIfAbsent(i, 0);
        }
        if(player.getTimeResCheck() == null){
            player.setTimeResCheck(Maps.newHashMap());
        }
		if(!MyUtil.isNullOrEmpty(player.getCheckPoint())){
			for(String cc :player.getCheckPoint().split(",")){//已过关卡有，但是资源关卡时间计数器没有添加
				int cid = Integer.parseInt(cc);
				CheckPointTemplate ct = checkPointData.getTemplate(cid);
				if(ct != null && ct.getChapterType() == 2 && !MyUtil.isNullOrEmpty(ct.getDropPoint())
						&& !player.getTimeResCheck().containsKey(cid)){
					player.getTimeResCheck().put(cid, ct.getMaxTime() * 60);
				}
			}
			for(int id : player.getTimeResCheck().keySet()){//已过关卡里没有的，资源关卡时间计数器有的删除
				if(!player.hasCheckPoint(String.valueOf(id))){
					player.getTimeResCheck().remove(id);
				}
			}
		}
	}

	private Map<Long, Map<Integer, ResCdto>> resC = new ConcurrentHashMap<>();//金币资源关卡

	public void calPlayerTimeRes(Player player){
		synchronized (getTimeLock(player)) {
			resourceService.calRes(player);
			if(!player.getTimeResCheck().isEmpty()){
				long now = System.currentTimeMillis();
				for(Map.Entry<Integer, Integer> m : player.getTimeResCheck().entrySet()){
					CheckPointTemplate ct = checkPointData.getTemplate(m.getKey());
					if(ct != null && !MyUtil.isNullOrEmpty(ct.getDropPoint())){
						int total = m.getValue();//原有分钟数
						if(player.getLoginoutTime() != null){//计算新的
							int timeAdd = (int)((now - player.getLoginoutTime().getTime())/60000);
							total += timeAdd;
							if(total > ct.getMaxTime() * 60){
								total = ct.getMaxTime()  * 60;
							}
							player.getTimeResCheck().put(m.getKey(), total);
						}
						RewardDto dto = resourceService.getResRewardDto(ct.getDropPoint(), total, ct.getMaxTime() * 60);
//    					MessageUtil.notifyTimeResToPlayer(player, m.getKey(), dto);
						resC.computeIfAbsent(player.getPlayerId(),pid->new HashMap<>())
								.put(ct.getChapterId(),new ResCdto(ct.getChapterId(), ResourceService.getRewardString(dto)));

					}
				}
			}
			initXinMo(player);

		}

        player.calLeftTime();//算每个心魔剩余时间
	}

	//登录时候初始化
	private void initXinMo(Player player){

		if(!MyUtil.isNullOrEmpty(player.getCheckPoint()) && player.getCheckPoint().split(",").length >= 30){

			long now = System.currentTimeMillis();
			//先是删掉24小时没打的
			List<Integer> remove = Lists.newArrayList();
			for(XingMoDto xin :player.getXinMo().values()){
				if(xin.getStatTime() == 0 || now - xin.getStatTime()  >= 24 * 3600 * 1000){
					remove.add(xin.getCheckPiontId());
				}
			}
			if(!remove.isEmpty()){
				for(Integer id :remove){
					player.getXinMo().remove(id);
					GoldLog.info("remove xinMo:" + id);
				}
			}

			//判断是否刷新新的
			if(player.getLoginoutTime() != null){
				int timeAdd = (int)((now - player.getLoginoutTime().getTime())/60000);//分钟数
				int total = player.getXinMoMinuts() +  timeAdd;
				int count  = total /60; //几个小时-可刷几个
				if(count > 0){
					String[] ccs =  player.getCheckPoint().split(",");
					List<Integer> ls = Collections.synchronizedList(Lists.newArrayList());
					for(String cc : ccs){
						if(!player.getXinMo().containsKey(Integer.parseInt(cc)) && checkPointData.getTemplate(Integer.parseInt(cc)).getChapterType() != 2 && Integer.parseInt(cc) <=140){//有心魔的关卡不在生成列表中
							ls.add(Integer.parseInt(cc));
						}
					}
					if(!ls.isEmpty()){
						if(count > 25-player.getXinMo().size()){
							count = 25-player.getXinMo().size();
						}
						while(!ls.isEmpty() && count > 0){
							int index = GameMath.getRandInt(ls.size());
							Integer cid = ls.remove(index);
							XingMoDto xx = new XingMoDto();
							xx.setCheckPiontId(cid);
							Map<String, RobotDto> robs = robotService.getRobot();
							if(!robs.isEmpty()){
								RobotDto rb = new ArrayList<>(robs.values()).get(GameMath.getRandInt(robs.size()));
								xx.setPlayerId(rb.getPlayerId());
								xx.setMid(rb.getName());
								xx.setPlayerFrameId(rb.getPlayerFrameId());
								xx.setPlayerHeadId(rb.getPlayerHeadId());
							}else{
								xx.setMid("");//待改变和生成具体机器人数据
							}
							xx.setStatTime(now);
							player.getXinMo().put(cid, xx);
							count--;
							GoldLog.info("add xinMo:" + cid);
							if(player.getXinMo().size() >= 25){
								break;
							}
						}

					}
					player.setXinMoMinuts(total % 60);//保存剩余分钟数
				}else{
					player.setXinMoMinuts(total);//保存剩余分钟数
				}

			}
		}
	}

	public Collection<ResCdto> getRec(Player player) {
		return resC.computeIfAbsent(player.getPlayerId(),pid->new HashMap<>()).values();
	}
}