package com.igame.work.activity.meiriLiangfa;

import com.igame.util.DateUtil;
import com.igame.work.activity.ActivityConfigTemplate;
import com.igame.work.gm.service.GMService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import org.mongodb.morphia.annotations.Transient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

public class MeiriLiangfaData {
    @Transient
    private static List<ActivityConfigTemplate> configs = new ArrayList<>();
    public static void addActivityConfigTemplate(ActivityConfigTemplate template) {
        if (1004==template.getActivity_sign()) {
            configs.add(template);
        }
    }

    private String record="null,null";  // 上次领取的时间

    public String receive(Player player, int index) {

        Optional<ActivityConfigTemplate> config = configs.stream()
                .filter(c -> c.getOrder() == index)
                .findAny();

        if(!config.isPresent()) {   // 找不到配置
            return null;
        }

        String configTime = config.get().getGet_value();
        String[] splitTime = configTime.split(",");
        if(splitTime.length<2){return null;}
        int begin = Integer.parseInt(splitTime[0]);
        int end = Integer.parseInt(splitTime[1]);
        int current = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        String[] splitRecord = record.split(",");
        String today = DateUtil.formatToday();
        if (today.equals(splitRecord[index - 1])) { // 今天已经领过
            return null;
        }

        if(current<begin||current>end) {    // 不在配置时间内当做补领
            ResourceService.ins().addDiamond(player, -20);
        }

        if(index==1) {
            record = today + "," + splitRecord[1];
        } else {
            record = splitRecord[0] + "," + today;
        }
        GMService.processGM(player, config.get().getActivity_drop());

        return clientData();
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public String clientData() {

        String today = DateUtil.formatToday();
        String[] split = record.split(",");
        if (today.equals(split[0])) {
            split[0] = "1";
        } else {
            split[0] = "0";
        }
        if (today.equals(split[1])) {
            split[1] = "1";
        } else {
            split[1] = "0";
        }

        return split[0] + "," + split[1];
    }
}
