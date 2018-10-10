package com.igame.work.item.data;


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
public class PropGroupData
{
	@XmlElement(name="item")
	private List<PropGroupTemplate> its;
	
	private Map<Integer,PropGroupTemplate> maps	= Maps.newHashMap();
	

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(PropGroupTemplate it: its)
		{
			maps.put(it.getItemId(), it);
			
		}
	}
	
	public PropGroupTemplate getTemplate(int id)
	{
		return maps.get(id);
	}
	
	public List<PropGroupTemplate> getAll(){
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

 
