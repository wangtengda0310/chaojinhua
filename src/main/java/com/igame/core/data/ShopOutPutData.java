package com.igame.core.data;

import com.google.common.collect.Maps;
import com.igame.core.data.template.ShopOutPutTemplate;

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
 * shoptype = 2 时商品数据
 */
@XmlRootElement(name = "shopoutput")
@XmlAccessorType(XmlAccessType.NONE)
public class ShopOutPutData {

    @XmlElement(name="low")
    private List<ShopOutPutTemplate> its;

    private Map<Integer,ShopOutPutTemplate> maps	= Maps.newHashMap();


    void afterUnmarshal(Unmarshaller u, Object parent)
    {
        for(ShopOutPutTemplate it: its)
        {
            maps.put(it.getShopId(), it);

        }
    }

    public ShopOutPutTemplate getTemplate(int id)
    {
        return maps.get(id);
    }

    public List<ShopOutPutTemplate> getAll(){
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
