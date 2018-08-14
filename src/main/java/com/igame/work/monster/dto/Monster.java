package com.igame.work.monster.dto;

import java.util.*;

import com.igame.work.fight.service.ComputeFightService;
import com.igame.work.user.dto.Team;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Transient;
import org.mongodb.morphia.utils.IndexDirection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.igame.core.data.DataManager;
import com.igame.core.data.template.ItemTemplate;
import com.igame.core.data.template.MonsterTemplate;
import com.igame.core.data.template.TongDiaoTemplate;
import com.igame.core.db.BasicVO;
import com.igame.core.log.ExceptionLog;
import com.igame.util.MyUtil;
import com.igame.work.item.dto.Item;
import com.igame.work.monster.service.EffectService;
import com.igame.work.monster.service.MonsterService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.dto.TongAddDto;

/**
 * 
 * @author Marcus.Z
 *
 */
@Entity(value = "Monster", noClassnameStored = true)
public class Monster extends BasicVO implements Cloneable {
	
	@Indexed(unique=true,value=IndexDirection.ASC)
	public long objectId;//唯一ID
	
	@Indexed
	@JsonIgnore
	public long playerId;//角色ID
	
	public int hp;//当前HP
	
	public boolean isLock;//是否已锁
	
	public int level = 1;//怪物等级
	
	public int monsterId;//怪物ID
	
	@Transient
	@JsonIgnore
	public int atkType;//攻击类型
	
	@Transient
	@JsonIgnore
	public int dtate;//数据库状态 0-NO 1-新增 2-更新 3-删除
	

	public int exp;//
	
	//基因数据
	public String breaklv = "-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1";
	
	
	@Transient
	public int attack;//总攻击 
	
	@Transient
	public int speed;//总速度
	
	@Transient
	public float ias;//攻击速度
	
	@Transient
	public int rng;//攻击距离
	
	@Transient
	public int repel;//击退距离 霸气
	
	@Transient
	public int bulletSpeed;//子弹速度
	
	@JsonIgnore
	private Map<Integer,Integer> skillMap = Maps.newHashMap();//技能列表-技能ID 等级
	
	@JsonIgnore
	private Map<Integer,Integer> skillExp = Maps.newHashMap();//技能列表-技能ID 经验值
	
	@Transient
	public String skill = "";
	
	@Transient
	public long fightValue;

	@JsonIgnore
	private String equip = "-1,-1,-1,-1";//装备字符串

	@Transient
	private String[] teamEquip = new String[]{"-1,-1,-1,-1","-1,-1,-1,-1","-1,-1,-1,-1","-1,-1,-1,-1","-1,-1,-1,-1","-1,-1,-1,-1"};//阵容装备

	@JsonIgnore
	@Transient
	private int expAadd;
	
	
	@JsonIgnore
	@Transient
	public int hpTemp;//当前HP
	
	@JsonIgnore
	@Transient
	public int attackTemp;//当前HP
	
	@JsonIgnore
	@Transient
	public long fightValueTemp;//当前HP
	
	@Transient
	private int suitLv;
	
	@JsonIgnore
	@Transient
	public int damgeRed;//伤害减少值
	
	@JsonIgnore
	@Transient
	public float repeled;//伤害减少百分比
	
	
	
	
	@JsonIgnore
	@Transient
	public int hpExt;//103  //图鉴HP增加
	
	@JsonIgnore
	@Transient
	public int damgeRedExt; //102 图鉴伤害减免
	
	@JsonIgnore
	@Transient
	public int attackExt;//106 //图鉴攻击增加
	
	@JsonIgnore
	@Transient
	public int damgeAddExt;//112 //图鉴额外伤害提高
	
	@JsonIgnore
	@Transient
	public FightProp fightProp = new FightProp();//战斗数据 
	
	@Transient
	public int hpInit;//maxHP
	
	public Monster(){}
	
