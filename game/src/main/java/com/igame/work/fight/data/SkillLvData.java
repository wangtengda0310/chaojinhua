package com.igame.work.fight.data;


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
@XmlRootElement(name = "skillexp")
@XmlAccessorType(XmlAccessType.NONE)
public class SkillLvData
{	
	private int maxLevel = 6;
	
	@XmlElement(name="low")
	private List<SkillLvTemplate> its;
	
	private Map<Integer,SkillLvTemplate> maps	= Maps.newHashMap();
	

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(SkillLvTemplate it: its)
		{
			maps.put(it.getSkillId(), it);
			
		}
	}
	
	public SkillLvTemplate getTemplate(int skillId)
	{
		return maps.get(skillId);
	}
	
	public List<SkillLvTemplate> getAll(){
		return its;
	}


	public int size()
	{
		return maps.size();
	}

	public int getMaxLevel() {
		return maxLevel;
	}
 
	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}
	
	
}

 
