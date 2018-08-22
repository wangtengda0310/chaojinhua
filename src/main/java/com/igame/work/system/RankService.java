package com.igame.work.system;




import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.igame.core.db.BasicDto;
import com.igame.work.user.dto.Player;

import java.util.*;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Transient;


@Entity(value = "RankService", noClassnameStored = true)
public class RankService    extends BasicDto {

	@Transient
	private static final RankService domain = new RankService();

	public static final RankService ins(){
		domain.set_id(new ObjectId("5ae95c14f9745d2b047cfa3a"));
		return domain;
	}


	private Map<Integer,Map<Long,Ranker>> rankMap = Maps.newHashMap();//命运之门排行榜
    
    @Transient
    private Map<Integer,List<Ranker>> rankList = Maps.newHashMap();//命运之门排行榜

    @Transient
    private Map<Integer,Object> objects = Maps.newConcurrentMap();//锁

	public Map<Integer, Map<Long, Ranker>> getRankMap() {
		return rankMap;
	}

	public void setRankMap(Map<Integer, Map<Long, Ranker>> rankMap) {
		this.rankMap = rankMap;
	}

	public Map<Integer, Object> getObjects() {
		return objects;
	}

	public void setObjects(Map<Integer, Object> objects) {
		this.objects = objects;
	}

	public Map<Integer, List<Ranker>> getRankList() {
		return rankList;
	}

	public void setRankList(Map<Integer, List<Ranker>> rankList) {
		this.rankList = rankList;
	}


    public int getMRank(Player player){
    	int i = 0;
    	List<Ranker> ls = rankList.get(player.getSeverId());
    	if(ls != null){
    		for(Ranker rank : ls){
    			i++;
    			if(rank.getPlayerId() == player.getPlayerId()){
    				return i;
    			}
    		}
    	}
        return 0;
    }
    
    public void setMRank(Player player){

		Object oo = objects.computeIfAbsent(player.getSeverId(), key -> new Object());
    	synchronized (oo) {
			Map<Long, Ranker> lm = rankMap.computeIfAbsent(player.getSeverId(), key -> new HashMap<Long, Ranker>());
    		Ranker rr = lm.get(player.getPlayerId());
    		if(rr == null){
    			rr = new Ranker();
    			rr.setPlayerId(player.getPlayerId());
    			rr.setName(player.getNickname());
    			rr.setScore(player.getFateData().getTodayBoxCount());
    			lm.put(player.getPlayerId(), rr);
    		}else{
    			if(rr.getScore() < player.getFateData().getTodayBoxCount()){
    				rr.setScore(player.getFateData().getTodayBoxCount());
    			}
    		} 		
    		
		}
    }
    
    public void sort(){
    	rankList.clear();
    	for(Map.Entry<Integer,Map<Long,Ranker>> m :rankMap.entrySet()){
    		List<Ranker> ll = Lists.newArrayList();
    		ll.addAll(m.getValue().values());
    		Collections.sort(ll);
    		rankList.put(m.getKey(), ll);
    	}
    }


	public void loadData(){
		this.rankMap = RankServiceDAO.ins().loadData();
		this.sort();
	}
	
	
	public void saveData(){
		RankServiceDAO.ins().update(this);
	}

	public void zero() {
		rankMap.clear();
	}
}
