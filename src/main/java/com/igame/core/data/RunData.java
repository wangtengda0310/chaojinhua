package com.igame.core.data;

import com.google.common.collect.Maps;
import com.igame.core.data.template.RunTemplate;
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
 * 暴走时刻怪物生成数据
 */
@XmlRootElement(name = "rundata")
@XmlAccessorType(XmlAccessType.NONE)
public class RunData {

    @XmlElement(name="low")
    private List<RunTemplate> its;

    private Map<Integer,RunTemplate> maps	= Maps.newHashMap();


    void afterUnmarshal(Unmarshaller u, Object parent)
    {
        for(RunTemplate it: its)
        {
            maps.put(it.getKillNum(), it);

        }
    }

    public RunTemplate getTemplate(int killNum)
    {
        return maps.get(killNum);
    }

    public List<RunTemplate> getAll(){
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
