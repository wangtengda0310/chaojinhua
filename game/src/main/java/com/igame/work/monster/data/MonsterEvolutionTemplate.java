package com.igame.work.monster.data;



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
public class MonsterEvolutionTemplate {
	


	@XmlAttribute(name = "monster_id", required = true)
	private int monsterId;
	
	@XmlAttribute(name = "monster_object")
	private String monsterObject;
	
	@XmlAttribute(name = "evolution_type")
	private String evolutionType;
	
	@XmlAttribute(name = "level_limit")
	private int levelLimit;
	
	@XmlAttribute(name = "gold")
	private long gold;
	
	@XmlAttribute(name = "item")
	private String item;
	
	@XmlAttribute(name = "evolution_tree")
	private String evolutionTree;


	public int getMonsterId() {
		return monsterId;
	}

	public void setMonsterId(int monsterId) {
		this.monsterId = monsterId;
	}

	public String getMonsterObject() {
		return monsterObject;
	}

	public void setMonsterObject(String monsterObject) {
		this.monsterObject = monsterObject;
	}

	public String getEvolutionType() {
		return evolutionType;
	}

	public void setEvolutionType(String evolutionType) {
		this.evolutionType = evolutionType;
	}

	public int getLevelLimit() {
		return levelLimit;
	}

	public void setLevelLimit(int levelLimit) {
		this.levelLimit = levelLimit;
	}

	public long getGold() {
		return gold;
	}

	public void setGold(long gold) {
		this.gold = gold;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getEvolutionTree() {
		return evolutionTree;
	}

	public void setEvolutionTree(String evolutionTree) {
		this.evolutionTree = evolutionTree;
	}

	
	
	
	

}
