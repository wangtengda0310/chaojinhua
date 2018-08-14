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
import com.igame.core.data.template.PokedexdataTemplate;
import com.igame.core.data.template.PropGroupTemplate;
import com.igame.core.data.template.TangSuoTemplate;
import com.igame.core.data.template.TongDiaoTemplate;





/**
 * 
 * @author Marcus.Z
 *
 */
@XmlRootElement(name = "pokedex")
@XmlAccessorType(XmlAccessType.NONE)
public class PokedexData
{
	@XmlElement(name="low")
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

 
