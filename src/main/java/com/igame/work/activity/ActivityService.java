package com.igame.work.activity;

import com.igame.core.ISFSModule;
import com.igame.util.DateUtil;
import com.igame.work.activity.QitianDenglu.QitianDengluService;
import com.igame.work.activity.meiriLiangfa.MeiriLiangfaData;
import com.igame.work.activity.sign.SignConfigTemplate;
import com.igame.work.activity.sign.SignData;
import com.igame.work.activity.tansuoZhiLu.TanSuoZhiLuActivityData;
import com.igame.work.gm.service.GMService;
import com.igame.work.user.dto.Player;

import java.util.HashMap;
import java.util.Map;

public class ActivityService implements ISFSModule {
    public static void loadPlayer(Player player) {
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

        // 七天活动 发邮件
        QitianDengluService.loadPlayer(player);
    }

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
            GMService.processGM(player, reward);

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

                GMService.processGM(player, reward);

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
            GMService.processGM(player, template.getTotalSign3());
        }
        if (index == 2) {
            GMService.processGM(player, template.getTotalSign7());
        }
        if (index == 3) {
            GMService.processGM(player, template.getTotalSign15());
        }
        if (index == 4) {
            GMService.processGM(player, template.getTotalSign30());
        }
        Map<String, Object> map = new HashMap<>();
        map.put("round", Integer.parseInt(data[0]));
        map.put("signed", Integer.parseInt(data[1]));
        map.put("totalSign", totalSign);
        return map;
    }
    @Override
    public void init() {

    }
}
