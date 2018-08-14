package com.igame.work.monster.dto;

import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.igame.util.MyUtil;
import com.igame.work.fight.dto.FightBase;
import com.igame.work.fight.dto.RetFightCmd;
import com.igame.work.fight.service.FightProcessser;
import com.igame.work.fight.service.FightUtil;

/**
 * 
 * @author Marcus.Z
 *
 */
public class Effect  implements Cloneable  {
	
	private int   effect;//唯一ID
		
//	101.伤害减免%
//	102.伤害减免+-
//	103.体力+-
//	104.体力%+-
//	105.攻击力%
//	106.攻击力+-
//	107.击退%
//	108击退+-
//	109攻击速度%
//	110攻击速度+-
//	111额外伤害%
//	112额外伤害+-
//	113生命恢复%（自身血量）
//	114生命恢复%（自身攻击）
//	115.生命恢复+-
//	116治疗效果%
//	117.伤害反射
//	118.伤害吸收（造成的伤害%自身回血）
//	119.立即原地生成一个友军
//	120.获得怒气
//	121.吸血（目标当前血量的%吸取）
//	122.伤害减免%近战
//	123.伤害减免%远战
//	124.伤害反射%近战
//	125.伤害反射%远战
//	126.生效范围%
//	127.移动速度%+-
//	128.移动速度+-
//	129.免疫伤害
//	130.沉默
//	131.持续时间+-
//	132.豁免伤害（手动释放的技能除外）
//	133.被击退%
//	134.X秒清除负面效果（效果值为生效时间：-1为1次清除，正值为经过时间）
//	135.伤害改变为回血
//	136.攻击力%（击杀目标的攻击力值）
//	137.产生护盾（击杀目标的血量）
//	138.增益效果清除（效果值为生效时间：-1为1次清除，正值为经过时间）
//	139.体型%
//	140.攻击力%+-（仅普通攻击有效）
//	141.复活回复生命X%
//	142.死亡时固定范围伤害X%攻击力的伤害
//	143.怪物经验增加X点

	private int   effectId;//效果ID
	
	private long targetId;//生效者ID
	
	private int   center = -10000;//中心点
	
	private int   range;//生效半径
	
	private float value;//影响数值
	
	private long  availTime;//持续时间,大于0为有持续时间，否则为一次性效果
	
	
	
	
	@JsonIgnore
	private long  lastTime;//上次生效时间
	
	@JsonIgnore
	private int touchTime;//触发时间
	
	@JsonIgnore
	private int repeat;//叠加次数
	
	@JsonIgnore
	private long  hotTime;//每多少毫秒生效一次

	@JsonIgnore
	private String camp = "";//生效阵营
	
	@JsonIgnore
	private String campTarget = "";//生效目标
	
	@JsonIgnore
	private String campLimit = "";//生效条件
	
	@JsonIgnore
	private int effectType;//生效类型1,怪物2技能
	
	@JsonIgnore
	private long  sender;//释放者
	
	@JsonIgnore
	private long  startTime;//开始生效的时间
	
	
	public Effect(){}

	public Effect(int effect,int effectId, float value) {
		super();
		this.effect = effect;
		this.effectId = effectId;
		this.value = value;
	}
	
	

	public Effect(int effect,int effectId,long sender, long startTime, long availTime, float value,String campLimit,int effectType,int touchTime,int repeat) {
		super();
		this.effect = effect;
		this.effectId = effectId;
		this.sender = sender;
		this.startTime = startTime;
		this.availTime = availTime;
		this.value = value;
		this.campLimit = campLimit;
		this.effectType = effectType;
		this.touchTime = touchTime;
		this.repeat = repeat;
	}

	public int getEffectId() {
		return effectId;
	}

