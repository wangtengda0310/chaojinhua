package com.igame.core.data.template;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author xym
 *
 * 会员配置
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "low")
public class VipDataTemplate {

    @XmlAttribute(name = "num", required = true)
    private int num;//序号

    @XmlAttribute(name = "system_type")
    private String systemType;//系统类型

    @XmlAttribute(name = "system_name")
    private String systemName;//系统名

    @XmlAttribute(name = "vip_lv")
    private String vipLv;//vip等级

    @XmlAttribute(name = "open_system")
    private Integer openSystem;//开放功能

    @XmlAttribute(name = "fight_limit")
    private Integer fightLimit;//挑战上限

    @XmlAttribute(name = "buy_limit")
    private Integer buyLimit;//购买次数

    @XmlAttribute(name = "value")
    private Integer value;//普通值

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getSystemType() {
        return systemType;
    }

    public void setSystemType(String systemType) {
        this.systemType = systemType;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getVipLv() {
        return vipLv;
    }

    public void setVipLv(String vipLv) {
        this.vipLv = vipLv;
    }

    public Integer getOpenSystem() {
        return openSystem;
    }

    public void setOpenSystem(Integer openSystem) {
        this.openSystem = openSystem;
    }

    public Integer getFightLimit() {
        return fightLimit;
    }

    public void setFightLimit(Integer fightLimit) {
        this.fightLimit = fightLimit;
    }

    public Integer getBuyLimit() {
        return buyLimit;
    }

    public void setBuyLimit(Integer buyLimit) {
        this.buyLimit = buyLimit;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
