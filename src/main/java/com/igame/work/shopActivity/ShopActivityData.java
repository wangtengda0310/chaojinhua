package com.igame.work.shopActivity;


import com.google.common.collect.Maps;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 *
 * @author Marcus.Z
 *
 */
@XmlRootElement(name = "activity_data")
@XmlAccessorType(XmlAccessType.NONE)
public class ShopActivityData
{
	@XmlElement(name="low")
	private List<ShopActivityDataTemplate> its = new ArrayList<>();

	private Map<Integer, ShopActivityDataTemplate> maps	= Maps.newHashMap();


	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(ShopActivityDataTemplate it: its)
		{
			maps.put(it.getNum(), it);

		}
	}

	public ShopActivityDataTemplate getTemplate(int id)
	{
		return maps.get(id);
	}

	public List<ShopActivityDataTemplate> getAll(){

		return its;
	}

}


