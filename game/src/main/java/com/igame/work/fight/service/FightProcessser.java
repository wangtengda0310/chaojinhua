package com.igame.work.fight.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.igame.core.di.Inject;
import com.igame.core.log.ExceptionLog;
import com.igame.util.MyUtil;
import com.igame.util.ThreadPoolManager;
import com.igame.work.fight.FightService;
import com.igame.work.fight.data.SkillTemplate;
import com.igame.work.fight.dto.FightBase;
import com.igame.work.fight.dto.FightCmd;
import com.igame.work.fight.dto.RetFightCmd;
import com.igame.work.monster.dto.Effect;
import com.igame.work.monster.dto.Monster;

import java.util.Iterator;
import java.util.List;
import java.util.Map;



/**
 * 
 * @author Marcus.Z
 *
 */
public class FightProcessser {
	
	private static final FightProcessser domain = new FightProcessser();

	@Inject private FightService fightService;
	@Inject private FightEffectService fightEffectService;
	@Inject private FightUtil fightUtil;
    public static final FightProcessser ins() {
        return domain;
    }
    
    /**
     * 到了范围出手，客户端传双方位移动置信息，根据双方位置信息和出手规则调用此方法，此方法为怪物出手要做的逻辑的主入口
     */
    public List<RetFightCmd> processAction(FightBase fb,long attackerId,long targetId){
    	List<RetFightCmd> retCmd = Lists.newArrayList();
    	Monster attacker = getMonsterById(fb, attackerId);
    	if(attacker != null && attacker.getFightProp().getHp() > 0){
    		boolean normalAttack = true;
    		attacker.getFightProp().addAttackCount();
    		
    		//判断是否触发技能攻击
    		for(Integer skillId : attacker.getSkillMap().keySet()){
    			SkillTemplate skillTemplate = fightService.skillData.getTemplate(skillId);
        		if(skillTemplate != null && skillTemplate.getSubtype() != 2 
        				&& skillTemplate.getTouch() == 1 && !MyUtil.isNullOrEmpty(skillTemplate.getTouchValue()) && attacker.getFightProp().getAttackCount() >= Integer.parseInt(skillTemplate.getTouchValue().split(",")[0])){
        			normalAttack = false;
        			FightCmd fc = new FightCmd(1,  attackerId, 2, skillTemplate.getSkillId(), targetId);
        			processCmd(fb, fc,retCmd);
        		}
    		}
    		//判断是否触发技能攻击
    		
    		if(normalAttack){//无技能攻击，为普通攻击
    			FightCmd fc = new FightCmd(1,  attackerId, 1, 0, targetId);
    			processCmd(fb, fc,retCmd);
    		}else{
    			attacker.getFightProp().setAttackCount(0);//重置出手次数
    		}
    	}
    	return retCmd;
    }

    
    
