package com.igame.work.vip;

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
 * 会员礼包数据
 */
@XmlRootElement(name = "info")
@XmlAccessorType(XmlAccessType.NONE)
public class VipData {

    @XmlElement(name="item")
    private List<VipDataTemplate> its;

    private Map<Integer,VipDataTemplate> maps = Maps.newHashMap();


    void afterUnmarshal(Unmarshaller u, Object parent)
    {
        for(VipDataTemplate it: its)
        {
            maps.put(it.getNum(), it);

        }
    }

    public VipDataTemplate getTemplate(int num)
    {
        return maps.get(num);
    }

    public List<VipDataTemplate> getAll(){
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
