package com.igame.work.shopActivity;

import com.igame.core.ISFSModule;
import com.igame.core.di.Inject;
import com.igame.core.event.EventService;
import com.igame.core.event.EventType;
import com.igame.core.event.PlayerEventObserver;
import com.igame.core.handler.RetVO;
import com.igame.util.DateUtil;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.work.PlayerEvents;
import com.igame.work.user.dto.Player;

import java.util.*;
import java.util.stream.Collectors;

public class ShopActivityService extends EventService implements ISFSModule {
    @Inject
    private ShopActivityDAO shopActivityDAO;

    static int HOUR_MILLIS = 60 * 60 * 1000;
    Map<Integer, ShopActivityDto> dto = new HashMap<>();

    private ShopActivityDto createShopActivityDto(int activityId) {
        ShopActivityDto dto = new ShopActivityDto();
        dto.setActivityId(activityId);
        return dto;
    }

    private ShopActivityPlayerDto createShopActivityPlayerDto() {
        ShopActivityPlayerDto dto = new ShopActivityPlayerDto();
        dto.openMillis=System.currentTimeMillis();
        return dto;
    }

    private ShopActivityPlayerDto createShopActivityPlayerDto1(long playerId) {
        ShopActivityPlayerDto dto = new ShopActivityPlayerDto();
        dto.openMillis=System.currentTimeMillis();
        return dto;
    }

    private void recordGoldData(Player player, long timeMillis, long amount, ShopActivityDataTemplate c) {
        dto.computeIfAbsent(c.getNum(), this::createShopActivityDto)
                .players
                .computeIfAbsent(player.getPlayerId(), k -> createShopActivityPlayerDto())
                .goldActivityInfo.put(timeMillis, amount);
    }

    private boolean canOpenGoldActivityForPlayer(Player player, ShopActivityDataTemplate c) {
        Map<Long, Long> records = dto.get(c.getNum()).players.get(player.getPlayerId()).goldActivityInfo;
        int rangeMillis = c.getTime_range() * HOUR_MILLIS;
        LinkedList<Long> timestamps = new LinkedList<>(records.keySet());
        do {

            long min = Collections.min(timestamps);
            long max = Collections.max(timestamps);
            if (Math.abs(max - min) > rangeMillis) {
                records.remove(min);
                timestamps.remove(min);
            } else {
                return Math.abs(records.values().stream().mapToLong(x -> x).sum()) >= c.getTouch_value();
            }
        }
        while (timestamps.size() > 1);
        return false;
    }

    private void openGoldActivity(Player player, ShopActivityDataTemplate c) {
        dto.get(c.getNum()).players.get(player.getPlayerId()).goldActivityInfo.clear();
        dto.computeIfAbsent(c.getNum(), this::createShopActivityDto)
                .players.put(player.getPlayerId(), createShopActivityPlayerDto());
    }

    private void recordItemData(Player player, int itemId, int amount, ShopActivityDataTemplate c) {
        dto
                .computeIfAbsent(c.getNum(), this::createShopActivityDto)
                .players.computeIfAbsent(player.getPlayerId(), k -> createShopActivityPlayerDto())
                .itemActivityInfo
                .merge(itemId, amount, (i, j) -> i + j);
    }

    private boolean canOpenItemActivityForPlayer(Player player, ShopActivityDataTemplate c) {
        int itemCount = dto.get(c.getNum()).players.get(player.getPlayerId()).itemActivityInfo.get(c.getItem_id());
        return itemCount >= c.getTouch_value();
    }

    private void openItemActivity(Player player, ShopActivityDataTemplate c) {
        dto.get(c.getNum()).players.get(player.getPlayerId()).itemActivityInfo.clear();
        dto.computeIfAbsent(c.getNum(), this::createShopActivityDto)
                .players.put(player.getPlayerId(), createShopActivityPlayerDto());
    }

