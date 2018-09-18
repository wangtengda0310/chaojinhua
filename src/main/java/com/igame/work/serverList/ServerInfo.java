package com.igame.work.serverList;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author Marcus.Z
 *
 */
public class ServerInfo {
	
	public String serverId;//服务器ID
	
	public String serverName;//服务器列表

	@JsonProperty("Zone")   // 客户端有个字段保存在只读文件里 需要指定一下名字
	public String zoneName;//SmartFoxServer配置的zone name

	public String ip;
	public int port;
	
	public int status;//状态 1空闲 2正常 3:火爆
	
	public int worldRoomId;//房间ID
	
	public boolean has;//角色是否存在
	
	public ServerInfo(){}


	public ServerInfo(String serverId, String serverName, String zoneName, int status, int worldRoomId, String ip, int port) {
		this.serverId = serverId;
		this.serverName = serverName;
		this.zoneName = zoneName;
		this.status = status;
		this.worldRoomId = worldRoomId;
		this.ip = ip;
		this.port = port;
	}

	public ServerInfo(String serverId, String serverName, String zoneName, int status, int worldRoomId) {
		this.serverId = serverId;
		this.serverName = serverName;
		this.zoneName = zoneName;
		this.status = status;
		this.worldRoomId = worldRoomId;
	}



	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getZoneName() {
		return zoneName;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
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

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
