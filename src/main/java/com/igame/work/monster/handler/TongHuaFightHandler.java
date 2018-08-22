package com.igame.work.monster.handler;



import com.igame.work.monster.MonsterDataManager;
import net.sf.json.JSONObject;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.work.monster.data.StrengthenRouteTemplate;
import com.igame.work.monster.data.StrengthenlevelTemplate;
import com.igame.work.monster.data.StrengthenmonsterTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.core.log.GoldLog;
import com.igame.dto.RetVO;
import com.igame.util.GameMath;
import com.igame.util.MyUtil;
import com.igame.work.user.dto.Player;
import com.igame.work.user.dto.TongHuaDto;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class TongHuaFightHandler extends BaseHandler{
	

	@Override
	public void handleClientRequest(User user, ISFSObject params) {
		RetVO vo = new RetVO();
		if(reviceMessage(user,params,vo)){
			return;
		}
		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);
		Player player = SessionManager.ins().getSession(Long.parseLong(user.getName()));
		if(player == null){
			this.getLogger().error(this.getClass().getSimpleName()," get player failed Name:" +user.getName());
			return;
		}

		int index = jsonObject.getInt("index");
		int win = jsonObject.getInt("win");
		String info = "";
		int timeIndex = 0;
		long leftTime = 0;
		int tongExp = 0;
		int attackAdd = 0;
		int hpAdd = 0;
		int damgeRed = 0;
		float attackAddPer = 0;
		float hpAddPer = 0;
		float damgeRedPer = 0;
		float repelAddPer = 0;
		float repeledAddPer = 0;
		int addType = 0;
		String addString = "";
		String unlocks = "";
		int ret = 0;
		/**
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
			ret = ErrorCode.ERROR;
		}else{
			String[] tss = tdo.getTongStr().split(";");
			if(index< 1 || index>tss.length){
				ret = ErrorCode.ERROR;
			}else{
				String[] t = tss[index-1].split(",");//"type,state,rewardtype,rewardId,count"
				if(!"1".equals(t[0]) || "-1".equals(t[1]) || "0".equals(t[1]) || "3".equals(t[1])){
					ret = ErrorCode.ERROR;
				}else{
					if(tdo.getTimeIndex() == index){//倒计时
						timeIndex = tdo.getTimeIndex();
						leftTime = tdo.calLeftTime();
						if(leftTime > 0){
							ret = ErrorCode.ERROR;
						}else{
							t[1] = "2";
							tdo.setStartTime(0);
							tdo.setTimeCount(0);
							tdo.setTimeIndex(0);
							timeIndex = tdo.getTimeIndex();
							leftTime = tdo.calLeftTime();
						}						
					}
					if(ret == 0){
						StrengthenlevelTemplate st = MonsterDataManager.TongHuaData.getTemplate(player.getTongAdd().getTongLevel());
						StrengthenmonsterTemplate mt = MonsterDataManager.StrengthenmonsterData.getTemplate(Integer.parseInt(t[3]));
						if(win == 1){
							tongExp = mt.getMonster_rarity() * 5;
							ResourceService.ins().addTongExp(player, tongExp);
							
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

							StrengthenRouteTemplate srt = MonsterDataManager.StrengthenRouteData.getTemplate(tdo.getSid());
							if(srt != null && "1".equals(t[0])){
								String points = null;
								for(String temp : srt.getCoordinate().split(";")){
									if(temp.split(":")[0].equals(String.valueOf(index -1))){
										points = temp;
										break;
									}
								}
								if(points != null){
									String unlock = points.split(":")[2];
									if(unlock != null){
										String[] unls = unlock.split(",");
										for(String un : unls){
											String[] t1 = tss[Integer.parseInt(un)].split(",");
											if("-1".equals(t1[1])){
												t1[1] = "0";
												tss[Integer.parseInt(un)] = MyUtil.toString(t1, ",");
												unlocks += ("," + (Integer.parseInt(un) +1));
											}
										}
									}
								}
								if(unlocks.length() > 0){
									unlocks = unlocks.substring(1);
								}
							}
							
	 
							MessageUtil.notiyTongHuaAddChange(player);
							MessageUtil.notiyMonsterChange(player, player.reCalMonsterValue());
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
		}
		
		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
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

		send(MProtrol.toStringProtrol(MProtrol.TONGHUA_FIGHT), vo, user);
	}

	
}
