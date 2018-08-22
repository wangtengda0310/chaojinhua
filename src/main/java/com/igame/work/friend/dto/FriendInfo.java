package com.igame.work.friend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.igame.core.db.BasicDto;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Transient;

import java.util.List;


/**
 * @author xym
 *
 * 好友信息
 */
@Entity(value = "Friends", noClassnameStored = true)
public class FriendInfo extends BasicDto {
	
	@Indexed
	@JsonIgnore
	private long playerId;//所属角色ID

	@Transient
	private int physicalCount;

	@Transient
	private int exploreCount;

	@Transient
	private int maxFriendCount = 20;

	private List<Friend> curFriends = Lists.newArrayList();	//好友列表

	private List<Friend> reqFriends = Lists.newArrayList();	//好友请求列表

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	public List<Friend> getCurFriends() {
		return curFriends;
	}

	public void setCurFriends(List<Friend> curFriends) {
		this.curFriends = curFriends;
	}

	public List<Friend> getReqFriends() {
		return reqFriends;
	}

	public void setReqFriends(List<Friend> reqFriends) {
		this.reqFriends = reqFriends;
	}

	public int getPhysicalCount() {
		return physicalCount;
	}

	public void setPhysicalCount(int physicalCount) {
		this.physicalCount = physicalCount;
	}

	public int getExploreCount() {
		return exploreCount;
	}

	public void setExploreCount(int exploreCount) {
		this.exploreCount = exploreCount;
	}

	public int getMaxFriendCount() {
		return maxFriendCount;
	}

	public void setMaxFriendCount(int maxFriendCount) {
		this.maxFriendCount = maxFriendCount;
	}
}
