package com.igame.work.fight.service;

import com.igame.util.MyUtil;
import com.igame.work.fight.FightService;
import com.igame.work.fight.data.SkillTemplate;
import com.igame.work.monster.dto.Effect;
import com.igame.work.monster.dto.Monster;

/**
 * 
 * @author Marcus.Z
 *
 */
public class FightUtil {

	
	/**
	 * 普通攻击伤害值
	 */
	public static int getNormalDamge(Monster attacker,Monster target){
		
		attacker.getFightProp().reCalProp();
		target.getFightProp().reCalProp();
		
		float damgeRedu = 0;
		if(attacker.getAtkType() == 1){//对方近战减免
			damgeRedu = target.getFightProp().getDamgeRedMelee();//被攻击者近战伤害减免
		}else if(attacker.getAtkType() == 2){//对方远程减免
			damgeRedu = target.getFightProp().getDamgeRedFar();//被攻击者远程伤害减免
		}
		//攻击方攻击力*(1+攻击方额外伤害加成百分比-目标方伤害减少百分比-目标方近/远 程伤害减免百分比) + 攻击方额外伤害增加-目标方伤害减少
		int damge =  (int)(attacker.getFightProp().getAttack()*(1 + attacker.getFightProp().getDamgeAddExtPre() - target.getFightProp().getRepeled() - damgeRedu)
				 			+ attacker.getFightProp().getDamgeAddExt() - target.getFightProp().getDamgeRed());
		if(damge < 0){
			damge = 0;
		}
		return damge;
		
	}
	
	/**
	 * 技能攻击伤害值
	 */
	public static int getSkillDamge(Monster attacker,Monster target,SkillTemplate skillTemplate,int skillLevel,int skillExp){
		
		attacker.getFightProp().reCalProp();
		target.getFightProp().reCalProp();
		
		float damgeRedu = 0;
		if(attacker.getAtkType() == 1){//对方近战减免
			damgeRedu = target.getFightProp().getDamgeRedMelee();//被攻击者近战伤害减免
		}else if(attacker.getAtkType() == 2){//对方远程减免
			damgeRedu = target.getFightProp().getDamgeRedFar();//被攻击者远程伤害减免
		}
		float skillUpAdd = 0;//技能up加成
		if(skillTemplate.getSubtype() == 1){//直接触发类型有技能UP加成  
			skillUpAdd = (1 + (skillLevel-1)*5 +
					skillLevel>= FightService.skillLvData.getMaxLevel() ? 0 : (int)(skillExp/(FightService.skillLvData.getTemplate(skillTemplate.getSkillId()).getSkillExp()+.0)*5))
							* skillTemplate.getHurtUp();
		}
		//攻击力 * 技能加成(基本数值+UP值*等级)
		float totalAttack = attacker.getFightProp().getAttack()   *   (skillTemplate.getHurt() + skillUpAdd)/100.0f;

		//攻击方攻击力*技能加成*(1+攻击方额外伤害加成百分比-目标方伤害减少百分比-目标方近/远 程伤害减免百分比) + 攻击方额外伤害增加-目标方伤害减少
		int damge =  (int)(totalAttack*(1 + attacker.getFightProp().getDamgeAddExtPre() - target.getFightProp().getRepeled() - damgeRedu)
				 			+ attacker.getFightProp().getDamgeAddExt() - target.getFightProp().getDamgeRed());
		if(damge < 0){
			damge = 0;
		}
		return damge;
		
	}
	
	
	/**
	 * buffer是否生效
	 */
	public static boolean isValidEffect(Monster mm,int effectType,String campLimit){
		
		if(effectType == 2){
			return false;
		}
		if(!MyUtil.isNullOrEmpty(campLimit)){
			String[] cls = campLimit.split(",");
			switch(cls[0]){
				case "1"://血量低于
					if(mm.getFightProp().getHp()/(mm.getFightProp().getHpInit() + .0) < Integer.parseInt(cls[1])/100.0){
						return true;
					}
					break;
				case "2"://血量高于
					if(mm.getFightProp().getHp()/(mm.getFightProp().getHpInit() + .0) > Integer.parseInt(cls[1])/100.0){
						return true;
					}
					break;
				case "3"://存在某效果
					for(Effect ee:mm.getFightProp().getEffectList()){
						if(ee.getEffectId() == Integer.parseInt(cls[1])){
							return true;
						}
					}
					break;
				default:
					break;
			
			}
			return false;
		}else{
			return true;
		}	
		
	} 

	/**
	 * 判断是增益还是减益效果
	 */
	public static int effectSacle(Effect effect){
		
		int ret = 0;
		if(String.valueOf(effect.getEffectId())
				.contains("101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126,127,128,129,131,135,136,137,139,140,141,142,143")){
			if(effect.getValue() >= 0){
				return 1;
			}else{
				return -1;
			}
		}
		return ret;
		
	}

}
