package com.igame.core.data.template;



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
public class NewMonsterTemplate {
	


	@XmlAttribute(name = "num", required = true)
	private int num;
	
	@XmlAttribute(name = "new_monster")
	private int newMonster;
	
	@XmlAttribute(name = "condition")
	private String condition;//

	@XmlAttribute(name = "item")
	private String item;

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public int getNewMonster() {
		return newMonster;
	}

	public void setNewMonster(int newMonster) {
		this.newMonster = newMonster;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}


	
	
	
	
	

}