	public Monster(long objectId,int monsterId,int level,int site,int skillLv,String equip){
		super();
		this.objectId = objectId;
		this.monsterId = monsterId;
		this.level = level;
		if(!MyUtil.isNullOrEmpty(equip)){
			String[] ee = "-1,-1,-1,-1".split(",");
			String[] oo = equip.split(",");
			int length = oo.length >= 4 ? 4: oo.length;
			for(int i =0;i <length;i++){
				ee[i] = oo[i];
			}
			this.equip = MyUtil.toString(ee, ",");
			
		}
		MonsterTemplate mt = DataManager.MONSTER_DATA.getMonsterTemplate(monsterId);
		if(mt != null && mt.getSkill() != null){
			String[] skills = mt.getSkill().split(",");
			if(skills != null){
				for(String skill : skills){
					if(skillLv<=0){
						skillMap.put(Integer.parseInt(skill), 1);
					}else{
						skillMap.put(Integer.parseInt(skill), skillLv);
					}
					
					skillExp.put(Integer.parseInt(skill), 0);
				}
			}
			this.atkType = mt.getAtk_type();
		}
		initSkillString();
		reCalLevelValue(true);
		reCalEquip();
//		reCalculate(null,true);
		resetFightProp(site);
	}
	
	

	public Monster(Player player,long objectId, long playerId,  int hp, int dtate,int monsterId) {
		super();
		this.objectId = objectId;
		this.playerId = playerId;
		this.hp = hp;
		this.dtate = dtate;
		this.monsterId = monsterId;
		MonsterTemplate mt = DataManager.MONSTER_DATA.getMonsterTemplate(monsterId);
		if(mt != null && mt.getSkill() != null){
			String[] skills = mt.getSkill().split(",");
			if(skills.length != 0){
				for(String skill : skills){
					skillMap.put(Integer.parseInt(skill), 1);
					skillExp.put(Integer.parseInt(skill), 0);
				}
			}
			this.atkType = mt.getAtk_type();
		}
		initSkillString();
//		reCalculate(player,true);
	}



	public long getObjectId() {
		return objectId;
	}

