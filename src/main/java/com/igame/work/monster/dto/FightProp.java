package com.igame.work.monster.dto;



import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.igame.core.data.DataManager;
import com.igame.core.data.template.GodsEffectTemplate;
import com.igame.core.data.template.GodsdataTemplate;
import com.igame.util.MyUtil;


import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Marcus.Z
 *
 */
public class FightProp {
	
	
	@JsonIgnore
	public long playerId;//角色ID

	public int hp;//HP	
	
	@JsonIgnore
	public int attack;//攻击 

	@JsonIgnore
	public int speed;//速度
	
	@JsonIgnore
	public float ias;//攻击速度
	
	@JsonIgnore
	public int rng;//攻击距离
	
	@JsonIgnore
	public int repel;//击退距离 霸气
	
	@JsonIgnore
	public int bulletSpeed;//子弹速度
	
	@JsonIgnore
	public float xiHp;//吸血百分比
	
	@JsonIgnore
	public int damgeRev;//伤害反射
	
	@JsonIgnore
	public float damgeXi;//伤害吸收
	
	@JsonIgnore
	public float damgeRevMelee;//伤害反射近战
	
	@JsonIgnore
	public float damgeRevFar;//伤害反射远战
	
	@JsonIgnore
	public float repeledPer;//被击退
	
	@JsonIgnore
	public int clearBad;//多少秒后清楚负面效果
	
	@JsonIgnore
	public int clearGood;//增益效果清除（效果值为生效时间：-1为1次清除，正值为经过时间）
	
	@JsonIgnore
	public int damgeRed;//伤害减少值
	
	@JsonIgnore
	public float repeled;//伤害减少百分比
	
	@JsonIgnore
	public float damgeRedMelee;//伤害减少值近战
	
	@JsonIgnore
	public float damgeRedFar;//伤害减少百分比远程
	
	@JsonIgnore
	public int damgeAddExt;//额外伤害提高
	
	@JsonIgnore
	public float damgeAddExtPre;//额伤害提高%
	
	@JsonIgnore
	private int expAadd;//经验值额外增加
	
	
	
	
	@JsonIgnore
	public float x;//坐标位置
	
	@JsonIgnore
	public int i;//初始位置索引
	
	@JsonIgnore
	public long lastTime;//上次移动时间
	
	@JsonIgnore
	public List<Effect> effectList = Lists.newArrayList();//身上拥有的BUFFER列表
	
	@JsonIgnore
	public List<Effect> hotList = Lists.newArrayList();//身上拥有的HOTBUFFER列表
	
	@JsonIgnore
	public List<Effect> areaList = Lists.newArrayList();//身上拥有的区域BUFFER列表
	
	@JsonIgnore
	public int attackCount;//攻击次数
	
	@JsonIgnore
	public int attackedCount;//被攻击次数
	
