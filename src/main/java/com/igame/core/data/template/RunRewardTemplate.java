package com.igame.core.data.template;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author xym
 *
 * 暴走时刻奖励模板
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "low")
public class RunRewardTemplate {


    @XmlAttribute(name = "num", required = true)
    private int num;//奖励等级

    @XmlAttribute(name = "rank_min")
    private int rankMin;//最小排名

    @XmlAttribute(name = "rank_max")
    private int rankMax;//最大排名

    @XmlAttribute(name = "reward")
    private String reward;//奖励  同GM字符串结构

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getRankMin() {
        return rankMin;
    }

    public void setRankMin(int rankMin) {
        this.rankMin = rankMin;
    }

    public int getRankMax() {
        return rankMax;
    }

    public void setRankMax(int rankMax) {
        this.rankMax = rankMax;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }
}