    /**
     * 处理战斗指令 指令主入口
     */
    public RetFightCmd processCmd(FightBase fb,FightCmd fc,List<RetFightCmd> retCmd){
    	
    	if(fb != null){
    		synchronized (fb.getLock()) {
    			if(fb.getWinner() != -1){//战斗已结束
    				return null;
    			}
        		Monster attacker = getMonsterById(fb,fc.getAttackerId());
        		if(attacker == null || attacker.getFightProp().getHp() <= 0){//发起者不存在或者死亡
        			return null;
        		}
        		if(fc.getCmdType() == 1 && attacker.getAtkType() == 1){//普通攻击
        			RetFightCmd ret = processNormalCmd(fb, fc, attacker,retCmd); 
        			return ret;
        		}else if(fc.getCmdType() == 2 && attacker.getAtkType() == 2){//技能攻击
        			RetFightCmd ret = processSkillCmd(fb, fc, attacker,retCmd);
        			if(ret != null){
        				onSkillUsed(fb,attacker, fc.getCmdId(),retCmd);
        			}
        			return ret;
        		}
			}

    	}
    	
    	return null;
    }
    
    
    /**
     * 使用技能触发
     */
    public void onSkillUsed(FightBase fb,Monster attacker,int skllId,List<RetFightCmd> retCmd){
    	
    	//触发技能触发引起的技能触发
    	if(attacker.getFightProp().getHp() > 0){
			for(Integer skillId : attacker.getSkillMap().keySet()){
				SkillTemplate skillTemplate = fightService.skillData.getTemplate(skillId);
	    		if(skillTemplate != null && skillTemplate.getSubtype() != 2 && skillTemplate.getTouch() == 4 ){
	    			FightCmd fc = new FightCmd(1,  attacker.getObjectId(), 2, skillTemplate.getSkillId(), 0);
	    			processCmd(fb, fc,retCmd);
	    		}
			}
    	}
    	
    	
		//触发同场技能是否触发节能
		for(Monster mm : fb.getFightA().getMonsters().values()){
			if(mm.getFightProp().getHp() > 0){
				for(Integer skillId : mm.getSkillMap().keySet()){
					SkillTemplate skillTemplate = fightService.skillData.getTemplate(skillId);
		    		if(skillTemplate != null && skillTemplate.getSubtype() != 2 && skillTemplate.getTouch() == 8 ){
		    			int touchValue = Integer.parseInt(skillTemplate.getTouchValue().split(",")[0]);
		    			if(skllId == touchValue){
			    			FightCmd fc = new FightCmd(1,  mm.getObjectId(), 2, skillTemplate.getSkillId(), 0);
			    			processCmd(fb, fc,retCmd);
		    			}
		    		}
				}
			}
		}
		for(Monster mm : fb.getFightB().getMonsters().values()){
			if(mm.getFightProp().getHp() > 0){
				for(Integer skillId : mm.getSkillMap().keySet()){
					SkillTemplate skillTemplate = fightService.skillData.getTemplate(skillId);
		    		if(skillTemplate != null && skillTemplate.getSubtype() != 2 && skillTemplate.getTouch() == 8 ){
		    			int touchValue = Integer.parseInt(skillTemplate.getTouchValue().split(",")[0]);
		    			if(skllId == touchValue){
			    			FightCmd fc = new FightCmd(1,  mm.getObjectId(), 2, skillTemplate.getSkillId(), 0);
			    			processCmd(fb, fc,retCmd);
		    			}
		    		}
				}
			}
		}
    }
    
    
    /**
     * 时间触发技能
     */
    public void onOverTime(FightBase fb,List<RetFightCmd> retCmd){
    	
    	
    	long now = System.currentTimeMillis();
		for(Monster mm : fb.getFightA().getMonsters().values()){
			if(mm.getFightProp().getHp() > 0){
				for(Integer skillId : mm.getSkillMap().keySet()){
					SkillTemplate skillTemplate = fightService.skillData.getTemplate(skillId);
		    		if(skillTemplate != null && skillTemplate.getSubtype() != 2 && skillTemplate.getTouch() == 3 ){
		    			int touchValue = Integer.parseInt(skillTemplate.getTouchValue().split(",")[0]) * 1000;
		    			if(touchValue > 0 && (mm.getFightProp().getLastActTime().get(skillId) == null && now-fb.getStartTime() >= touchValue || now-mm.getFightProp().getLastActTime().get(skillId) >= touchValue)){
			    			FightCmd fc = new FightCmd(1,  mm.getObjectId(), 2, skillTemplate.getSkillId(), 0);
			    			processCmd(fb, fc,retCmd);
			    			mm.getFightProp().getLastActTime().put(skillId, now);
		    			}
		    		}
				}
			}
		}
		for(Monster mm : fb.getFightB().getMonsters().values()){
			if(mm.getFightProp().getHp() > 0){
				for(Integer skillId : mm.getSkillMap().keySet()){
					SkillTemplate skillTemplate = fightService.skillData.getTemplate(skillId);
		    		if(skillTemplate != null && skillTemplate.getSubtype() != 2 && skillTemplate.getTouch() == 3 ){
		    			int touchValue = Integer.parseInt(skillTemplate.getTouchValue().split(",")[0]) * 1000;
		    			if(touchValue > 0 && (mm.getFightProp().getLastActTime().get(skillId) == null && now-fb.getStartTime() >= touchValue || now-mm.getFightProp().getLastActTime().get(skillId) >= touchValue)){
			    			FightCmd fc = new FightCmd(1,  mm.getObjectId(), 2, skillTemplate.getSkillId(), 0);
			    			processCmd(fb, fc,retCmd);
			    			mm.getFightProp().getLastActTime().put(skillId, now);
		    			}
		    		}
				}
			}
		}
    }
    
    
    /**
     * 处理普通攻击
     */
    public RetFightCmd processNormalCmd(FightBase fb,FightCmd fc,Monster attacker,List<RetFightCmd> retCmd){
    	
		Monster target = getMonsterById(fb,fc.getTargetId());
		if(target == null || target.getFightProp().getHp() <= 0 || attacker.getPlayerId() == target.getPlayerId()){//目标不存在或者死亡或自己打自己
			ExceptionLog.error("NormalCmd error:此次普通攻击指令无效。");
			return null;
		}
		int frontHp = target.getFightProp().getHp();
		int value = target.getFightProp().reduceHp(FightUtil.getNormalDamge(attacker, target));
		onReduceHp(fb,frontHp,target,retCmd);//是否触发减血和死亡技能
		RetFightCmd rcd = new RetFightCmd();
		rcd.setAttackerType(fc.getAttackerType());
		rcd.setAttackerId(fc.getAttackerId());
		rcd.setCmdType(fc.getCmdType());
		rcd.setCmdId(fc.getCmdId());
		rcd.setTargetId(fc.getTargetId());
		
		//指令处理
		StringBuilder sb = new StringBuilder();
		sb.append(target.getObjectId()).append(",").append(value).append(",").append(target.getFightProp().getHp());
		
		rcd.setTarget(sb.toString());
//		rcd.setHp(String.valueOf(value));
		retCmd.add(rcd);//添加到返回指令集合
		if(isEndFight(fb) != -1){//战斗已经结束
			endFight(fb);
		}
		onAttacked(fb,target,retCmd);//可能触发技能
		return rcd;
		
    }

