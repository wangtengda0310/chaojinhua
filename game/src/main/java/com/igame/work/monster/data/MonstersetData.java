package com.igame.work.monster.data;

import com.google.common.collect.Maps;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Map;

@XmlRootElement(name = "info")
@XmlAccessorType(XmlAccessType.NONE)
public class MonstersetData
{
    @XmlElement(name="item")
    private List<MonstersetTemplate> its;

    private Map<Integer,MonstersetTemplate> monsters	= Maps.newHashMap();


    void afterUnmarshal(Unmarshaller u, Object parent)
    {
        for(MonstersetTemplate it: its)
        {
            monsters.put(it.getChapterId(), it);

        }
    }

    public MonstersetTemplate getMonsterTemplate(int id)
    {
        return monsters.get(id);
    }


    public List<MonstersetTemplate> getAll(){
        return its;
    }

    public int size()
    {
        return monsters.size();
    }
}


