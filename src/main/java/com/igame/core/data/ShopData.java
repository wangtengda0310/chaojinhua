package com.igame.core.data;

import com.google.common.collect.Maps;
import com.igame.core.data.template.ShopTemplate;

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
 * 商店
 */
@XmlRootElement(name = "shopdata")
@XmlAccessorType(XmlAccessType.NONE)
public class ShopData {

    @XmlElement(name="low")
    private List<ShopTemplate> its;

    private Map<Integer,ShopTemplate> maps	= Maps.newHashMap();


    void afterUnmarshal(Unmarshaller u, Object parent)
    {
        for(ShopTemplate it: its)
        {
            maps.put(it.getShopId(), it);

        }
    }

    public ShopTemplate getTemplate(int id)
    {
        return maps.get(id);
    }

    public List<ShopTemplate> getAll(){
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
