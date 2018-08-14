package com.igame.core.data.template;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author xym
 *
 * 道具合成模板
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "low")
public class ItemGroupTemplate {

    @XmlAttribute(name = "item_id", required = true)
    private int itemId;//头像ID

    @XmlAttribute(name = "num")
    private int num;//触发条件1玩家等级2获得人物3活动

    @XmlAttribute(name = "group_id")
    private int groupId;//触发条件 值

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

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
}
