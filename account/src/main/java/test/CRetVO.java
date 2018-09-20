package test;

import java.util.Map;

/**
 * 
 * @author Marcus.Z
 *
 */
public class CRetVO {
	
	public int cmd;
	
	public int state;
	
	public int errCode;
	
	public Object data;	

	
	public CRetVO(){}
	
	

	public CRetVO(int cmd) {
		super();
		this.cmd = cmd;
	}



	public int getCmd() {
		return cmd;
	}

	public void setCmd(int cmd) {
		this.cmd = cmd;
	}

	

	public int getState() {
		return state;
	}



	public void setState(int state) {
		this.state = state;
	}
	


	public int getErrCode() {
		return errCode;
	}


	public void setErrCode(int errCode) {
		this.errCode = errCode;
	}

	public Object getData() {
		return data;
	}


	public void setData(Object data) {
		this.data = data;
	}

	public void setData(Map<String,Object> data) {
		this.data = data;
	}
	


	
	
	

}
