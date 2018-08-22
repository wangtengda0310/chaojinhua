package com.igame.work.serverList;

/**
 * 
 * @author Marcus.Z
 *
 */
public class ServerInfo {
	
	public int serverId;//服务器ID
	
	public String serverName;//服务器列表
	
	public int status;//状态 1空闲 2正常 3:火爆
	
	public int worldRoomId;//房间ID
	
	public boolean has;//角色是否存在
	
	public ServerInfo(){}
	
	

	public ServerInfo(int serverId, String serverName, int status,
			int worldRoomId) {
		super();
		this.serverId = serverId;
		this.serverName = serverName;
		this.status = status;
		this.worldRoomId = worldRoomId;
	}



	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getWorldRoomId() {
		return worldRoomId;
	}

	public void setWorldRoomId(int worldRoomId) {
		this.worldRoomId = worldRoomId;
	}



	public boolean isHas() {
		return has;
	}



	public void setHas(boolean has) {
		this.has = has;
	}
	
	
	

}
