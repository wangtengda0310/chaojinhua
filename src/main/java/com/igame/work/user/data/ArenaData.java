package com.igame.work.user.data;



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
@XmlRootElement(name = "arenadata")
@XmlAccessorType(XmlAccessType.NONE)
public class ArenaData
{
	@XmlElement(name="low")
	private List<ArenadataTemplate> its;
	
	private Map<Integer,ArenadataTemplate> maps	= Maps.newHashMap();
	

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(ArenadataTemplate it: its)
		{
			maps.put(it.getArenaType(), it);
			
		}
	}
	
	public ArenadataTemplate getTemplate(int id)
	{
		return maps.get(id);
	}
	
	public List<ArenadataTemplate> getAll(){
		
		return its;
	}
	
	public ArenadataTemplate getTemplateByPlayerLevel(int level)
	{
		for(ArenadataTemplate it : its){
			String[] lv = it.getPlayerLv().split(",");
			if(level >= Integer.parseInt(lv[0]) && (level <= Integer.parseInt(lv[1]) || Integer.parseInt(lv[1]) == -1)){
				return it;
			}
		}
		return null;
	}
	


	/**
	 * @return 
	 */
	public int size()
	{
		return maps.size();
	}
	

	
}

 
