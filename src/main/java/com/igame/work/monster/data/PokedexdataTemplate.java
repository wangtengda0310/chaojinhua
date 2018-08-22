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
@XmlRootElement(name = "low")
public class PokedexdataTemplate {
	

	@XmlAttribute(name = "pokedex_type")
	private int pokedexType;
	
	@XmlAttribute(name = "monster_stock")
	private String monsterStock;
	
	@XmlAttribute(name = "take_monster")
	private String takeMonster;
	
	@XmlAttribute(name = "effect_id")
	private String effectId;
	
	@XmlAttribute(name = "value")
	private String value;
	
	@XmlAttribute(name = "plus100")
	private String plus100;
	
	@XmlAttribute(name = "plus50")
	private String plus50;
	
	@XmlAttribute(name = "plus25")
	private String plus25;

	public int getPokedexType() {
		return pokedexType;
	}

	public void setPokedexType(int pokedexType) {
		this.pokedexType = pokedexType;
	}

	public String getMonsterStock() {
		return monsterStock;
	}

	public void setMonsterStock(String monsterStock) {
		this.monsterStock = monsterStock;
	}

	public String getTakeMonster() {
		return takeMonster;
	}

	public void setTakeMonster(String takeMonster) {
		this.takeMonster = takeMonster;
	}

	public String getEffectId() {
		return effectId;
	}

	public void setEffectId(String effectId) {
		this.effectId = effectId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getPlus100() {
		return plus100;
	}

	public void setPlus100(String plus100) {
		this.plus100 = plus100;
	}

	public String getPlus50() {
		return plus50;
	}

	public void setPlus50(String plus50) {
		this.plus50 = plus50;
	}

	public String getPlus25() {
		return plus25;
	}

	public void setPlus25(String plus25) {
		this.plus25 = plus25;
	}
	
	
	
	
	
	
	
	

}
