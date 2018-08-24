package com.igame.work.activity;


import com.google.common.collect.Maps;
import com.igame.work.activity.meiriLiangfa.MeiriLiangfaData;
import com.igame.work.activity.tansuoZhiLu.TanSuoZhiLuActivityData;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@XmlRootElement(name = "activity_data")
@XmlAccessorType(XmlAccessType.NONE)
public class ActivityConfig
{
	@XmlElement(name="low")
	private List<ActivityConfigTemplate> its;
	
	private Map<Integer,ActivityConfigTemplate> maps	= Maps.newHashMap();
	

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		its.forEach(((Consumer<ActivityConfigTemplate>)c->{})
				.andThen(MeiriLiangfaData::addActivityConfigTemplate)
				.andThen(ActivityConfig::addActivityConfigTemplate));
	}

	private static void addActivityConfigTemplate(ActivityConfigTemplate config) {
		((Consumer<ActivityConfigTemplate>) o -> {})
				.andThen(MeiriLiangfaData::addActivityConfigTemplate)
				.andThen(TanSuoZhiLuActivityData::addActivityConfigTemplate);
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

 
