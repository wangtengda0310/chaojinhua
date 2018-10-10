package com.igame.work.user.data;


import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import com.google.common.collect.Maps;


/**
 * 
 * @author Marcus.Z
 *
 */
@XmlRootElement(name = "info")
@XmlAccessorType(XmlAccessType.NONE)
public class PlayerLvData
{	
	private int maxLevel = 80;
	
	@XmlElement(name="item")
	private List<PlayerLvTemplate> its;
	
	private Map<Integer,PlayerLvTemplate> list	= Maps.newHashMap();
	

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(PlayerLvTemplate it: its)
		{
			list.put(it.getPlayerLv(), it);
			
		}
	}
	
	public PlayerLvTemplate getTemplate(int id)
	{
		return list.get(id);
	}
	
	public List<PlayerLvTemplate> getAll(){
		return its;
	}


	public int size()
	{
		return list.size();
	}

	public int getMaxLevel() {
		return maxLevel;
	}
 
	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}
	
	
}

 
