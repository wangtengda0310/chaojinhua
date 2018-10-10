package com.igame.work.monster.handler;


import com.google.common.collect.Lists;
import com.igame.core.di.Inject;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.core.log.GoldLog;
import com.igame.util.GameMath;
import com.igame.util.MyUtil;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.work.monster.data.StrengthenRouteTemplate;
import com.igame.work.monster.data.StrengthenlevelTemplate;
import com.igame.work.monster.data.StrengthenmonsterTemplate;
import com.igame.work.monster.dto.Monster;
import com.igame.work.monster.service.MonsterService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.dto.TongHuaDto;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * 
 * @author Marcus.Z
 *
 */
public class TongHuaFightHandler extends ReconnectedHandler {

	@Inject
	private MonsterService monsterService;
	@Inject
	private ResourceService resourceService;

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int index = jsonObject.getInt("index");
		int win = jsonObject.getInt("win");
		String info;
		int timeIndex = 0;
		long leftTime = 0;
		int tongExp = 0;
		int attackAdd;
		int hpAdd;
		int damgeRed;
		float attackAddPer;
		float hpAddPer;
		float damgeRedPer;
		float repelAddPer;
		float repeledAddPer;
		int addType = 0;
		String addString = "";
		String unlocks = "";
		/*
		 *"type,state,rewardtype,rewardId,count;";//如1,0,1,1,1;2,-1,3,200005,100
		 *
		 * type:0-普通 1-怪物 2-强化袍子 3-进化袍子 4-神性袍子
	     * state:-1-未解锁 0-已解锁/可开启 1-倒计时/进行中 2-可领取 3-已领取/已完结	
		 * rewardtype:1-怪物关卡  3-道具 5-同化经验值
		 * rewardId:当rewardtype为1 对应strengthenmonster.xml中的num 
		 *  当rewardtype为3 对应道具ID
		 *  当rewardtype为5 为5
		 * count:对应的数量
		 */
		TongHuaDto tdo = player.getTonghua();
		if(tdo == null){
			return error(ErrorCode.ERROR);
		}else{
			String[] tss = tdo.getTongStr().split(";");
			if(index< 1 || index>tss.length){
				return error(ErrorCode.ERROR);
			}else{
				String[] t = tss[index-1].split(",");//"type,state,rewardtype,rewardId,count"
				if(!"1".equals(t[0]) || "-1".equals(t[1]) || "0".equals(t[1]) || "3".equals(t[1])){
					return error(ErrorCode.ERROR);
				}else{
					if(tdo.getTimeIndex() == index){//倒计时
						timeIndex = tdo.getTimeIndex();
						leftTime = tdo.calLeftTime();
						if(leftTime > 0){
							return error(ErrorCode.ERROR);
						}else{
							t[1] = "2";
							tdo.setStartTime(0);
							tdo.setTimeCount(0);
							tdo.setTimeIndex(0);
							timeIndex = tdo.getTimeIndex();
							leftTime = tdo.calLeftTime();
						}						
					}
					StrengthenlevelTemplate st = monsterService.tongHuaData.getTemplate(player.getTongAdd().getTongLevel());
					StrengthenmonsterTemplate mt = monsterService.strengthenmonsterData.getTemplate(Integer.parseInt(t[3]));
					if(win == 1){
						tongExp = mt.getMonster_rarity() * 5;
						resourceService.addTongExp(player, tongExp);

						attackAdd = GameMath.getRandomCount(Integer.parseInt(mt.getAtk_up().split("-")[0]), Integer.parseInt(mt.getAtk_up().split("-")[1]));//攻击加成值
						hpAdd = GameMath.getRandomCount(Integer.parseInt(mt.getHp_up().split("-")[0]), Integer.parseInt(mt.getHp_up().split("-")[1]));//HP加成值
						damgeRed = GameMath.getRandomCount(Integer.parseInt(mt.getInjured().split("-")[0]), Integer.parseInt(mt.getInjured().split("-")[1]));//伤害减少值

						attackAddPer = GameMath.getRandomFloat(Float.parseFloat(mt.getAtk_up1().split("-")[0]), Float.parseFloat(mt.getAtk_up1().split("-")[1]));//攻击加成百分比
						hpAddPer = GameMath.getRandomFloat(Float.parseFloat(mt.getHp_up1().split("-")[0]), Float.parseFloat(mt.getHp_up1().split("-")[1]));//HP加成百分比
						damgeRedPer = GameMath.getRandomFloat(Float.parseFloat(mt.getInjured1().split("-")[0]), Float.parseFloat(mt.getInjured1().split("-")[1]));//伤害减少百分比
						repelAddPer = GameMath.getRandomFloat(Float.parseFloat(mt.getRepel().split("-")[0]), Float.parseFloat(mt.getRepel().split("-")[1]));//霸气加成百分比
						repeledAddPer = GameMath.getRandomFloat(Float.parseFloat(mt.getRepeled().split("-")[0]), Float.parseFloat(mt.getRepeled().split("-")[1]));//伤害减少百分比

						addType = GameMath.getRandomCount(1,8);

						if(addType == 1){
							player.getTongAdd().setAttackAdd(player.getTongAdd().getAttackAdd() + attackAdd);
							if(player.getTongAdd().getAttackAdd() > st.getAtk_limit()){
								player.getTongAdd().setAttackAdd(st.getAtk_limit());
							}
							addString = String.valueOf(attackAdd);
						}else if(addType == 2){
							player.getTongAdd().setHpAdd(player.getTongAdd().getHpAdd() + hpAdd);
							if(player.getTongAdd().getHpAdd() > st.getHp_limit()){
								player.getTongAdd().setHpAdd(st.getHp_limit());
							}
							addString = String.valueOf(hpAdd);
						}else if(addType == 3){
							player.getTongAdd().setDamgeRed(player.getTongAdd().getDamgeRed() + damgeRed);
							if(player.getTongAdd().getDamgeRed() > st.getInjured_limit()){
								player.getTongAdd().setDamgeRed(st.getInjured_limit());
							}
							addString = String.valueOf(damgeRed);
						}else if(addType == 4){
							player.getTongAdd().setAttackAddPer(player.getTongAdd().getAttackAddPer() + attackAddPer);
							if(player.getTongAdd().getAttackAddPer() > st.getAtk_limit1()){
								player.getTongAdd().setAttackAddPer(st.getAtk_limit1());
							}
							addString = GameMath.formatNumber(attackAddPer);
						}else if(addType == 5){
							player.getTongAdd().setHpAddPer(player.getTongAdd().getHpAddPer() + hpAddPer);
							if(player.getTongAdd().getHpAddPer() > st.getHp_limit1()){
								player.getTongAdd().setHpAddPer(st.getHp_limit1());
							}
							addString = GameMath.formatNumber(hpAddPer);
						}else if(addType == 6){
							player.getTongAdd().setDamgeRedPer(player.getTongAdd().getDamgeRedPer() + damgeRedPer);
							if(player.getTongAdd().getDamgeRedPer() > st.getInjured_limit1()){
								player.getTongAdd().setDamgeRedPer(st.getInjured_limit1());
							}
							addString = GameMath.formatNumber(damgeRedPer);
						}else if(addType == 7){
							player.getTongAdd().setRepelAddPer(player.getTongAdd().getRepelAddPer() + repelAddPer);
							if(player.getTongAdd().getRepelAddPer() > st.getRepel_limit()){
								player.getTongAdd().setRepelAddPer(st.getRepel_limit());
							}
							addString = GameMath.formatNumber(repelAddPer);
						}else if(addType == 8){
							player.getTongAdd().setRepeledAddPer(player.getTongAdd().getRepeledAddPer() + repeledAddPer);
							if(player.getTongAdd().getRepeledAddPer() > st.getRepeled_limit()){
								player.getTongAdd().setRepeledAddPer(st.getRepeled_limit());
							}
							addString = GameMath.formatNumber(repeledAddPer);
						}

						StrengthenRouteTemplate srt = monsterService.strengthenRouteData.getTemplate(tdo.getSid());
						if(srt != null && "1".equals(t[0])){
							String points = null;
							for(String temp : srt.getCoordinate().split(";")){
								if(temp.split(":")[0].equals(String.valueOf(index -1))){
									points = temp;
									break;
								}
							}
							unlocks = getString(unlocks, tss, points);
							if(unlocks.length() > 0){
								unlocks = unlocks.substring(1);
							}
						}


						MessageUtil.notifyTongHuaAddChange(player);
						MessageUtil.notifyMonsterChange(player, reCalMonsterValue(player));
						t[1] = "3";
					}

					tss[index-1] = MyUtil.toString(t, ",");
					info = tss[index-1];
					tdo.setTongStr(MyUtil.toString(tss, ";"));

					boolean allOver = true;
					for(String tt : tss){
						if(!"3".equals(tt.split(",")[1])){
							allOver = false;
							break;
						}
					}
					if(allOver && tdo.calRefLeftTime() > 0){
						tdo.setStartRefTime(0);
						tdo.calRefLeftTime();
					}
					GoldLog.info(player.getSeverId(), player.getUserId(), player.getPlayerId(), GoldLog.TONGHUAFIGHT, "#index:" + index + "#win:" +win+"#info:"+tss[index-1]);


				}
			}
		}

