package com.igame.work.checkpoint.guanqia;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.igame.core.ErrorCode;
import com.igame.util.GameMath;
import com.igame.util.MyUtil;
import com.igame.work.checkpoint.guanqia.data.CheckPointTemplate;
import com.igame.work.checkpoint.guanqia.data.DropDataTemplate;
import com.igame.work.checkpoint.mingyunZhiMen.data.FatedataTemplate;
import com.igame.work.checkpoint.mingyunZhiMen.MingyunZhiMenDataManager;
import com.igame.work.checkpoint.wujinZhiSen.EndlessdataTemplate;
import com.igame.work.checkpoint.wujinZhiSen.WuZhengDto;
import com.igame.work.checkpoint.wujinZhiSen.WujinZhiSenDataManager;
import com.igame.work.fight.dto.GodsDto;
import com.igame.work.fight.dto.MatchMonsterDto;
import com.igame.work.fight.service.FightUtil;
import com.igame.work.monster.dto.JiyinType;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;

import java.util.List;
import java.util.Map;


/**
 * 
 * @author Marcus.Z
 *
 */
public class CheckPointService {
	
	/**
	 * 获取奖励
	 * @param player
	 * @param chapterId 关卡ID
	 * @param win 是否胜利
	 * @param type 可能的活动ID
	 * @return
	 */
	public static RewardDto getReward(Player player,int chapterId,int win,boolean first,int type){
		
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
					other = ResourceService.ins().getRewardDto(dt.getFirstDrop(), "100");
				}else{
					other = ResourceService.ins().getRewardDto(dt.getItemDrop(), dt.getRate());
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
		if(vrl != null){
			for(String vl : vrl){
				if(JiyinType.TYPE_013.equals(vl)){//经验加成
					add += 0.2;
				}
			}
		}
		ret = ret + (int)(exp * add) + mm.getExpAadd();
		return ret;
		
	}
	
	
	/**
	 * 无尽之森刷新
	 * @param player
	 * @return
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
	 * @param floorNum
	 * @return
	 */
	public static Map<Long,Monster> getNormalFateMonster(int floorNum){
		
		FatedataTemplate ft  = MingyunZhiMenDataManager.FateData.getTemplate(floorNum);
		if(ft == null){
			return Maps.newHashMap();
		}
		String monsterId = "";
		String monsterLevel = "";
		String skillLv = "";
		String[] m1 = ft.getMonste1rLibrary().split(",");
//		if(ft.getMonste2rLibrary() == null){
//			System.err.println(ft.getFloorNum());
//		}
		String[] m2 = ft.getMonste2rLibrary().split(",");
		for(int i = 1;i<=2;i++){
			monsterId += "," + m1[GameMath.getRandInt(m1.length)];
			monsterLevel += "," + ft.getMonster1Lv();
			skillLv += "," + ft.getSkill1Lv();
		}
		for(int i = 1;i<=8;i++){
			monsterId += ","+ m2[GameMath.getRandInt(m2.length)];
			monsterLevel += ","+ ft.getMonster2Lv();
			skillLv += "," + ft.getSkill2Lv();
		}
		if(monsterId.length() > 0){
			monsterId = monsterId.substring(1);
		}
		if(monsterLevel.length() > 0){
			monsterLevel = monsterLevel.substring(1);
		}
		if(skillLv.length() > 0){
			skillLv = skillLv.substring(1);
		}
		return FightUtil.createMonster(monsterId, monsterLevel, "", skillLv,"");
		
	}


}
