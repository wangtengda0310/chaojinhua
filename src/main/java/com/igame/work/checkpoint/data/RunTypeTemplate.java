package com.igame.work.checkpoint.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author xym
 *
 * 暴走时刻buff模板
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "low")
public class RunTypeTemplate {


    @XmlAttribute(name = "run_type", required = true)
    private int runType;//buff类型

    @XmlAttribute(name = "type_name")
    private String typeName;//buff名称

    @XmlAttribute(name = "type_text")
    private String typeText;//buff描述

    @XmlAttribute(name = "effect_id")
    private String effectId;//效果ID  ','号分割

    @XmlAttribute(name = "effect_value")
    private String effectValue;//效果 值  ','号分割

    @XmlAttribute(name = "monstertype")
    private int monsterType;//怪物类型

    public int getRunType() {
        return runType;
    }

    public void setRunType(int runType) {
        this.runType = runType;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeText() {
        return typeText;
    }

    public void setTypeText(String typeText) {
        this.typeText = typeText;
    }

    public String getEffectId() {
        return effectId;
    }

    public void setEffectId(String effectId) {
        this.effectId = effectId;
    }

    public String getEffectValue() {
        return effectValue;
    }

    public void setEffectValue(String effectValue) {
        this.effectValue = effectValue;
    }

    public int getMonsterType() {
        return monsterType;
    }

    public void setMonsterType(int monsterType) {
        this.monsterType = monsterType;
    }
}
