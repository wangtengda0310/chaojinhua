package com.igame.core.data; 



import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Maps;
import com.igame.core.data.template.DestinyrateTemplate;
import com.igame.core.data.template.FatedataTemplate;





/**
 * 
 * @author Marcus.Z
 *
 */
@XmlRootElement(name = "destinyrate")
@XmlAccessorType(XmlAccessType.NONE)
public class DestinyData
{
	@XmlElement(name="low")
	private List<DestinyrateTemplate> its;
	
	private Map<Integer,DestinyrateTemplate> maps	= Maps.newHashMap();
	

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(DestinyrateTemplate it: its)
		{
			maps.put(it.getDestinyType(), it);
			
		}
	}
	
	public DestinyrateTemplate getTemplate(int id)
	{
		return maps.get(id);
	}
	
	public List<DestinyrateTemplate> getAll(){
		
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

 
