package com.igame.work.fight.service;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;
import com.igame.work.fight.dto.FightBase;
import com.igame.work.fight.dto.RetFightCmd;
import com.igame.work.monster.dto.Effect;
import com.igame.work.monster.dto.Monster;


/**
 * 
 * @author Marcus.Z
 *
 */
public class SceneEffectTask implements Runnable {

	@Override
	public void run() {
		
		for(FightBase fb : PVPFightService.ins().fights.values()){
			if(fb.getWinner() != -1){//战斗已经结束
				return;
			}
			
			List<RetFightCmd> retCmd = Lists.newArrayList();
			
			FightProcessser.ins().onOverTime(fb,retCmd);//检测时间到触发技能
			
			long now = System.currentTimeMillis();
			
			RetFightCmd rcd = new RetFightCmd();
			
			//场景固定区域的BUFFER处理
			Iterator<Effect> efs = fb.getScBuffers().iterator();
			while(efs.hasNext()){
				Effect ef = efs.next();
				if(ef.getStartTime() + ef.getAvailTime() >= now){
					if(now - ef.getLastTime() >= ef.getHotTime()){//间隔时间到
						//取出在范围内生效的怪物集合
						List<Monster> targets = FightProcessser.ins().getTargetByFixedRange(fb, ef.getSender(), ef.getCamp(), ef.getCampTarget(), ef.getCenter(), ef.getRange());
						for(Monster mm : targets){
							ef.actionHot(fb, mm,rcd,retCmd);
						}
						ef.setLastTime(now);
					}
				}else{
					efs.remove();
				}
			}			
			
			//挂在怪物身上的HOT处理
			for(Monster mm : fb.getFightA().monsters.values()){
				if(mm.getFightProp().getHp() > 0){
					Iterator<Effect> efsA = mm.getFightProp().getHotList().iterator();
					while(efsA.hasNext())
					{
						Effect ef = efsA.next();
						if(ef.getStartTime() + ef.getAvailTime() >= now){
							if(now - ef.getLastTime() >= ef.getHotTime()){//间隔时间到
								if(ef.getCenter() != -10000){//区域范围HOT类型
									//取出在范围内生效的怪物集合
									List<Monster> targets = FightProcessser.ins().getTargetByFixedRange(fb, ef.getSender(), ef.getCamp(), ef.getCampTarget(), ef.getCenter(), ef.getRange());
									for(Monster other : targets){
										ef.actionHot(fb, other,rcd,retCmd);
									}
								}else{//挂在怪物身上只影响自己， 但是每隔XX秒就生效的BUFFER
									ef.actionHot(fb, mm,rcd,retCmd);
								}
								ef.setLastTime(now);
							}
						}else{
							efsA.remove();
						}
					}
				}
			}
			for(Monster mm : fb.getFightB().monsters.values()){
				if(mm.getFightProp().getHp() > 0){
					Iterator<Effect> efsB = mm.getFightProp().getHotList().iterator();
					while(efsB.hasNext()){
						Effect ef = efsB.next();
						if(ef.getStartTime() + ef.getAvailTime() >= now){
							if(now - ef.getLastTime() >= ef.getHotTime()){//间隔时间到
								if(ef.getCenter() != -10000){//区域HOT类型
									//取出在范围内生效的怪物集合
									List<Monster> targets = FightProcessser.ins().getTargetByFixedRange(fb, ef.getSender(), ef.getCamp(), ef.getCampTarget(), ef.getCenter(), ef.getRange());
									for(Monster other : targets){
										ef.actionHot(fb, other,rcd,retCmd);
									}
								}else{
									ef.actionHot(fb, mm,rcd,retCmd);
								}
								ef.setLastTime(now);
							}
						}else{
							efsB.remove();
						}
					}
				}
			}		
			
//			MessageUtil.notiyMonsterPropChange(pa, props);
//			MessageUtil.notiyWinner(pa,FightProcessser.ins().isEndFight(fb));
			
			if(FightProcessser.ins().isEndFight(fb) != -1){//战斗已经结束
				FightProcessser.ins().endFight(fb);
			}

		}
		

	}

}
