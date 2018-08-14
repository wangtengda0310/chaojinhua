package com.igame.work.checkpoint.dto;

import java.util.List;

import com.google.common.collect.Lists;


/**
 * 
 * @author Marcus.Z
 *
 */
public class WuZhengDto {
	
	public String wuGods;
	
	public List<String> wuMons = Lists.newArrayList();

	public String getWuGods() {
		return wuGods;
	}

	public void setWuGods(String wuGods) {
		this.wuGods = wuGods;
	}

	public List<String> getWuMons() {
		return wuMons;
	}

	public void setWuMons(List<String> wuMons) {
		this.wuMons = wuMons;
	}
	
	

}
