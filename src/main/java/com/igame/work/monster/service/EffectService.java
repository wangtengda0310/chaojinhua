package com.igame.work.monster.service;

import java.util.List;

import com.google.common.collect.Lists;
import com.igame.core.data.DataManager;
import com.igame.core.data.template.ItemTemplate;
import com.igame.work.item.dto.Item;
import com.igame.work.monster.dto.Effect;

/**
 * 
 * @author Marcus.Z
 *
 */
public class EffectService {
	
	
	/**
	 * 
	 * @param items
	 * @return
	 */
	public static List<Effect> getEffectByItem(List<Integer> items,boolean equip){
		
		List<Effect> ls = Lists.newArrayList();
		for(Integer item : items){
			ItemTemplate it = DataManager.ins().ItemData.getTemplate(item);
			if(it != null && it.getEffect() != 0){
				if(equip && it.getItemType() != 1){
					continue;
				}
				ls.add(new Effect(0,it.getEffect(),it.getValue()));
			}
		}
		
		return ls;
		
	}
	
	
	
	/**
	 * 
	 * @param items
	 * @return
	 */
	public static Effect getEffectByItem(int item,boolean equip){
		

		ItemTemplate it = DataManager.ins().ItemData.getTemplate(item);
		if(it != null && it.getEffect() != 0){
			if(equip && it.getItemType() != 1){
				return null;
			}
			return new Effect(0,it.getEffect(),it.getValue());
		}
		
		return null;
		
	}

}
