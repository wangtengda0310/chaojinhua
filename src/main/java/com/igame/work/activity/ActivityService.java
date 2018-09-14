package com.igame.work.activity;

import com.igame.core.ISFSModule;
import com.igame.core.di.Inject;
import com.igame.core.event.EventService;
import com.igame.core.event.EventType;
import com.igame.core.event.PlayerEventObserver;
import com.igame.core.log.ExceptionLog;
import com.igame.work.PlayerEvents;
import com.igame.work.activity.condition.Conditions;
import com.igame.work.activity.denglu.DengluService;
import com.igame.work.gm.service.GMService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * activity_type配置1的时候time_limit可能是一个正数 也可能是-1
 * 配置2的时候time_limit只允许配置为正数
 * <br/>
 * gift_bag配置为3的时候 活动一定是个登录几天 每天对应不同奖励的活动
 * <br/>
 * start_time 配置是2的时候后面会跟一个时间戳 如果没有就当做配置错误
 * <br/>
 * gift_bag 配置为2的时候 才会用到get_limit和get_value
 * <br/>
 * start_limit和value字段会删掉提取到单独的配置
 * <br/>
 * 神秘商人的配置提取到单独的配置
 * <br/>
 * buy_price和sale_picture服务器用不到
 * <br/>
 * 所有活动记录一下配置的开始时间戳，如果时间戳有变化就当做是新活动
 */
public class ActivityService extends EventService implements ISFSModule {
    /**
     * 大部分活动共用的配置
     */
    public static ActivityConfig activityConfig;

    @Inject public ResourceService resourceService;
    @Inject private DengluService dengluService;
    @Inject private GMService gMService;
    @Inject private ActivityDAO dao;

    Map<Integer, ActivityDto> dtos=new ConcurrentHashMap<>();
    @Override
    public void init() {
        super.init();
        // todo 手动关闭指定活动 活动领奖状态判断是否关闭
        Map<Integer, List<ActivityConfigTemplate>> collect = ActivityConfig.its.stream()
                .collect(Collectors.groupingBy(ActivityConfigTemplate::getActivity_sign));
        collect.forEach((key, value) -> {
            ActivityDto dto = dao.getActivityById(key);
            if (dto == null) {
                dto = new ActivityDto();
                dto.setActivityId(key);
            }
            dtos.put(key, dto);
        });

        // 配置中没找到 认为是活动下线 把数据清掉
        for (int activityId : new HashSet<>(dtos.keySet())) {
            if (!collect.containsKey(activityId)) {
                try {
                    dao.remove(dtos.remove(activityId));
                } catch (Exception e) {
                    extensionHolder.SFSExtension.getLogger().warn("",e);
                }
            }
        }
    }

    @Override
    public void destroy() {
        dtos.values().forEach(dto->dao.saveActivity(dto));
    }

    @Override
    protected PlayerEventObserver playerObserver() {
        return new PlayerEventObserver() {

            private PlayerEvents[] playerEvents;

            @Override
            public EventType[] interestedTypes() {
                playerEvents = new PlayerEvents[]{PlayerEvents.LEVEL_UP,PlayerEvents.GOD_LEVEL_UP
                        , PlayerEvents.CONSUME_GOLD
                        , PlayerEvents.CONSUME_DIAMOND
                        , PlayerEvents.RECHARGE
                , PlayerEvents.GOT_MONSTER
                , PlayerEvents.DRAW_BY_DIAMOND};
                return playerEvents;
            }

            @Override
            public void observe(Player player, EventType eventType, Object event) {
                Map<Integer, List<ActivityConfigTemplate>> collect = ActivityConfig.its.stream()
                        .collect(Collectors.groupingBy(ActivityConfigTemplate::getActivity_sign));
                collect.forEach((key, value) -> {

                    ActivityDto activityData = dtos.get(key);
                    if (activityData == null) {
                        activityData = new ActivityDto();
                        activityData.setActivityId(key);
                    }

                    ActivityOrderDto orderData = activityData.getOrderData().computeIfAbsent(player.getPlayerId(), playerId -> {
                        ActivityOrderDto orderData1 = new ActivityOrderDto();
                        orderData1.state = new int[value.size()];
                        return orderData1;
                    });

                    ActivityDto d = activityData;
                    value.forEach(template -> {

                        if (orderData.state[template.getOrder() - 1] == 2) {    // 已经领取奖励
                            return;
                        }

                        Arrays.stream(Conditions.values())
                                .filter(c->c.intrestEvent()==eventType)
                                .peek(c->dao.updatePlayer(d, player.getPlayerId(),orderData))
                                .forEach(c->c.onEvent(player, template, orderData, event));

                    });
                });
            }
        };
    }