    public void onAttack(Monster attacker){
    	
    }
    
    /**
     * 减/加 HP  触发
     */
    public void onReduceHp(FightBase fb,int frontHp,Monster target,List<RetFightCmd> retCmd){
    	
    	if(target.getFightProp().getHp() > 0){
			//判断是否触发技能攻击
			for(Integer skillId : target.getSkillMap().keySet()){
				SkillTemplate skillTemplate = fightService.skillData.getTemplate(skillId);
	    		if(skillTemplate != null && skillTemplate.getSubtype() != 2 && skillTemplate.getTouch() == 2 && !MyUtil.isNullOrEmpty(skillTemplate.getTouchValue())){
	    			int touchValue = Integer.parseInt(skillTemplate.getTouchValue().split(",")[0]);
	    			double frontPec = frontHp*100/(target.getFightProp().getHpInit()+.0);
	    			double currPec = target.getFightProp().getHp()*100/(target.getFightProp().getHpInit()+.0);
	    			if(touchValue > 0 && frontPec >= touchValue && currPec < touchValue || touchValue < 0 && frontPec <= touchValue && currPec > touchValue){
		    			FightCmd fc = new FightCmd(1,  target.getObjectId(), 2, skillTemplate.getSkillId(), 0);
		    			processCmd(fb, fc,retCmd);
	    			}

	    		}
			}
			//判断是否触发技能攻击
    	}else{
			onDie(fb,target,retCmd);
		}
    }
    
    
    /**
     * 死亡触发
     */
    public void onDie(FightBase fb,Monster target,List<RetFightCmd> retCmd){
    	
		//判断是否触发技能攻击
		for(Integer skillId : target.getSkillMap().keySet()){
			SkillTemplate skillTemplate = fightService.skillData.getTemplate(skillId);
    		if(skillTemplate != null && skillTemplate.getSubtype() != 2 && skillTemplate.getTouch() == 5 ){
    			FightCmd fc = new FightCmd(1,  target.getObjectId(), 2, skillTemplate.getSkillId(), 0);
    			processCmd(fb, fc,retCmd);
    		}
		}
		//判断是否触发技能攻击
		
		
		//指定怪物死亡触发的技能
		for(Monster mm : fb.getFightA().getMonsters().values()){
			if(mm.getFightProp().getHp() > 0){
				for(Integer skillId : mm.getSkillMap().keySet()){
					SkillTemplate skillTemplate = fightService.skillData.getTemplate(skillId);
		    		if(skillTemplate != null && skillTemplate.getSubtype() != 2 && skillTemplate.getTouch() == 9 ){
		    			int touchValue = Integer.parseInt(skillTemplate.getTouchValue().split(",")[0]);
		    			if(target.getMonsterId() == touchValue){
			    			FightCmd fc = new FightCmd(1,  mm.getObjectId(), 2, skillTemplate.getSkillId(), 0);
			    			processCmd(fb, fc,retCmd);
		    			}
		    		}
				}
			}
		}
		for(Monster mm : fb.getFightB().getMonsters().values()){
			if(mm.getFightProp().getHp() > 0){
				for(Integer skillId : mm.getSkillMap().keySet()){
					SkillTemplate skillTemplate = fightService.skillData.getTemplate(skillId);
		    		if(skillTemplate != null && skillTemplate.getSubtype() != 2 && skillTemplate.getTouch() == 9 ){
		    			int touchValue = Integer.parseInt(skillTemplate.getTouchValue().split(",")[0]);
		    			if(target.getMonsterId() == touchValue){
			    			FightCmd fc = new FightCmd(1,  mm.getObjectId(), 2, skillTemplate.getSkillId(), 0);
			    			processCmd(fb, fc,retCmd);
		    			}
		    		}
				}
			}
		}
    	
    }
    
