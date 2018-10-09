package com.igame.work.checkpoint.wujinZhiSen;



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
public class EndlessdataTemplate {
	

	@XmlAttribute(name = "num")
	private int num;
		

	@XmlAttribute(name = "lv_range")
	private String lvRange;
	
	@XmlAttribute(name = "difficulty")
	private  int difficulty;
	
	@XmlAttribute(name = "monsterset")
	private  String monsterset;

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getLvRange() {
		return lvRange;
	}

	public void setLvRange(String lvRange) {
		this.lvRange = lvRange;
	}

	public int getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}

	public String getMonsterset() {
		return monsterset;
	}

	public void setMonsterset(String monsterset) {
		this.monsterset = monsterset;
	}
}
