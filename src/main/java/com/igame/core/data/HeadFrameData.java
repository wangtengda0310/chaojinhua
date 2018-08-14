package com.igame.core.data;

import com.google.common.collect.Maps;
import com.igame.core.data.template.HeadFrameTemplate;
import com.igame.core.data.template.HeadTemplate;
import com.igame.core.data.template.ShopTemplate;

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
 * 头像框
 */
@XmlRootElement(name = "headframe")
@XmlAccessorType(XmlAccessType.NONE)
public class HeadFrameData {

    @XmlElement(name="low")
    private List<HeadFrameTemplate> its;

    private Map<Integer,HeadFrameTemplate> maps	= Maps.newHashMap();

    private Map<Integer,List<HeadFrameTemplate>> touchMap = Maps.newHashMap();


    void afterUnmarshal(Unmarshaller u, Object parent)
    {
        for(HeadFrameTemplate it: its)
        {
            maps.put(it.getHeadFrameId(), it);

            List<HeadFrameTemplate> templates = touchMap.get(it.getTouchLimit());
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

    public HeadFrameTemplate getTemplate(int id)
    {
        return maps.get(id);
    }

    public List<HeadFrameTemplate> getAll(){
        return its;
    }





    /**
     * @return
     */
    public int size()
    {
        return maps.size();
    }

    public List<HeadFrameTemplate> getTemplates(int touchLimit) {
        return touchMap.get(touchLimit);
    }
}
