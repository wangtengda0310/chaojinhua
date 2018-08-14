package com.igame.core.data; 


import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Maps;
import com.igame.core.data.template.MonsterBreakTemplate;
import com.igame.core.data.template.MonsterEvolutionTemplate;





/**
 * 
 * @author Marcus.Z
 *
 */
@XmlRootElement(name = "monster_break")
@XmlAccessorType(XmlAccessType.NONE)
public class MonsterBreakData
{
	@XmlElement(name="low")
	private List<MonsterBreakTemplate> its;
	
	private Map<Integer,MonsterBreakTemplate> maps	= Maps.newHashMap();
	

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(MonsterBreakTemplate it: its)
		{
			maps.put(it.getBreaklv(), it);
			
		}
	}
	
	public MonsterBreakTemplate getTemplate(int id)
	{
		return maps.get(id);
	}
	
	public List<MonsterBreakTemplate> getAll(){
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

 
