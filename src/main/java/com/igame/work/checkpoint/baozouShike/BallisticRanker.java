package com.igame.work.checkpoint.baozouShike;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Transient;

/**
 * @author xym
 */
@Entity(noClassnameStored = true)
public class BallisticRanker implements Comparable<BallisticRanker> {


    private long playerId;

    @Transient
    private String name;    //昵称

    private int score;      //分数

    @Transient
    private int rank;      //排名

    @JsonIgnore
    private long time;   //通关时长，毫秒



    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BallisticRanker) {
            BallisticRanker target = (BallisticRanker) o;
            return target.getPlayerId() == this.playerId;
        }
        return false;
    }


    public BallisticRanker clone() {
        BallisticRanker ranker = new BallisticRanker();

        return ranker;
    }

    @Override
    public int compareTo(BallisticRanker o) {
        if (o == null || !(o instanceof BallisticRanker)) {
            return 0;
        }
        if (this.score > o.getScore()) {
            return -1;
        } else if (this.score < o.getScore()) {
            return 1;
        } else {
            if (this.time < o.getTime()) {
                return -1;
            } else if (this.time > o.getTime()) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
