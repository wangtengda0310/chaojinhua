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
 * 会员礼包数据
 */
@XmlRootElement(name = "vippack")
@XmlAccessorType(XmlAccessType.NONE)
public class VipPackData {

    @XmlElement(name="low")
    private List<VipPackTemplate> its;

    private Map<Integer,VipPackTemplate> maps = Maps.newHashMap();


    void afterUnmarshal(Unmarshaller u, Object parent)
    {
        for(VipPackTemplate it: its)
        {
            maps.put(it.getVipLv(), it);

        }
    }

    public VipPackTemplate getTemplate(int vipLv)
    {
        return maps.get(vipLv);
    }

    public List<VipPackTemplate> getAll(){
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
