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
public class DropData
{
	@XmlElement(name="item")
	private List<DropDataTemplate> its;
	
	private Map<Integer,DropDataTemplate> maps	= Maps.newHashMap();
	

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(DropDataTemplate it: its)
		{
			maps.put(it.getDropId(), it);
			
		}
	}
	
	public DropDataTemplate getTemplate(int id)
	{
		return maps.get(id);
	}
	
	public List<DropDataTemplate> getAll(){
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

 
