package com.igame.work.turntable.data;

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
 * 会员礼包数据
 */
@XmlRootElement(name = "lucktable")
@XmlAccessorType(XmlAccessType.NONE)
public class LuckTableData {

    @XmlElement(name="low")
    private List<LuckTableTemplate> its;

    private Map<String,List<LuckTableTemplate>> maps = Maps.newHashMap();


    void afterUnmarshal(Unmarshaller u, Object parent)
    {
        for(LuckTableTemplate it: its)
        {
            String playerLv = it.getPlayerLv();
            List<LuckTableTemplate> luckTableTemplates = maps.get(playerLv);
            if (luckTableTemplates == null){
                luckTableTemplates = new ArrayList<>();
                luckTableTemplates.add(it);
                maps.put(playerLv,luckTableTemplates);
            }else {
                luckTableTemplates.add(it);
            }

        }
    }

    public List<LuckTableTemplate> getTemplate(int playerLv)
    {
        String playerLvStr = getPlayerLv(playerLv);
        return maps.get(playerLvStr);
    }

    public List<LuckTableTemplate> getAll(){
        return its;
    }

    public String getPlayerLv(int playerLv){

        if (playerLv < 15)
            return "";
        else if (15 <= playerLv && playerLv < 25)
            return "15,25";
        else if (26 <= playerLv && playerLv < 35)
            return "26,35";
        else if (36 <= playerLv && playerLv < 45)
            return "36,45";
        else if (46 <= playerLv && playerLv < 55)
            return "46,55";
        else if (56 <= playerLv && playerLv < 65)
            return "56,65";
        else if (66 <= playerLv && playerLv < 75)
            return "66,75";
        else if (76 <= playerLv && playerLv < 85)
            return "76,85";
        else
            return "";
    }

    /**
     * @return
     */
    public int size()
    {
        return maps.size();
    }
}
