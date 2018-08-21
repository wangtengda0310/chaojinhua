package com.igame.work.activity.sign;

import com.igame.core.data.DataManager;
import com.igame.util.DateUtil;
import com.igame.work.activity.Activities;
import com.igame.work.gm.service.GMService;
import com.igame.work.user.dto.Player;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Transient;

import java.util.Arrays;


@Entity(noClassnameStored = true)
public class SignData implements Activities {
    private String signData;
    private String totalSign;

    @Transient
    private Player player;
    public SignData(Player player) {
        this.player = player;
    }

    public int getType() {
        return 1;
    }

    @Override
    public JSONObject toClientData() {
        String[] split = signData.split(",");
        int round = Integer.parseInt(split[0]);
        int signDays = Integer.parseInt(split[1]);
        SignConfigTemplate template = DataManager.signConfig.getTemplate(round + 1);
        JSONArray array = new JSONArray();
        int index = 0;
        for(String reward :template.getRewardData().split(";")) {
            JSONObject object = new JSONObject();
            object.put("reward",reward);
            if(index++ <= signDays) {
                object.put("signed",true);
            }
            array.add(object);
        }
        JSONObject object = new JSONObject();
        object.put("type", 1);
        object.put("signData", array);
        object.put("totalSign", totalSign);
        return object;
    }

    @Override
    public String loadConfig() {
        return null;
    }

    public void setSignData(String signData) {
        this.signData = signData;
    }

    public String getSignData() {
        return this.signData;
    }

    public String getTotalSign() {
        return totalSign;
    }

    public void setTotalSign(String totalSign) {
        this.totalSign = totalSign;
    }

    /**
     * 今天签到
     *
     * @return 成功返回今天日期 失败返回null
     */
    String signToday() {
        String today = DateUtil.formatToday();
        if (signData == null || "".equals(signData)) {
            signData = "0,0," + today;
            SignConfigTemplate template = DataManager.signConfig.getTemplate(1);
            String reward = template.getRewardData().split(";")[0];
            GMService.processGM(player,reward);
        } else {
            String[] data = signData.split(",");
            if (!today.equals(data[2])) {
                return null;
            } else {
                int round = Integer.parseInt(data[0]);
                SignConfigTemplate template = DataManager.signConfig.getTemplate(round + 1);    // 数据库中round从0开始记，配置文件中round从1开始记
                int signed = Integer.parseInt(data[1]) + 1;

                String reward = template.getRewardData().split(";")[0];
                GMService.processGM(player,reward);
                if (signed > template.getRewardData().split(";").length) {
                    signed = 0;
                    round = (round + 1) % DataManager.signConfig.size();
                    totalSign = "0,0,0,0";
                }
                setSignData(round + "," + signed + "," + today);
            }
        }
        return today;
    }

    /**
     * 累签
     */

    int signTotal(int index) {
        String[] data = signData.split(",");
        int signedDays = Integer.parseInt(data[1]);
        if(index<1||index>4) {
            return -1;
        }
        if((index == 4 && signedDays<30) || (index == 3 && signedDays<15) || (index==2 && signedDays < 7) || (index==1 && signedDays < 3)) {
            return -1;
        }
        if (getTotalSign() == null||getTotalSign().split(",").length==0) {
            setTotalSign("0,0,0,0");
        }
        String[] totalSigns = getTotalSign().split(",");
        if("".equals(totalSigns[index-1])) {
            return -1;
        } else {
            totalSigns[index - 1] = DateUtil.formatToday();
            setTotalSign(String.join(",",Arrays.asList(totalSigns)));

            SignConfigTemplate template = DataManager.signConfig.getTemplate(1);
            if(index==1) {
                GMService.processGM(player, template.getTotalSign3());
            }
            if(index==2) {
                GMService.processGM(player, template.getTotalSign7());
            }
            if(index==3) {
                GMService.processGM(player, template.getTotalSign15());
            }
            if(index==4) {
                GMService.processGM(player, template.getTotalSign30());
            }
            return index;
        }
    }
}
