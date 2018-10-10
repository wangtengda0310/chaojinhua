package com.igame.work.user.data;

import com.google.common.collect.Maps;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author xym
 *
 * 头像
 */
@XmlRootElement(name = "info")
@XmlAccessorType(XmlAccessType.NONE)
public class HeadData {

    @XmlElement(name="item")
    private List<HeadTemplate> its;

    private Map<Integer,HeadTemplate> maps	= Maps.newHashMap();

    private Map<Integer,List<HeadTemplate>> touchMap = Maps.newHashMap();


    void afterUnmarshal(Unmarshaller u, Object parent)
    {
        for(HeadTemplate it: its)
        {
            maps.put(it.getHeadId(), it);

            List<HeadTemplate> templates = touchMap.get(it.getTouchLimit());
            if (templates == null){
                templates = new ArrayList<>();
                templates.add(it);
                touchMap.put(it.getTouchLimit(),templates);
            }else {
                templates.add(it);
                touchMap.put(it.getTouchLimit(),templates);
            }

        }
    }

    public HeadTemplate getTemplate(int id)
    {
        return maps.get(id);
    }

    public List<HeadTemplate> getAll(){
        return its;
    }





    /**
     * @return
     */
    public int size()
    {
        return maps.size();
    }

    public List<HeadTemplate> getTemplates(int touchLimit) {
        return touchMap.get(touchLimit);
    }
}
