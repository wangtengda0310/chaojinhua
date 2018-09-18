package com.igame.work.fight.arena;

import com.igame.core.db.BasicDto;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Marcus.Z
 *
 */
@Entity(value = "ArenaRanker", noClassnameStored = true)
public class ArenaServiceDto extends BasicDto {
	
    
    private List<ArenaRanker> rank1 = new ArrayList<>();//排行榜
    
    private List<ArenaRanker> rank2 = new ArrayList<>();
    
    private List<ArenaRanker> rank3 = new ArrayList<>();
    
    private List<ArenaRanker> rank4 = new ArrayList<>();
    
    private List<ArenaRanker> rank5 = new ArrayList<>();
    
    private List<ArenaRanker> rank6 = new ArrayList<>();
    
    private List<ArenaRanker> rank7 = new ArrayList<>();
    
    private List<ArenaRanker> rank8 = new ArrayList<>();
    
	{

		set_id(new ObjectId("5af9513cf9745d248cc8b287"));
	}

	public List<ArenaRanker> getRank1() {
		return rank1;
	}

	public void setRank1(List<ArenaRanker> rank1) {
		this.rank1 = rank1;
	}

	public List<ArenaRanker> getRank2() {
		return rank2;
	}

	public void setRank2(List<ArenaRanker> rank2) {
		this.rank2 = rank2;
	}

	public List<ArenaRanker> getRank3() {
		return rank3;
	}

	public void setRank3(List<ArenaRanker> rank3) {
		this.rank3 = rank3;
	}

	public List<ArenaRanker> getRank4() {
		return rank4;
	}

	public void setRank4(List<ArenaRanker> rank4) {
		this.rank4 = rank4;
	}

	public List<ArenaRanker> getRank5() {
		return rank5;
	}

	public void setRank5(List<ArenaRanker> rank5) {
		this.rank5 = rank5;
	}

	public List<ArenaRanker> getRank6() {
		return rank6;
	}

	public void setRank6(List<ArenaRanker> rank6) {
		this.rank6 = rank6;
	}

	public List<ArenaRanker> getRank7() {
		return rank7;
	}

	public void setRank7(List<ArenaRanker> rank7) {
		this.rank7 = rank7;
	}

	public List<ArenaRanker> getRank8() {
		return rank8;
	}

	public void setRank8(List<ArenaRanker> rank8) {
		this.rank8 = rank8;
	}
}
