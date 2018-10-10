package com.igame.work.monster.data;


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
public class PVEMonsterData
{
	@XmlElement(name="item")
	private List<PVEMonsterTemplate> its;
	
	private Map<Integer,PVEMonsterTemplate> monsters	= Maps.newHashMap();
	

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(PVEMonsterTemplate it: its)
		{
			monsters.put(it.getMonsterId(), it);
			
		}
	}
	
	public PVEMonsterTemplate getMonsterTemplate(int id)
	{
		return monsters.get(id);
	}

	
	public List<PVEMonsterTemplate> getAll(){
		return its;
	}

	public int size()
	{
		return monsters.size();
	}
}

 
