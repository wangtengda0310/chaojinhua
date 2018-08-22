package com.igame.work.fight.dto;



import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.igame.core.db.BasicDto;
import com.igame.work.user.dto.Player;


@Entity(noClassnameStored = true)
public class AreaRanker extends BasicDto implements Comparable<AreaRanker>{
	
	private long playerId;//玩家为100011开头
	private int rank;//排行
	private String name = "";
	
	private long fightValue;
	
	@JsonIgnore
	@Transient
	private Object lock = new Object();
	
	@JsonIgnore
	private int severId;
	

    public AreaRanker(){}
    
    

    public AreaRanker(long playerId, int rank, String name, long fightValue,
			int severId) {
		super();
		this.playerId = playerId;
		this.rank = rank;
		this.name = name;
		this.fightValue = fightValue;
		this.severId = severId;
	}



	public AreaRanker parse(Player player){
    	return null;
    }
    

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

	public long getFightValue() {
		return fightValue;
	}

	public void setFightValue(long fightValue) {
		this.fightValue = fightValue;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	@Override
    public boolean equals(Object o) {
        if(o instanceof AreaRanker){
            AreaRanker target = (AreaRanker)o;
            return target.getPlayerId()==this.playerId;
        }
        return false;
    }


    public AreaRanker clone(){
        AreaRanker ranker = new AreaRanker();

        return ranker;
    }
    
    @Override
    public int compareTo(AreaRanker o) {
		if (o == null || !(o instanceof AreaRanker)) {
			return 0;
		}
		if(this.rank < o.getRank()){
			return -1;
		}else if(this.rank > o.getRank()){
			return 1;
		}else{
			return 0;
		}
    }

	public Object getLock() {
		return lock;
	}

	public void setLock(Object lock) {
		this.lock = lock;
	}

	public int getSeverId() {
		return severId;
	}

	public void setSeverId(int severId) {
		this.severId = severId;
	}
    

    
}
