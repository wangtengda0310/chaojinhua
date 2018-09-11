package com.igame.work.activity;

import com.igame.core.ISFSModule;
import com.igame.core.di.Inject;
import com.igame.util.DateUtil;
import com.igame.work.activity.denglu.DengluService;
import com.igame.work.activity.meiriLiangfa.MeiriLiangfaData;
import com.igame.work.activity.sign.SignConfigTemplate;
import com.igame.work.activity.sign.SignData;
import com.igame.work.activity.tansuoZhiLu.TanSuoZhiLuActivityData;
import com.igame.work.gm.service.GMService;
import com.igame.work.user.dto.Player;

import java.util.HashMap;
import java.util.Map;

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
public class ActivityService implements ISFSModule {
    @Inject private DengluService dengluService;
    @Inject private GMService gMService;

    public void loadPlayer(Player player) {
        if (player.getActivityData() == null) {
            player.setActivityData(new PlayerActivityData());
        }
        if (player.getActivityData().getSign() == null) {
            player.getActivityData().setSign(new SignData());
        }

        String[] signDataSplit = player.getActivityData().getSign().getSignData().split(",");
        int round = Integer.parseInt(signDataSplit[0]);
        int signedDays = Integer.parseInt(signDataSplit[1]);
        String lastSignDate = signDataSplit[2];

        if (!DateUtil.formatToday().equals(lastSignDate)) {
            // 昨天已经签满上个周期，今天应该给客户端的数据是新的周期签到0天，今天刚签满这个周期则显示全部签到，累签数据保留到玩家签到操作为止
            SignConfigTemplate template = ActivityDataManager.signConfig.getTemplate(round);
            String[] split = template.getRewardData().split(";");
            if (signedDays >= split.length) {
                signedDays = 0;
                round += 1;
                if (round > ActivityDataManager.signConfig.size()) {
                    round = 1;
                }
                player.getActivityData().getSign().setSignData(round + "," + signedDays + "," + lastSignDate);
            }
        }

        if (player.getActivityData().getMeiriLiangfa() == null) {
            player.getActivityData().setMeiriLiangfa(new MeiriLiangfaData());
        }

        if (player.getActivityData().getTansuo() == null) {
            player.getActivityData().setTansuo(new TanSuoZhiLuActivityData());
        }

        // 七天登录活动
        dengluService.loadPlayer(player);
    }

    /**
     * 因为数据库里存的数据跟客户端协议的格式不一样，这里做下转换
     */
    public Map<String, Object> clientData(Player player) {
        Map<String, Object> map = new HashMap<>();

        String totalSign = player.getActivityData().getSign().getTotalSign();
        String[] signDataSplit = player.getActivityData().getSign().getSignData().split(",");

        Map<String, Object> signData = new HashMap<>();

        signData.put("round", Integer.parseInt(signDataSplit[0]));
        signData.put("signed", Integer.parseInt(signDataSplit[1]));
        signData.put("totalSign", totalSign);
        signData.put("canSign", DateUtil.formatToday().equals(signDataSplit[2]) ? 0 : 1);
        map.put("sign", signData);

        map.put("meiriLiangfa", player.getActivityData().getMeiriLiangfa().clientData());

        map.put("tansuoZhiLu", player.getActivityData().getTansuo().clientData(player));

        map.put("denglu", dengluService.clientData(player));
        return map;
    }

    /**
     * 今天签到
     */
    public Map<String, Object> signToday(Player player) {
        SignData playerSignData = player.getActivityData().getSign();
        String today = DateUtil.formatToday();
        String signData = playerSignData.getSignData();
        if (signData == null || "".equals(signData)) {
            playerSignData.setSignData("1,1," + today);
            SignConfigTemplate template = ActivityDataManager.signConfig.getTemplate(1);
            String reward = template.getRewardData().split(";")[0];
            gMService.processGM(player, reward);

            Map<String, Object> map = new HashMap<>();
            map.put("canSign", 0);
            map.put("totalSign", playerSignData.getTotalSign());
            return map;
        } else {
            String[] data = signData.split(",");
            if (today.equals(data[2])) {
                return null;
            } else {
                // 跨周期重置数据测造作在登录时已经完成
                int round = Integer.parseInt(data[0]);
                int signed = Integer.parseInt(data[1]);

                SignConfigTemplate template = ActivityDataManager.signConfig.getTemplate(round);
                String[] split = template.getRewardData().split(";");
                String reward = split[signed];

                signed += 1;

                playerSignData.setSignData(round + "," + signed + "," + today);
                if (signed == 1) {
                    playerSignData.setTotalSign("0,0,0,0");
                }

                gMService.processGM(player, reward);

                Map<String, Object> map = new HashMap<>();
                map.put("cansign", 0);
                map.put("totalSign", playerSignData.getTotalSign());
                return map;
            }
        }
    }

    /**
     * 累签
     */

    public Map<String, Object> signTotal(Player player, int index) {
        if (index < 1 || index > 4) {  // 1 3天累签， 2 7天累签， 3 15天累签， 4 30天累签
            return null;
        }

        SignData playerSignData = player.getActivityData().getSign();
        String signData = playerSignData.getSignData();

        if (signData == null || "".equals(signData)) {
            return null;
        }
        String[] data = signData.split(",");
        int signedDays = Integer.parseInt(data[1]);
        if ((index == 4 && signedDays < 30) || (index == 3 && signedDays < 15) || (index == 2 && signedDays < 7) || (index == 1 && signedDays < 3)) {
            return null;
        }

        String totalSign = playerSignData.getTotalSign();

        if (totalSign == null || totalSign.split(",").length == 0) {
            playerSignData.setTotalSign("0,0,0,0");
        }

        String[] totalSigns = playerSignData.getTotalSign().split(",");
        totalSigns[index - 1] = "1";
        playerSignData.setTotalSign(totalSign = String.join(",", totalSigns));

        SignConfigTemplate template = ActivityDataManager.signConfig.getTemplate(1);
        if (index == 1) {
            gMService.processGM(player, template.getTotalSign3());
        }
        if (index == 2) {
            gMService.processGM(player, template.getTotalSign7());
        }
        if (index == 3) {
            gMService.processGM(player, template.getTotalSign15());
        }
        if (index == 4) {
            gMService.processGM(player, template.getTotalSign30());
        }
        Map<String, Object> map = new HashMap<>();
        map.put("round", Integer.parseInt(data[0]));
        map.put("signed", Integer.parseInt(data[1]));
        map.put("totalSign", totalSign);
        return map;
    }
}
