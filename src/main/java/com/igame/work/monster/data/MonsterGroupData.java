package com.igame.work.monster.data;

import com.google.common.collect.Maps;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Map;

/**
 * @author xym
 *
 * 召唤怪物生成数据
 */
@XmlRootElement(name = "monstergroup")
@XmlAccessorType(XmlAccessType.NONE)
public class MonsterGroupData {

    @XmlElement(name="low")
    private List<MonsterGroupTemplate> its;

    private Map<Integer,MonsterGroupTemplate> maps = Maps.newHashMap();


    void afterUnmarshal(Unmarshaller u, Object parent)
    {
        for(MonsterGroupTemplate it: its)
        {
            maps.put(it.getItemId(), it);

        }
    }

    public MonsterGroupTemplate getTemplate(int itemId)
    {
        return maps.get(itemId);
    }

    public List<MonsterGroupTemplate> getAll(){
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
