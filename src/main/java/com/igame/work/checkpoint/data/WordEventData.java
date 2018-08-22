package com.igame.work.checkpoint.data;


import com.google.common.collect.Maps;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;





/**
 * 
 * @author Marcus.Z
 *
 */
@XmlRootElement(name = "worldevent")
@XmlAccessorType(XmlAccessType.NONE)
public class WordEventData
{
	@XmlElement(name="low")
	private List<WorldEventTemplate> its;
	
	private Map<String,WorldEventTemplate> maps	= Maps.newHashMap();
	

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(WorldEventTemplate it: its)
		{
			maps.put(it.getEvent_type()+"_"+it.getLevel(), it);
			
		}
	}
	
	public WorldEventTemplate getTemplate(String id)
	{
		return maps.get(id);
	}
	
	public List<WorldEventTemplate> getAll(){
		return its;
	}
	
	public List<WorldEventTemplate> getByEvent_type(int type){
		
		List<WorldEventTemplate> ls = its.stream().filter(e->e.getEvent_type() == type).collect(Collectors.toList());
		ls.sort((h1, h2) -> h1.getLevel() - h2.getLevel());
		return ls;
	}


	/**
	 * @return 
	 */
	public int size()
	{
		return maps.size();
	}
}

 
