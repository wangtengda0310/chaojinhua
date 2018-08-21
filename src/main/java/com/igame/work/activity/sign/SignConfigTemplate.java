package com.igame.work.activity.sign;



import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * 
 * @author Marcus.Z
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "low")
public class SignConfigTemplate {
	

	@XmlAttribute(name = "sign_num")
	private int signNum;
		
	@XmlAttribute(name = "reward_data")
	private String rewardData;
	
	@XmlAttribute(name = "total_sign3")
	private String totalSign3;
	
	@XmlAttribute(name = "total_sign7")
	private  String totalSign7;
	
	@XmlAttribute(name = "total_sign15")
	private  String totalSign15;
	
	@XmlAttribute(name = "total_sign30")
	private  String totalSign30;

	public int getSignNum() {
		return signNum;
	}

	public void setSignNum(int signNum) {
		this.signNum = signNum;
	}

	public String getRewardData() {
		return rewardData;
	}

	public void setRewardData(String rewardData) {
		this.rewardData = rewardData;
	}

	public String getTotalSign3() {
		return totalSign3;
	}

	public void setTotalSign3(String totalSign3) {
		this.totalSign3 = totalSign3;
	}

	public String getTotalSign7() {
		return totalSign7;
	}

	public void setTotalSign7(String totalSign7) {
		this.totalSign7 = totalSign7;
	}

	public String getTotalSign15() {
		return totalSign15;
	}

	public void setTotalSign15(String totalSign15) {
		this.totalSign15 = totalSign15;
	}

	public String getTotalSign30() {
		return totalSign30;
	}

	public void setTotalSign30(String totalSign30) {
		this.totalSign30 = totalSign30;
	}
}
