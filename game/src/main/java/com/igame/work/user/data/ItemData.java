package com.igame.work.user.data;


import com.google.common.collect.Maps;
import com.igame.work.item.dto.Item;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;





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
		Comparator<Item> itemComparator;
		if(rtype == 1){
			itemComparator = (h1, h2) -> getTemplate(h2.getItemId()).getItenRarity() - getTemplate(h1.getItemId()).getItenRarity() == 0 ? getTemplate(h2.getItemId()).getSale() - getTemplate(h1.getItemId()).getSale() : getTemplate(h2.getItemId()).getItenRarity() - getTemplate(h1.getItemId()).getItenRarity();
		}else{
			itemComparator = (h1, h2) -> getTemplate(h1.getItemId()).getItenRarity() - getTemplate(h2.getItemId()).getItenRarity() == 0 ? getTemplate(h1.getItemId()).getSale() - getTemplate(h2.getItemId()).getSale() : getTemplate(h1.getItemId()).getItenRarity() - getTemplate(h2.getItemId()).getItenRarity();
		}

		return items.stream().filter(e->
				e.getUsableCount(-1) >= 3
						&& getTemplate(e.getItemId()) != null
						&& getTemplate(e.getItemId()).getItemType() == 1
						&& effects.contains(String.valueOf(getTemplate(e.getItemId()).getEffect())))
				.sorted(itemComparator)
				.collect(Collectors.toList());
	}
}

 
