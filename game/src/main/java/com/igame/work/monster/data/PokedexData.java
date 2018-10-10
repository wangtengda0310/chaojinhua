package com.igame.work.monster.data;


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
public class PokedexData
{
	@XmlElement(name="item")
	private List<PokedexdataTemplate> its;
	
	private Map<Integer,PokedexdataTemplate> maps	= Maps.newHashMap();
	

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(PokedexdataTemplate it: its)
		{
			maps.put(it.getPokedexType(), it);
			
		}
	}
	
	public PokedexdataTemplate getTemplate(int id)
	{
		return maps.get(id);
	}
	
	public List<PokedexdataTemplate> getAll(){
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

 