		vo.addData("index", index);
		vo.addData("win", win);
		vo.addData("info", info);
		vo.addData("timeIndex", timeIndex);
		vo.addData("leftTime", leftTime);
		vo.addData("tongExp", tongExp);
		
		vo.addData("addType", addType);
		vo.addData("addString", addString);
		
		vo.addData("unlock", unlocks);

		return vo;
	}

	private List<Monster> reCalMonsterValue(Player player) {
		List<Monster> ll = Lists.newArrayList();
		player.setFightValue(0);
        /*String[] mms = player.teams[0].split(",");
        for (String mid : mms) {
            if (!"-1".equals(mid)) {
                Monster mm = player.monsters.get(Long.parseLong(mid));
                if (mm != null) {
                    mm.reCalculate(player, true);
                    player.fightValue += mm.getFightValue();
                    ll.add(mm);
                }
            }
        }*/
		long[] teamMonster = player.getTeams().get(player.getCurTeam()).getTeamMonster();
		for (long mid : teamMonster) {
			if (-1 != mid) {
				Monster mm = player.getMonsters().get(mid);
				if (mm != null) {
					monsterService.reCalculate(player, mm.getMonsterId(), mm, true);
					player.setFightValue(player.getFightValue() + mm.getFightValue());
					ll.add(mm);
				}
			}
		}
		return ll;
	}

	static String getString(String unlocks, String[] tss, String points) {
		if(points != null){
			String unlock = points.split(":")[2];
			if(unlock != null){
				String[] unls = unlock.split(",");
				StringBuilder unlocksBuilder = new StringBuilder(unlocks);
				for(String un : unls){
					String[] t1 = tss[Integer.parseInt(un)].split(",");
					if("-1".equals(t1[1])){
						t1[1] = "0";
						tss[Integer.parseInt(un)] = MyUtil.toString(t1, ",");
						unlocksBuilder.append(",").append(Integer.parseInt(un) + 1);
					}
				}
				unlocks = unlocksBuilder.toString();
			}
		}
		return unlocks;
	}

	@Override
    public int protocolId() {
		return MProtrol.TONGHUA_FIGHT;
	}

}