	public void setObjectId(long objectId) {
		this.objectId = objectId;
	}
	
	
	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}


	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public boolean getIsLock() {
		return isLock;
	}

	public void setIsLock(boolean isLock) {
		this.isLock = isLock;
	}

	public int getDtate() {
		return dtate;
	}
	
	

	public int getLevel() {
		return level;
	}



	public void setLevel(int level) {
		this.level = level;
	}



	public void setDtate(int ndtate) {
		switch (ndtate) {
			case 1://添加
				if (this.dtate == 2 || this.dtate == 3)
					this.dtate = 2;
				else
					this.dtate = 1;
				break;
			case 2://更新
				if(this.dtate == 1){
					this.dtate = 1;
				} else{
					this.dtate = 2;
				}
				break;
			case 3://删除
				if (this.dtate == 1)
					this.dtate = 0;
				else {
					this.dtate = 3;
				}
				break;
			default:
				this.dtate = ndtate;
				break;
		}
	}



	public int getMonsterId() {
		return monsterId;
	}



	public void setMonsterId(int monsterId) {
		this.monsterId = monsterId;
	}


	public int getExp() {
		return exp;
	}



	public void setExp(int exp) {
		this.exp = exp;
	}



	public String getBreaklv() {
		return breaklv;
	}



	public void setBreaklv(String breaklv) {
		this.breaklv = breaklv;
	}
	

	public int getHpInit() {
		return hpInit;
	}

	public void setHpInit(int hpInit) {
		this.hpInit = hpInit;
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



	public double getIas() {
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



	public Map<Integer, Integer> getSkillMap() {
		return skillMap;
	}


	public void setSkillMap(Map<Integer, Integer> skillMap) {
		this.skillMap = skillMap;
	}
	
	


	public String getSkill() {
		return skill;
	}



	public void setSkill(String skill) {
		this.skill = skill;
	}
	
	/**
	 * 初始化技能字符串
	 */
	public void initSkillString(){
		StringBuffer ss = new StringBuffer();
		for(Map.Entry<Integer, Integer> m : this.skillMap.entrySet()){
			Integer exp = this.skillExp.computeIfAbsent(m.getKey(), value -> 0);
			ss.append(m.getKey()).append(",").append(m.getValue()).append(",").append(exp).append(";");
		}
		String rr = ss.toString();
		if(rr.lastIndexOf(";") != -1){
			rr = rr.substring(0,rr.lastIndexOf(";"));
		}
		this.skill = rr;
	}



	//重新计算怪物各项属性  包含模板、等级、突破、同化、符文系统加成、图鉴增加
	public void reCalculate(Player player,boolean dbHp){
		
		MonsterTemplate mt = DataManager.MONSTER_DATA.getMonsterTemplate(monsterId);
		if(mt != null){
			
			reCalLevelValue(dbHp);
			
			String[] vrl = this.breaklv.split(",");
			if(vrl.length != 0){
				for(String vl : vrl){
					switch (vl){
						case JiyinType.TYPE_001: //攻击+30
							this.attack += 30;
							break;
						case JiyinType.TYPE_002: //体力+90
							this.hp += 90;
							this.hpInit += 90;
							break;
						case JiyinType.TYPE_003: //霸气+20
							this.repel += 20;
							break;
						case JiyinType.TYPE_004: //移速+20
							this.speed += 20;
							break;
						case JiyinType.TYPE_005: //攻击+40 体力-30
							this.attack += 40;
							this.hp -= 30;
							this.hpInit -= 30;
							break;
						case JiyinType.TYPE_006: //体力+120 攻击-10
							this.hp += 120;
							this.hpInit += 120;
							this.attack -= 10;
							break;
						case JiyinType.TYPE_007: //霸气+10 移速+10
							this.repel += 10;
							this.speed += 10;
							break;
						case JiyinType.TYPE_008: //攻击+15 霸气+10
							this.attack += 15;
							this.repel += 10;
							break;
						case JiyinType.TYPE_009: //攻击+15 移速+10
							this.attack += 15;
							this.speed += 10;
							break;
						case JiyinType.TYPE_010: //体力+45 霸气+10
							this.hp += 45;
							this.hpInit += 45;
							this.repel += 10;
							break;
						case JiyinType.TYPE_011: //体力+45 移速+10
							this.hp += 45;
							this.hpInit += 45;
							this.speed += 10;
							break;
						case JiyinType.TYPE_017: //射程+15%
							this.rng += mt.getMonster_rng() * 0.15;
							break;
						default:
							break;
					}
				}
			}
			if(player != null){
				TongAddDto tad = player.getTongAdd();
				if(tad != null){
					if(dbHp){
						this.hp += tad.getHpAdd();
						this.hp += mt.getMonster_hp() * tad.getHpAddPer()/100.0;
					}
					this.hpInit += tad.getHpAdd();
					this.hpInit += mt.getMonster_hp() * tad.getHpAddPer()/100.0;
					this.attack += tad.getAttackAdd();
					this.attack += mt.getMonster_atk() * tad.getAttackAddPer()/100.0;				
					this.damgeRed += tad.getDamgeRed();
					this.repel += mt.getMonster_repel() * tad.getRepelAddPer()/100.0;
					this.repeled += tad.getRepeledAddPer();
				}
			}

		}else{
			ExceptionLog.error("get MonsterTemplate null,monsterId:" + monsterId);
		}
		reCalEquip();
		reCalExtValue();
		//reCalFightValue();
		ComputeFightService.ins().computeMonsterFight(this);
	}
	
	/**
	 * 计算怪物的基础和等级属性总值
	 * @param dbHp
	 */
	public void reCalLevelValue(boolean dbHp){
		MonsterTemplate mt = DataManager.MONSTER_DATA.getMonsterTemplate(monsterId);
		if(mt != null){
			if(dbHp){
				this.hp = mt.getMonster_hp() +(int)( mt.getHp_up() * (this.level - 1));
			}		
			this.hpInit = mt.getMonster_hp()  +(int)( mt.getHp_up() * (this.level - 1));		
			this.attack = mt.getMonster_atk() +(int)( mt.getAtk_up() * (this.level - 1));
			
			this.speed = mt.getMonster_speed();
			this.ias = mt.getMonster_ias();
			this.repel = mt.getMonster_repel();
			this.rng =  mt.getMonster_rng();
			this.bulletSpeed =  mt.getBulletSpeed();
		}
	}
	
	
	//重新计算怪物身上的符文增加属性，包含装备同调加成
	public void reCalEquip(){
		
		List<Effect> effes = Lists.newArrayList();
		List<Effect> effeAdd = Lists.newArrayList();
		String[] is = this.equip.split(",");
		int total = 0;
		if(is.length != 0){
			for(String s : is){
				Effect ef = EffectService.getEffectByItem(Integer.parseInt(s), true);
				if(ef != null){
					effes.add(ef);
				}
				if(!"0".equals(s) && !"-1".equals(s)){
					total++;
				}
			}
		}
		
		this.suitLv = 0;
		if(total >= 4){//触发同调
			List<TongDiaoTemplate> lts = Lists.newArrayList();
			for(String s : is){
				TongDiaoTemplate tt = DataManager.TongDiaoData.getByItemId(s);
				if(tt != null){
					lts.add(tt);
				}
			}
			if(!lts.isEmpty()){
				lts.sort((h1, h2) -> h1.getSuitLv() - h2.getSuitLv());
				TongDiaoTemplate index = lts.get(0);
				this.suitLv = index.getSuitLv();
				String[] effect_id = index.getEffectId().split(",");
				String[] ability_up = index.getAbilityUp().split(",");
				for(int i = 0;i < effect_id.length;i++){
					effeAdd.add(new Effect(i,Integer.parseInt(effect_id[i]),Float.parseFloat(ability_up[i])));
				}			
			}
			
		}		
		
		
		MonsterTemplate mt = DataManager.MONSTER_DATA.getMonsterTemplate(monsterId);
		if(mt != null){
			for(Effect vl : effes){
				switch (vl.getEffectId()){
					case 103: //体力+-
						this.hp += vl.getValue();
						this.hpInit += vl.getValue();
						break;
					case 104: //体力%+-
						this.hp += mt.getMonster_hp() * vl.getValue()/100.0;
						this.hpInit += mt.getMonster_hp() * vl.getValue()/100.0;
						break;
					case 105: //攻击力%
						this.attack += mt.getMonster_atk() * vl.getValue()/100.0;
						break;
					case 106: //攻击力+-
						this.attack += vl.getValue();
						break;
					case 109: //攻击速度%
						this.ias += mt.getMonster_ias() * vl.getValue()/100.0;
						break;
					case 110: //攻击速度+-
						this.ias += vl.getValue();
						break;
					case 126: //生效范围%
						break;
					case 127: //移动速度%+-
						this.speed += mt.getMonster_speed() * vl.getValue()/100.0;
						break;
					case 128: //移动速度+-
						this.speed += vl.getValue();
						break;
					case 133: //被击退%
						break;
					case 140: //攻击力%+-（仅普通攻击有效）
						this.attack += mt.getMonster_atk() * vl.getValue()/100.0;
						break;
					case 143: //143.怪物经验增加X点
						this.expAadd += vl.getValue();
						break;
					default:
						break;
				}
			}
			
			//同调
			for(Effect vl : effeAdd){
				switch (vl.getEffectId()){
					case 104: //体力%+-
						this.hp += mt.getMonster_hp() * vl.getValue()/100.0;
						this.hpInit += mt.getMonster_hp() * vl.getValue()/100.0;
						break;
					case 105: //攻击力%
						this.attack += mt.getMonster_atk() * vl.getValue()/100.0;
						break;
					default:
						break;
				}
			}
		}


	}
	
	/**
	 * 计算图鉴增加值
	 */
	public void reCalExtValue(){
		this.hp += hpExt;
		this.hpInit += hpExt;
		this.attack += attackExt;
		this.damgeRed += damgeRedExt;
	}


	public long getFightValue() {
		return fightValue;
	}



	public void setFightValue(long fightValue) {
		this.fightValue = fightValue;
	}


	public String getEquip() {
		return equip;
	}

	public void setEquip(String equip) {
		this.equip = equip;
	}

	public int getExpAadd() {
		return expAadd;
	}



	public void setExpAadd(int expAadd) {
		this.expAadd = expAadd;
	}


	public int getSuitLv() {
		return suitLv;
	}

	public void setSuitLv(int suitLv) {
		this.suitLv = suitLv;
	}



	public Monster clonew(){
		try {
			return (Monster)super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
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



	public int getHpTemp() {
		return hpTemp;
	}



	public void setHpTemp(int hpTemp) {
		this.hpTemp = hpTemp;
	}



	public int getAttackTemp() {
		return attackTemp;
	}



	public void setAttackTemp(int attackTemp) {
		this.attackTemp = attackTemp;
	}



	public long getFightValueTemp() {
		return fightValueTemp;
	}



	public void setFightValueTemp(long fightValueTemp) {
		this.fightValueTemp = fightValueTemp;
	}



	public int getHpExt() {
		return hpExt;
	}



	public void setHpExt(int hpExt) {
		this.hpExt = hpExt;
	}



	public int getDamgeRedExt() {
		return damgeRedExt;
	}



	public void setDamgeRedExt(int damgeRedExt) {
		this.damgeRedExt = damgeRedExt;
	}



	public int getAttackExt() {
		return attackExt;
	}



	public void setAttackExt(int attackExt) {
		this.attackExt = attackExt;
	}



	public int getDamgeAddExt() {
		return damgeAddExt;
	}



	public void setDamgeAddExt(int damgeAddExt) {
		this.damgeAddExt = damgeAddExt;
	}



	public FightProp getFightProp() {
		return fightProp;
	}



	public void setFightProp(FightProp fightProp) {
		this.fightProp = fightProp;
	}



	public void setLock(boolean isLock) {
		this.isLock = isLock;
	}
	
	
	public int getAtkType() {
		return atkType;
	}

	public void setAtkType(int atkType) {
		this.atkType = atkType;
	}
	

	public Map<Integer, Integer> getSkillExp() {
		return skillExp;
	}

	public void setSkillExp(Map<Integer, Integer> skillExp) {
		this.skillExp = skillExp;
	}

	public String[] getTeamEquip() {
		return teamEquip;
	}

	public void setTeamEquip(String[] teamEquip) {
		this.teamEquip = teamEquip;
	}

	public void resetFightProp(int i){
		
		this.fightProp.setPlayerId(this.playerId);
		this.fightProp.setMonsterId(this.monsterId);
		this.fightProp.setAtkType(this.atkType);
		
		this.fightProp.setHp(this.hpInit * 10);
		this.fightProp.setHpInit(this.hpInit * 10);

		this.fightProp.setAttack(this.attack);
		this.fightProp.setAttackInit(this.attack);
		
		this.fightProp.setSpeed(this.speed);
		this.fightProp.setSpeedInit(this.speed);
		
		this.fightProp.setIas(this.ias);
		this.fightProp.setIasInit(this.ias);
		
		this.fightProp.setRng(this.rng);
		
		this.fightProp.setRepel(this.repel);
		this.fightProp.setRepelInit(this.repel);
		
		this.fightProp.setBulletSpeed(this.bulletSpeed);
		
		this.fightProp.setDamgeRed(this.damgeRed);
		this.fightProp.setDamgeRedInit(this.damgeRed);
		
		this.fightProp.setRepeled(this.repeled);
		this.fightProp.setRepeledInit(this.repeled);
		
		this.fightProp.setDamgeAddExt(this.damgeAddExt);
		this.fightProp.setDamgeAddExtInit(this.damgeAddExt);
		
		this.fightProp.setLastTime(0);
		this.fightProp.setI(i);
		this.fightProp.setX(0);
		this.fightProp.effectList.clear();
		this.fightProp.hotList.clear();
		
		
	}




	public void setTeamEquip(Collection<Team> teams) {

		String[] defEquips = new String[]{"-1","-1","-1","-1"};

		if(level >= 5){
			defEquips[0] = "0";
		}
		if(level >= 15){
			defEquips[1] = "0";
		}
		if(level >= 22){
			defEquips[2] = "0";
		}
		if(level >= 30){
			defEquips[3] = "0";
		}

		String[] tempEquips = defEquips.clone();

		for (Team team : teams) {

			int teamId = team.getTeamId();
			Map<Integer, Integer> equipMap = team.getTeamEquip().get(objectId);
			if (equipMap != null){
				for (Map.Entry<Integer, Integer> entry : equipMap.entrySet()) {
					Integer location = entry.getKey();
					Integer equipId = entry.getValue();

					tempEquips[location-1] = String.valueOf(equipId);
				}
			}

			teamEquip[teamId-1] = MyUtil.toString(tempEquips,",");

			tempEquips = defEquips.clone();
		}

	}

	
	
	
	
	

}
