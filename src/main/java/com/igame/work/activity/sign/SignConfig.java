package com.igame.work.activity.sign;


import com.google.common.collect.Maps;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Map;

@XmlRootElement(name = "sign_reward")
@XmlAccessorType(XmlAccessType.NONE)
public class SignConfig
{
	@XmlElement(name="low")
	private List<SignConfigTemplate> its;
	
	private Map<Integer,SignConfigTemplate> maps	= Maps.newHashMap();
	

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(SignConfigTemplate it: its)
		{
			maps.put(it.getSignNum(), it);
			
		}
	}
	
	public SignConfigTemplate getTemplate(int id)
	{
		return maps.get(id);
	}

	public int size()
	{
		return maps.size();
	}

}

 