    /**
     * 处理被攻击
     */
    public void onAttacked(FightBase fb,Monster target,List<RetFightCmd> retCmd){
    	if(target.getFightProp().getHp() > 0){
    		target.getFightProp().addAttackedCount();//增加被攻击次数
    		
    		//判断是否触发技能攻击
    		for(Integer skillId : target.getSkillMap().keySet()){
    			SkillTemplate skillTemplate = fightService.skillData.getTemplate(skillId);
        		if(skillTemplate != null && skillTemplate.getSubtype() != 2 
        				&& skillTemplate.getTouch() == 6 && !MyUtil.isNullOrEmpty(skillTemplate.getTouchValue()) && target.getFightProp().getAttackedCount() >= Integer.parseInt(skillTemplate.getTouchValue().split(",")[0])){
        			FightCmd fc = new FightCmd(1,  target.getObjectId(), 2, skillTemplate.getSkillId(), 0);
        			processCmd(fb, fc,retCmd);
        		}
    		}
    		//判断是否触发技能攻击
    		
    		target.getFightProp().setAttackedCount(0);
    	}
    }
    
    /**
     * 处理技能逻辑
     */
    public RetFightCmd processSkillCmd(FightBase fb,FightCmd fc,Monster attacker,List<RetFightCmd> retCmd){
    	
    	
		long now = System.currentTimeMillis();
		boolean can = true;//是否正常发出技能
		Iterator<Effect> efs = attacker.getFightProp().getEffectList().iterator();
		while(efs.hasNext()){
			Effect ef = efs.next();
			if(ef.getEffectId() == 130){
				if(ef.getAvailTime() <= 0 || ef.getStartTime() + ef.getAvailTime() >= now){
					can = false;
				}else{
					if(ef.getAvailTime() > 0){
						efs.remove();
					}			
				}
			}
		}
		if(!can){//被沉默了
			return null;
		}
    	
    	RetFightCmd rcd = new RetFightCmd();
		rcd.setAttackerType(fc.getAttackerType());
		rcd.setAttackerId(fc.getAttackerId());
		rcd.setCmdType(fc.getCmdType());
		rcd.setCmdId(fc.getCmdId());
		rcd.setTargetId(fc.getTargetId());
		
    	int skillId = fc.getCmdId();
    	if(attacker.getSkillMap().containsKey(skillId)){
    		SkillTemplate skillTemplate = fightService.skillData.getTemplate(skillId);
    		if(skillTemplate != null && skillTemplate.getSubtype() != 2){
    			int skillLevel = attacker.getSkillMap().get(skillId);//技能等级
        		int skillExp = attacker.getSkillExp().get(skillId) == null ? 0 : attacker.getSkillExp().get(skillId);//技能经验值
        		if(skillTemplate.getSkillType() == 1){//伤害类型技能
        			Map<Integer,List<Monster>> targets = getTarget(fb, fc,attacker, skillTemplate);
        			if(targets.isEmpty() || targets.get(1) == null){//没有任何生效目标
        				return null;
        			}
        			String targetStr = "";
        			for(Monster mm : targets.get(1)){//伤害
        				int damge = fightUtil.getSkillDamge(attacker, mm, skillTemplate, skillLevel, skillExp);
        				int frontHp = mm.getFightProp().getHp();
        				mm.getFightProp().reduceHp(damge);
        				onReduceHp(fb,frontHp,mm,retCmd);//是否触发减血和死亡技能
        				targetStr +=  ";" + mm.getObjectId() + ","+damge+","+mm.getFightProp().getHp();
        			}
        			if(targetStr.length() > 1){
        				rcd.setTarget(targetStr.substring(1));
        			}
        			
        			//BUFFER
        			retCmd.add(rcd);//先添加到返回指令集合，因为触发BUFFER生效之后有可能再触发技能
        			
        			if(targets.get(2) != null && !MyUtil.isNullOrEmpty(skillTemplate.getEffect())){
        				fightEffectService.processAddEffect(fb,fc, attacker, skillTemplate, targets.get(2), rcd,retCmd);//处理主动技能BUFFER
        			}
        			processEffectTouch(fb, fc, attacker, skillTemplate.getEffectTouch(), rcd,retCmd);//处理被动技能BUFFER
        			
        			for(Monster mm : targets.get(1)){//被攻击者可能触发技能
        				onAttacked(fb,mm,retCmd);
        			}
        		}else if(skillTemplate.getSkillType() == 2){//纯buffer类型技能
        			Map<Integer,List<Monster>> targets = getTarget(fb, fc,attacker, skillTemplate);
        			if(targets.isEmpty() || targets.get(1) == null  || MyUtil.isNullOrEmpty(skillTemplate.getEffect())){//没有任何生效目标
        				return null;
        			}
        			String[] effes = skillTemplate.getEffect().split(";");
        			
        			retCmd.add(rcd);//先添加到返回指令集合，因为触发BUFFER生效之后有可能再触发技能
        			
        			fightEffectService.processAddEffect(fb,fc, attacker, skillTemplate, targets.get(1), rcd,retCmd);//处理主动技能BUFFER
        			if(targets.get(2) != null  && effes.length > 1){//第二批
        				fightEffectService.processAddEffect(fb,fc, attacker, skillTemplate, targets.get(2), rcd,retCmd);//处理主动技能BUFFER
        			}
        			processEffectTouch(fb, fc, attacker, skillTemplate.getEffectTouch(), rcd,retCmd);//处理被动技能BUFFER
        			
        		}else{
        			return null;
        		}
    			
    			if(isEndFight(fb) != -1){//战斗已经结束
    				endFight(fb);
    			}
    		}else{
    			return null;
    		}
    	}else{
    		return null;
    	}
    	
		return rcd;
    	
    }
    
    
    
