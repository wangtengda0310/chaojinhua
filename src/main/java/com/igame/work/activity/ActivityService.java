package com.igame.work.activity;

import com.igame.core.ISFSModule;
import com.igame.core.di.Inject;
import com.igame.core.event.EventService;
import com.igame.core.event.EventType;
import com.igame.core.event.PlayerEventObserver;
import com.igame.core.log.ExceptionLog;
import com.igame.util.DateUtil;
import com.igame.work.PlayerEvents;
import com.igame.work.activity.condition.Conditions;
import com.igame.work.activity.denglu.DengluService;
import com.igame.work.gm.service.GMService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.igame.work.activity.condition.Conditions.*;

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
    @Inject public ResourceService resourceService;
    @Inject private DengluService dengluService;
    @Inject private GMService gMService;
    @Inject private ActivityDAO dao;

    private Map<Integer, ActivityDto> dtos=new ConcurrentHashMap<>();
    @Override
    public void init() {
        // todo 手动关闭指定活动 活动领奖状态判断是否关闭
        Map<Integer, List<ActivityConfigTemplate>> collect = ActivityConfig.its.stream()
                .collect(Collectors.groupingBy(ActivityConfigTemplate::getActivity_sign));
        collect.forEach((key, value) -> {
            ActivityDto dto = dao.getActivityById(key);
            if (dto == null) {
                dto = new ActivityDto();
            }
            dtos.put(key, dto);
        });

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
                playerEvents = new PlayerEvents[]{PlayerEvents.LEVEL_UP
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
                    }
                    ActivityOrderDto orderData = activityData.getOrderData().computeIfAbsent(player.getPlayerId(), playerId -> {
                        ActivityOrderDto orderData1 = new ActivityOrderDto();
                        orderData1.state = new int[value.size()];
                        return orderData1;
                    });
                    value.forEach(template -> {

                        if (orderData.state[template.getOrder() - 1] == 2) {    // 已经领取奖励
                            return;
                        }

                        if (PlayerEvents.LEVEL_UP == eventType) {
                            int[] levelInfo = (int[]) event;
                            if (levelInfo[1]>=Integer.parseInt(template.getGet_value()))
                                orderData.state[template.getOrder()-1] = 1;
                        }
                        if (PlayerEvents.CONSUME_DIAMOND == eventType) {
                            Object[] eventInfo = (Object[]) event;
                            long eventMillis = (long) eventInfo[0];
                            int amount = (int) eventInfo[1];
                            Map<Long, Integer> records = (Map<Long, Integer>) orderData.recrod.get(template.getOrder());
                            records.put(eventMillis, amount);
                            // todo 这里更新state还是1003协议toClientData的时候更新
                        }
                        if (PlayerEvents.GOT_MONSTER == eventType) {
                            int monsterId = (int) event;
                            if (monsterId==Integer.parseInt(template.getGet_value()))
                                orderData.state[template.getOrder()-1] = 1;
                        }
                        if (PlayerEvents.DRAW_BY_DIAMOND == eventType) {
                            int times = (int) orderData.recrod.get(template.getOrder());
                            orderData.recrod.put(template.getOrder(), times + 1);
                            if (times + 1>=Integer.parseInt(template.getGet_value()))
                                orderData.state[template.getOrder()-1] = 1;
                        }
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
                if (!template.isActive(player, now)) {
                    state = 0;  // todo 活动过期是否新定义一个状态值
                } else if (template.getGet_limit() == NO_LIMIT.type()) {
                    state = 1;
                } else if (template.getGet_limit() == LEVEL.type()) {
                    state = player.getPlayerLevel()>=Integer.valueOf(template.getGet_value())?1:0;
                } else if (template.getGet_limit() == TIME_INTERVAL.type()) {
                    // todo 每日两发做在这
                } else if (template.getGet_limit() == CONSUME_DIAMOND.type()) {
                    // todo 记录活动期间内的钻石消耗
                } else if (template.getGet_limit() == CONSUME_GOLD.type()) {

                } else if (template.getGet_limit() == TURNTABLE_TIMES.type()) {

                } else if (template.getGet_limit() == ARENA_WIN_TIMES.type()) {

                } else if (template.getGet_limit() == ARENA_TIMES.type()) {

                } else if (template.getGet_limit() == ADVANCE_CARD.type()) {
                    // todo 2057 event
                } else if (template.getGet_limit() == GOT_MONSTER.type()) {
                    // todo monster event
                } else if (template.getGet_limit() == GOLD_LEVEL.type()) {
                    if (player.getTeams().values().stream().anyMatch(team -> player.getGods().get(team.getTeamGod()).getGodsLevel() >= Integer.valueOf(template.getGet_value()))) {
                        state = 1;
                    }
                } else if (template.getGet_limit() == RECHARGE_DIAMOND.type()) {
                    // todo 终极馈赠
                }
            } catch (Exception e) {
                ExceptionLog.error("初始化玩家活动数据错误", e);
            }
            data[template.getOrder()-1] = state;
        }

        return data;
    }

    /**
     * 因为数据库里存的数据跟客户端协议的格式不一样，这里做下转换
     */
    public Map<String, Object> clientData(Player player) {
        Map<String, Object> map = new HashMap<>();  // todo string->int

        String totalSign = player.getSign().getTotalSign();
        String[] signDataSplit = player.getSign().getSignData().split(",");

        Map<String, Object> signData = new HashMap<>();

        signData.put("round", Integer.parseInt(signDataSplit[0]));
        signData.put("signed", Integer.parseInt(signDataSplit[1]));
        signData.put("totalSign", totalSign);
        signData.put("canSign", DateUtil.formatToday().equals(signDataSplit[2]) ? 0 : 1);
        map.put("sign", signData);

        map.put("denglu", dengluService.clientData(player));

//        map.put("tansuoZhiLu", activityData.getTansuo().clientData(player));
//        map.put("meiriLiangfa", activityData.getTansuo().clientData(player));

        Map<Integer, List<ActivityConfigTemplate>> collect = ActivityConfig.its.stream()
                .collect(Collectors.groupingBy(ActivityConfigTemplate::getActivity_sign));
        collect.entrySet().stream()
                .forEach(entry->{
                    ActivityDto activityData = dtos.get(entry.getKey());
                    if (activityData.getOrderData()==null) {
                        activityData.setOrderData(new HashMap<>());
                    }
                    ActivityOrderDto activityOrderDto = activityData.getOrderData()
                            .computeIfAbsent(player.getPlayerId(), playerId -> new ActivityOrderDto());

                    int[] orderData = activityOrderDto.state;
                    if (activityOrderDto.state==null) {
                        activityOrderDto.state=orderData=initPlayerActivityData(player, entry.getValue());
                    } else if(orderData.length!=entry.getValue().size()) {
                        int[] newOrderData = new int[entry.getValue().size()];
                        System.arraycopy(orderData, 0,newOrderData,0,Math.min(newOrderData.length, orderData.length));
                        orderData = newOrderData;
                    }

                    map.put(String.valueOf(entry.getKey()), join(orderData));
                    if (entry.getKey() == 1004) {   // todo remove after
                        map.put("meiriLiangfa", join(orderData));
                    }
                    if (entry.getKey() == 1005) {   // todo remove after
                        map.put("tansuoZhiLu", join(orderData));
                    }
                });
        return map;
    }

    private String join(int[] array) {  // todo move to util
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
                    if (!(c.getGift_bag()==2&&c.getGet_limit()==2) && orderData.state[order-1]!=1) {  // todo 每日两发消耗钻石不该判断这里
                        return;
                    }
                    if (!c.isActive(player, now)) {
                        return;
                    }
                    boolean canReceive;
                    if (c.getGift_bag() == 3) {
                        canReceive = dengluService.reward(player,c)==0;
                    } else {
                        canReceive = Arrays.stream(Conditions.values())
                                    .filter(e->e.type()==c.getGet_limit())
                                    .anyMatch(e->e.reward(player,c,orderData, this)==0);
                    }

                    if (canReceive) {
                        orderData.state[c.getOrder()-1]=2;
                        gMService.processGM(player, c.getActivity_drop());
                    }
                    ret.append(join(orderData.state));
                });
        return ret.toString();
    }

}
