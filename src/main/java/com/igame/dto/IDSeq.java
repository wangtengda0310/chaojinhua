package com.igame.dto;



import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Indexed;

import com.igame.core.db.BasicDto;


/**
 * 
 * @author Marcus.Z
 *
 */
@Entity(value = "IDSeq", noClassnameStored = true)
public class IDSeq extends BasicDto {
	

	@Indexed
	private String type;
	
    private int value;
    
    public IDSeq(){}

	public IDSeq(String type, int value) {
		super();
		this.type = type;
		this.value = value;
	    
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	
	
    
    

}