    /**
     * 处理被动技能BUFFER
     */
    public void processEffectTouch(FightBase fb,FightCmd fc,Monster attacker,String effectTouch,RetFightCmd rcd,List<RetFightCmd> retCmd){
    	if(!MyUtil.isNullOrEmpty(effectTouch)){
    		String[] ets = effectTouch.split(";");
    		for(String et : ets){
    			SkillTemplate skillTemplate = fightService.skillData.getTemplate(Integer.parseInt(et));
    			if(skillTemplate != null){
        			Map<Integer,List<Monster>> targets = getTarget(fb, fc,attacker, skillTemplate);
        			if(targets.isEmpty() || targets.get(1) == null || MyUtil.isNullOrEmpty(skillTemplate.getEffect())){//没有任何生效目标
        				continue;
        			}
        			String[] effes = skillTemplate.getEffect().split(";");
        			fightEffectService.processAddEffect(fb,fc,attacker, skillTemplate, targets.get(1), rcd,retCmd);//处理被动技能BUFFER
        			if(targets.get(2) != null  && effes.length > 1){//第二批
        				fightEffectService.processAddEffect(fb,fc, attacker, skillTemplate, targets.get(2), rcd,retCmd);//处理被动技能BUFFER
        			}
    			}
    		}
    	}
    	
    }
    
