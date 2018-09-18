package com.igame.work.draw;

import org.mongodb.morphia.annotations.Entity;


/**
 * 
 * @author Marcus.Z
 *
 */
@Entity(noClassnameStored = true)
public class DrawData {
	
	public int drawLv = 1;
	
	public int drawExp;
	
	public String drawList = "";

	public int getDrawLv() {
		return drawLv;
	}

	public void setDrawLv(int drawLv) {
		this.drawLv = drawLv;
	}

	public int getDrawExp() {
		return drawExp;
	}

	public void setDrawExp(int drawExp) {
		this.drawExp = drawExp;
	}

	public String getDrawList() {
		return drawList;
	}

	public void setDrawList(String drawList) {
		this.drawList = drawList;
	}
	
	
	
}
