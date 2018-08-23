package com.igame.work.checkpoint.baozouShike.data;

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
 * 暴走时刻怪物生成数据
 */
@XmlRootElement(name = "runtypedata")
@XmlAccessorType(XmlAccessType.NONE)
public class RunTypeData {

    @XmlElement(name="low")
    private List<RunTypeTemplate> its;

    private Map<Integer,RunTypeTemplate> maps	= Maps.newHashMap();


    void afterUnmarshal(Unmarshaller u, Object parent)
    {
        for(RunTypeTemplate it: its)
        {
            maps.put(it.getRunType(), it);

        }
    }

    public RunTypeTemplate getTemplate(int runType)
    {
        return maps.get(runType);
    }

    public List<RunTypeTemplate> getAll(){
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
