package com.igame.work.monster.data;


import com.google.common.collect.Maps;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 
 * @author Marcus.Z
 *
 */
@XmlRootElement(name = "info")
@XmlAccessorType(XmlAccessType.NONE)
public class ExchangeData
{
	@XmlElement(name="item")
	private List<ExchangedataTemplate> its;
	
	private Map<String,ExchangedataTemplate> maps	= Maps.newHashMap();
	

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(ExchangedataTemplate it: its)
		{
			maps.put(it.getExchange_type()+"_"+it.getTimes(), it);
			
		}
	}
	
	
	public ExchangedataTemplate getTemplate(String id)
	{
		return maps.get(id);
	}
	
	public List<ExchangedataTemplate> getAll(){
		return its;
	}
	
	public List<ExchangedataTemplate> getByType(int type){
		
		List<ExchangedataTemplate> ls = its.stream().filter(e->e.getExchange_type() == type).collect(Collectors.toList());
		ls.sort((h1, h2) -> h1.getTimes() - h2.getTimes());
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

 
