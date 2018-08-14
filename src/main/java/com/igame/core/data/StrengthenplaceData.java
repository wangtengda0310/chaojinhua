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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.igame.core.data.template.DropDataTemplate;
import com.igame.core.data.template.NewMonsterTemplate;
import com.igame.core.data.template.PropGroupTemplate;
import com.igame.core.data.template.StrengthenmonsterTemplate;
import com.igame.core.data.template.StrengthenplaceTemplate;
import com.igame.core.data.template.TangSuoTemplate;





/**
 * 
 * @author Marcus.Z
 *
 */
@XmlRootElement(name = "strengthenplace")
@XmlAccessorType(XmlAccessType.NONE)
public class StrengthenplaceData
{
	@XmlElement(name="low")
	private List<StrengthenplaceTemplate> its;
	
	private Map<String,StrengthenplaceTemplate> maps	= Maps.newHashMap();
	
	private List<Integer> set	= Lists.newArrayList();
	

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(StrengthenplaceTemplate it: its)
		{
			maps.put(it.getNum()+"_"+it.getStrengthen_type(), it);
			if(!set.contains(it.getNum())){
				set.add(it.getNum());
			}
			
		}
	}
	
	public List<StrengthenplaceTemplate> getRandom(int id){
		return its.stream().filter(e -> e.getNum() == id && e.getStrengthen_type() != 0).collect(Collectors.toList());
	}
	
	public StrengthenplaceTemplate getTemplate(String id)
	{
		return maps.get(id);
	}
	
	public List<StrengthenplaceTemplate> getAll(){
		return its;
	}
	
	
	
	
	


	public List<Integer> getSet() {
		return set;
	}

	public void setSet(List<Integer> set) {
		this.set = set;
	}

	/**
	 * @return 
	 */
	public int size()
	{
		return maps.size();
	}
}

 
