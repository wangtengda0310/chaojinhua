package com.igame.core.data; 

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.igame.core.data.template.ChatTabooTemplate;


/**
 * 
 * @author zhh
 *
 */
@XmlRootElement(name = "taboos")
@XmlAccessorType(XmlAccessType.NONE)
public class ChatTabooData
{
	@XmlElement(name="taboo")
	private List<ChatTabooTemplate> its;
	
	private  List<String> chat_taboos	= new ArrayList<String>();
	

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(ChatTabooTemplate it: its)
		{
			chat_taboos.add(it.getTxt());
			
		}
	}
	
	/**
	 * 替换非法字符 的方法
	 * @param content
	 * @return
	 */
	public  String  filterWords (String  content){
		for(String stopword : chat_taboos){
			content = content.replaceAll(stopword, createTemp(stopword));
		}
		return content;
	}
	/**
	 * @param stopword
	 * @return
	 */
	private String createTemp(String stopword) {
		StringBuilder sler= new StringBuilder();
		for(int i=0; i<stopword.length();i++){
			sler.append("*");
		}
		return sler.toString();
	}

	public List<String> getChatTaboos() {
		return chat_taboos;
	}
	

	/**
	 * @return 
	 */
	public int size()
	{
		return chat_taboos.size();
	}
	
//	public static void main(String[] arg){
//		System.err.println(DataManager.getInstance().CHAT_TABOO_DATA.filterWords("总理...sdfdsfsf中华"));
//	}
}

 
