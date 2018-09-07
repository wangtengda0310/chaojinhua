package com.igame.work.checkpoint.baozouShike;

import org.mongodb.morphia.annotations.Entity;

/**
 * @author xym
 */
@Entity(noClassnameStored = true)
public class BallisticRanker implements Comparable<BallisticRanker> {


    private long playerId;

    private int score;      //分数

    private long time;   //通关时长，毫秒



    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
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
        if (o == null) {
            return -1;
        }
        if (this.score == o.getScore()) {
            return Long.compare(this.time, o.getTime());
        } else {
            return Integer.compare(this.score, o.getScore());
        }
    }
}
