package com.igame.core.data; 


import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Maps;
import com.igame.core.data.template.MonsterTemplate;





/**
 * 
 * @author Marcus.Z
 *
 */
@XmlRootElement(name = "monsterdata")
@XmlAccessorType(XmlAccessType.NONE)
public class MonsterData
{
	@XmlElement(name="low")
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

	/**
	 * @return 
	 */
	public int size()
	{
		return monsters.size();
	}
}

 