    /**
     * 获取生效目标
     */
    public Map<Integer,List<Monster>> getTarget(FightBase fb,FightCmd fc,Monster attacker,SkillTemplate skillTemplate){
    	
    	Map<Integer,List<Monster>> map = Maps.newHashMap();
		String campStr = skillTemplate.getCamp();//生效阵营
		String campTargetcampStr = skillTemplate.getCampTarget();//生效目标
		if(!MyUtil.isNullOrEmpty(campStr) && !MyUtil.isNullOrEmpty(campTargetcampStr)){
			String[] campArr = campStr.split(";");
			String[] campTargetArr = campTargetcampStr.split(";");
	    	if(skillTemplate.getRangeType()==1){//范围类型
				List<Monster> one = getTargetByRange(fb, fc, attacker, skillTemplate, campArr[0], campTargetArr[0]);//第一批目标 技能伤害或BUFFER
				map.put(1, one);//第一生效目标
				if(campArr.length > 1){
					List<Monster> two = getTargetByRange(fb, fc, attacker, skillTemplate, campArr[1], campTargetArr[1]);//第二批目标 BUFFER
					map.put(2, two);//第二生效目标
				}
	    		
	    	}else if(skillTemplate.getRangeType()==2){//非范围类型
	
				List<Monster> one = getTargetByNoRange(fb, fc, attacker, skillTemplate, campArr[0], campTargetArr[0]);//第一批目标 技能伤害或BUFFER
				map.put(1, one);
				if(campArr.length > 1){
					List<Monster> two = getTargetByNoRange(fb, fc, attacker, skillTemplate, campArr[1], campTargetArr[1]);//第二批目标 BUFFER
					map.put(2, two);
				}    		
	    	}
		}
    	return map;
    	
    }
    
