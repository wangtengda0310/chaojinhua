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
public class DrawLevelTemplate {
	

	@XmlAttribute(name = "draw_level")
	private int drawLevel;
	
	@XmlAttribute(name = "draw_exp")
	private int drawExp;

	public int getDrawLevel() {
		return drawLevel;
	}

	public void setDrawLevel(int drawLevel) {
		this.drawLevel = drawLevel;
	}

	public int getDrawExp() {
		return drawExp;
	}

	public void setDrawExp(int drawExp) {
		this.drawExp = drawExp;
	}

	
	
	
	
	
	
	
	

}
