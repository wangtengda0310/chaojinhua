package com.igame.work.fight.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Transient;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.igame.core.db.BasicVO;
import com.igame.core.db.DBManager;
import com.igame.util.GameMath;
import com.igame.work.fight.dto.AreaRanker;
import com.igame.work.user.dao.AreaRobotDAO;
import com.igame.work.user.dto.Player;
import com.igame.work.user.dto.RobotDto;
import com.igame.work.user.service.RobotService;

/**
 * 
 * @author Marcus.Z
 *
 */
@Entity(value = "AreaRanker", noClassnameStored = true)
public class ArenaService extends BasicVO {
	
    
    private Map<Integer,List<AreaRanker>> rank1 = Maps.newHashMap();//排行榜
    
    private Map<Integer,List<AreaRanker>> rank2 = Maps.newHashMap();
    
    private Map<Integer,List<AreaRanker>> rank3 = Maps.newHashMap();
    
    private Map<Integer,List<AreaRanker>> rank4 = Maps.newHashMap();
    
    private Map<Integer,List<AreaRanker>> rank5 = Maps.newHashMap();
    
    private Map<Integer,List<AreaRanker>> rank6 = Maps.newHashMap();
    
    private Map<Integer,List<AreaRanker>> rank7 = Maps.newHashMap();
    
    private Map<Integer,List<AreaRanker>> rank8 = Maps.newHashMap();
    
    
    
    
    @Transient
    public Map<Integer,Map<Long,RobotDto>> robot = Maps.newHashMap();//玩家阵容数据
    
    @Transient
    public Map<Integer,Object> lock = Maps.newHashMap();
    
    public boolean up;
    
    /**
     * 随机出对手
     * @param total 全部数据
     * @param rank 当前角色排名
     */
    public List<AreaRanker> getOpponent(List<AreaRanker> total,int rank){
    	List<AreaRanker> opponent = Lists.newArrayList();
    	if(rank<=6){
    		for(int i = 0;i<=4;i++){
    			opponent.add(total.get(i));
    		}
    	}else if (rank < 10){	//大于6小于10时 下面算法可能出现重复结果

			int r1 = GameMath.getRandomInt(rank - 6 ,rank - 5);
			int r2 = GameMath.getRandomInt(rank - 5 ,rank - 4);
			int r3 = GameMath.getRandomInt(rank - 4 ,rank - 3);
			int r4 = GameMath.getRandomInt(rank - 3 ,rank - 2);
			int r5 = GameMath.getRandomInt(rank - 2 ,rank);

			opponent.add(total.get(r1-1));
			opponent.add(total.get(r2-1));
			opponent.add(total.get(r3-1));
			opponent.add(total.get(r4-1));
			opponent.add(total.get(r5-1));

		}else {
    		int r1 = (int)(rank * GameMath.getRandomDouble(0.4,  0.5) - 1);
    		int r2 = (int)(rank * GameMath.getRandomDouble(0.51, 0.6) - 1);
    		int r3 = (int)(rank * GameMath.getRandomDouble(0.61, 0.7) - 1);
    		int r4 = (int)(rank * GameMath.getRandomDouble(0.71, 0.8) - 1);
    		int r5 = (int)(rank * GameMath.getRandomDouble(0.81, 1.0) - 1);
    		if(total.size()>=r1){
    			opponent.add(total.get(r1-1));
    		}
    		if(total.size()>=r2){
    			opponent.add(total.get(r2-1));
    		}
    		if(total.size()>=r3){
    			opponent.add(total.get(r3-1));
    		}
    		if(total.size()>=r4){
    			opponent.add(total.get(r4-1));
    		}
    		if(total.size()>=r5){
    			opponent.add(total.get(r5-1));
    		}
    	}
    	return opponent;
    }


    /**
     * 
     * @param severId
     * @return
     */
    public Object getLockByPlayer(int severId){

    	return lock.computeIfAbsent(severId,k -> new Object());
    }
    
    
    /**
     * 排序
     */
    public void ref(){
    	for(List<AreaRanker> ll : rank1.values()){
    		Collections.sort(ll);
    	}
    	for(List<AreaRanker> ll : rank2.values()){
    		Collections.sort(ll);
    	}
    	for(List<AreaRanker> ll : rank3.values()){
    		Collections.sort(ll);
    	}
    	for(List<AreaRanker> ll : rank4.values()){
    		Collections.sort(ll);
    	}
    	for(List<AreaRanker> ll : rank5.values()){
    		Collections.sort(ll);
    	}
    	for(List<AreaRanker> ll : rank6.values()){
    		Collections.sort(ll);
    	}
    	for(List<AreaRanker> ll : rank7.values()){
    		Collections.sort(ll);
    	}
    	for(List<AreaRanker> ll : rank8.values()){
    		Collections.sort(ll);
    	}
    	
    }
    
    
    public List<AreaRanker> getRank(int type,int serverId){
    	
    	Map<Integer,List<AreaRanker>> maps = null;
    	if(type == 1){
    		maps = rank1;
    	}else if(type == 2){
    		maps = rank2;
    	}else if(type == 3){
    		maps = rank3;
    	}else if(type == 4){
    		maps = rank4;
    	}else if(type == 5){
    		maps = rank5;
    	}else if(type == 6){
    		maps = rank6;
    	}else if(type == 7){
    		maps = rank7;
    	}else if(type == 8){
    		maps = rank8;
    	}
    	if(maps == null){
    		return null;
    	}
    	if(maps.get(serverId) == null){
    		return null;
    	}
    	return maps.get(serverId);
    }
    