    /**
     * 固定区域类型取值
     */
    public static List<Monster> getTargetByFixedRange(FightBase fb,long sender ,String camp,String campTarget,int center,int range){
    	
    	List<Monster> ts = getTargetByCamp(fb, sender, camp);
    	Iterator<Monster> its = ts.iterator();
    	while(its.hasNext()){
    		Monster mm = its.next();
    		if(mm.getFightProp().getHp() <=0 || Math.abs(mm.getFightProp().getX() - center) > range){
    			its.remove();//删除不在范围内的怪物
    		}
    	}
    	return getTargetByCampTarget(fb, 0, null, campTarget, ts);
    	
    }
    
    
    /**
     * 获取范围类型的目标集合
     */
    private List<Monster> getTargetByRange(FightBase fb,FightCmd fc,Monster attacker,SkillTemplate skillTemplate,String camp,String campTarget){
    	
    	List<Monster> ts = getTargetByCamp(fb, attacker.getPlayerId(), camp);
    	float center = -10000;
    	if(skillTemplate.getCentre() == 1){//命中目标
    		 Monster other = getMonsterById(fb,fc.getTargetId());
    		 if(other == null){
    			 return null;
    		 }
    		 center = other.getFightProp().getX();
    	}else if(skillTemplate.getCentre() == 2){//自身
    		center = attacker.getFightProp().getX();
    	}else{
    		return null;
    	}
    	if(center == -10000){
    		return null;
    	}
    	Iterator<Monster> its = ts.iterator();
    	while(its.hasNext()){
    		Monster mm = its.next();
    		if(mm.getFightProp().getHp() <=0 || Math.abs(mm.getFightProp().getX() - center) > skillTemplate.getRange()){
    			its.remove();//删除不在范围内的怪物
    		}
    	}
    	return getTargetByCampTarget(fb, fc.getTargetId(), attacker, campTarget, ts);
    	
    }
    
    /**
     * 获取非范围类型的目标集合
     */
    private List<Monster> getTargetByNoRange(FightBase fb,FightCmd fc,Monster attacker,SkillTemplate skillTemplate,String camp,String campTarget){

    	List<Monster> ts = getTargetByCamp(fb, attacker.getPlayerId(), camp);
    	return getTargetByCampTarget(fb, fc.getTargetId(), attacker, campTarget, ts);
    	
    }
    
    /**
     * 根据生效阵营获取目标对象集合
     */
    private static List<Monster> getTargetByCamp(FightBase fb,long playerId,String camp){
    	
    	Map<Long,Monster> self = null;//自方
    	Map<Long,Monster> enemy = null;//对方
    	if(playerId == fb.getFightA().getPlayerId()){
    		self = fb.getFightA().getMonsters();
    		enemy = fb.getFightB().getMonsters();
    	}else{
    		self = fb.getFightB().getMonsters();
    		enemy = fb.getFightA().getMonsters();
    	}
    	List<Monster> ts = Lists.newArrayList();//待选目标方怪物集合
    	if("0".equals(camp)){//自己方
    		ts.addAll(self.values());
    	}else if("1".equals(camp)){//敌方
    		ts.addAll(enemy.values());
    	}else if("2".equals(camp)){//全部
    		ts.addAll(self.values());
    		ts.addAll(enemy.values());
    	}
    	return ts;
    	
    }
    
    
    /**
     * 根据生效阵营目标获取对象集合
     */
    private static List<Monster> getTargetByCampTarget(FightBase fb,long targetId,Monster attacker,String campTarget,List<Monster> ts){
    	List<Monster> ret = Lists.newArrayList();
    	switch (campTarget){
    		case "0"://自身
    			if(attacker != null){
    				ret.add(attacker);
    			}			
    			break;
    		case "1"://命中目标
    			Monster t1 = getMonsterById(fb, targetId);
    			if(t1 != null && t1.getFightProp().getHp() > 0){
    				ret.add(t1);
    			}	
    			break;
    		case "2"://全体
    			for(Monster t2 : ts){
    				if(t2.getFightProp().getHp() > 0){
    					ret.add(t2);
    				}
    			}
    			break;
    		case "3"://血量最少
    			Monster r3 = null;
    			for(Monster t3 : ts){
    				if(t3.getFightProp().getHp() > 0){
    					if(r3 == null || t3.getFightProp().getHp() < r3.getFightProp().getHp()){
    						r3 = t3;
    					}
    				}
    			}
    			if(r3 != null){
    				ret.add(r3);
    			}
    			break;
    		case "4"://最前排  待改
    			for(Monster t4 : ts){
    				if(t4.getFightProp().getHp() > 0){
    					ret.add(t4);
    					break;
    				}
    			}
    			break;
    		case "5"://技能命中目标
    			Monster t5 = getMonsterById(fb, targetId);
    			if(t5 != null && t5.getFightProp().getHp() > 0){
    				ret.add(t5);
    			}	
    			break;
    		case "6"://对我造成攻击的目标   待改

    			break;
    		case "7"://攻击力最高
    			Monster r7 = null;
    			for(Monster t7 : ts){
    				if(t7.getFightProp().getHp() > 0){
    					if(r7 == null || t7.getFightProp().getAttack() > r7.getFightProp().getAttack()){
    						r7 = t7;
    					}
    				}
    			}
    			if(r7 != null){
    				ret.add(r7);
    			}
    			break;
    		case "8"://血量%最少
    			Monster r8 = null;
    			for(Monster t8 : ts){
    				if(t8.getFightProp().getHp() > 0){
    					if(r8 == null || t8.getFightProp().getHp()/(t8.getFightProp().getHpInit() + .0) < r8.getFightProp().getHp()/(r8.getFightProp().getHpInit() + .0)){
    						r8 = t8;
    					}
    				}
    			}
    			if(r8 != null){
    				ret.add(r8);
    			}
    			break;
    		default:
    			break;
    	}
    	
    	
    	return ret;
    	
    }
    
