package com.igame.work.checkpoint.xingheZhiYan;



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
@XmlRootElement(name = "trialdata")
@XmlAccessorType(XmlAccessType.NONE)
public class TrialData
{
	@XmlElement(name="low")
	private List<TrialdataTemplate> its;
	
	private Map<Integer,TrialdataTemplate> maps	= Maps.newHashMap();
	

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(TrialdataTemplate it: its)
		{
			maps.put(it.getNum(), it);
			
		}
	}
	
	public TrialdataTemplate getTemplate(int id)
	{
		return maps.get(id);
	}
	
	public List<TrialdataTemplate> getAll(){
		
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

 
