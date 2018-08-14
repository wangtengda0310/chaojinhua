package com.igame.core.data;

import com.google.common.collect.Maps;
import com.igame.core.data.template.RunRewardTemplate;
import com.igame.core.data.template.RunTemplate;

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
@XmlRootElement(name = "runrewarddata")
@XmlAccessorType(XmlAccessType.NONE)
public class RunRewardData {

    @XmlElement(name="low")
    private List<RunRewardTemplate> its;

    private Map<Integer,RunRewardTemplate> maps	= Maps.newHashMap();


    void afterUnmarshal(Unmarshaller u, Object parent)
    {
        for(RunRewardTemplate it: its)
        {
            maps.put(it.getNum(), it);

        }
    }

    public RunRewardTemplate getTemplate(int num)
    {
        return maps.get(num);
    }

    public List<RunRewardTemplate> getAll(){
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
