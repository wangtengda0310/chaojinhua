package com.igame.work.activity;


import com.google.common.collect.Maps;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@XmlRootElement(name = "activity_data")
@XmlAccessorType(XmlAccessType.NONE)
public class ActivityConfig
{
	@XmlElement(name="low")
	public static
	List<ActivityConfigTemplate> its = new ArrayList<>();
	
	private Map<Integer,ActivityConfigTemplate> maps	= Maps.newHashMap();
	

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		its.forEach((c -> {
			maps.put(c.getActivity_sign(), c);
		}));
	}

	public ActivityConfigTemplate getTemplate(int id)
	{
		return maps.get(id);
	}

	public int size()
	{
		return maps.size();
	}

}

 
