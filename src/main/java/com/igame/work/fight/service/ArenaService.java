package com.igame.work.fight.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.igame.core.ISFSModule;
import com.igame.core.db.DBManager;
import com.igame.core.event.EventService;
import com.igame.core.event.EventType;
import com.igame.core.event.PlayerEventObserver;
import com.igame.core.quartz.TimeListener;
import com.igame.server.GameServerExtension;
import com.igame.util.GameMath;
import com.igame.work.PlayerEvents;
import com.igame.work.fight.dto.AreaRanker;
import com.igame.work.user.dao.AreaRobotDAO;
import com.igame.work.user.dto.Player;
import com.igame.work.user.dto.RobotDto;
import com.igame.work.user.service.RobotService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ArenaService extends EventService implements ISFSModule, TimeListener {
    @Override
    public void minute5() {

        saveRobot();
        if(isUp()){
            saveRank();
            setUp(false);
        }
    }

    @Override
    public void week7() {
        clearRank();
    }

    @Override
    public void zero() {
        giveReward();
    }

    public static boolean up;

    public static boolean isUp() {
        return up;
    }


    public static void setUp(boolean up) {
        ArenaService.up = up;
    }

    private Map<Integer, Map<Long, RobotDto>> robot = Maps.newHashMap();//玩家阵容数据

    public Map<Integer, Map<Long, RobotDto>> getRobot() {
        return robot;
    }

    @Override
    protected PlayerEventObserver playerObserver() {
        return new PlayerEventObserver() {
            @Override
            public EventType interestedType() {
                return PlayerEvents.UPDATE_M_RANK;
            }

            @Override
            public void observe(Player player, Object event) {
                addPlayerRobotDto(player,false);
            }
        };
    }

    /**
     * 玩家阵容数据添加到竞技场数据中
     */
    private void addPlayerRobotDto(Player player, boolean update) {//同步处理

        Map<Long, RobotDto> map = robot.computeIfAbsent(player.getSeverId(), k -> Maps.newHashMap());

        RobotDto rb = map.get(player.getPlayerId());
        if (rb == null || update) {
            rb = RobotService.createRobotLike(player);

            map.put(player.getPlayerId(), rb);
        }

    }

    private void saveRank() {
        AreaRobotDAO.ins().updateRank(as);
    }

    private void saveRobot() {
        for (Map<Long, RobotDto> db : robot.values()) {
            try {
                for (RobotDto m : db.values()) {
                    AreaRobotDAO.ins().update(m);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private ArenaServiceDto as;

    private void loadRank(boolean loadDB) {
        DBManager dbManager = GameServerExtension.dbManager;

        if (loadDB) {
            as = dbManager.getDatastore("accounts").find(ArenaServiceDto.class).get();
        }

        String DBName = dbManager.p.getProperty("DBName");
        String[] DBNames = DBName.split(",");

        for (String db : DBNames) {
            int serverId = Integer.parseInt(db.substring(5));
            as.getRank1().computeIfAbsent(serverId, k -> Lists.newArrayList());
            as.getRank2().computeIfAbsent(serverId, k -> Lists.newArrayList());
            as.getRank3().computeIfAbsent(serverId, k -> Lists.newArrayList());
            as.getRank4().computeIfAbsent(serverId, k -> Lists.newArrayList());
            as.getRank5().computeIfAbsent(serverId, k -> Lists.newArrayList());
            as.getRank6().computeIfAbsent(serverId, k -> Lists.newArrayList());
            as.getRank7().computeIfAbsent(serverId, k -> Lists.newArrayList());
            as.getRank8().computeIfAbsent(serverId, k -> Lists.newArrayList());

            List<Map<Integer, List<AreaRanker>>> allRank = new ArrayList<>();
            allRank.add(as.getRank1());
            allRank.add(as.getRank2());
            allRank.add(as.getRank3());
            allRank.add(as.getRank4());
            allRank.add(as.getRank5());
            allRank.add(as.getRank6());
            allRank.add(as.getRank7());
            allRank.add(as.getRank8());


            // 重置时，玩家排行榜要排在机器人后面。服务器玩家数据量大的时候也保留5000个机器人数据，防止玩家挑战次数用不完
            for (Map<Integer, List<AreaRanker>> r : allRank) {

                List<AreaRanker> l = r.get(serverId);
                if (l.size() < 5000) {
                    int left = l.size() + 1;
                    for (int i = left; i <= 5000; i++) {
                        l.add(new AreaRanker(10 + i, i, "玩家_" + i, GameMath.getRandomCount(10000 - i, 20000 - i), serverId));
                    }
                }
            }
        }
    }

    @Override
    public void init() {

        String DBName = GameServerExtension.dbManager.p.getProperty("DBName");
        String[] DBNames = DBName.split(",");
        for (String db : DBNames) {
            int serverId = Integer.parseInt(db.substring(5));
            robot.put(serverId, AreaRobotDAO.ins().loadData());
        }

        loadRank(true);
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
        loadRank(false);
        setUp(true);
    }

    /**
     * 随机出对手
     *
     * @param total 全部数据
     * @param rank  当前角色排名
     */
    public static List<AreaRanker> getOpponent(List<AreaRanker> total, int rank) {
        List<AreaRanker> opponent = Lists.newArrayList();
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
        for (List<AreaRanker> ll : as.getRank1().values()) {
            Collections.sort(ll);
        }
        for (List<AreaRanker> ll : as.getRank2().values()) {
            Collections.sort(ll);
        }
        for (List<AreaRanker> ll : as.getRank3().values()) {
            Collections.sort(ll);
        }
        for (List<AreaRanker> ll : as.getRank4().values()) {
            Collections.sort(ll);
        }
        for (List<AreaRanker> ll : as.getRank5().values()) {
            Collections.sort(ll);
        }
        for (List<AreaRanker> ll : as.getRank6().values()) {
            Collections.sort(ll);
        }
        for (List<AreaRanker> ll : as.getRank7().values()) {
            Collections.sort(ll);
        }
        for (List<AreaRanker> ll : as.getRank8().values()) {
            Collections.sort(ll);
        }

    }


    public List<AreaRanker> getRank(int type, int serverId) {

        Map<Integer, List<AreaRanker>> maps = null;
        maps = getIntegerListMap(type, maps);
        if (maps == null) {
            return null;
        }
        if (maps.get(serverId) == null) {
            return null;
        }
        return maps.get(serverId);
    }

    /**
     * 获取排名
     */
    public int getMRank(int type, int serverId, long playerId) {
        int i = 0;
        Map<Integer, List<AreaRanker>> maps = null;
        maps = getIntegerListMap(type, maps);
        if (maps == null) {
            return 5001;
        }
        List<AreaRanker> ls = maps.get(serverId);
        if (ls != null) {
            for (AreaRanker rank : ls) {
                i++;
                if (rank.getPlayerId() == playerId) {
                    return rank.getRank();
                }
            }
        }
        if (i >= ls.size()) {
            return ls.size() + 1;
        }
        return ls.size() + 1;
    }

    private Map<Integer, List<AreaRanker>> getIntegerListMap(int type, Map<Integer, List<AreaRanker>> maps) {
        if (type == 1) {
            maps = as.getRank1();
        } else if (type == 2) {
            maps = as.getRank2();
        } else if (type == 3) {
            maps = as.getRank3();
        } else if (type == 4) {
            maps = as.getRank4();
        } else if (type == 5) {
            maps = as.getRank5();
        } else if (type == 6) {
            maps = as.getRank6();
        } else if (type == 7) {
            maps = as.getRank7();
        } else if (type == 8) {
            maps = as.getRank8();
        }
        return maps;
    }


    /**
     * 排行榜奖励结算
     */
    private void giveReward() {

    }

}
