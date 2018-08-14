package com.igame.core.data; 


import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Maps;
import com.igame.core.data.template.DropDataTemplate;
import com.igame.core.data.template.NewMonsterTemplate;
import com.igame.core.data.template.PropGroupTemplate;





/**
 * 
 * @author Marcus.Z
 *
 */
@XmlRootElement(name = "propgroup")
@XmlAccessorType(XmlAccessType.NONE)
public class PropGroupData
{
	@XmlElement(name="low")
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

 
