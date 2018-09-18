package com.igame.work.turntable.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author xym
 *
 * 会员等级
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "low")
public class LuckTableTemplate {

    @XmlAttribute(name = "table", required = true)
    private int table;//序号

    @XmlAttribute(name = "player_lv")
    private String playerLv;//等级区间

    @XmlAttribute(name = "site")
    private int site;//奖励位置

    @XmlAttribute(name = "table_type")
    private int tableType;//抽取类型    0=不限次

    @XmlAttribute(name = "item")
    private String item;//道具

    @XmlAttribute(name = "show_rate")
    private String showRate;//展示概率

    @XmlAttribute(name = "get_rate")
    private float getRate;//抽取概率

    @XmlAttribute(name = "up_rate")
    private Float upRate;//vip文本值

    public int getTable() {
        return table;
    }

    public void setTable(int table) {
        this.table = table;
    }

    public String getPlayerLv() {
        return playerLv;
    }

    public void setPlayerLv(String playerLv) {
        this.playerLv = playerLv;
    }

    public int getSite() {
        return site;
    }

    public void setSite(int site) {
        this.site = site;
    }

    public int getTableType() {
        return tableType;
    }

    public void setTableType(int tableType) {
        this.tableType = tableType;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getShowRate() {
        return showRate;
    }

    public void setShowRate(String showRate) {
        this.showRate = showRate;
    }

    public float getGetRate() {
        return getRate;
    }

    public void setGetRate(float getRate) {
        this.getRate = getRate;
    }

    public Float getUpRate() {
        return upRate;
    }

    public void setUpRate(Float upRate) {
        this.upRate = upRate;
    }
}
