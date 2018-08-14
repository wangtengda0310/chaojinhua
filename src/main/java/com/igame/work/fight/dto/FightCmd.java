package com.igame.work.fight.dto;


/**
 * 
 * @author Marcus.Z
 *
 */
public class FightCmd {
	
	public int attackerType;//指令发起者类型 1-玩家怪物 2-玩家神灵 3-AI怪物（PVE）
	public long attackerId; //发起者的怪物ID
	public int cmdType;//指令类型 1-普通攻击 2-技能
	public int cmdId; //指令ID  如果有技能则为技能ID 物理攻击为0
	public long targetId; //目标id
	
	public FightCmd(){}
	
	
	
	public FightCmd(int attackerType, long attackerId, int cmdType, int cmdId,long targetId) {
		super();
		this.attackerType = attackerType;
		this.attackerId = attackerId;
		this.cmdType = cmdType;
		this.cmdId = cmdId;
		this.targetId = targetId;
	}



	public int getAttackerType() {
		return attackerType;
	}
	public void setAttackerType(int attackerType) {
		this.attackerType = attackerType;
	}
	public long getAttackerId() {
		return attackerId;
	}
	public void setAttackerId(long attackerId) {
		this.attackerId = attackerId;
	}
	public int getCmdType() {
		return cmdType;
	}
	public void setCmdType(int cmdType) {
		this.cmdType = cmdType;
	}
	public int getCmdId() {
		return cmdId;
	}
	public void setCmdId(int cmdId) {
		this.cmdId = cmdId;
	}
	public long getTargetId() {
		return targetId;
	}
	public void setTargetId(long targetId) {
		this.targetId = targetId;
	}
	
	
	
	
	

}
