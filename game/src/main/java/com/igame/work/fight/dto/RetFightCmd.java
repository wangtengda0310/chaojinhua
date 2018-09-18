package com.igame.work.fight.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.igame.work.monster.dto.Effect;


/**
 * 
 * @author Marcus.Z
 *
 */
public class RetFightCmd {
	
	@JsonIgnore
	public int res;
	public int attackerType;//指令发起者类型 0-无（比如各种HOT类型BUFFER，可忽略attackerId，cmdType，cmdId，targetId具体值） 1-玩家怪物 2-玩家神灵 3-AI怪物（PVE）
	public long attackerId; //发起者的怪物ID
	public int cmdType;//指令类型 1-普通攻击 2-技能 0-无（比如BUFFER）
	public int cmdId; //指令ID  如果有技能则为技能ID 物理攻击为0 -1 为BUFFER
	public long targetId; //目标id
	public String target = "";//受到伤害/影响     目标的ID,HP变动值,剩余HP;目标2的ID,HP变动值,剩余HP
//	public String hp = "";//受到伤害/影响目标的变动HP值 逗号分隔
//	public String leftHp = "";//受到伤害/影响目标的剩余HP值 逗号分隔
//	public String bufferTarget = "";//BUFFER    目标的ID,bufferId:bufferId;目标的ID2,bufferId2:bufferId2
	public String bufferStr = "";// buffer效果ID,生效者ID,中心点,生效半径,影响数值
	
	@JsonIgnore
	public List<Effect> buffer = Lists.newArrayList();//生效buffer列表
	
	
	
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
	public String getTarget() {
		return target;
	}
	public void addTargetStr(String str) {
		 target+=str;
	}
	public void setTarget(String target) {
		this.target = target;
	}

	public int getRes() {
		return res;
	}
	public void setRes(int res) {
		this.res = res;
	}
	public List<Effect> getBuffer() {
		return buffer;
	}
	public void setBuffer(List<Effect> buffer) {
		this.buffer = buffer;
	}
	public String getBufferStr() {
		return bufferStr;
	}
	public void setBufferStr(String bufferStr) {
		this.bufferStr = bufferStr;
	}
	
	public void initBufferStr(){
		StringBuilder sb = new StringBuilder();
		if(!buffer.isEmpty()){
			for(Effect ee : buffer){
				sb.append(";").append(ee.getEffectId()).append(",").append(ee.getTargetId()).append(",").append(ee.getCenter())
					.append(",").append(ee.getRange()).append(",").append(ee.getValue());
			}
			this.bufferStr = sb.toString().substring(1);
		}
	}
	
	
	
	
	

}
