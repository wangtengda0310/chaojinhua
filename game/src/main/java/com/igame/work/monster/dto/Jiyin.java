package com.igame.work.monster.dto;

/**
 * 
 * @author Marcus.Z
 *
 */
public class Jiyin {
	
	public int level;
	
	public String type;
	
	public Jiyin(){}
	
	

	public Jiyin(int level, String type) {
		super();
		this.level = level;
		this.type = type;
	}



	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	

}
