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
 * @author xym
 *
 * 道具合成
 */
@XmlRootElement(name = "info")
@XmlAccessorType(XmlAccessType.NONE)
public class ItemGroupData {
    @XmlElement(name="item")
    private List<ItemGroupTemplate> its;

    private Map<Integer,ItemGroupTemplate> maps	= Maps.newHashMap();


    void afterUnmarshal(Unmarshaller u, Object parent)
    {
        for(ItemGroupTemplate it: its)
        {
            maps.put(it.getItemId(), it);

        }
    }

    public ItemGroupTemplate getTemplate(int id)
    {
        return maps.get(id);
    }

    public List<ItemGroupTemplate> getAll(){
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
