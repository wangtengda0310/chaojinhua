package com.igame.work.friend.dto;

import com.igame.work.checkpoint.dto.TangSuoDto;
import com.igame.work.monster.dao.MonsterDAO;
import com.igame.work.monster.dto.Monster;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Transient;

/**
 * 
 * @author xym
 *
 */
public class FriendExplore {

	private int id;

	private String mons = "";//上阵怪物列表

	private long leftTime;

	private int state;//0-未在探索 1-探索中

	private int isHelp;	//0=未帮助,1=已帮助	如果已帮助，提前 90 分钟结束

	private String accPlayerName = "";	//热心好友昵称

	public FriendExplore() {
	}

	public FriendExplore(TangSuoDto tangSuoDto, int severId) {

		this.id = tangSuoDto.getId();
		this.leftTime = tangSuoDto.getLeftTime();
		this.state = tangSuoDto.getState();
		this.isHelp = tangSuoDto.getIsHelp();
		this.accPlayerName = tangSuoDto.getAccPlayerName();

		//  "monId:monLv:breakLv;monId,monLv,breakLv;"
		StringBuilder sb = new StringBuilder();
		String[] split = tangSuoDto.getMons().split(",");
		for (String s : split) {

			Monster monsterByOid = MonsterDAO.ins().getMonsterByOid(severId, Long.parseLong(s));
			if (monsterByOid != null)
				sb.append(monsterByOid.getMonsterId()+":"+monsterByOid.getLevel()+":"+monsterByOid.getBreaklv()+";");

		}

		this.mons = sb.toString();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMons() {
		return mons;
	}

	public void setMons(String mons) {
		this.mons = mons;
	}

	public long getLeftTime() {
		return leftTime;
	}

	public void setLeftTime(long leftTime) {
		this.leftTime = leftTime;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getIsHelp() {
		return isHelp;
	}

	public void setIsHelp(int isHelp) {
		this.isHelp = isHelp;
	}

	public String getAccPlayerName() {
		return accPlayerName;
	}

	public void setAccPlayerName(String accPlayerName) {
		this.accPlayerName = accPlayerName;
	}
}
