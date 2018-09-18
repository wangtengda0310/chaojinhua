package com.igame.work.checkpoint.wujinZhiSen;



import com.google.common.collect.Maps;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Map;


/**
 * 
 * @author Marcus.Z
 *
 */
@XmlRootElement(name = "endless")
@XmlAccessorType(XmlAccessType.NONE)
public class EndlessData
{
	@XmlElement(name="low")
	private List<EndlessdataTemplate> its;
	
	private Map<Integer,EndlessdataTemplate> maps	= Maps.newHashMap();
	

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(EndlessdataTemplate it: its)
		{
			maps.put(it.getNum(), it);
			
		}
	}
	
	public EndlessdataTemplate getTemplate(int id)
	{
		return maps.get(id);
	}
	
	public List<EndlessdataTemplate> getAll(){
		
		return its;
	}
	


	/**
	 * @return 
	 */
	public int size()
	{
		return maps.size();
	}
	

	
}

 
