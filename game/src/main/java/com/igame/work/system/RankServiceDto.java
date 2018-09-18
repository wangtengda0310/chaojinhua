package com.igame.work.system;




import com.google.common.collect.Maps;
import com.igame.core.db.BasicDto;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;

import java.util.Map;


@Entity(value = "RankService", noClassnameStored = true)
public class RankServiceDto extends BasicDto {
	{

		set_id(new ObjectId("5ae95c14f9745d2b047cfa3a"));
	}

	private Map<Integer,Map<Long,Ranker>> rankMap = Maps.newHashMap();//命运之门排行榜

	public Map<Integer, Map<Long, Ranker>> getRankMap() {
		return rankMap;
	}

	public void setRankMap(Map<Integer, Map<Long, Ranker>> rankMap) {
		this.rankMap = rankMap;
	}

}
