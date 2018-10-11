package com.igame.work.monster.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Marcus.Z
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "item")
public class MonstersetTemplate {



    @XmlAttribute(name = "chapter_id", required = true)
    private int chapterId;

    @XmlAttribute(name = "monster_id")
    private String monsterId;

    @XmlAttribute(name = "god")
    private String god;

    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public String getMonsterId() {
        return monsterId;
    }

    public void setMonsterId(String monsterId) {
        this.monsterId = monsterId;
    }

    public String getGod() {
        return god;
    }

    public void setGod(String god) {
        this.god = god;
    }
}
