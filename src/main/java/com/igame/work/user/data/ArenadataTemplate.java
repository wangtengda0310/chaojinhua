package com.igame.work.user.data;



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
public class ArenadataTemplate {
	

	@XmlAttribute(name = "arena_type")
	private int arenaType;
		
	@XmlAttribute(name = "player_lv")
	private String playerLv;
	
	@XmlAttribute(name = "ai_lv")
	private String aiLv;
	
	@XmlAttribute(name = "front")
	private  String front;
	
	@XmlAttribute(name = "num")
	private  String num;
	
	@XmlAttribute(name = "back")
	private  String back;

	public int getArenaType() {
		return arenaType;
	}

	public void setArenaType(int arenaType) {
		this.arenaType = arenaType;
	}

	public String getPlayerLv() {
		return playerLv;
	}

	public void setPlayerLv(String playerLv) {
		this.playerLv = playerLv;
	}

	public String getAiLv() {
		return aiLv;
	}

	public void setAiLv(String aiLv) {
		this.aiLv = aiLv;
	}

	public String getFront() {
		return front;
	}

	public void setFront(String front) {
		this.front = front;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getBack() {
		return back;
	}

	public void setBack(String back) {
		this.back = back;
	}


	
	

}
