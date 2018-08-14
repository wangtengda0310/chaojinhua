package com.igame.core.data; 


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Maps;
import com.igame.core.data.template.DrawLevelTemplate;
import com.igame.core.data.template.DrawdataTemplate;





/**
 * 
 * @author Marcus.Z
 *
 */
@XmlRootElement(name = "draw_level")
@XmlAccessorType(XmlAccessType.NONE)
public class DrawLevelData
{
	@XmlElement(name="low")
	private List<DrawLevelTemplate> its;
	
	private Map<Integer,DrawLevelTemplate> maps	= Maps.newHashMap();
	

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(DrawLevelTemplate it: its)
		{
			maps.put(it.getDrawLevel(), it);
			
		}
		its.sort((h1, h2) -> h1.getDrawLevel() - h2.getDrawLevel());
	}
	
	public DrawLevelTemplate getTemplate(int id)
	{
		return maps.get(id);
	}
	
	public List<DrawLevelTemplate> getAll(){
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

 
