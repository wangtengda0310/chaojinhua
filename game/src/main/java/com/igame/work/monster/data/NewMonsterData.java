package com.igame.work.monster.data;


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
@XmlRootElement(name = "newmonster")
@XmlAccessorType(XmlAccessType.NONE)
public class NewMonsterData
{
	@XmlElement(name="low")
	private List<NewMonsterTemplate> its;
	
	private Map<Integer,NewMonsterTemplate> maps	= Maps.newHashMap();
	

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(NewMonsterTemplate it: its)
		{
			maps.put(it.getNewMonster(), it);
			
		}
	}
	
	public NewMonsterTemplate getTemplate(int id)
	{
		return maps.get(id);
	}
	
	public List<NewMonsterTemplate> getAll(){
		return its;
	}
	


	/**
	 * @return 
	 */
	public int size()
	{
		return maps.size();
	}
}

 
