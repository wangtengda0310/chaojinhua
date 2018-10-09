package com.igame.work.fight.arena;



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
	
	@XmlAttribute(name = "monsterset")
	private String monsterset;
	
	@XmlAttribute(name = "type_name")
	private  String type_name;

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

	public String getMonsterset() {
		return monsterset;
	}

	public void setMonsterset(String monsterset) {
		this.monsterset = monsterset;
	}

	public String getType_name() {
		return type_name;
	}

	public void setType_name(String type_name) {
		this.type_name = type_name;
	}
}
