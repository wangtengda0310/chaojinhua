package com.igame.work.fight.data;


import com.google.common.collect.Maps;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Map;


/**
 * 
 * @author Marcus.Z
 *
 */
@XmlRootElement(name = "info")
@XmlAccessorType(XmlAccessType.NONE)
public class SkillData
{
	@XmlElement(name="item")
	private List<SkillTemplate> its;
	
	private Map<Integer,SkillTemplate> maps	= Maps.newHashMap();
	

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(SkillTemplate it: its)
		{
			maps.put(it.getSkillId(), it);
			
		}
	}
	
	public SkillTemplate getTemplate(int id)
	{
		return maps.get(id);
	}
	
	public List<SkillTemplate> getAll(){
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

 
