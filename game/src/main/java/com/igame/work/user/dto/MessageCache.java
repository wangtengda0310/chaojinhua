package com.igame.work.user.dto;

import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class MessageCache {
	
	public String cmdName;
	public ISFSObject iSFSObject;
	
	public MessageCache(){}
	
	
	
	public MessageCache(String cmdName, ISFSObject iSFSObject) {
		super();
		this.cmdName = cmdName;
		this.iSFSObject = iSFSObject;
	}



	public String getCmdName() {
		return cmdName;
	}
	public void setCmdName(String cmdName) {
		this.cmdName = cmdName;
	}
	public ISFSObject getiSFSObject() {
		return iSFSObject;
	}
	public void setiSFSObject(ISFSObject iSFSObject) {
		this.iSFSObject = iSFSObject;
	}
	
	
	
	

}
