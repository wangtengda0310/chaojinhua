package com.igame.work.fight.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.igame.work.fight.FightService;
import com.igame.work.fight.data.GodsEffectTemplate;
import com.igame.work.fight.data.GodsdataTemplate;
import com.igame.util.MyUtil;
import com.igame.work.monster.dto.Gods;
import com.igame.work.monster.dto.Monster;
import com.igame.work.monster.dto.WuEffect;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Transient;

import java.util.List;

/**
 * 
 * @author Marcus.Z
 * 可考虑优化为extends FightProp这个对象，共用很多字段，并在FightProp对象修正@JsonIgnore
 */
@Entity(noClassnameStored = true)
public class MatchMonsterDto   implements Cloneable  {
	
	public long objectId;//唯一ID
	
	public int level = 1;//怪物等级
	
	public int monsterId;//怪物ID
	
	public int hp;//HP

	public int attack;//攻击 
	
	@JsonIgnore
	public int attackInit;//攻击

	public int speed;//速度
	
	@JsonIgnore
	public int speedInit;//速度
	
	public float ias;//攻击速度
	
	public int rng;//攻击距离
	
	public int repel;//击退距离 霸气
	
	@JsonIgnore
	public int repelInit;//击退距离 霸气
	
	public int bulletSpeed;//子弹速度
	
	@JsonIgnore
	public float repeled;//伤害减少百分比
	
	public int location;//初始位置索引
	
	
	@JsonIgnore
	@Transient
	public float x;//坐标位置
	
	
	@JsonIgnore
	public String breaklv = "-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1";
	
	public int hpInit;//maxHP
	
	public String skill = "";
	
	public String round = "";//周目信息
	
	@JsonIgnore
	public int atkType;//攻击类型
	
	@JsonIgnore
	public float iasInit;//最大攻击速度

	private String equip = "-1,-1,-1,-1";//装备(纹章)
	
	public MatchMonsterDto(){
		
	}
	
	public MatchMonsterDto(Monster monster){
		
		this.objectId = monster.getObjectId();
		this.level = monster.getLevel();
		this.monsterId = monster.getMonsterId();
		this.hp = monster.getFightProp().getHp();
		this.attack = monster.getFightProp().getAttack();
		this.speed = monster.getFightProp().getSpeed();
		this.ias = monster.getFightProp().getIas();
		this.rng = monster.getFightProp().getRng();
		this.repel = monster.getFightProp().getRepel();
		this.bulletSpeed = monster.getFightProp().getBulletSpeed();
		this.x = monster.getFightProp().getX();
		this.location = monster.getFightProp().getI();
		if(this.location > 5){
			this.location -= 5;
		}
		this.breaklv = monster.getBreaklv();
		this.repeled = monster.getFightProp().getRepeled();
		this.attackInit = monster.getFightProp().getAttackInit();
		this.speedInit = monster.getFightProp().getSpeedInit();
		this.repelInit = monster.getFightProp().getRepelInit();
		this.hpInit = monster.getFightProp().getHpInit();
		this.skill = monster.getSkill();
		this.atkType = monster.getAtkType();
		this.iasInit = monster.getFightProp().getIasInit();
		this.equip = monster.getEquip();
		
	}

	public long getObjectId() {
		return objectId;
	}

