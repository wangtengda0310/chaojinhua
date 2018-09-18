package com.igame.work.chat.data;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * 
 * @author zhh
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "taboo")
public class ChatTabooTemplate{
	
	/**
	 * ID
	 */
	@XmlAttribute(name = "txt")
	private String txt;

	public String getTxt() {
		return txt;
	}

	public void setTxt(String txt) {
		this.txt = txt;
	}

	
	

}
