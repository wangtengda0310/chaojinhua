package com.igame.work.checkpoint.baozouShike;

import com.google.common.collect.Maps;
import com.igame.core.db.BasicDto;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Transient;

import java.util.List;
import java.util.Map;

/**
 * @author xym
 *
 * 暴走时刻排行榜
 */
@Entity(value = "ballisticRank", noClassnameStored = true)
public class BallisticRank extends BasicDto {

    @Transient
    private static final BallisticRank domain = new BallisticRank();

    public static final BallisticRank ins(){
        return domain;
    }


    private Map<Integer,Map<Long,BallisticRanker>> rankMap = Maps.newHashMap();//暴走时刻排行榜

    private Map<Integer,Integer> buffMap = Maps.newHashMap();//暴走时刻buff

    @Transient
    private Map<Integer,List<BallisticRanker>> rankList = Maps.newHashMap();//暴走时刻排行榜


    public Map<Integer, Map<Long, BallisticRanker>> getRankMap() {
        return rankMap;
    }

    public void setRankMap(Map<Integer, Map<Long, BallisticRanker>> rankMap) {
        this.rankMap = rankMap;
    }

    public Map<Integer, Integer> getBuffMap() {
        return buffMap;
    }

    public void setBuffMap(Map<Integer, Integer> buffMap) {
        this.buffMap = buffMap;
    }

    public Map<Integer, List<BallisticRanker>> getRankList() {
        return rankList;
    }

    public void setRankList(Map<Integer, List<BallisticRanker>> rankList) {
        this.rankList = rankList;
    }
}
