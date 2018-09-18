package com.igame.work.fight.service;

import com.google.common.collect.Lists;
import com.igame.core.quartz.TimeListener;
import com.igame.work.MessageUtil;
import com.igame.work.fight.dto.FightBase;
import com.igame.work.fight.dto.FightData;
import com.igame.work.user.dto.Player;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



/**
 * 
 * @author Marcus.Z
 *
 */
public class PVPFightService implements TimeListener {
	
	private static final PVPFightService domain = new PVPFightService();

    public static PVPFightService ins() {
        return domain;
    }
    
    public Map<Long,Player> palyers = new ConcurrentHashMap<Long, Player>();//匹配池玩家
    
    public Map<Long,FightBase> fights = new ConcurrentHashMap<Long, FightBase>();//进行中的战斗对象
    
    public Object lock = new Object();
    
    /**
     * 开始匹配
     */
    public void readFight(Player player){
    	
    	synchronized (lock) {
        	palyers.put(player.getPlayerId(), player);
        	if(palyers.size() >= 2){
        		List<Player> ls = Lists.newArrayList();
        		for(Player pp : palyers.values()){
        			ls.add(pp);
        			if(ls.size() >= 2){
        				processStartFigth(ls.get(0),ls.get(1));
        				palyers.remove(ls.get(0).getPlayerId());
        				palyers.remove(ls.get(1).getPlayerId());
        				ls.clear();
        			}
        		}
        	}
		}
    	
    }
    
    /**
     * 开始匹配
     */
    public synchronized void chancelFight(Player player){
    	
    	synchronized (lock) {
    		palyers.remove(player.getPlayerId());
    	}

    	
    }
    
    
    /**
     * 成功匹配到玩家
     */
    public void processStartFigth(Player playerA,Player playerB){
    	
    	FightBase fb  = new FightBase(playerA.getPlayerId(),new FightData(playerA),new FightData(playerB));
    	fb.setType(1);
//    	fights.put(playerA.getPlayerId(), fb);
    	playerA.setFightBase(fb);
    	playerB.setFightBase(fb);
    	MessageUtil.notifyMatchEnd(fb);
    	
    	
    }
    
    
    /**
     * 倒计时结束
     */
    public void readCDFight(Player player){
    	
    	FightBase fb = player.getFightBase();
    	if(fb != null){
    		synchronized (fb.lock) {
    			if(System.currentTimeMillis() - fb.getStartTime() >= 9 * 1000){
        			fb.state.add(player.getPlayerId());
        			if(fb.state.size() >= 2){
        				MessageUtil.notifyMatchLoad(fb);
        				fb.state.clear();
        			}
    			}
			}    		
    	}
    	
    }
    
    
    /**
     * 加载游戏完毕
     */
    public void gameLoadEnd(Player player){
    	
    	FightBase fb = player.getFightBase();
    	if(fb != null){
    		synchronized (fb.lock) {
    			fb.state.add(player.getPlayerId());
    			if(fb.state.size() >= 2){
    				fb.setStartTime(System.currentTimeMillis());
    				MessageUtil.gameStart(fb);
    				fb.state.clear();
    			}
			}    		
    	}
    	
    }
    
   
	public Map<Long, FightBase> getFights() {
		return fights;
	}


	public void setFights(Map<Long, FightBase> fights) {
		this.fights = fights;
	}

	public Map<Long, Player> getPalyers() {
		return palyers;
	}


	public void setPalyers(Map<Long, Player> palyers) {
		this.palyers = palyers;
	}


	@Override
	public void minute5() {

		long now = System.currentTimeMillis();  // todo 这个是quartz通知过来的 时间不是时间发送当时的时间了
		for(FightBase fb : fights.values()){
			if(now - fb.getStartTime() >= 300000){
				fights.remove(fb.getId());
			}
		}

	}
}
