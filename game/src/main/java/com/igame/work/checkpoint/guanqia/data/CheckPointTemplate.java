package com.igame.work.checkpoint.guanqia.data;



import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * 
 * @author Marcus.Z
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "item")
public class CheckPointTemplate {
	


	@XmlAttribute(name = "chapter_id", required = true)
	private int chapterId;
	
	@XmlAttribute(name = "city_id")
	private int cityId;
	
	@XmlAttribute(name = "chapter_type")
	private int chapterType;
	
	@XmlAttribute(name = "limit")
	private String limit;//前置关卡
	
	@XmlAttribute(name = "unlock")
	private String unlock;//解锁关卡
	
	@XmlAttribute(name = "monster_id")
	private String monsterId;//
	
	@XmlAttribute(name = "level")
	private String level;//
	
	@XmlAttribute(name = "site")
	private String site;//站位
	
	@XmlAttribute(name = "monster_skill")
	private String monsterSkill;
	
	@XmlAttribute(name = "monster_prop")
	private String monsterProp;
	
	@XmlAttribute(name = "drop_point")
	private String dropPoint;

	@XmlAttribute(name = "physical")
	private int physical;//消耗体力
	
	@XmlAttribute(name = "exp")
	private int exp;
	
	@XmlAttribute(name = "drop_id")
	private int dropId;//掉落包ID
	
	@XmlAttribute(name = "count")
	private Integer count;
	
	@XmlAttribute(name = "round")
	private int round;//二周目

	public int getChapterId() {
		return chapterId;
	}

	public void setChapterId(int chapterId) {
		this.chapterId = chapterId;
	}

	public int getChapterType() {
		return chapterType;
	}

	public void setChapterType(int chapterType) {
		this.chapterType = chapterType;
	}

	public String getLimit() {
		return limit;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}

	public String getMonsterId() {
		return monsterId;
	}

	public void setMonsterId(String monsterId) {
		this.monsterId = monsterId;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getMonsterSkill() {
		return monsterSkill;
	}

	public void setMonsterSkill(String monsterSkill) {
		this.monsterSkill = monsterSkill;
	}

	public String getMonsterProp() {
		return monsterProp;
	}

	public void setMonsterProp(String monsterProp) {
		this.monsterProp = monsterProp;
	}

	public String getDropPoint() {
		return dropPoint;
	}

	public void setDropPoint(String dropPoint) {
		this.dropPoint = dropPoint;
	}

	public int getPhysical() {
		return physical;
	}

	public void setPhysical(int physical) {
		this.physical = physical;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getDropId() {
		return dropId;
	}

	public void setDropId(int dropId) {
		this.dropId = dropId;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public String getUnlock() {
		return unlock;
	}

	public void setUnlock(String unlock) {
		this.unlock = unlock;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}
	
	
	
	
	

}
