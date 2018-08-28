package com.igame.work.checkpoint.baozouShike;

import com.google.common.collect.Maps;
import com.igame.core.db.BasicDto;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;

import java.util.Map;

/**
 * @author xym
 *
 * 暴走时刻排行榜
 */
@Entity(value = "BallisticRank", noClassnameStored = true)
public class BallisticRank extends BasicDto {
    {
        set_id(new ObjectId("5b3ee0d9f9745d1d90069cf3"));
    }

    private Map<Integer,Map<Long,BallisticRanker>> rankMap = Maps.newHashMap();//暴走时刻排行榜

    private Map<Integer,Integer> buffMap = Maps.newHashMap();//暴走时刻buff


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

}
