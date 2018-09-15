package com.igame.work.checkpoint.guanqia;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.igame.core.ISFSModule;
import com.igame.core.SessionManager;
import com.igame.core.di.Inject;
import com.igame.core.log.GoldLog;
import com.igame.core.quartz.TimeListener;
import com.igame.util.GameMath;
import com.igame.util.MyUtil;
import com.igame.work.ErrorCode;
import com.igame.work.MessageUtil;
import com.igame.work.checkpoint.guanqia.data.CheckPointTemplate;
import com.igame.work.checkpoint.guanqia.data.DropDataTemplate;
import com.igame.work.checkpoint.mingyunZhiMen.MingyunZhiMenDataManager;
import com.igame.work.checkpoint.mingyunZhiMen.data.FatedataTemplate;
import com.igame.work.checkpoint.wujinZhiSen.EndlessdataTemplate;
import com.igame.work.checkpoint.wujinZhiSen.WuZhengDto;
import com.igame.work.checkpoint.wujinZhiSen.WujinZhiSenDataManager;
import com.igame.work.checkpoint.xinmo.XingMoDto;
import com.igame.work.fight.dto.GodsDto;
import com.igame.work.fight.dto.MatchMonsterDto;
import com.igame.work.fight.service.FightUtil;
import com.igame.work.monster.dto.JiyinType;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dto.Player;
import com.igame.work.user.dto.RobotDto;
import com.igame.work.user.load.ResourceService;
import com.igame.work.user.service.RobotService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 
 * @author Marcus.Z
 *
 */
public class CheckPointService implements ISFSModule, TimeListener {
	@Inject private RobotService robotService;
	@Inject private ResourceService resourceService;
	@Inject private SessionManager sessionManager;

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

	//定时执行玩家金币关卡计算
	private void calPlayerTimeRes(){
		for(Player player : sessionManager.getSessions().values()){
			synchronized (player.getTimeLock()) {
				if(!player.getTimeResCheck().isEmpty()){
					for(Map.Entry<Integer, Integer> m : player.getTimeResCheck().entrySet()){
						CheckPointTemplate ct = GuanQiaDataManager.CheckPointData.getTemplate(m.getKey());
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
						if(!player.getXinMo().containsKey(Integer.parseInt(cc)) && GuanQiaDataManager.CheckPointData.getTemplate(Integer.parseInt(cc)).getChapterType() != 2 && Integer.parseInt(cc) <=140){//有心魔的关卡不在生成列表中
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
		CheckPointTemplate ct = GuanQiaDataManager.CheckPointData.getTemplate(chapterId);
		DropDataTemplate dt = GuanQiaDataManager.DropData.getTemplate(ct.getDropId());

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
	
	
	/**
	 * 无尽之森刷新
	 */
	public static int refEndlessRef(Player player){
		
		int ret = 0;
		int lv = player.getPlayerLevel();
		if(lv < 21){
			lv = 21;
		}
		List<EndlessdataTemplate> ls = Lists.newArrayList();
		for(EndlessdataTemplate et : WujinZhiSenDataManager.EndlessData.getAll()){
			if(lv >= Integer.parseInt(et.getLvRange().split(",")[0]) && lv <= Integer.parseInt(et.getLvRange().split(",")[1])){
				ls.add(et);
			}
		}
		if(ls.isEmpty()){
			ret = ErrorCode.ERROR;
		}else{
			player.getWuMap().clear();
			player.getWuZheng().clear();
			player.setWuGods(new GodsDto());
			player.setWuNai(0);
			player.getWuEffect().clear();
			for(EndlessdataTemplate et : ls){
				String str = String.valueOf(et.getNum());
				str+=";"+String.valueOf(et.getDifficulty())+";0";
				String[] mons = et.getMonsterId().split(",");
				List<String> temp = Lists.newArrayList();
				List<Integer> lvs = Lists.newArrayList();
				for(int i = 1;i <=5;i++){
					temp.add(mons[GameMath.getRandInt(mons.length)]);
					lvs.add(GameMath.getRandomInt(lv+Integer.parseInt(et.getMonsterLv().split(",")[0]), lv+Integer.parseInt(et.getMonsterLv().split(",")[1])));
				}
				str += ";"+MyUtil.toString(temp, ",");
				str += ";"+MyUtil.toStringInt(lvs, ",");
				str += ";0;0";
				player.getWuMap().put(et.getNum(), str);
			}
			
		}
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
	
	/**
	 * 生成命运之门普通门怪物数据
	 */
	public static Map<Long,Monster> getNormalFateMonster(int floorNum){
		
		FatedataTemplate ft  = MingyunZhiMenDataManager.FateData.getTemplate(floorNum);
		if(ft == null){
			return Maps.newHashMap();
		}
		StringBuilder monsterId = new StringBuilder();
		StringBuilder monsterLevel = new StringBuilder();
		StringBuilder skillLv = new StringBuilder();
		String[] m1 = ft.getMonste1rLibrary().split(",");
//		if(ft.getMonste2rLibrary() == null){
//			System.err.println(ft.getFloorNum());
//		}
		String[] m2 = ft.getMonste2rLibrary().split(",");
		for(int i = 1;i<=2;i++){
			monsterId.append(",").append(m1[GameMath.getRandInt(m1.length)]);
			monsterLevel.append(",").append(ft.getMonster1Lv());
			skillLv.append(",").append(ft.getSkill1Lv());
		}
		for(int i = 1;i<=8;i++){
			monsterId.append(",").append(m2[GameMath.getRandInt(m2.length)]);
			monsterLevel.append(",").append(ft.getMonster2Lv());
			skillLv.append(",").append(ft.getSkill2Lv());
		}
		if(monsterId.length() > 0){
			monsterId = new StringBuilder(monsterId.substring(1));
		}
		if(monsterLevel.length() > 0){
			monsterLevel = new StringBuilder(monsterLevel.substring(1));
		}
		if(skillLv.length() > 0){
			skillLv = new StringBuilder(skillLv.substring(1));
		}
		return FightUtil.createMonster(monsterId.toString(), monsterLevel.toString(), "", skillLv.toString(),"");
		
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
}