    /**
     * 获取排名
     * @param type
     * @param serverId
     * @param playerId
     * @return
     */
    public int getMRank(int type,int serverId, long playerId){
    	int i = 0;
    	Map<Integer,List<AreaRanker>> maps = null;
    	if(type == 1){
    		maps = rank1;
    	}else if(type == 2){
    		maps = rank2;
    	}else if(type == 3){
    		maps = rank3;
    	}else if(type == 4){
    		maps = rank4;
    	}else if(type == 5){
    		maps = rank5;
    	}else if(type == 6){
    		maps = rank6;
    	}else if(type == 7){
    		maps = rank7;
    	}else if(type == 8){
    		maps = rank8;
    	}
    	if(maps == null){
    		return 5001;
    	}
    	List<AreaRanker> ls = maps.get(serverId);
    	if(ls != null){
    		for(AreaRanker rank : ls){
    			i++;
    			if(rank.getPlayerId() == playerId){
    				return rank.getRank();
    			}
    		}
    	}
    	if(i >= ls.size()){
    		return ls.size()+1;
    	}
        return ls.size()+1;
    }
    
    
    /**
     * 玩家阵容数据添加到竞技场数据中
     * @param player
     */
    public void addPlayerRobotDto(Player player,boolean update){//同步处理
    	
		Map<Long,RobotDto> map = robot.computeIfAbsent(player.getSeverId(),k -> Maps.newHashMap());

		RobotDto rb = map.get(player.getPlayerId());
		if(rb == null || update){
			rb = RobotService.ins().createRobotLike(player);

			map.put(player.getPlayerId(), rb);
		}
    	
    }

    
	public void loadRank(boolean loadDB){
		if(loadDB){
			AreaRobotDAO.ins().loadRank(this);
		}
		String DBName = DBManager.getInstance().p.getProperty("DBName");
		String[] DBNames = DBName.split(",");
		for(String db : DBNames){
			int serverId=Integer.parseInt(db.substring(5));
			rank1.computeIfAbsent(serverId,k -> Lists.newArrayList());
			rank2.computeIfAbsent(serverId,k -> Lists.newArrayList());
			rank3.computeIfAbsent(serverId,k -> Lists.newArrayList());
			rank4.computeIfAbsent(serverId,k -> Lists.newArrayList());
			rank5.computeIfAbsent(serverId,k -> Lists.newArrayList());
			rank6.computeIfAbsent(serverId,k -> Lists.newArrayList());
			rank7.computeIfAbsent(serverId,k -> Lists.newArrayList());
			rank8.computeIfAbsent(serverId,k -> Lists.newArrayList());
			
			List<Map<Integer,List<AreaRanker>>> allRank = new ArrayList<>();
			allRank.add(rank1);
			allRank.add(rank2);
			allRank.add(rank3);
			allRank.add(rank4);
			allRank.add(rank5);
			allRank.add(rank6);
			allRank.add(rank7);
			allRank.add(rank8);
			
			
			// 重置时，玩家排行榜要排在机器人后面。服务器玩家数据量大的时候也保留5000个机器人数据，防止玩家挑战次数用不完
			for (Map<Integer,List<AreaRanker>> r : allRank) {

				List<AreaRanker> l = r.get(serverId);
				if(l.size()<5000){
					int left = l.size()+1;
					for(int i = left;i<= 5000;i++){
						l.add(new AreaRanker(10+i, i, "玩家_"+i, GameMath.getRandomCount(10000-i, 20000-i),serverId));
					}
				}
			}
		}
	}
	
	
	public void saveRank(){
		AreaRobotDAO.ins().updateRank(this);
	}
    
    public void loadRobot(){
		String DBName = DBManager.getInstance().p.getProperty("DBName");
		String[] DBNames = DBName.split(",");
		for(String db : DBNames){
			int serverId=Integer.parseInt(db.substring(5));
			robot.put(serverId, AreaRobotDAO.ins().loadData(serverId));
		}
    }
    
    public void saveRobot(){
		for(Map<Long, RobotDto> db : robot.values()){
			try{
				for(RobotDto m : db.values()){
					AreaRobotDAO.ins().update(m);
				}
			}catch(Exception e){
				e.printStackTrace();
			}

		}
    }


	public Map<Integer, Map<Long, RobotDto>> getRobot() {
		return robot;
	}

	public void setRobot(Map<Integer, Map<Long, RobotDto>> robot) {
		this.robot = robot;
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
	
	public boolean isUp() {
		return up;
	}


	public void setUp(boolean up) {
		this.up = up;
	}


	/**
	 * 排行榜奖励结算
	 */
	public void giveReward(){
		
	}
	
	public void clearRank(){
		rank1.clear();
		rank2.clear();
		rank3.clear();
		rank4.clear();
		rank5.clear();
		rank6.clear();
		rank7.clear();
		rank8.clear();
		this.loadRank(false);
		this.setUp(true);
	}
    
	@Transient
    private static final ArenaService domain = new ArenaService();

    public static final ArenaService ins() {
    	domain.set_id(new ObjectId("5af9513cf9745d248cc8b287"));
        return domain;
    }
	
    

}
