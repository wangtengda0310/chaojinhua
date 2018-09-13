package com.igame.work.activity.condition;

import com.igame.util.DateUtil;
import com.igame.work.ErrorCode;
import com.igame.work.activity.ActivityConfigTemplate;
import com.igame.work.activity.ActivityOrderDto;
import com.igame.work.activity.ActivityService;
import com.igame.work.user.dto.Player;

import java.util.Calendar;
import java.util.HashMap;

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
    NO_LIMIT(0)
    , LEVEL(1){
        @Override
        public int reward(Player player, ActivityConfigTemplate c, ActivityOrderDto orderData, ActivityService activityService) {
            return player.getPlayerLevel()>=Integer.valueOf(c.getGet_value())?0: ErrorCode.CAN_NOT_RECEIVE;
        }
    }
    , TIME_INTERVAL(2){
        @Override
        public int reward(Player player, ActivityConfigTemplate c, ActivityOrderDto orderData, ActivityService activityService) {

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
    , CONSUME_DIAMOND(3)
    , CONSUME_GOLD(4)
    , TURNTABLE_TIMES(5)
    , ARENA_WIN_TIMES(6)
    , ARENA_TIMES(7)
    , ADVANCE_CARD(8)
    , GOT_MONSTER(9)
    , GOLD_LEVEL(10){
        @Override
        public int reward(Player player, ActivityConfigTemplate c, ActivityOrderDto orderData, ActivityService activityService) {
           return player.getTeams().values().stream()
                   .anyMatch(team -> player.getGods().get(team.getTeamGod()).getGodsLevel() >= Integer.valueOf(c.getGet_value()))
                   ?0:ErrorCode.CAN_NOT_RECEIVE;
        }
    }
    , RECHARGE_DIAMOND(11);
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
    public int reward(Player player, ActivityConfigTemplate c, ActivityOrderDto orderData, ActivityService activityService) {return ErrorCode.ERROR;}
}
