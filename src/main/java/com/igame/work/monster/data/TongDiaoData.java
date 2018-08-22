package com.igame.work.monster.data;


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
@XmlRootElement(name = "suitdata")
@XmlAccessorType(XmlAccessType.NONE)
public class TongDiaoData
{
	@XmlElement(name="low")
	private List<TongDiaoTemplate> its;
	
	private Map<Integer,TongDiaoTemplate> maps	= Maps.newHashMap();
	

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(TongDiaoTemplate it: its)
		{
			maps.put(it.getSuitLv(), it);
			
		}
		its.sort((h1, h2) -> h1.getSuitLv() - h2.getSuitLv());
	}
	
	public TongDiaoTemplate getTemplate(int id)
	{
		return maps.get(id);
	}
	
	public List<TongDiaoTemplate> getAll(){
		
		return its;
	}
	
	
	public TongDiaoTemplate getByItemId(String itemId){
		for(TongDiaoTemplate tt : getAll()){
			if(tt.getSuitItem().indexOf(itemId) != -1){
				return tt;
			}
		}
		return null;
	}


	/**
	 * @return 
	 */
	public int size()
	{
		return maps.size();
	}
}

 
