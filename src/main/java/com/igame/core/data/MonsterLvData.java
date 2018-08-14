package com.igame.core.data; 


import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Maps;
import com.igame.core.data.template.MonsterLvTemplate;
import com.igame.core.data.template.PlayerLvTemplate;





/**
 * 
 * @author Marcus.Z
 *
 */
@XmlRootElement(name = "monsterlv")
@XmlAccessorType(XmlAccessType.NONE)
public class MonsterLvData
{	
	private int maxLevel = 80;
	
	@XmlElement(name="low")
	private List<MonsterLvTemplate> its;
	
	private Map<Integer,MonsterLvTemplate> list	= Maps.newHashMap();
	

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(MonsterLvTemplate it: its)
		{
			list.put(it.getLevel(), it);
			
		}
	}
	
	public MonsterLvTemplate getTemplate(int id)
	{
		return list.get(id);
	}
	
	public List<MonsterLvTemplate> getAll(){
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

 
