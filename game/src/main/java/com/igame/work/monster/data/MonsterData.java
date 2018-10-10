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
@XmlRootElement(name = "info")
@XmlAccessorType(XmlAccessType.NONE)
public class MonsterData
{
	@XmlElement(name="item")
	private List<MonsterTemplate> its;
	
	private Map<Integer,MonsterTemplate> monsters	= Maps.newHashMap();
	

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(MonsterTemplate it: its)
		{
			monsters.put(it.getMonster_id(), it);
			
		}
	}
	
	public MonsterTemplate getMonsterTemplate(int id)
	{
		return monsters.get(id);
	}

	
	public List<MonsterTemplate> getAll(){
		return its;
	}

	public int size()
	{
		return monsters.size();
	}
}

 
