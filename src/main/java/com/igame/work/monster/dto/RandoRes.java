package com.igame.work.monster.dto;

/**
 * 
 * @author Marcus.Z
 *
 */
public class RandoRes {
	
	public int res;
	
	public String type;
	
	public long total;
	
	public RandoRes(){}
	
	

	public RandoRes(int res, String type) {
		super();
		this.res = res;
		this.type = type;
	}



	public int getRes() {
		return res;
	}

	public void setRes(int res) {
		this.res = res;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}



	public long getTotal() {
		return total;
	}



	public void setTotal(long total) {
		this.total = total;
	}
	
	
	

}
