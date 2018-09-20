package test;

/**
 * 
 * @author Marcus.Z
 *
 */
public class ServerInfo {
	
	public int id;
	public String name;
	public String ip;
	public int port;
	public int room;
	public int state;
	
	public ServerInfo(){}
	
	
	
	public ServerInfo(int id, String name, String ip, int port, int room, int state) {
		super();
		this.id = id;
		this.name = name;
		this.ip = ip;
		this.port = port;
		this.room = room;
		this.state = state;
	}



	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public int getRoom() {
		return room;
	}
	public void setRoom(int room) {
		this.room = room;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	
	

}
