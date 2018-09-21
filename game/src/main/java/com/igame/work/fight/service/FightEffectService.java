package com.igame.work.fight.service;

import com.google.common.collect.Lists;
import com.igame.core.ISFSModule;
import com.igame.core.di.Inject;
import com.igame.core.event.EventService;
import com.igame.util.ThreadPoolManager;
import com.igame.work.fight.FightService;
import com.igame.work.fight.data.EffectTemplate;
import com.igame.work.fight.data.SkillTemplate;
import com.igame.work.fight.dto.FightBase;
import com.igame.work.fight.dto.FightCmd;
import com.igame.work.fight.dto.RetFightCmd;
import com.igame.work.monster.dto.Effect;
import com.igame.work.monster.dto.Monster;

import java.util.List;
import java.util.concurrent.ScheduledFuture;





/**
 * 
 * @author Marcus.Z
 *
 */
public class FightEffectService {

	@Inject private FightProcessser fightProcessser;
	private ScheduledFuture<?> sc = null;

    public void init(){
    	sc = ThreadPoolManager.getInstance().scheduleAtFixedRate(new SceneEffectTask(), 2000, 200);
    }
    
    /**
     * 处理技能触发的添加BUFFER效果
     */
    public static List<Effect> processAddEffect(FightBase fb,FightCmd fc,Monster attacker,SkillTemplate skillTemplate, List<Monster> targets,RetFightCmd rcd,List<RetFightCmd> retCmd){
    	
    	List<Effect> ls = Lists.newArrayList();
		EffectTemplate et = FightService.effectData.getTemplate(Integer.parseInt(skillTemplate.getEffect()));
		if(et != null){
			
			String[] efs = et.getEffectId().split(et.getEffectId());
			int i = 0;
			for(String ef : efs){
				Effect eet = new Effect(et.getEffect(),Integer.parseInt(ef),attacker.getPlayerId(), System.currentTimeMillis(), et.getTimes() * 1000, Float.parseFloat(et.getEffectValue().split(";")[i]),et.getCampLimit() ,et.getEffectType(),Integer.parseInt(et.getTouchTime().split(";")[i]),Integer.parseInt(et.getRepeat().split(";")[i]));
				if(et.getFollow() == 2){//固定在场景的区域BUUFER
					
					Effect addEet = eet.clonew();//添加到指令中的EFFECT
					
			    	float center = -10000;
			    	if(skillTemplate.getCentre() == 1){//命中目标
			    		 Monster other = FightProcessser.getMonsterById(fb,fc.getTargetId());
			    		 if(other != null){
			    			 center = other.getFightProp().getX();
			    			 addEet.setTargetId(other.getObjectId());
			    		 }	    		 
			    	}else if(skillTemplate.getCentre() == 2){//自身
			    		center = attacker.getFightProp().getX();
			    		addEet.setTargetId(attacker.getObjectId());
			    	}
			    	if(center != -10000){//固定区域HOT BUFFER
						eet.setCamp(skillTemplate.getCamp());
						eet.setCampTarget(skillTemplate.getCampTarget());
						eet.setCenter((int)center);
						eet.setRange(skillTemplate.getRange());
						
						if(eet.getTouchTime() == 1 && eet.getAvailTime()<=0){//增加瞬发一次性生效BUFFER，加、减HP
		    				ls.add(eet);
		    				List<Monster> mons = FightProcessser.getTargetByFixedRange(fb, eet.getSender(), eet.getCamp(), eet.getCampTarget(), eet.getCenter(), eet.getRange());
							for(Monster mm : mons){
								Effect addE = eet.clonew();//添加到指令中的EFFECT
								addE.setTargetId(mm.getObjectId());
								rcd.getBuffer().add(addE);
								addE.actionHot(fb, mm,rcd, retCmd);
							}
							
		    			}else{//非一次性
						
							if(et.getHotTime() > 0){//HOT
								if(et.getTimes() > 0){//加入场景对象中
									eet.setHotTime(et.getHotTime() * 1000);
									if(addEffectToHotList(fb, eet) != null){
										ls.add(eet);
										rcd.getBuffer().add(addEet);
									}	
								}
							}else{//区域属性BUFFER列表
								
							}
		    			}
			    	}
			    	
					
				}else if(et.getFollow() == 1){//跟随怪物区域范围BUUFER
					
					if(eet.getTouchTime() == 1 && eet.getAvailTime()<=0){//增加瞬发一次性生效BUFFER，加、减HP
	    				ls.add(eet);
						for(Monster mm : targets){
							eet.setCamp(skillTemplate.getCamp());
							eet.setCampTarget(skillTemplate.getCampTarget());
							eet.setCenter((int)mm.getFightProp().getX());
							eet.setRange(skillTemplate.getRange());
							
							Effect addE = eet.clonew();//添加到指令中的EFFECT
							addE.setTargetId(mm.getObjectId());
							rcd.getBuffer().add(addE);
							addE.actionHot(fb, mm,rcd, retCmd);
						}
						
	    			}else{
					//HOT
						if(et.getHotTime() > 0){
							eet.setHotTime(et.getHotTime() * 1000);
				    		for(Monster mm : targets){//挂在怪物身上的HOT
								if(et.getTimes() > 0){
									
									eet.setCamp(skillTemplate.getCamp());
									eet.setCampTarget(skillTemplate.getCampTarget());
									eet.setCenter((int)mm.getFightProp().getX());
									eet.setRange(skillTemplate.getRange());
									if(mm.getFightProp().addEffectToHotList(eet)!= null){
										ls.add(eet);
										
										Effect addEet = eet.clonew();//添加到指令中的EFFECT
										addEet.setTargetId(mm.getObjectId());
										rcd.getBuffer().add(addEet);
									}								
								}
				    		}
						}
	    			}
//					else{//非HOT
//			    		for(Monster mm : targets){
//							if(et.getTimes() > 0){
//								mm.getFightProp().getEffectList().add(eet);
//								ls.add(eet);
//							}
//			    		}
//					}

				}else{//挂在怪物身上的BUUFER
					
					//HOT
					if(et.getHotTime() > 0){
						eet.setHotTime(et.getHotTime() * 1000);
			    		for(Monster mm : targets){//挂在怪物身上的HOT
							if(et.getTimes() > 0){
								if(mm.getFightProp().addEffectToHotList(eet)!=null){
									ls.add(eet);
									
									Effect addEet = eet.clonew();//添加到指令中的EFFECT
									addEet.setTargetId(mm.getObjectId());
									rcd.getBuffer().add(addEet);
								}
							}
			    		}
					}else{//非HOT
			    		for(Monster mm : targets){
			    			if(eet.getTouchTime() == 1 && eet.getAvailTime()<=0){//增加瞬发一次性生效BUFFER，加、减HP
			    				ls.add(eet);
			    				Effect addEet = eet.clonew();//添加到指令中的EFFECT
								addEet.setTargetId(mm.getObjectId());
								rcd.getBuffer().add(addEet);
								addEet.actionHot(fb, mm,rcd, retCmd);
								
			    			}else{
				    			if(mm.getFightProp().addEffectToList(eet)!=null){
				    				ls.add(eet);
									Effect addEet = eet.clonew();//添加到指令中的EFFECT
									addEet.setTargetId(mm.getObjectId());
									rcd.getBuffer().add(addEet);
				    			}
			    			}
			    		}
					}
				}
				i++;
			}
		}
		return ls;		
    }


	private static Effect addEffectToHotList(FightBase fb, Effect effect){
		if(effect.getRepeat() <= 1){//替换
			fb.scBuffers.removeIf(ef -> ef.getEffectId() == effect.getEffectId());
			fb.scBuffers.add(effect);
			return effect;
		}else{
			int count = 0;
			for(Effect ef : fb.scBuffers){
				if(ef.getEffectId() == effect.getEffectId()){
					count++;
				}
			}
			if(count < effect.getRepeat()){
				fb.scBuffers.add(effect);
				return effect;
			}else{
				return null;
			}
		}
	}

}