    @Override
    protected Collection<PlayerEventObserver> playerObservers() {
        Collection<PlayerEventObserver> observers = new HashSet<>();
        /*
         * 事件监听 因为在构造块中调用这个方法 这个PlayerEventObserver如果提取成类属性会导致没有初始化
         * todo 事件监听器加载的逻辑改后 这里可做调整
         * 一段时间内消耗金币
         */
        observers.add(new PlayerEventObserver() {
            @Override
            public EventType interestedType() {
                return PlayerEvents.CONSUME_GOLD;
            }

            @Override
            public void observe(Player player, Object event) {
                Object[] customEvent = (Object[]) event;
                long timeMillis = (long) customEvent[0];
                long amount = (long) customEvent[1];

                Map<Integer, String> result = ShopActivityDataManager.Configs.getAll().stream()
                        .filter(c -> c.getTouch_limit() == 1)                       // 找出消耗金币类的配置
                        .filter(c -> !isOpen(player, c))                           // 判断是否已经开启，避免重复记录数据
                        .filter(c -> notCd(player, c))                              // 是否cd
                        .peek(c -> recordGoldData(player, timeMillis, amount, c))   // 记录数据
                        .filter(c -> canOpenGoldActivityForPlayer(player, c))       // 是否达成目标
                        .peek(c -> openGoldActivity(player, c))                     // 活动状态改为开启
                        .collect(Collectors.toMap(ShopActivityDataTemplate::getNum
                                , c->DateUtil.formatClientDateTime(dto.get(c.getNum()).players.get(player.getPlayerId()).openMillis)));

                save(player);
                notifyClient(player, result);
            }
        });
        /*
         * 事件监听 因为在构造块中调用这个方法 这个PlayerEventObserver如果提取成类属性会导致没有初始化
         * todo 事件监听器加载的逻辑改后 这里可做调整
         * 累计消耗道具
         */
        observers.add(new PlayerEventObserver() {
            @Override
            public EventType interestedType() {
                return PlayerEvents.CONSUME_ITEM;
            }

            @Override
            public void observe(Player player, Object event) {
                Object[] customEvent = (Object[]) event;
                int itemId = (int) customEvent[0];
                int count = (int) customEvent[1];

                Map<Integer, String> result = ShopActivityDataManager.Configs.getAll().stream()               // 判断每个活动
                        .filter(c -> c.getTouch_limit() == 3)                   // 找出消耗道具类的配置
                        .filter(c -> !isOpen(player, c))                       // 判断是否已经开启，避免重复记录数据
                        .filter(c -> notCd(player, c))                          // 是否cd
                        .peek(c -> recordItemData(player, itemId, count, c))    // 记录数据
                        .filter(c -> canOpenItemActivityForPlayer(player, c))   // 是否达成目标
                        .peek(c -> openItemActivity(player, c))                 // 活动状态改为开启
                        .collect(Collectors.toMap(ShopActivityDataTemplate::getNum
                                , c->DateUtil.formatClientDateTime(dto.get(c.getNum()).players.get(player.getPlayerId()).openMillis)));                    // 通知客户端    todo 合并发送

                save(player);
                notifyClient(player, result);
            }
        });
        return observers;
    }

    private boolean notCd(Player player, ShopActivityDataTemplate config) {
        return !dto.containsKey(config.getNum())
                || !dto.get(config.getNum()).players.containsKey(player.getPlayerId())
                || dto.get(config.getNum()).players.get(player.getPlayerId()).openMillis + config.getCd() * HOUR_MILLIS <= System.currentTimeMillis();
    }

    private boolean isOpen(Player player, ShopActivityDataTemplate config) {
        return dto.containsKey(config.getNum())
                && dto.get(config.getNum()).players.containsKey(player.getPlayerId())
                && dto.get(config.getNum()).players.get(player.getPlayerId()).openMillis + config.getTime_limit() * HOUR_MILLIS > System.currentTimeMillis();

    }

    private void notifyClient(Player player, Map<Integer, String> result) {
        if (result == null || result.isEmpty()) {
            return;
        }
        RetVO vo = new RetVO();
        vo.addData("open", result);
        MessageUtil.sendMessageToPlayer(player, MProtrol.SHOP_ACTICITY_STATE, vo);
    }

    public Map<Integer,String> clientData(Player player) {
        shopActivityDAO.listAll().forEach(e->dto.put(e.getActivityId(),e));
        ShopActivityDataManager.Configs.getAll().forEach(c->dto.computeIfAbsent(c.getNum(), this::createShopActivityDto));

        return ShopActivityDataManager.Configs.getAll().stream()
                .filter(c -> dto.containsKey(c.getNum()))
                .filter(c -> dto.get(c.getNum()).players.containsKey(player.getPlayerId()))
                .filter(c -> !dto.get(c.getNum()).players.get(player.getPlayerId()).received)
                .filter(c -> isOpen(player, c))
                .collect(Collectors.toMap(ShopActivityDataTemplate::getNum,c-> DateUtil.formatClientDateTime(dto.get(c.getNum()).players.get(player.getPlayerId()).openMillis)))
                ;
    }

    void save(Player player) {
        dto.values().forEach(e->shopActivityDAO.save(e));
    }

    // todo remove database that config not exists
}
