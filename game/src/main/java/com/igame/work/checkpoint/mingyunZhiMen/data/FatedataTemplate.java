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
@XmlRootElement(name = "item")
public class FatedataTemplate {
	

	@XmlAttribute(name = "boss_monsterset")
	private String bossMonsterset;
		

	@XmlAttribute(name = "floor_num")
	private int floorNum;
	
	@XmlAttribute(name = "normal_monsterset")
	private  String normalMonsterset;

	public String getBossMonsterset() {
		return bossMonsterset;
	}

	public void setBossMonsterset(String bossMonsterset) {
		this.bossMonsterset = bossMonsterset;
	}

	public int getFloorNum() {
		return floorNum;
	}

	public void setFloorNum(int floorNum) {
		this.floorNum = floorNum;
	}

	public String getNormalMonsterset() {
		return normalMonsterset;
	}

	public void setNormalMonsterset(String normalMonsterset) {
		this.normalMonsterset = normalMonsterset;
	}
}
