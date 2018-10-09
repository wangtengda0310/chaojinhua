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
@XmlRootElement(name = "low")
public class CheckPointTemplate {


	/**
	 * 关卡ID
	 */
	@XmlAttribute(name = "chapter_id", required = true)
	private int chapterId;

	/**
	 *   1普通关卡
	 *   2资源关卡
	 */
	@XmlAttribute(name = "chapter_type")
	private int chapterType;

	/**
	 * 关卡名
	 */
	@XmlAttribute(name = "city_name")
	private int cityName;

	/**
	 * 章节ID
	 */
	@XmlAttribute(name = "city_id")
	private int cityId;

	/**
	 * 每日挑战次数
	 */
	@XmlAttribute(name = "count")
	private int count;

	/**
	 * 产出货币
	 *
	 * 仅对资源关卡有效
	 */
	@XmlAttribute(name = "drop_point")
	private String dropPoint;

	/**
	 * 产出数量/小时
	 *
	 * 仅对资源关卡有效
	 */
	@XmlAttribute(name = "drop_value")
	private int dropValue;

	/**
	 * 体力消耗
	 */
	@XmlAttribute(name = "exp")
	private int exp;

	/**
	 * 首次掉落包ID
	 */
	@XmlAttribute(name = "fisrt_drop_id")
	private int fisrtDropId;

	/**
	 * 前置关卡
	 */
	@XmlAttribute(name = "limit")
	private String limit;

	@XmlAttribute(name = "monster_set")
	private String monsterSet;

	@XmlAttribute(name = "physical")
	private int physical;

	/**
	 * 随机掉落包ID
	 *
	 * 可能有多个
	 */
	@XmlAttribute(name = "random_drop_id")
	private int randomDropId;

	/**
	 * 周目
	 */
	@XmlAttribute(name = "round")
	private int round;

	@XmlAttribute(name = "specil_monster_set")
	private int specilMonsterSet;

	/**
	 * 解锁关卡
	 */
	@XmlAttribute(name = "unlock")
	private String unlock;

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

	public int getCityName() {
		return cityName;
	}

	public void setCityName(int cityName) {
		this.cityName = cityName;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getDropPoint() {
		return dropPoint;
	}

	public void setDropPoint(String dropPoint) {
		this.dropPoint = dropPoint;
	}

	public int getDropValue() {
		return dropValue;
	}

	public void setDropValue(int dropValue) {
		this.dropValue = dropValue;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

    public int getFisrtDropId() {
        return fisrtDropId;
    }

    public void setFisrtDropId(int fisrtDropId) {
        this.fisrtDropId = fisrtDropId;
    }

    public String getLimit() {
		return limit;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}

	public String getMonsterSet() {
		return monsterSet;
	}

	public void setMonsterSet(String monsterSet) {
		this.monsterSet = monsterSet;
	}

	public int getPhysical() {
		return physical;
	}

	public void setPhysical(int physical) {
		this.physical = physical;
	}

	public int getRandomDropId() {
		return randomDropId;
	}

	public void setRandomDropId(int randomDropId) {
		this.randomDropId = randomDropId;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public int getSpecilMonsterSet() {
		return specilMonsterSet;
	}

	public void setSpecilMonsterSet(int specilMonsterSet) {
		this.specilMonsterSet = specilMonsterSet;
	}

	public String getUnlock() {
		return unlock;
	}

	public void setUnlock(String unlock) {
		this.unlock = unlock;
	}
}
