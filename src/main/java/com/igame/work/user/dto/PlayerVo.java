package com.igame.work.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Sets;
import com.igame.core.db.BasicVO;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.utils.IndexDirection;

import java.util.Date;
import java.util.Set;

/**
 * @author Marcus.Z
 */
@Entity(noClassnameStored = true)
public class PlayerVo extends BasicVO {

    @Indexed(unique = true, value = IndexDirection.ASC)
    protected long userId;//账号ID

    protected long playerId;//角色ID

    protected int openId;//平台ID

    protected int severId;//服务器ID

    protected String username;//角色名

    protected String nickname;//角色昵称

    @JsonIgnore
    private String lastNickname="";//修改前的昵称

    protected int gender;//玩家性别

    protected int playerFrameId;//玩家头像框

    protected int playerHeadId;//玩家头像

    protected int playerLevel;//玩家等级

    protected int exp;//玩家经验

    protected int vip;//VIP

    protected int bagSpace;//背包空间

    protected int physical = 100;//体力

    protected long gold;//金币

    protected int diamond;//钻石

    //阵容
    //protected String[] teams = new String[]{"-1,-1,-1,-1,-1", "-1,-1,-1,-1,-1", "-1,-1,-1,-1,-1", "-1,-1,-1,-1,-1", "-1,-1,-1,-1,-1"};

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", locale = "zh", timezone = "GMT+8")
    protected Date buildTime;//创建时间

    protected int battleSpace = 1;//初始站位数量

    protected int lastCheckpointId = -1;//上次打过的关卡

    protected String checkPoint = "";//已过关卡列表

    protected long fightValue;//战力

    protected int tongRes = 100;//同化资源

    protected int sao = 10;//扫荡券

    protected int xing = 10;//星能

    protected int phBuyCount;//今日体力购买次数

    protected int xinBuyCount;//今日星能购买次数

    protected int goldBuyCount;//今日金币购买次数

    protected int towerId;//当前已过最高星河之眼关卡Id

    protected int oreCount;//今日已挖矿次数

//	@JsonIgnore
//	protected Map<Long,Integer> wuHpMap = Maps.newHashMap();//无尽之森保存的怪物HP

    protected int wuNai;//当前已使用奶的次数


    protected int wuReset;//当前已使用重置次数

    protected int areaCount = 0;// //今日竞技场已用挑战次数

    protected Set<Integer> unlockHead = Sets.newHashSet();    //已解锁头像

    protected Set<Integer> unlockFrame = Sets.newHashSet();    //已解锁头像框

    protected int isBanStrangers = 0;    //拒绝陌生人私聊 0 = 拒绝,1 = 接受

    protected int curTeam = 1;    //当前出战阵容

    protected double totalMoney = 0;    //充值金额
    
    protected int round = 1;//二周目 1-一周目  2-二周目

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public int getOpenId() {
        return openId;
    }

    public void setOpenId(int openId) {
        this.openId = openId;
    }

    public int getSeverId() {
        return severId;
    }

    public void setSeverId(int severId) {
        this.severId = severId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getPlayerFrameId() {
        return playerFrameId;
    }

    public void setPlayerFrameId(int playerFrameId) {
        this.playerFrameId = playerFrameId;
    }

    public int getPlayerHeadId() {
        return playerHeadId;
    }

    public void setPlayerHeadId(int playerHeadId) {
        this.playerHeadId = playerHeadId;
    }

    public int getPlayerLevel() {
        return playerLevel;
    }

    public void setPlayerLevel(int playerLevel) {
        this.playerLevel = playerLevel;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    public int getBagSpace() {
        return bagSpace;
    }

    public void setBagSpace(int bagSpace) {
        this.bagSpace = bagSpace;
    }

    public int getPhysical() {
        return physical;
    }

    public void setPhysical(int physical) {
        this.physical = physical;
    }

    public long getGold() {
        return gold;
    }

    public void setGold(long gold) {
        this.gold = gold;
    }

    public int getDiamond() {
        return diamond;
    }

    public void setDiamond(int diamond) {
        this.diamond = diamond;
    }

    public Date getBuildTime() {
        return buildTime;
    }

    public void setBuildTime(Date buildTime) {
        this.buildTime = buildTime;
    }

    public int getBattleSpace() {
        return battleSpace;
    }

    public void setBattleSpace(int battleSpace) {
        this.battleSpace = battleSpace;
    }

    public int getLastCheckpointId() {
        return lastCheckpointId;
    }

    public void setLastCheckpointId(int lastCheckpointId) {
        this.lastCheckpointId = lastCheckpointId;
    }

    public String getCheckPoint() {
        return checkPoint;
    }

    public void setCheckPoint(String checkPoint) {
        this.checkPoint = checkPoint;
    }

    public long getFightValue() {
        return fightValue;
    }

    public void setFightValue(long fightValue) {
        this.fightValue = fightValue;
    }

    public int getTongRes() {
        return tongRes;
    }

    public void setTongRes(int tongRes) {
        this.tongRes = tongRes;
    }

    public int getSao() {
        return sao;
    }

    public void setSao(int sao) {
        this.sao = sao;
    }

    public int getXing() {
        return xing;
    }

    public void setXing(int xing) {
        this.xing = xing;
    }

    public int getPhBuyCount() {
        return phBuyCount;
    }

    public void setPhBuyCount(int phBuyCount) {
        this.phBuyCount = phBuyCount;
    }

    public int getXinBuyCount() {
        return xinBuyCount;
    }

    public void setXinBuyCount(int xinBuyCount) {
        this.xinBuyCount = xinBuyCount;
    }

    public int getGoldBuyCount() {
        return goldBuyCount;
    }

    public void setGoldBuyCount(int goldBuyCount) {
        this.goldBuyCount = goldBuyCount;
    }

    public int getTowerId() {
        return towerId;
    }

    public void setTowerId(int towerId) {
        this.towerId = towerId;
    }

    public int getOreCount() {
        return oreCount;
    }

    public void setOreCount(int oreCount) {
        this.oreCount = oreCount;
    }

    public int getWuNai() {
        return wuNai;
    }

    public void setWuNai(int wuNai) {
        this.wuNai = wuNai;
    }

    public int getWuReset() {
        return wuReset;
    }

    public void setWuReset(int wuReset) {
        this.wuReset = wuReset;
    }

    public Set<Integer> getUnlockHead() {
        return unlockHead;
    }

    public void setUnlockHead(Set<Integer> unlockHead) {
        this.unlockHead = unlockHead;
    }

    public Set<Integer> getUnlockFrame() {
        return unlockFrame;
    }

    public void setUnlockFrame(Set<Integer> unlockFrame) {
        this.unlockFrame = unlockFrame;
    }

	public int getAreaCount() {
		return areaCount;
	}

	public void setAreaCount(int areaCount) {
		this.areaCount = areaCount;
	}

    public int getIsBanStrangers() {
        return isBanStrangers;
    }

    public void setIsBanStrangers(int isBanStrangers) {
        this.isBanStrangers = isBanStrangers;
    }

    public int getCurTeam() {
        return curTeam;
    }

    public void setCurTeam(int curTeam) {
        this.curTeam = curTeam;
    }

    public double getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(double totalMoney) {
        this.totalMoney = totalMoney;
    }

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

    public String getLastNickname() {
        return lastNickname;
    }

    public void setLastNickname(String lastNickname) {
        this.lastNickname = lastNickname;
    }
}
