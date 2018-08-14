package com.igame.core.data; 


import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.igame.core.data.template.GodsdataTemplate;





/**
 * 
 * @author Marcus.Z
 *
 */
@XmlRootElement(name = "godsdata")
@XmlAccessorType(XmlAccessType.NONE)
public class GodsData
{
	@XmlElement(name="low")
	private List<GodsdataTemplate> its;
	
	private Map<String,GodsdataTemplate> maps	= Maps.newHashMap();
	
	private Set<Integer> sets = Sets.newHashSet();
	

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(GodsdataTemplate it: its)
		{
			maps.put(it.getGodsType()+"_"+it.getGodsLevel(), it);
			sets.add(it.getGodsType());
			
		}
	}
	
	public GodsdataTemplate getTemplate(String id)
	{
		return maps.get(id);
	}
	
	public List<GodsdataTemplate> getAll(){
		return its;
	}
	
	public List<GodsdataTemplate> getByType(int type){
		
		List<GodsdataTemplate> ls = its.stream().filter(e->e.getGodsType() == type).collect(Collectors.toList());
		ls.sort((h1, h2) -> h1.getGodsLevel() - h2.getGodsLevel());
		return ls;
	}
	
	public List<GodsdataTemplate> getPreList(int type,int level){
		
		List<GodsdataTemplate> ls = its.stream().filter(e->e.getGodsType() == type && e.getGodsLevel() < level).collect(Collectors.toList());
		ls.sort((h1, h2) -> h1.getGodsLevel() - h2.getGodsLevel());
		return ls;
	}
	
	public GodsdataTemplate getNextLevelTemplate(int type,int level)
	{	
		int next = level + 1;
		return maps.get(type+"_"+next);
	}


	/**
	 * @return 
	 */
	public int size()
	{
		return maps.size();
	}

	public Set<Integer> getSets() {
		return sets;
	}

	public void setSets(Set<Integer> sets) {
		this.sets = sets;
	}
	
	
	
}

 
