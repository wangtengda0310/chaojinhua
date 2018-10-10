package com.igame.work.user.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author xym
 *
 * 头像模板
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "item")
public class HeadTemplate {

    @XmlAttribute(name = "head_id", required = true)
    private int headId;//头像ID

    @XmlAttribute(name = "touch_limit")
    private int touchLimit;//触发条件1玩家等级2获得人物3活动

    @XmlAttribute(name = "value")
    private int value;//触发条件 值

    @XmlAttribute(name = "head_frame_picture")
    private String headFramePicture;//头像图片

    public int getHeadId() {
        return headId;
    }

    public void setHeadId(int headId) {
        this.headId = headId;
    }

    public int getTouchLimit() {
        return touchLimit;
    }

    public void setTouchLimit(int touchLimit) {
        this.touchLimit = touchLimit;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getHeadFramePicture() {
        return headFramePicture;
    }

    public void setHeadFramePicture(String headFramePicture) {
        this.headFramePicture = headFramePicture;
    }
}
