package com.igame.work.fight.arena;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.igame.core.ISFSModule;
import com.igame.core.di.Inject;
import com.igame.core.di.LoadXml;
import com.igame.core.event.EventService;
import com.igame.core.event.EventType;
import com.igame.core.event.PlayerEventObserver;
import com.igame.core.quartz.TimeListener;
import com.igame.util.GameMath;
import com.igame.work.PlayerEvents;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dto.Player;
import com.igame.work.user.dto.RobotDto;
import com.igame.work.user.dto.Team;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ArenaService extends EventService implements ISFSModule, TimeListener {
    /**
     * 竞技场数据
     */
    @LoadXml("arenadata.xml") public ArenaData arenaData;
    private ArenaServiceDto as;

    @Inject private ArenaRobotDAO dao;

    private Map<Long, Long> opponent = new ConcurrentHashMap<>();//临时竞技场对手ID

    private Map<Long, Integer> arenaType = new ConcurrentHashMap<>();//临时竞技场ID  刷新对手用


    public void setArenaType(Player player, int atype) {
        arenaType.put(player.getPlayerId(), atype);
    }

    public int getArenaType(Player player) {
        return arenaType.get(player.getPlayerId());
    }

    @Override
    public void minute5() {

        if(isPrepareToSave()){
            dao.updateRank(as);
            setPrepareToSave(false);
        }
    }

    public static boolean prepareToSave;

    public static boolean isPrepareToSave() {
        return prepareToSave;
    }


    public static void setPrepareToSave(boolean prepareToSave) {
        ArenaService.prepareToSave = prepareToSave;
    }

    @Override
    public void week7() {
        clearRank();
    }

    @Override
    public void zero() {
        giveReward();
    }

    /**
     * 排行榜奖励结算
     */
    private void giveReward() {

    }

    private Map<Long, RobotDto> robot = Maps.newHashMap();//玩家阵容数据

    public Map<Long, RobotDto> getRobot() {
        return robot;
    }

    @Override
    protected PlayerEventObserver playerObserver() {
        return new PlayerEventObserver() {
            @Override
            public EventType interestedType() {
                return PlayerEvents.ARENA_RANK;
            }

            @Override
            public void observe(Player player, EventType eventType, Object event) {
                if (player != null) {
                    int type = arenaType.get(player.getPlayerId());
                    List<ArenaRanker> rank = rankOfType(type);
                    Collections.sort(rank);
                }
            }
        };
    }

    private void loadRank(boolean loadDB) {

        if (loadDB) {
            as = dao.getDatastore().find(ArenaServiceDto.class).get();
            if (as == null) {
                as = new ArenaServiceDto();
            }
        }

        List<List<ArenaRanker>> allRank = new ArrayList<>();
        allRank.add(as.getRank1());
        allRank.add(as.getRank2());
        allRank.add(as.getRank3());
        allRank.add(as.getRank4());
        allRank.add(as.getRank5());
        allRank.add(as.getRank6());
        allRank.add(as.getRank7());
        allRank.add(as.getRank8());


        // 重置时，玩家排行榜要排在机器人后面。服务器玩家数据量大的时候也保留5000个机器人数据，防止玩家挑战次数用不完
        for (List<ArenaRanker> l : allRank) {
            if (l.size() < 5000) {
                int left = l.size() + 1;
                for (int i = left; i <= 5000; i++) {
                    l.add(new ArenaRanker(10 + i, i, "玩家_" + i
                            , GameMath.getRandomCount(10000 - i, 20000 - i)));
                }
            }
        }
    }

    @Override
    public void init() {

        loadRank(true); // module init
    }

    private void clearRank() {
        as.getRank1().clear();
        as.getRank2().clear();
        as.getRank3().clear();
        as.getRank4().clear();
        as.getRank5().clear();
        as.getRank6().clear();
        as.getRank7().clear();
        as.getRank8().clear();
        loadRank(false);    // after clear rank
        setPrepareToSave(true);
    }

    /**
     * 随机出对手
     *
     * @param total 全部数据
     * @param rank  当前角色排名
     */
    List<ArenaRanker> getOpponent(List<ArenaRanker> total, int rank) {
        List<ArenaRanker> opponent = Lists.newArrayList();
        if (rank <= 6) {
            for (int i = 0; i <= 4; i++) {
                opponent.add(total.get(i));
            }
        } else if (rank < 10) {    //大于6小于10时 下面算法可能出现重复结果

            int r1 = GameMath.getRandomInt(rank - 6, rank - 5);
            int r2 = GameMath.getRandomInt(rank - 5, rank - 4);
            int r3 = GameMath.getRandomInt(rank - 4, rank - 3);
            int r4 = GameMath.getRandomInt(rank - 3, rank - 2);
            int r5 = GameMath.getRandomInt(rank - 2, rank);

            opponent.add(total.get(r1 - 1));
            opponent.add(total.get(r2 - 1));
            opponent.add(total.get(r3 - 1));
            opponent.add(total.get(r4 - 1));
            opponent.add(total.get(r5 - 1));

        } else {
            int r1 = (int) (rank * GameMath.getRandomDouble(0.4, 0.5) - 1);
            int r2 = (int) (rank * GameMath.getRandomDouble(0.51, 0.6) - 1);
            int r3 = (int) (rank * GameMath.getRandomDouble(0.61, 0.7) - 1);
            int r4 = (int) (rank * GameMath.getRandomDouble(0.71, 0.8) - 1);
            int r5 = (int) (rank * GameMath.getRandomDouble(0.81, 1.0) - 1);
            if (total.size() >= r1) {
                opponent.add(total.get(r1 - 1));
            }
            if (total.size() >= r2) {
                opponent.add(total.get(r2 - 1));
            }
            if (total.size() >= r3) {
                opponent.add(total.get(r3 - 1));
            }
            if (total.size() >= r4) {
                opponent.add(total.get(r4 - 1));
            }
            if (total.size() >= r5) {
                opponent.add(total.get(r5 - 1));
            }
        }
        return opponent;
    }


    /**
     * 排序
     */
    public void refresh() {
        Collections.sort(as.getRank1());
        Collections.sort(as.getRank2());
        Collections.sort(as.getRank3());
        Collections.sort(as.getRank4());
        Collections.sort(as.getRank5());
        Collections.sort(as.getRank6());
        Collections.sort(as.getRank7());
        Collections.sort(as.getRank8());
    }


    ArenaRanker getRankInfo(Player player) {
        int type = arenaType.get(player.getPlayerId());
        List<ArenaRanker> rank = rankOfType(type);
        ArenaRanker self = rank.stream()
                .filter(r -> r.getPlayerId() == player.getPlayerId())
                .findAny()
                .orElse(null);
        if(self == null){
            self = new ArenaRanker(player, getMRank(type, player.getPlayerId()));
            rank.add(self);
        }

        return self;

    }

    List<ArenaRanker> rankOfType(int type) {
        if (type == 1) {
            return as.getRank1();
        } else if (type == 2) {
            return as.getRank2();
        } else if (type == 3) {
            return as.getRank3();
        } else if (type == 4) {
            return as.getRank4();
        } else if (type == 5) {
            return as.getRank5();
        } else if (type == 6) {
            return as.getRank6();
        } else if (type == 7) {
            return as.getRank7();
        } else if (type == 8) {
            return as.getRank8();
        }
        return new ArrayList<>();
    }

    /**
     * 获取排名
     */
    int getMRank(int type, long playerId) {
        List<ArenaRanker> ls = rankOfType(type);
        if (ls == null) {
            return 5001;
        }

        for (ArenaRanker rank : ls) {
            if (rank.getPlayerId() == playerId) {
                return rank.getRank();
            }
        }
        return ls.size() + 1;
    }

    /**
     * get and remove
     */
    long getOpponent(Player player) {
        return opponent.remove(player.getPlayerId());
    }

    void setOpponent(Player player, long playerId) {
        opponent.put(player.getPlayerId(), playerId);
    }

    public void afterPlayerLogin(Player player) {
        if(player.getTeams().get(6) == null){
            long id1 = -1;
            long id2 = -1;
            int count = 0;
            for(Monster mm : player.getMonsters().values()){
                if(count == 0){
                    id1 = mm.getObjectId();
                }else{
                    id2 = mm.getObjectId();
                }
                count++;
                if(count >= 2){
                    break;
                }
            }
            player.getTeams().put(6,new Team(6,"竞技场防守阵容",id1,id2));
        }
    }
}