	public void setEffectId(int effectId) {
		this.effectId = effectId;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getAvailTime() {
		return availTime;
	}

	public void setAvailTime(long availTime) {
		this.availTime = availTime;
	}

	public long getSender() {
		return sender;
	}

	public void setSender(long sender) {
		this.sender = sender;
	}

	public long getLastTime() {
		return lastTime;
	}

	public void setLastTime(long lastTime) {
		this.lastTime = lastTime;
	}

	public void setCenter(int center) {
		this.center = center;
	}

	public long getHotTime() {
		return hotTime;
	}

	public void setHotTime(long hotTime) {
		this.hotTime = hotTime;
	}

	public int getCenter() {
		return center;
	}

	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public String getCamp() {
		return camp;
	}

	public void setCamp(String camp) {
		this.camp = camp;
	}

	public String getCampTarget() {
		return campTarget;
	}

	public void setCampTarget(String campTarget) {
		this.campTarget = campTarget;
	}

	public String getCampLimit() {
		return campLimit;
	}

	public void setCampLimit(String campLimit) {
		this.campLimit = campLimit;
	}

	public int getEffectType() {
		return effectType;
	}

	public void setEffectType(int effectType) {
		this.effectType = effectType;
	}
	
	
	public int getTouchTime() {
		return touchTime;
	}

	public void setTouchTime(int touchTime) {
		this.touchTime = touchTime;
	}
	

	public int getRepeat() {
		return repeat;
	}

	public void setRepeat(int repeat) {
		this.repeat = repeat;
	}
	
	
	public long getTargetId() {
		return targetId;
	}

	public void setTargetId(long targetId) {
		this.targetId = targetId;
	}
	
	public int getEffect() {
		return effect;
	}

	public void setEffect(int effect) {
		this.effect = effect;
	}

	//	113生命恢复%（自身血量）
//	114生命恢复%（自身攻击）		
	public void actionHot(FightBase fb,Monster mm,RetFightCmd rcd,List<RetFightCmd> retCmd){
		
		switch (this.effectId){
			case 113:
				mm.getFightProp().reCalProp();
				int frontHp = mm.getFightProp().getHp();
				mm.getFightProp().addHp((int)(value/100.0*mm.getFightProp().getAttack()));
				if(!MyUtil.isNullOrEmpty(rcd.getTarget())){
					rcd.addTargetStr(";");
				}
				rcd.addTargetStr(mm.getObjectId() + ","+(int)(value/100.0*mm.getFightProp().getAttack())+","+mm.getFightProp().getHp());
				FightProcessser.ins().onReduceHp(fb,frontHp,mm,retCmd);//是否触发减血和死亡技能
				break;
			case 114:
				mm.getFightProp().reCalProp();
				int frontHp114 = mm.getFightProp().getHp();
				mm.getFightProp().addHp((int)(value/100.0*mm.getFightProp().getHp()));
				if(!MyUtil.isNullOrEmpty(rcd.getTarget())){
					rcd.addTargetStr(";");
				}
				rcd.addTargetStr(mm.getObjectId() + ","+(int)(value/100.0*mm.getFightProp().getHp())+","+mm.getFightProp().getHp());
				FightProcessser.ins().onReduceHp(fb,frontHp114,mm,retCmd);//是否触发减血和死亡技能
				break;
			case 115:
				mm.getFightProp().reCalProp();
				int frontHp115 = mm.getFightProp().getHp();
				mm.getFightProp().addHp((int)value);
				if(!MyUtil.isNullOrEmpty(rcd.getTarget())){
					rcd.addTargetStr(";");
				}
				rcd.addTargetStr(mm.getObjectId() + ","+(int)value+","+mm.getFightProp().getHp());
				FightProcessser.ins().onReduceHp(fb,frontHp115,mm,retCmd);//是否触发减血和死亡技能
				break;
			case 134://清除负面
				Iterator<Effect> efs134 = mm.getFightProp().getEffectList().iterator();
				while(efs134.hasNext()){
					Effect ef = efs134.next();
					if(FightUtil.effectSacle(ef) == -1){
						efs134.remove();
					}
				}
				Iterator<Effect> hots134 = mm.getFightProp().getHotList().iterator();
				while(hots134.hasNext()){
					Effect ef = hots134.next();
					if(FightUtil.effectSacle(ef) == -1){
						hots134.remove();
					}
				}
				break;
			case 138://清除正面
				Iterator<Effect> efs138 = mm.getFightProp().getEffectList().iterator();
				while(efs138.hasNext()){
					Effect ef = efs138.next();
					if(FightUtil.effectSacle(ef) == 1){
						efs138.remove();
					}
				}
				Iterator<Effect> hots138 = mm.getFightProp().getHotList().iterator();
				while(hots138.hasNext()){
					Effect ef = hots138.next();
					if(FightUtil.effectSacle(ef) == 1){
						hots138.remove();
					}
				}
				break;
			default:
				break;
		}
	}
	
	/**
	 * 克隆一个新对象
	 * @return
	 */
	public Effect clonew(){
		try {
			return (Effect)super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	

}
