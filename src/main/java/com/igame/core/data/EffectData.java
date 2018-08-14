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
import com.igame.core.data.template.EffectTemplate;
import com.igame.core.data.template.NewMonsterTemplate;
import com.igame.core.data.template.PropGroupTemplate;
import com.igame.core.data.template.SkillTemplate;
import com.igame.core.data.template.StrengthenmonsterTemplate;
import com.igame.core.data.template.StrengthenrewardTemplate;
import com.igame.core.data.template.TangSuoTemplate;





/**
 * 
 * @author Marcus.Z
 *
 */
@XmlRootElement(name = "effectdata")
@XmlAccessorType(XmlAccessType.NONE)
public class EffectData
{
	@XmlElement(name="low")
	private List<EffectTemplate> its;
	
	private Map<Integer,EffectTemplate> maps	= Maps.newHashMap();
	

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(EffectTemplate it: its)
		{
			maps.put(it.getEffect(), it);
			
		}
	}
	
	public EffectTemplate getTemplate(int id)
	{
		return maps.get(id);
	}
	
	public List<EffectTemplate> getAll(){
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

 
