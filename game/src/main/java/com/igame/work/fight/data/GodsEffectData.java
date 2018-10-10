package com.igame.work.fight.data;


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
public class GodsEffectData
{
	@XmlElement(name="item")
	private List<GodsEffectTemplate> its;
	
	private Map<Integer,GodsEffectTemplate> maps	= Maps.newHashMap();
	

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(GodsEffectTemplate it: its)
		{
			maps.put(it.getGodsEffect(), it);
			
		}
	}
	
	public GodsEffectTemplate getTemplate(int id)
	{
		return maps.get(id);
	}
	
	public List<GodsEffectTemplate> getAll(){
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

 
