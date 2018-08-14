package com.igame.core.data.template;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "low")
public class PlayerLvTemplate {
	
	@XmlAttribute(name = "player_lv", required = true)
	private int playerLv;
	
	@XmlAttribute(name = "monster_lv")
	private int monsterLv;
	
	@XmlAttribute(name = "player_exp")
	private int playerExp;
	
	@XmlAttribute(name = "unlock")
	private String unlock;

	public int getPlayerLv() {
		return playerLv;
	}

	public void setPlayerLv(int playerLv) {
		this.playerLv = playerLv;
	}

	public int getMonsterLv() {
		return monsterLv;
	}

	public void setMonsterLv(int monsterLv) {
		this.monsterLv = monsterLv;
	}

	public int getPlayerExp() {
		return playerExp;
	}

	public void setPlayerExp(int playerExp) {
		this.playerExp = playerExp;
	}

	public String getUnlock() {
		return unlock;
	}

	public void setUnlock(String unlock) {
		this.unlock = unlock;
	}
	
	

}
