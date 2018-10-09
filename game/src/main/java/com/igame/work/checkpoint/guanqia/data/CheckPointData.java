package com.igame.work.checkpoint.guanqia.data;


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
public class CheckPointData
{
	@XmlElement(name="item")
	private List<CheckPointTemplate> its;
	
	private Map<Integer,CheckPointTemplate> maps	= Maps.newHashMap();
	

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(CheckPointTemplate it: its)
		{
			maps.put(it.getChapterId(), it);
			
		}
	}
	
	public CheckPointTemplate getTemplate(int id)
	{
		return maps.get(id);
	}
	
	public List<CheckPointTemplate> getAll(){
		return its;
	}

	public int size()
	{
		return maps.size();
	}
}

 