	@JsonIgnore
	public Map<Integer,Long> lastActTime = Maps.newHashMap();//上次触发时间

	
	public synchronized int getHp() {
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

	public int getDamgeRed() {
		return damgeRed;
	}

	public void setDamgeRed(int damgeRed) {
		this.damgeRed = damgeRed;
	}

	public float getRepeled() {
		return repeled;
	}

	public void setRepeled(float repeled) {
		this.repeled = repeled;
	}

	public int getDamgeAddExt() {
		return damgeAddExt;
	}

	public void setDamgeAddExt(int damgeAddExt) {
		this.damgeAddExt = damgeAddExt;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public long getLastTime() {
		return lastTime;
	}

	public void setLastTime(long lastTime) {
		this.lastTime = lastTime;
	}

	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}
	
	public synchronized int reduceHp(int value){
		this.hp -= value;
		if(hp < 0){
			hp = 0;
		}
		return value;
	}
	
	public synchronized int addHp(int value){
		this.hp += value;
		if(hp < 0){
			hp = 0;
		}
		return value;
	}

	public List<Effect> getEffectList() {
		return effectList;
	}

	public void setEffectList(List<Effect> effectList) {
		this.effectList = effectList;
	}
	
	public Effect addEffectToList(Effect effect){
		if(effect.getRepeat() <= 1){//替换
			Iterator<Effect> efs = this.effectList.iterator();
			while(efs.hasNext()){
				Effect ef = efs.next();
				if(ef.getEffectId() == effect.getEffectId()){
					efs.remove();
				}
			}
			this.effectList.add(effect);
			return effect;
		}else{//最多叠加X次
			int count = 0;
			for(Effect ef : this.effectList){
				if(ef.getEffectId() == effect.getEffectId()){
					count++;
				}
			}
			if(count < effect.getRepeat()){
				this.effectList.add(effect);
				return effect;
			}else{
				return null;
			}
		}
	}
	
	public float getDamgeAddExtPre() {
		return damgeAddExtPre;
	}

	public void setDamgeAddExtPre(float damgeAddExtPre) {
		this.damgeAddExtPre = damgeAddExtPre;
	}

	public float getXiHp() {
		return xiHp;
	}

	public void setXiHp(float xiHp) {
		this.xiHp = xiHp;
	}

	public int getDamgeRev() {
		return damgeRev;
	}

	public void setDamgeRev(int damgeRev) {
		this.damgeRev = damgeRev;
	}

	public float getDamgeXi() {
		return damgeXi;
	}

	public void setDamgeXi(float damgeXi) {
		this.damgeXi = damgeXi;
	}

	public float getDamgeRedMelee() {
		return damgeRedMelee;
	}

	public void setDamgeRedMelee(float damgeRedMelee) {
		this.damgeRedMelee = damgeRedMelee;
	}

	public float getDamgeRedFar() {
		return damgeRedFar;
	}

	public void setDamgeRedFar(float damgeRedFar) {
		this.damgeRedFar = damgeRedFar;
	}

	public float getDamgeRevMelee() {
		return damgeRevMelee;
	}

	public void setDamgeRevMelee(float damgeRevMelee) {
		this.damgeRevMelee = damgeRevMelee;
	}

	public float getDamgeRevFar() {
		return damgeRevFar;
	}

	public void setDamgeRevFar(float damgeRevFar) {
		this.damgeRevFar = damgeRevFar;
	}

	public float getRepeledPer() {
		return repeledPer;
	}

	public void setRepeledPer(float repeledPer) {
		this.repeledPer = repeledPer;
	}

	public int getClearBad() {
		return clearBad;
	}

	public void setClearBad(int clearBad) {
		this.clearBad = clearBad;
	}

	public int getClearGood() {
		return clearGood;
	}

	public void setClearGood(int clearGood) {
		this.clearGood = clearGood;
	}


	public List<Effect> getHotList() {
		return hotList;
	}

	public void setHotList(List<Effect> hotList) {
		this.hotList = hotList;
	}
	
	public Effect addEffectToHotList(Effect effect){
		if(effect.getRepeat() <= 1){//替换
			Iterator<Effect> efs = this.hotList.iterator();
			while(efs.hasNext()){
				Effect ef = efs.next();
				if(ef.getEffectId() == effect.getEffectId()){
					efs.remove();
				}
			}
			this.hotList.add(effect);
			return effect;
		}else{//最多叠加X次
			int count = 0;
			for(Effect ef : this.hotList){
				if(ef.getEffectId() == effect.getEffectId()){
					count++;
				}
			}
			if(count < effect.getRepeat()){
				this.hotList.add(effect);
				return effect;
			}else{
				return null;
			}
		}
	}





	@JsonIgnore
	public int monsterId;//模板ID
	
	@JsonIgnore
	public int atkType;//攻击类型
	
	@JsonIgnore
	public int hpInit;//HP 
	
	@JsonIgnore
	public int attackInit;//攻击 
	
	@JsonIgnore
	public int speedInit;//速度
	
	@JsonIgnore
	public float iasInit;//攻击速度
	
	@JsonIgnore
	public int repelInit;//击退距离 霸气
	
	@JsonIgnore
	public int damgeRedInit;//伤害减少值
	
	@JsonIgnore
	public float repeledInit;//伤害减少百分比
	
	@JsonIgnore
	public int damgeAddExtInit;//额外伤害提高
	
	
	

	public int getMonsterId() {
		return monsterId;
	}

	public void setMonsterId(int monsterId) {
		this.monsterId = monsterId;
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

	public float getIasInit() {
		return iasInit;
	}

	public void setIasInit(float iasInit) {
		this.iasInit = iasInit;
	}

	public int getRepelInit() {
		return repelInit;
	}

	public void setRepelInit(int repelInit) {
		this.repelInit = repelInit;
	}

	public int getDamgeRedInit() {
		return damgeRedInit;
	}

	public void setDamgeRedInit(int damgeRedInit) {
		this.damgeRedInit = damgeRedInit;
	}

	public float getRepeledInit() {
		return repeledInit;
	}

	public void setRepeledInit(float repeledInit) {
		this.repeledInit = repeledInit;
	}

	public int getDamgeAddExtInit() {
		return damgeAddExtInit;
	}

	public void setDamgeAddExtInit(int damgeAddExtInit) {
		this.damgeAddExtInit = damgeAddExtInit;
	}

	public int getAtkType() {
		return atkType;
	}

	public void setAtkType(int atkType) {
		this.atkType = atkType;
	}

	public int getExpAadd() {
		return expAadd;
	}

	public void setExpAadd(int expAadd) {
		this.expAadd = expAadd;
	}
	
	private void reCalEffectValue(int effectId,float value){
		switch (effectId){
			case 101://	101.伤害减免%+-
				this.repeled += value;
				break;
			case 102://	102.伤害减免+-
				this.damgeRed += value;
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
				this.damgeAddExtPre += value;
				break;
			case 112:// 112额外伤害+-
				this.damgeAddExt += value;
				break;
			case 113://	113生命恢复%+-（自身血量）
				break;
			case 114:// 114生命恢复%+-（自身攻击）
				break;
			case 115:// 115生命恢复+-
				break;
			case 117://	117.伤害反射+-
				this.damgeRev += value;
			case 118:// 118伤害吸收%+-（造成的伤害%自身回血）
				this.damgeXi += value;
				break;
			case 121://	121.吸血（目标当前血量的%吸取）+-
				this.xiHp += value;
				break;
			case 122://	122.伤害减免%近战+-
				this.damgeRedMelee += value;
				break;
			case 123://	123.伤害减免%远战+-
				this.damgeRedFar += value;
				break;
			case 124://	124.伤害反射%近战+-
				this.damgeRevMelee += value;
				break;
			case 125://	125.伤害反射%远战+-
				this.damgeRevFar += value;
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
				this.repeledPer += value;
				break;
			case 134://	134.X秒清除负面效果（效果值为生效时间：-1为1次清除，正值为经过时间）
				this.clearBad += value;
				break;
			case 138://	138.增益效果清除（效果值为生效时间：-1为1次清除，正值为经过时间）
				this.clearGood += value;
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
				this.expAadd +=value;
				break;
			default:
				break;
		}
	}

	public void reCalProp(){
		
		if(this.hp <= 0){
			return;
		}	
		long now = System.currentTimeMillis();
		Iterator<Effect> efs = effectList.iterator();
		while(efs.hasNext()){
			Effect ef = efs.next();
			if(ef.getAvailTime() <= 0 || ef.getStartTime() + ef.getAvailTime() >= now){
				reCalEffectValue(ef.getEffectId(),ef.getValue());
			}else{
				if(ef.getAvailTime() > 0){
					efs.remove();
				}			
			}

		}
		
	}

	public synchronized int getAttackCount() {
		return attackCount;
	}

	public synchronized void setAttackCount(int attackCount) {
		this.attackCount = attackCount;
	}
	
	public synchronized void addAttackCount() {
		this.attackCount++;
	}

	public synchronized int getAttackedCount() {
		return attackedCount;
	}

	public synchronized void setAttackedCount(int attackedCount) {
		this.attackedCount = attackedCount;
	}
	
	public synchronized void addAttackedCount() {
		this.attackedCount++;
	}

	public Map<Integer,Long> getLastActTime() {
		return lastActTime;
	}

	public void setLastActTime(Map<Integer,Long> lastActTime) {
		this.lastActTime = lastActTime;
	}

	public List<Effect> getAreaList() {
		return areaList;
	}

	public void setAreaList(List<Effect> areaList) {
		this.areaList = areaList;
	}
	
	/**
	 * 神灵被动技能效果加成
	 * @param selfGods 己方神灵，enemyGods 敌方神灵，均可为null
	 */
	public void reCalGods(Gods selfGods,Gods enemyGods){
		
		if(selfGods != null){//处理己方神灵对自己的加成效果，
			GodsdataTemplate godsTemplate = DataManager.GodsData.getTemplate(selfGods.getGodsType()+"_"+ selfGods.getGodsLevel());
			if(godsTemplate != null){
				GodsEffectTemplate effectTemplate =  DataManager.GodsEffectData.getTemplate(godsTemplate.getGodsEffect());
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
			GodsdataTemplate enemyTemplate = DataManager.GodsData.getTemplate(enemyGods.getGodsType()+"_"+ enemyGods.getGodsLevel());
			if(enemyTemplate != null){
				GodsEffectTemplate effectTemplateE =  DataManager.GodsEffectData.getTemplate(enemyTemplate.getGodsEffect());
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
