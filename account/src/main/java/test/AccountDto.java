package test;


import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Transient;


@Entity(value = "AccountDto", noClassnameStored = true)
public class AccountDto  extends CBasicVO  {

	/**
	 * 用户账户表
	 */
	
	private long userId;
	
	@Indexed
	private String username;
	
	@JsonIgnore
	private String password;
	
	@JsonIgnore
	private String email;
	
	@JsonIgnore
	private int isBind;
	
	@JsonIgnore
	private String phoneNum;
	
	@JsonIgnore
	private Date regDate;
	
	@JsonIgnore
	private String lastLoginIp;
	
	@JsonIgnore
	private Date lastLoginTime;
	
	@JsonIgnore
	private int isActive;
	
	private int lastServerId = 1;
	
	@JsonIgnore
	private String macCode;

	@Transient
	private String serverIp = "192.168.2.234";

	@Transient
	private int serverPort = 9933;

	public AccountDto(){}
	
	public AccountDto( String username, String password){
		this.username = username;
		this.password = password;
	}
	
	public AccountDto( String username, String password, String email, int isBind, String phoneNum, String lastLoginIp, Date date, int isActive) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.isBind = isBind;
		this.phoneNum = phoneNum;
		this.lastLoginIp = lastLoginIp;
		this.lastLoginTime = date;
		this.isActive = isActive;
	}
	
	
	
	

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getIsBind() {
		return isBind;
	}

	public void setIsBind(int isBind) {
		this.isBind = isBind;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public Date getRegDate() {
		return regDate;
	}

	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}

	public String getLastLoginIp() {
		return lastLoginIp;
	}

	public void setLastLoginIp(String lastLoginIp) {
		this.lastLoginIp = lastLoginIp;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	public int getLastServerId() {
		return lastServerId;
	}

	public void setLastServerId(int lastServerId) {
		this.lastServerId = 1;
	}

	public String getMacCode() {
		return macCode;
	}

	public void setMacCode(String macCode) {
		this.macCode = macCode;
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}


	
}
