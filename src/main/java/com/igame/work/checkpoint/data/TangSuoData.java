package com.igame.work.checkpoint.data;


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
@XmlRootElement(name = "questteam")
@XmlAccessorType(XmlAccessType.NONE)
public class TangSuoData
{
	@XmlElement(name="low")
	private List<TangSuoTemplate> its;
	
	private Map<Integer,TangSuoTemplate> maps	= Maps.newHashMap();
	

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(TangSuoTemplate it: its)
		{
			maps.put(it.getNum(), it);
			
		}
	}
	
	public TangSuoTemplate getTemplate(int id)
	{
		return maps.get(id);
	}
	
	public List<TangSuoTemplate> getAll(){
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

 
