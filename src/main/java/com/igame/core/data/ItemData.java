package com.igame.core.data; 


import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Maps;
import com.igame.core.data.template.ItemTemplate;
import com.igame.work.item.dto.Item;





/**
 * 
 * @author Marcus.Z
 *
 */
@XmlRootElement(name = "itemdata")
@XmlAccessorType(XmlAccessType.NONE)
public class ItemData
{
	@XmlElement(name="low")
	private List<ItemTemplate> its;
	
	private Map<Integer,ItemTemplate> maps	= Maps.newHashMap();
	

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(ItemTemplate it: its)
		{
			maps.put(it.getItemId(), it);
			
		}
	}
	
	public ItemTemplate getTemplate(int id)
	{
		return maps.get(id);
	}
	
	public List<ItemTemplate> getAll(){
		return its;
	}

	/**
	 * @return 
	 */
	public int size()
	{
		return maps.size();
	}
	
	/**
	 * 
	 * @param itemType
	 * @param rtype 1-品质从高到底
	 * @return
	 */
	public List<ItemTemplate> getByEquip(int itemType,int rtype){
		
		List<ItemTemplate> ls = its.stream().filter(e->e.getItemType() == itemType).collect(Collectors.toList());
		if(rtype == 1){
			ls.sort((h1, h2) -> h2.getItenRarity() - h1.getItenRarity());
		}else{
			ls.sort((h1, h2) -> h1.getItenRarity() - h2.getItenRarity());
		}
		return ls;
	}
	
	/**
	 * 
	 * @param rtype 1-品质从高到底
	 * @return
	 */
	public List<Item> getByEquip(Collection<Item> items, int rtype,String effects){
		
		List<Item> ls = items.stream().filter(e->
				e.getUsableCount(-1) >= 3
						&& getTemplate(e.getItemId()) != null
						&& getTemplate(e.getItemId()).getItemType() == 1
						&& effects.contains(String.valueOf(getTemplate(e.getItemId()).getEffect())))
				.collect(Collectors.toList());

		if(rtype == 1){
			ls.sort((h1, h2) -> getTemplate(h2.getItemId()).getItenRarity() - getTemplate(h1.getItemId()).getItenRarity() == 0 ? getTemplate(h1.getItemId()).getEffect() - getTemplate(h2.getItemId()).getEffect() : getTemplate(h2.getItemId()).getItenRarity() - getTemplate(h1.getItemId()).getItenRarity());
		}else{
			ls.sort((h1, h2) -> getTemplate(h1.getItemId()).getItenRarity() - getTemplate(h2.getItemId()).getItenRarity() == 0 ? getTemplate(h1.getItemId()).getEffect() - getTemplate(h2.getItemId()).getEffect() : getTemplate(h1.getItemId()).getItenRarity() - getTemplate(h2.getItemId()).getItenRarity());
		}
		return ls;
	}
}

 
