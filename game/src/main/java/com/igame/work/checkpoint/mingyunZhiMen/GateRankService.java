package com.igame.work.checkpoint.mingyunZhiMen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.igame.core.ISFSModule;
import com.igame.core.di.Inject;
import com.igame.core.event.EventService;
import com.igame.core.event.EventType;
import com.igame.core.event.PlayerEventObserver;
import com.igame.core.quartz.TimeListener;
import com.igame.work.PlayerEvents;
import com.igame.work.system.RankServiceDAO;
import com.igame.work.system.RankServiceDto;
import com.igame.work.system.Ranker;
import com.igame.work.user.dto.Player;
import com.igame.work.user.dto.RobotDto;
import com.igame.work.user.service.RobotService;

import java.util.*;

public class GateRankService extends EventService implements ISFSModule, TimeListener {
    @Inject private RankServiceDAO rankServiceDAO;
    @Inject private RobotService robotService;

    public void minute5() {

        sort();
        saveData();
    }

    @Override
    public void zero() {
        zeroJob();
    }

    private static Map<Integer, List<Ranker>> rankList = Maps.newHashMap();//命运之门排行榜

    public static Map<Integer, List<Ranker>> getRankList() {
        return rankList;
    }

    private Map<Integer,Object> locks = Maps.newConcurrentMap();//锁

    private RankServiceDto dto;

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

    @Override
    protected Collection<PlayerEventObserver> playerObservers() {
        Collection<PlayerEventObserver> observers = new HashSet<>();
        observers.add(new PlayerEventObserver() {
            @Override
            public EventType interestedType() {
                return PlayerEvents.GATE_RANK;
            }

            @Override
            public void observe(Player player, EventType eventType, Object event) {
                if (player != null) {
                    //加入并刷新排行榜
                    setMRank(player);
                    // TODO 使用TreeMap?
                    sort();
                }
            }
        });
        observers.add(new PlayerEventObserver() {
            @Override
            public EventType interestedType() {
                return PlayerEvents.UPDATE_GATE_RANK;
            }

            @Override
            public void observe(Player player, EventType eventType, Object event) {

                RobotDto rb = robotService.getRobot().get(player.getNickname());
                if (rb == null) {
                    rb = player.robotOfDefence();

                    robotService.getRobot().put(player.getNickname(), rb);
                }

            }
        });
        return observers;
    }

    private void setMRank(Player player){

        int severId = player.getSeverId();
        long playerId = player.getPlayerId();

        Object lock = locks.computeIfAbsent(severId, key -> new Object());
        synchronized (lock) {
            Map<Long, Ranker> lm = dto.getRankMap().computeIfAbsent(severId, key -> new HashMap<>());
            Ranker rr = lm.get(playerId);
            if(rr == null){
                rr = new Ranker();
                rr.setPlayerId(playerId);
                rr.setName(player.getNickname());
                rr.setScore(player.getFateData().getTodayBoxCount());
                lm.put(playerId, rr);
            }else{
                if(rr.getScore() < player.getFateData().getTodayBoxCount()){
                    rr.setScore(player.getFateData().getTodayBoxCount());
                }
            }

        }
    }

    public void sort(){
        rankList.clear();
        for(Map.Entry<Integer,Map<Long,Ranker>> m :dto.getRankMap().entrySet()){
            List<Ranker> ll = Lists.newArrayList();
            ll.addAll(m.getValue().values());
            Collections.sort(ll);
            Integer serverId = m.getKey();
            rankList.put(serverId, ll);
        }
    }


    @Override
    public void init() {
        super.init();

        dto = rankServiceDAO.loadData();
        if (dto == null) {
            dto = new RankServiceDto();
        }
        sort();
    }


    private void saveData(){
        rankServiceDAO.update(dto);
    }

    private void zeroJob() {
        dto.getRankMap().clear();
    }
}
