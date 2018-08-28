package com.igame.work.fight.service;

import com.google.common.collect.Maps;
import com.igame.core.db.BasicDto;
import com.igame.work.fight.dto.AreaRanker;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author Marcus.Z
 *
 */
@Entity(value = "AreaRanker", noClassnameStored = true)
public class ArenaServiceDto extends BasicDto {
	
    
    private Map<Integer,List<AreaRanker>> rank1 = Maps.newHashMap();//排行榜
    
    private Map<Integer,List<AreaRanker>> rank2 = Maps.newHashMap();
    
    private Map<Integer,List<AreaRanker>> rank3 = Maps.newHashMap();
    
    private Map<Integer,List<AreaRanker>> rank4 = Maps.newHashMap();
    
    private Map<Integer,List<AreaRanker>> rank5 = Maps.newHashMap();
    
    private Map<Integer,List<AreaRanker>> rank6 = Maps.newHashMap();
    
    private Map<Integer,List<AreaRanker>> rank7 = Maps.newHashMap();
    
    private Map<Integer,List<AreaRanker>> rank8 = Maps.newHashMap();
    
	{

		set_id(new ObjectId("5af9513cf9745d248cc8b287"));
	}
    





	public Map<Integer, List<AreaRanker>> getRank1() {
		return rank1;
	}


	public void setRank1(Map<Integer, List<AreaRanker>> rank1) {
		this.rank1 = rank1;
	}


	public Map<Integer, List<AreaRanker>> getRank2() {
		return rank2;
	}


	public void setRank2(Map<Integer, List<AreaRanker>> rank2) {
		this.rank2 = rank2;
	}


	public Map<Integer, List<AreaRanker>> getRank3() {
		return rank3;
	}


	public void setRank3(Map<Integer, List<AreaRanker>> rank3) {
		this.rank3 = rank3;
	}


	public Map<Integer, List<AreaRanker>> getRank4() {
		return rank4;
	}


	public void setRank4(Map<Integer, List<AreaRanker>> rank4) {
		this.rank4 = rank4;
	}


	public Map<Integer, List<AreaRanker>> getRank5() {
		return rank5;
	}


	public void setRank5(Map<Integer, List<AreaRanker>> rank5) {
		this.rank5 = rank5;
	}


	public Map<Integer, List<AreaRanker>> getRank6() {
		return rank6;
	}


	public void setRank6(Map<Integer, List<AreaRanker>> rank6) {
		this.rank6 = rank6;
	}


	public Map<Integer, List<AreaRanker>> getRank7() {
		return rank7;
	}


	public void setRank7(Map<Integer, List<AreaRanker>> rank7) {
		this.rank7 = rank7;
	}


	public Map<Integer, List<AreaRanker>> getRank8() {
		return rank8;
	}


	public void setRank8(Map<Integer, List<AreaRanker>> rank8) {
		this.rank8 = rank8;
	}

}
