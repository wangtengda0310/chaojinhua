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
import com.igame.core.data.template.DrawdataTemplate;





/**
 * 
 * @author Marcus.Z
 *
 */
@XmlRootElement(name = "drawdata")
@XmlAccessorType(XmlAccessType.NONE)
public class DrawdataData
{
	@XmlElement(name="low")
	private List<DrawdataTemplate> its;
	
	private Map<String,DrawdataTemplate> maps	= Maps.newHashMap();
	


	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(DrawdataTemplate it: its)
		{
			maps.put(it.getDrawType()+"_"+it.getDrawLv(), it);
			
		}
	}
	
	public DrawdataTemplate getTemplate(String id)
	{
		return maps.get(id);
	}
	
	public List<DrawdataTemplate> getAll(){
		return its;
	}
	

	
	public List<DrawdataTemplate> getPreList(int type,int level){
		
		List<DrawdataTemplate> ls = its.stream().filter(e->e.getDrawType() == type && e.getDrawLv() < level).collect(Collectors.toList());
		ls.sort((h1, h2) -> h1.getDrawLv() - h2.getDrawLv());
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

 