    private int[] initPlayerActivityData(Player player, List<ActivityConfigTemplate> value) {
        int[] data = new int[value.size()];
        Date now = new Date();
        for (ActivityConfigTemplate template : value) {
            int state = 0;
            try {
                state = template.isActive(player, now)?Arrays.stream(Conditions.values())
                        .filter(c -> c.type() == template.getGet_limit())
                        .map(c -> c.initState(player, template))
                        .findAny().orElse(0):0;
            } catch (Exception e) {
                ExceptionLog.error("初始化玩家活动数据错误", e);
            }
            data[template.getOrder()-1] = state;
        }

        return data;
    }

    // 新的登录活动开始后 删除上轮的数据
    // id相同id时间有修改的话 认为是新一轮活动 把上一轮活动数据清掉
    // todo 修改数据库以活动为主数据后 统一在加载数据的地方处理
//        byPlayer.values().stream()
//                .filter(a -> configs.containsKey(a.getActivityId()))
//                .filter(a -> configs.get(a.getActivityId()).stream()
//                        .anyMatch(c ->
//                                    !String.valueOf(c.startTime(player).getTime()).equals(a.getOpenTime())))
//                .peek(a->
//                        configs.get(a.getActivityId()).stream().findAny()
//                                .ifPresent(c->{
//                                    if(a.getOpenTime()==null || c.startTime(player).getTime()>Long.parseLong(a.getOpenTime())) {
//                                        a.setOpenTime(String.valueOf(c.startTime(player).getTime()));
//                                    }
//                                }))
//                .forEach(a -> a.setRecord(new int[configs.get(a.getActivityId()).size()]));

    /**
     * 因为数据库里存的数据跟客户端协议的格式不一样，这里做下转换
     */
    public Map<Integer, Object> clientData(Player player) {
        Map<Integer, Object> map = new HashMap<>();  // todo string->int

        Date now = new Date();
        Map<Integer, List<ActivityConfigTemplate>> collect = ActivityConfig.its.stream()
                .filter(c->c.getGift_bag()==2||c.getGift_bag()==3)
                .filter(c->c.isActive(player, now))
                .collect(Collectors.groupingBy(ActivityConfigTemplate::getActivity_sign));
        collect.forEach((activityId, configs) -> {
            ActivityDto activityData = dtos.get(activityId);
            if (activityData.getOrderData() == null) {
                activityData.setOrderData(new HashMap<>());
            }
            ActivityOrderDto activityOrderDto = activityData.getOrderData()
                    .computeIfAbsent(player.getPlayerId(), playerId -> new ActivityOrderDto());

            // 只保证state的初始化，record留给活动逻辑单独处理初始化
            int[] orderData = activityOrderDto.state;
            if (activityOrderDto.state == null) {
                activityOrderDto.state = orderData = initPlayerActivityData(player, configs);
            } else if (orderData.length != configs.size()) {
                int[] newOrderData = new int[configs.size()];
                System.arraycopy(orderData, 0, newOrderData, 0, Math.min(newOrderData.length, orderData.length));
                orderData = newOrderData;
            }

            dengluService.loadPlayer(player, activityOrderDto, configs);	// todo event

            map.put(activityId, join(orderData));
        });
        return map;
    }

    String join(int[] array) {  // todo move to util
        return Arrays.stream(array).mapToObj(String::valueOf).collect(Collectors.joining(","));
    }

    public String reward(Player player,int activityId, int order) {
        Date now = new Date();
        StringBuilder ret = new StringBuilder();
        ActivityConfig.its.stream()
                .filter(c -> c.getActivity_sign() == activityId && c.getOrder() == order)
                .forEach(c->{
                    ActivityDto activityData = dtos.get(activityId);
                    if (activityData == null) {
                        return;
                    }
                    ActivityOrderDto orderData = activityData.getOrderData().get(player.getPlayerId());
                    if (!(c.getGift_bag()==2&&c.getGet_limit()==2) && orderData.state[order-1]!=1) {  // todo 每日两发消耗钻石跳过这里
                        return;
                    }
                    if (!c.isActive(player, now)) {
                        return;
                    }
                    boolean canReceive;
                    if (c.getGift_bag() == 3) {
                        canReceive = dengluService.checkReward(player,c,orderData)==0;
                    } else {
                        canReceive = Arrays.stream(Conditions.values())
                                    .filter(e->e.type()==c.getGet_limit())
                                    .anyMatch(e->e.checkReward(player,c,orderData, this)==0);
                    }

                    if (canReceive) {
                        orderData.state[c.getOrder()-1]=2;
                        dao.updatePlayer(activityData, player.getPlayerId(), orderData);
                        gMService.processGM(player, c.getActivity_drop());
                    }
                    ret.append(join(orderData.state));
                });
        return ret.toString();
    }

}
