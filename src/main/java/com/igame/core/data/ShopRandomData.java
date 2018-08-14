package com.igame.core.data;

import com.google.common.collect.Maps;
import com.igame.core.data.template.ShopRandomTemplate;

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
 * shoptype = 1 时商品数据
 */
@XmlRootElement(name = "shoprandom")
@XmlAccessorType(XmlAccessType.NONE)
public class ShopRandomData {

    @XmlElement(name="low")
    private List<ShopRandomTemplate> its;

    private Map<Integer,ShopRandomTemplate> maps	= Maps.newHashMap();


    void afterUnmarshal(Unmarshaller u, Object parent)
    {
        for(ShopRandomTemplate it: its)
        {
            maps.put(it.getLv(), it);

        }
    }

    public ShopRandomTemplate getTemplate(int shopLv)
    {
        return maps.get(shopLv);
    }

    public List<ShopRandomTemplate> getAll(){
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