	public void setObjectId(long objectId) {
		this.objectId = objectId;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getMonsterId() {
		return monsterId;
	}

	public void setMonsterId(int monsterId) {
		this.monsterId = monsterId;
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public int getAttack() {
		return attack;
	}

	public void setAttack(int attack) {
		this.attack = attack;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public float getIas() {
		return ias;
	}

	public void setIas(float ias) {
		this.ias = ias;
	}

	public int getRng() {
		return rng;
	}

	public void setRng(int rng) {
		this.rng = rng;
	}

	public int getRepel() {
		return repel;
	}

	public void setRepel(int repel) {
		this.repel = repel;
	}

	public int getBulletSpeed() {
		return bulletSpeed;
	}

	public void setBulletSpeed(int bulletSpeed) {
		this.bulletSpeed = bulletSpeed;
	}

	public int getLocation() {
		return location;
	}

	public void setLocation(int location) {
		this.location = location;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public String getBreaklv() {
		return breaklv;
	}

	public void setBreaklv(String breaklv) {
		this.breaklv = breaklv;
	}

	public float getRepeled() {
		return repeled;
	}

	public void setRepeled(float repeled) {
		this.repeled = repeled;
	}

	public int getHpInit() {
		return hpInit;
	}

	public void setHpInit(int hpInit) {
		this.hpInit = hpInit;
	}

	public int getAttackInit() {
		return attackInit;
	}

	public void setAttackInit(int attackInit) {
		this.attackInit = attackInit;
	}

	public int getSpeedInit() {
		return speedInit;
	}

	public void setSpeedInit(int speedInit) {
		this.speedInit = speedInit;
	}

	public int getRepelInit() {
		return repelInit;
	}

	public void setRepelInit(int repelInit) {
		this.repelInit = repelInit;
	}
	
	
	public String getSkill() {
		return skill;
	}

	public void setSkill(String skill) {
		this.skill = skill;
	}

	public String getEquip() {
		return equip;
	}

	public void setEquip(String equip) {
		this.equip = equip;
	}

	public String getRound() {
		return round;
	}

	public void setRound(String round) {
		this.round = round;
	}

	public MatchMonsterDto clonew(){
		try {
			return (MatchMonsterDto)super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public void reCalValue(List<WuEffect> buffers){
		this.attack = this.attackInit;
		this.speed = this.speedInit;
		this.repel = this.repelInit;
		this.repeled = 0;
		for(WuEffect ee : buffers){
			switch (ee.getEffectId()){
				case 101://	101.伤害减免%+-
					this.repeled += ee.getValue();
					break;
				case 105://	105.攻击力%+-
					this.attack += ee.getValue()/100.0 * this.attackInit;
				case 127://	127.移速%+-
					this.speed += ee.getValue()/100.0 * this.speedInit;
				case 107://	107.霸气力%+-
					this.repel += ee.getValue()/100.0 * this.repelInit;
				default:
					break;
			}	
		}
		
	}
	
	private void reCalEffectValue(int effectId,float value){
		switch (effectId){
			case 101://	101.伤害减免%+-
				this.repeled += value;
				break;
			case 102://	102.伤害减免+-
//				this.damgeRed += value;
				break;
			case 105://	105.攻击力%+-
				this.attack += attackInit * value;
				break;
			case 106://	106.攻击力+-
				this.attack += value;
				break;
			case 107://	107.击退%+-
				this.repel += repelInit * value;
				break;
			case 108://	108击退+-
				this.repel += value;
				break;
			case 109://	109攻击速度%+-
				this.ias += iasInit * value;
				break;
			case 110:// 110攻击速度+-
				this.ias += value;
				break;
			case 111://	111额外伤害%+-
//				this.damgeAddExtPre += value;
				break;
			case 112:// 112额外伤害+-
//				this.damgeAddExt += value;
				break;
			case 113://	113生命恢复%+-（自身血量）
				break;
			case 114:// 114生命恢复%+-（自身攻击）
				break;
			case 115:// 115生命恢复+-
				break;
			case 117://	117.伤害反射+-
//				this.damgeRev += value;
			case 118:// 118伤害吸收%+-（造成的伤害%自身回血）
//				this.damgeXi += value;
				break;
			case 121://	121.吸血（目标当前血量的%吸取）+-
//				this.xiHp += value;
				break;
			case 122://	122.伤害减免%近战+-
//				this.damgeRedMelee += value;
				break;
			case 123://	123.伤害减免%远战+-
//				this.damgeRedFar += value;
				break;
			case 124://	124.伤害反射%近战+-
//				this.damgeRevMelee += value;
				break;
			case 125://	125.伤害反射%远战+-
//				this.damgeRevFar += value;
				break;
			case 126://	126.生效范围%+-
				break;
			case 127://	127.移动速度%+-
				this.speed += this.speedInit * value;
				break;
			case 128://	128.移动速度+-
				this.speed += value;
				break;
			case 131://	131.持续时间+-
				break;
			case 133://	133.被击退%+-
//				this.repeledPer += value;
				break;
			case 134://	134.X秒清除负面效果（效果值为生效时间：-1为1次清除，正值为经过时间）
//				this.clearBad += value;
				break;
			case 138://	138.增益效果清除（效果值为生效时间：-1为1次清除，正值为经过时间）
//				this.clearGood += value;
				break;
			case 139://	139.体型%
				break;
			case 140://	140.攻击力%+-（仅普通攻击有效）
				if(this.atkType == 1){
					this.attack += attackInit * value;
				}
				break;
			case 141://	141.复活回复生命X%
				break;
			case 142://	142.死亡时固定范围伤害X%攻击力的伤害
				break;
			case 143://	143.怪物经验增加X点
//				this.expAadd +=value;
				break;
			default:
				break;
		}
	}
	
	/**
	 * 神灵被动技能效果加成
	 * @param selfGods 己方神灵，enemyGods 敌方神灵，均可为null
	 */
	public void reCalGods(Gods selfGods,Gods enemyGods){
		
		if(selfGods != null){//处理己方神灵对自己的加成效果，
			GodsdataTemplate godsTemplate = FightService.godsData.getTemplate(selfGods.getGodsType()+"_"+ selfGods.getGodsLevel());
			if(godsTemplate != null){
				GodsEffectTemplate effectTemplate =  FightService.godsEffectData.getTemplate(godsTemplate.getGodsEffect());
				if(effectTemplate != null && !MyUtil.isNullOrEmpty(effectTemplate.getPassiveEffect()) && effectTemplate.getPassiveTarget() == 0){//有效的增益
					String[] effects = effectTemplate.getPassiveEffect().split(",");
					String[] values = effectTemplate.getPassiveValue().split(",");
					for(int i = 0;i<effects.length;i++){
						reCalEffectValue(Integer.parseInt(effects[i]), Float.parseFloat(values[i]));
					}
				}
			}
		}
		
		if(enemyGods != null){//处理敌方方神灵对自己的加成效果，
			GodsdataTemplate enemyTemplate = FightService.godsData.getTemplate(enemyGods.getGodsType()+"_"+ enemyGods.getGodsLevel());
			if(enemyTemplate != null){
				GodsEffectTemplate effectTemplateE =  FightService.godsEffectData.getTemplate(enemyTemplate.getGodsEffect());
				if(effectTemplateE != null && !MyUtil.isNullOrEmpty(effectTemplateE.getPassiveEffect()) && effectTemplateE.getPassiveTarget() == 1){//有效的减益
					String[] effectsE = effectTemplateE.getPassiveEffect().split(",");
					String[] valuesE = effectTemplateE.getPassiveValue().split(",");
					for(int i = 0;i<effectsE.length;i++){
						reCalEffectValue(Integer.parseInt(effectsE[i]), Float.parseFloat(valuesE[i]) * -1);
					}
				}
			}
		}
	
	}
	
	
	

}
