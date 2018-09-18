package com.igame.work.shop.data;

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
 * 神秘商店等级数据
 */
@XmlRootElement(name = "shoprandom")
@XmlAccessorType(XmlAccessType.NONE)
public class ShopRandomLvData {

    @XmlElement(name="low")
    private List<ShopRandomLvTemplate> its;

    private Map<Integer,ShopRandomLvTemplate> maps	= Maps.newHashMap();


    void afterUnmarshal(Unmarshaller u, Object parent)
    {
        for(ShopRandomLvTemplate it: its)
        {
            maps.put(it.getLv(), it);

        }
    }

    public ShopRandomLvTemplate getTemplate(int shopLv)
    {
        return maps.get(shopLv);
    }

    public List<ShopRandomLvTemplate> getAll(){
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
