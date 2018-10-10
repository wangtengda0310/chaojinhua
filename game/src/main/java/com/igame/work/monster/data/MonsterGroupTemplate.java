package com.igame.work.monster.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author xym
 *
 * 召唤怪兽模板
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "item")
public class MonsterGroupTemplate {

    @XmlAttribute(name = "item_id", required = true)
    private int itemId;//道具ID

    @XmlAttribute(name = "num")
    private int num;//数量

    @XmlAttribute(name = "monster_id")
    private int monsterId;//怪兽ID

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getMonsterId() {
        return monsterId;
    }

    public void setMonsterId(int monsterId) {
        this.monsterId = monsterId;
    }
}
