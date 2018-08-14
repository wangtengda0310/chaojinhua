package com.igame.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Marcus.Z
 *
 */
public class SRetVO {
	

	public Map<String,Object> data;	

	
	public SRetVO(){}


	public Map<String,Object> getData() {
		return data;
	}

	public void setData(Map<String,Object> data) {
		this.data = data;
	}

	public void addData(String key,Object value) {
		if(this.data == null){
			this.data = new HashMap<String,Object>();
		}
		data.put(key, value);
	}

	
	

}
