package com.igame.work.fight.arena;



import com.igame.core.db.BasicDto;
import com.igame.work.user.dto.Player;
import org.mongodb.morphia.annotations.Entity;


@Entity(noClassnameStored = true)
public class ArenaRanker extends BasicDto implements Comparable<ArenaRanker>{
	
	private long playerId;//玩家为100011开头
	private int rank;//排行
	private String name = "";
	
	private long fightValue;

    public ArenaRanker(){}
    
    

    public ArenaRanker(long playerId, int rank, String name, long fightValue) {
		super();
		this.playerId = playerId;
		this.rank = rank;
		this.name = name;
		this.fightValue = fightValue;
	}

	public ArenaRanker(Player player, int playerRank) {
		this(player.getPlayerId(), playerRank, player.getNickname(), player.getTeams().get(6).getFightValue());
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
        if(o instanceof ArenaRanker){
            ArenaRanker target = (ArenaRanker)o;
            return target.getPlayerId()==this.playerId;
        }
        return false;
    }


    public ArenaRanker clone(){
        ArenaRanker ranker = new ArenaRanker();

        return ranker;
    }
    
    @Override
    public int compareTo(ArenaRanker o) {
		if (o == null) {
			return -1;
		}
		return Integer.compare(this.rank, o.getRank());
    }

}
