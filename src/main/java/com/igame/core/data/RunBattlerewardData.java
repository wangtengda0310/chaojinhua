package com.igame.core.data;

import com.google.common.collect.Maps;
import com.igame.core.data.template.RunBattlerewardTemplate;
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
 * 暴走时刻结束奖励
 */
@XmlRootElement(name = "runbattle")
@XmlAccessorType(XmlAccessType.NONE)
public class RunBattlerewardData {

    @XmlElement(name="low")
    private List<RunBattlerewardTemplate> its;

    private Map<Integer,RunBattlerewardTemplate> maps	= Maps.newHashMap();


    void afterUnmarshal(Unmarshaller u, Object parent)
    {
        for(RunBattlerewardTemplate it: its)
        {
            maps.put(it.getKillNum(), it);

        }
    }

    public RunBattlerewardTemplate getTemplate(int killNum)
    {
        return maps.get(killNum);
    }

    public List<RunBattlerewardTemplate> getAll(){
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
