package com.igame.work.checkpoint.mingyunZhiMen.data;



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
public class FatedataTemplate {
	

	@XmlAttribute(name = "floor_num")
	private int floorNum;
		

	@XmlAttribute(name = "monste1r_library")
	private String monste1rLibrary;
	
	@XmlAttribute(name = "monster1_lv")
	private  int monster1Lv;
	
	@XmlAttribute(name = "skill1_lv")
	private  int skill1Lv;
	
	@XmlAttribute(name = "monster2_library")
	private String monste2rLibrary;
	
	@XmlAttribute(name = "monster2_lv")
	private  int monster2Lv;
	
	@XmlAttribute(name = "skill2_lv")
	private  int skill2Lv;

	public int getFloorNum() {
		return floorNum;
	}

	public void setFloorNum(int floorNum) {
		this.floorNum = floorNum;
	}

	public String getMonste1rLibrary() {
		return monste1rLibrary;
	}

	public void setMonste1rLibrary(String monste1rLibrary) {
		this.monste1rLibrary = monste1rLibrary;
	}

	public int getMonster1Lv() {
		return monster1Lv;
	}

	public void setMonster1Lv(int monster1Lv) {
		this.monster1Lv = monster1Lv;
	}

	public int getSkill1Lv() {
		return skill1Lv;
	}

	public void setSkill1Lv(int skill1Lv) {
		this.skill1Lv = skill1Lv;
	}

	public String getMonste2rLibrary() {
		return monste2rLibrary;
	}

	public void setMonste2rLibrary(String monste2rLibrary) {
		this.monste2rLibrary = monste2rLibrary;
	}

	public int getMonster2Lv() {
		return monster2Lv;
	}

	public void setMonster2Lv(int monster2Lv) {
		this.monster2Lv = monster2Lv;
	}

	public int getSkill2Lv() {
		return skill2Lv;
	}

	public void setSkill2Lv(int skill2Lv) {
		this.skill2Lv = skill2Lv;
	}
	
	
	
	
	
	
	
	
	
	
	

}
