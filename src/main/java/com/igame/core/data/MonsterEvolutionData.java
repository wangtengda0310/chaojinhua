package com.igame.core.data; 


import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Maps;
import com.igame.core.data.template.MonsterEvolutionTemplate;





/**
 * 
 * @author Marcus.Z
 *
 */
@XmlRootElement(name = "monster_evolution")
@XmlAccessorType(XmlAccessType.NONE)
public class MonsterEvolutionData
{
	@XmlElement(name="low")
	private List<MonsterEvolutionTemplate> its;
	
	private Map<Integer,MonsterEvolutionTemplate> maps	= Maps.newHashMap();
	

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(MonsterEvolutionTemplate it: its)
		{
			maps.put(it.getMonsterId(), it);
			
		}
	}
	
	public MonsterEvolutionTemplate getTemplate(int id)
	{
		return maps.get(id);
	}
	
	public List<MonsterEvolutionTemplate> getAll(){
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

 
