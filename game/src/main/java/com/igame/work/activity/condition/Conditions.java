package com.igame.work.activity.condition;

import com.igame.core.event.EventType;
import com.igame.util.DateUtil;
import com.igame.work.ErrorCode;
import com.igame.work.PlayerEvents;
import com.igame.work.activity.ActivityConfigTemplate;
import com.igame.work.activity.ActivityOrderDto;
import com.igame.work.activity.ActivityService;
import com.igame.work.user.dto.Player;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 0无条件
 * 1玩家等级
 * 2时间段领取
 * 3钻石消费
 * 4金币消费
 * 5幸运转盘次数
 * 6竞技场胜利次数
 * 7竞技场战斗次数
 * 8高级抽卡次数
 * 9获得怪物ID
 * 10神灵等阶达成X级
 * 11钻石充值
 */
public enum Conditions {
    NO_LIMIT(0){
        @Override
        public void onEvent(Player player, ActivityConfigTemplate template, ActivityOrderDto orderData, Object event) {
            // do nothing
        }

        @Override
        public int initState(Player player, ActivityConfigTemplate c) {
            return 1;
        }

        @Override
        public EventType intrestEvent() {
            return null;
        }

        @Override
        public int checkReward(Player player, ActivityConfigTemplate c, ActivityOrderDto orderData, ActivityService activityService) {
            return orderData.state[c.getOrder()-1]!=2?0:ErrorCode.CAN_NOT_RECEIVE;
        }
    }
    , LEVEL(1){
        @Override
        public void onEvent(Player player, ActivityConfigTemplate template, ActivityOrderDto orderData, Object event) {

            Object[] levelInfo = (Object[]) event;
            if (Integer.parseInt(String.valueOf(levelInfo[1]))>=Integer.parseInt(template.getGet_value()))
                orderData.state[template.getOrder()-1] = 1;
        }

        @Override
        public int initState(Player player, ActivityConfigTemplate c) {
            return player.getPlayerLevel()>=Integer.valueOf(c.getGet_value())?1:0;
        }

        @Override
        public EventType intrestEvent() {
            return PlayerEvents.LEVEL_UP;
        }

        @Override
        public int checkReward(Player player, ActivityConfigTemplate c, ActivityOrderDto orderData, ActivityService activityService) {
            return player.getPlayerLevel()>=Integer.valueOf(c.getGet_value())?0: ErrorCode.CAN_NOT_RECEIVE;
        }
    }
    , TIME_INTERVAL(2){
        @Override
        public void onEvent(Player player, ActivityConfigTemplate template, ActivityOrderDto orderData, Object event) {
            // do nothing
        }

        @Override
        public EventType intrestEvent() {
            return null;
        }

        @Override
        public int checkReward(Player player, ActivityConfigTemplate c, ActivityOrderDto orderData, ActivityService activityService) {

            String configTime = c.getGet_value();
            String[] splitTime = configTime.split(",");
            if(splitTime.length<2){return 1;}
            int begin = Integer.parseInt(splitTime[0]);
            int end = Integer.parseInt(splitTime[1]);
            int current = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

            if (orderData.recrod == null) {
                orderData.recrod = new HashMap<>();
            }
            String splitRecord = (String) orderData.recrod.get(c.getOrder());
            String today = DateUtil.formatToday();
            if (today.equals(splitRecord)) { // 今天已经领过
                return ErrorCode.PACK_PURCHASED;
            }
            if(current<begin||current>end) {    // 不在配置时间内当做补领
                activityService.resourceService.addDiamond(player, -20);
            }
            orderData.recrod.put(c.getOrder(), today);
            orderData.state[c.getOrder()-1] = 2;
            return 0;
        }
    }
    , CONSUME_DIAMOND(3) {
        @Override
        public void onEvent(Player player, ActivityConfigTemplate template, ActivityOrderDto orderData, Object event) {

            Object[] eventInfo = (Object[]) event;
            long eventMillis = (long) eventInfo[0];
            int amount = (int) eventInfo[1];
            Map<Long, Integer> records = (Map<Long, Integer>) orderData.recrod.get(template.getOrder());
            records.put(eventMillis, amount);
            // todo 这里更新state还是1003协议toClientData的时候更新
        }

        @Override
        public EventType intrestEvent() {
            return PlayerEvents.CONSUME_DIAMOND;
        }
    }, CONSUME_GOLD(4){
        @Override
        public void onEvent(Player player, ActivityConfigTemplate template, ActivityOrderDto orderData, Object event) {
            // todo
        }

        @Override
        public EventType intrestEvent() {
            return PlayerEvents.CONSUME_GOLD;
        }

        @Override
        public int checkReward(Player player, ActivityConfigTemplate c, ActivityOrderDto orderData, ActivityService activityService) {
            return super.checkReward(player, c, orderData, activityService);// todo
        }
    }
    , TURNTABLE_TIMES(5){
        @Override
        public EventType intrestEvent() {
            return PlayerEvents.TURN_TABLE;
        }

        @Override
        public void onEvent(Player player, ActivityConfigTemplate template, ActivityOrderDto orderData, Object event) {
            int times = (int) orderData.recrod.get(template.getOrder());
            orderData.recrod.put(template.getOrder(), times + 1);
            if (times + 1 > Integer.parseInt(template.getGet_value())) {
                orderData.state[template.getOrder()-1] = 1;
            }
        }

        @Override
        public int checkReward(Player player, ActivityConfigTemplate c, ActivityOrderDto orderData, ActivityService activityService) {
            return orderData.state[c.getOrder()-1]==1?0:1;
        }
    }
    , ARENA_WIN_TIMES(6){
        @Override
        public EventType intrestEvent() {
            return null;// todo
        }

        @Override
        public void onEvent(Player player, ActivityConfigTemplate template, ActivityOrderDto orderData, Object event) {
            // todo
        }

        @Override
        public int checkReward(Player player, ActivityConfigTemplate c, ActivityOrderDto orderData, ActivityService activityService) {
            return super.checkReward(player, c, orderData, activityService);//todo
        }
    }
    , ARENA_TIMES(7){
        @Override
        public EventType intrestEvent() {
            return null;//todo
        }

        @Override
        public void onEvent(Player player, ActivityConfigTemplate template, ActivityOrderDto orderData, Object event) {
            // todo
        }

        @Override
        public int checkReward(Player player, ActivityConfigTemplate c, ActivityOrderDto orderData, ActivityService activityService) {
            return super.checkReward(player, c, orderData, activityService);// todo
        }
    }
    , ADVANCE_CARD(8){
        @Override
        public void onEvent(Player player, ActivityConfigTemplate template, ActivityOrderDto orderData, Object event) {

            int times = (int) orderData.recrod.get(template.getOrder());
            orderData.recrod.put(template.getOrder(), times + 1);
            if (times + 1>=Integer.parseInt(template.getGet_value()))
                orderData.state[template.getOrder()-1] = 1;
        }

        @Override
        public int checkReward(Player player, ActivityConfigTemplate c, ActivityOrderDto orderData, ActivityService activityService) {
            return orderData.state[c.getOrder()-1]!=2 && (int)orderData.recrod.get(c.getOrder())>=c.getGet_limit()?0:ErrorCode.CAN_NOT_RECEIVE;
        }

        @Override
        public EventType intrestEvent() {
            return PlayerEvents.DRAW_BY_DIAMOND;
        }
    }
    , GOT_MONSTER(9){
        @Override
        public void onEvent(Player player, ActivityConfigTemplate template, ActivityOrderDto orderData, Object event) {

            int monsterId = (int) event;
            if (monsterId==Integer.parseInt(template.getGet_value()))
                orderData.state[template.getOrder()-1] = 1;
        }

        @Override
        public int checkReward(Player player, ActivityConfigTemplate c, ActivityOrderDto orderData, ActivityService activityService) {
            if (orderData.state[c.getOrder()-1]!=2
                    && orderData.recrod.containsKey(c.getOrder())
                    && orderData.recrod.get(c.getOrder()).equals(c.getGet_limit())) {
                return 0;
            }
            return ErrorCode.CAN_NOT_RECEIVE;
        }

        @Override
        public EventType intrestEvent() {
            return PlayerEvents.GOT_MONSTER;
        }
    }
    , GOLD_LEVEL(10){
        @Override
        public EventType intrestEvent() {
            return PlayerEvents.GOD_LEVEL_UP;
        }

        @Override
        public void onEvent(Player player, ActivityConfigTemplate template, ActivityOrderDto orderData, Object event) {
            // todo
        }

        @Override
        public int initState(Player player, ActivityConfigTemplate c) {
            if (player.getTeams().values().stream().anyMatch(team -> player.getGods().get(team.getTeamGod()).getGodsLevel() >= Integer.valueOf(c.getGet_value()))) {
                return 1;
            }
            return 0;
        }

        @Override
        public int checkReward(Player player, ActivityConfigTemplate c, ActivityOrderDto orderData, ActivityService activityService) {
           return player.getTeams().values().stream()
                   .anyMatch(team -> player.getGods().get(team.getTeamGod()).getGodsLevel() >= Integer.valueOf(c.getGet_value()))
                   ?0:ErrorCode.CAN_NOT_RECEIVE;
        }
    }
    , RECHARGE_DIAMOND(11){
        @Override
        public EventType intrestEvent() {
            return PlayerEvents.RECHARGE;
        }

        @Override
        public int checkReward(Player player, ActivityConfigTemplate c, ActivityOrderDto orderData, ActivityService activityService) {
            return super.checkReward(player, c, orderData, activityService);// todo
        }

        @Override
        public void onEvent(Player player, ActivityConfigTemplate template, ActivityOrderDto orderData, Object event) {
            Object[] rechargeParam = (Object[])event;
            long rechargeMillis = (long) rechargeParam[0];
            int amount = (int) rechargeParam[1];
            if (!template.isActive(player, new Date())) {
                return;
            }
            Map<Long, Integer> records = (Map<Long, Integer>) orderData.recrod.computeIfAbsent(template.getOrder(), order -> new HashMap<>());
            records.put(rechargeMillis, amount);
            int sum = records.values().stream().mapToInt(i -> i).sum();
            if (sum > Integer.parseInt(template.getGet_value())) {
                orderData.state[template.getOrder()-1] = 1;
            }
        }
    };
    int conditionType;
    Conditions(int conditionType) {
        this.conditionType = conditionType;
    }
    public int type() {
        return conditionType;
    }

    /**
     * @return errorCode
     */
    public int checkReward(Player player, ActivityConfigTemplate c, ActivityOrderDto orderData, ActivityService activityService) {return ErrorCode.ERROR;}

    public int initState(Player player, ActivityConfigTemplate c) {
        return 0;
    }

    public abstract EventType intrestEvent();

    public abstract void onEvent(Player player, ActivityConfigTemplate template, ActivityOrderDto orderData, Object event);
}
