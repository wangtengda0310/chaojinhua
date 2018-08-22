package com.igame.work.system;



import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.igame.core.db.BasicDto;
import com.igame.work.user.dto.Player;

@Entity(noClassnameStored = true)
public class Ranker extends BasicDto implements Comparable<Ranker>{
	
	private long playerId;
	
	@Transient
	@JsonIgnore
	private int rank;//排行

	private String name;

	private int score;
	

    public Ranker(){}

    public Ranker parse(Player player){
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

	public int getScore() {
		return score;
	}


	public void setScore(int score) {
		this.score = score;
	}
	


	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	@Override
    public boolean equals(Object o) {
        if(o instanceof Ranker){
            Ranker target = (Ranker)o;
            return target.getPlayerId()==this.playerId;
        }
        return false;
    }


    public Ranker clone(){
        Ranker ranker = new Ranker();

        return ranker;
    }
    
    @Override
    public int compareTo(Ranker o) {
		if (o == null || !(o instanceof Ranker)) {
			return 0;
		}
		if(this.score > o.getScore()){
			return -1;
		}else if(this.score < o.getScore()){
			return 1;
		}else{
			if(this.playerId < o.getPlayerId()){
				return -1;
			}else if(this.playerId > o.getPlayerId()){
				return 1;
			}else{
				return 0;
			}
		}
    }
    

    
}
