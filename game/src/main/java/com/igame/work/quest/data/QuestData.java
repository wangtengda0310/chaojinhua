package com.igame.work.quest.data;



import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Maps;


/**
 * 
 * @author Marcus.Z
 *
 */
@XmlRootElement(name = "questdata")
@XmlAccessorType(XmlAccessType.NONE)
public class QuestData
{
	@XmlElement(name="low")
	private List<QuestTemplate> its;
	
	private Map<Integer,QuestTemplate> maps	= Maps.newHashMap();
	

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(QuestTemplate it: its)
		{
			maps.put(it.getQuestId(), it);
			
		}
	}
	
	public QuestTemplate getTemplate(int id)
	{
		return maps.get(id);
	}
	
	public List<QuestTemplate> getAll(){
		
		return its;
	}
	


	/**
	 * @return 
	 */
	public int size()
	{
		return maps.size();
	}
	
	
	public List<QuestTemplate> getByType(int type,int claim){
		
		List<QuestTemplate> ls = its.stream().filter(e-> e.getQuestType() == type && e.getClaim() == claim).collect(Collectors.toList());
		ls.sort((h1, h2) -> h1.getFinish() - h2.getFinish());
		return ls;
	}
	
}

 
