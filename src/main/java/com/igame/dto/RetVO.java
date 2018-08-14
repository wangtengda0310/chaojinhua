package com.igame.dto;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author Marcus.Z
 *
 */
public class RetVO {
	

	public int state;
	
	public int errCode;
	
	public int index;
	
	public int isPush;
	
	public Map<String,Object> data;	
	

	
	public RetVO(){}	
	

	public int getState() {
		return state;
	}



	public void setState(int state) {
		this.state = state;
	}



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



	public int getErrCode() {
		return errCode;
	}



	public void setErrCode(int errCode) {
		this.errCode = errCode;
	}


	public int getIndex() {
		return index;
	}


	public void setIndex(int index) {
		this.index = index;
	}


	public int getIsPush() {
		return isPush;
	}


	public void setIsPush(int isPush) {
		this.isPush = isPush;
	}
	
	
	
	

}
