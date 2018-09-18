package com.igame.work.activity.denglu;

import com.igame.util.DateUtil;
import com.igame.work.ErrorCode;
import com.igame.work.activity.ActivityConfigTemplate;
import com.igame.work.activity.ActivityOrderDto;
import com.igame.work.user.dto.Player;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DengluService {

    public void loadPlayer(Player player, ActivityOrderDto activityOrderDto, Collection<ActivityConfigTemplate> configs) {
        if (activityOrderDto.recrod == null) {
            activityOrderDto.recrod = new HashMap<>();
        }
        Map<Integer, Object> recrod = activityOrderDto.recrod;

        Date now = new Date();
        configs.forEach(c->{
            if (c.isActive(player, now)
                    && c.getOrder() - 1 <= DateUtil.getIntervalDays(c.startTime(player), now)
                    && activityOrderDto.state[c.getOrder()-1]!=2) {
                activityOrderDto.state[c.getOrder()-1] = 1;
                recrod.put(c.getOrder(), System.currentTimeMillis());
            }
        });

    }

    /**
     * @return errorCode
     */
    public int checkReward(Player player, ActivityConfigTemplate c,ActivityOrderDto orderData) {

        int order = c.getOrder();
        if (!c.isActive(player, new Date())) {
            return ErrorCode.CAN_NOT_RECEIVE;
        }

        if (orderData.state[order - 1]!=1) {
            return ErrorCode.PACK_PURCHASED;
        }
        orderData.state[order - 1] = 2;

        return 0;
    }
}
