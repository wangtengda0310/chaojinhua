package com.igame.core.data; 


import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Maps;
import com.igame.core.data.template.DrawrewardTemplate;





/**
 * 
 * @author Marcus.Z
 *
 */
@XmlRootElement(name = "drawreward")
@XmlAccessorType(XmlAccessType.NONE)
public class DrawrewardData
{
	@XmlElement(name="low")
	private List<DrawrewardTemplate> its;
	
	private Map<Integer,DrawrewardTemplate> maps	= Maps.newHashMap();
	

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(DrawrewardTemplate it: its)
		{
			maps.put(it.getRewardId(), it);
			
		}
	}
	
	public DrawrewardTemplate getTemplate(int id)
	{
		return maps.get(id);
	}
	
	public List<DrawrewardTemplate> getAll(){
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

 
