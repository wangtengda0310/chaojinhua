package com.igame.core.data; 


import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Maps;
import com.igame.core.data.template.StrengthenlevelTemplate;





/**
 * 
 * @author Marcus.Z
 *
 */
@XmlRootElement(name = "strengthenlevel")
@XmlAccessorType(XmlAccessType.NONE)
public class TongHuaData
{
	@XmlElement(name="low")
	private List<StrengthenlevelTemplate> its;
	
	private Map<Integer,StrengthenlevelTemplate> maps	= Maps.newHashMap();
	

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(StrengthenlevelTemplate it: its)
		{
			maps.put(it.getStrengthen_lv(), it);
			
		}
		its.sort((h1, h2) -> h1.getStrengthen_lv() - h2.getStrengthen_lv());
	}
	
	public StrengthenlevelTemplate getTemplate(int id)
	{
		return maps.get(id);
	}
	
	public List<StrengthenlevelTemplate> getAll(){
		
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

 
