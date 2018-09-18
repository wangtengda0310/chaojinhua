package com.igame.work.fight.dto;


import org.mongodb.morphia.annotations.Entity;
import com.igame.work.monster.dto.Gods;

/**
 * 
 * @author Marcus.Z
 *
 */
@Entity(noClassnameStored = true)
public class GodsDto extends Gods {
	
	
	public GodsDto(){
		
	}
	
	public GodsDto(Gods gods){
		
		this.godsType = gods.getGodsType();
		this.godsLevel = gods.getGodsLevel();
		
	}

	public int getGodsType() {
		return godsType;
	}

	public void setGodsType(int godsType) {
		this.godsType = godsType;
	}

	public int getGodsLevel() {
		return godsLevel;
	}

	public void setGodsLevel(int godsLevel) {
		this.godsLevel = godsLevel;
	}
	
	
	

}
