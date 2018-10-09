package com.igame.work.checkpoint.baozouShike.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author xym
 *
 * 暴走时刻怪物生成模板
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "item")
public class RunTemplate {


    @XmlAttribute(name = "kill_num", required = true)
    private int killNum;//击杀数

    @XmlAttribute(name = "refresh")
    private String refresh;//刷出数量

    @XmlAttribute(name = "monster")
    private String monster;//怪物库

    public int getKillNum() {
        return killNum;
    }

    public void setKillNum(int killNum) {
        this.killNum = killNum;
    }

    public String getRefresh() {
        return refresh;
    }

    public void setRefresh(String refresh) {
        this.refresh = refresh;
    }

    public String getMonster() {
        return monster;
    }

    public void setMonster(String monster) {
        this.monster = monster;
    }
}
