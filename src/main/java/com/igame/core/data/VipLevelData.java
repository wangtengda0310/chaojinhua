package com.igame.core.data;

import com.google.common.collect.Maps;
import com.igame.core.data.template.VipLevelTemplate;
import com.igame.core.data.template.VipPackTemplate;

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
@XmlRootElement(name = "viplevel")
@XmlAccessorType(XmlAccessType.NONE)
public class VipLevelData {

    @XmlElement(name="low")
    private List<VipLevelTemplate> its;

    private Map<Integer,VipLevelTemplate> maps = Maps.newHashMap();


    void afterUnmarshal(Unmarshaller u, Object parent)
    {
        for(VipLevelTemplate it: its)
        {
            maps.put(it.getVipLevel(), it);

        }
    }

    public VipLevelTemplate getTemplate(int vipLv)
    {
        return maps.get(vipLv);
    }

    public List<VipLevelTemplate> getAll(){
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
