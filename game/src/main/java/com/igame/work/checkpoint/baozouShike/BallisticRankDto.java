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
@Entity(value = "BallisticRankDto", noClassnameStored = true)
public class BallisticRankDto extends BasicDto {
    {
        set_id(new ObjectId("5b3ee0d9f9745d1d90069cf3"));
    }

    Map<Long,BallisticRanker> rank = Maps.newHashMap();//暴走时刻排行榜

    int buff;//暴走时刻buff 好像是配置文件中的序号之类的东西

    public Map<Long, BallisticRanker> getRank() {
        return rank;
    }

    public void setRank(Map<Long, BallisticRanker> rank) {
        this.rank = rank;
    }

    public int getBuff() {
        return buff;
    }

    public void setBuff(int buff) {
        this.buff = buff;
    }
}
