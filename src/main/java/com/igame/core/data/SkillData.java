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
import com.igame.core.data.template.SkillTemplate;
import com.igame.core.data.template.StrengthenmonsterTemplate;
import com.igame.core.data.template.StrengthenrewardTemplate;
import com.igame.core.data.template.TangSuoTemplate;





/**
 * 
 * @author Marcus.Z
 *
 */
@XmlRootElement(name = "skilldata")
@XmlAccessorType(XmlAccessType.NONE)
public class SkillData
{
	@XmlElement(name="low")
	private List<SkillTemplate> its;
	
	private Map<Integer,SkillTemplate> maps	= Maps.newHashMap();
	

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(SkillTemplate it: its)
		{
			maps.put(it.getSkillId(), it);
			
		}
	}
	
	public SkillTemplate getTemplate(int id)
	{
		return maps.get(id);
	}
	
	public List<SkillTemplate> getAll(){
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

 