    /**
     * 根据目标ID获取目标怪物
     */
    public static Monster getMonsterById(FightBase fb,long objectId){
    	
    	Monster m = fb.getFightA().getMonsters().get(objectId);
    	if(m == null){
    		return fb.getFightB().getMonsters().get(objectId);
    	}
    	return m;
    	
    }
    
    
//    /**
//     * 获取攻击者
//     */
//    public Monster getMonsterByCmd(FightBase fb,FightCmd fc){
//    	
//    	if(fc.getAttackerType() == 1){
//    		return fb.getFightA().monsters.get(fc.getAttackerId());
//    	}else if(fc.getAttackerType() == 3){
//    		return fb.getFightB().monsters.get(fc.getAttackerId());
//    	}
//    	return null;
//    	
//    }
    
    /**
     * 战斗是否结束
     */
    public long isEndFight(FightBase fb){
    	boolean allDeaDA = true;
    	for(Monster m :fb.getFightA().getMonsters().values()){
    		if(m.getFightProp().getHp() > 0){
    			allDeaDA = false;
    			break;
    		}
    	}
    	boolean allDeaDB = true;
    	for(Monster m :fb.getFightB().getMonsters().values()){
    		if(m.getFightProp().getHp() > 0){
    			allDeaDB = false;
    			break;
    		}
    	}
    	if(allDeaDA){
    		fb.setWinner(fb.getFightB().getPlayerId());
    	}else if(allDeaDB){
    		fb.setWinner(fb.getFightA().getPlayerId());
    	}
    	return fb.getWinner();
    }

    public void endFight(FightBase fb){
    	ThreadPoolManager.getInstance().execute(new EndFightTask(fb));	
    }
    
    
    
    

    /**
     * PVE用 暂且不用
     */
    public List<RetFightCmd> processCmdList(FightBase fb,List<FightCmd> fcs){
    	
    	List<RetFightCmd> ls = Lists.newArrayList();
    	if(fb == null){
    		return ls;
    	}
    	for(FightCmd fc :fcs){
    		RetFightCmd rc = processCmd(fb,fc,ls);
    		if(rc != null){
    			ls.add(rc);
    		}
    		if(fb.getWinner() != -1){
    			break;
    		}
    	}
    	return ls;
    }

}
