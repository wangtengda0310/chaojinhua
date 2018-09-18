package com.igame.work.user.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author xym
 *
 * 头像框模板
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "low")
public class HeadFrameTemplate {

    @XmlAttribute(name = "head_frame_id", required = true)
    private int headFrameId;//头像框ID

    @XmlAttribute(name = "touch_limit")
    private int touchLimit;//触发条件1玩家等级2获得人物3活动

    @XmlAttribute(name = "value")
    private int value;//触发条件 值

    @XmlAttribute(name = "head_frame_picture")
    private String headFramePicture;//头像框图片

    public int getHeadFrameId() {
        return headFrameId;
    }

    public void setHeadFrameId(int headFrameId) {
        this.headFrameId = headFrameId;
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
