package com.igame.work.activity.meiriLiangfa;

import com.igame.util.DateUtil;
import com.igame.work.activity.ActivityConfigTemplate;
import com.igame.work.user.load.ResourceService;
import org.mongodb.morphia.annotations.Transient;

import java.util.ArrayList;
import java.util.List;

public class MeiriLiangfaData {
    private ResourceService resourceService;
    @Transient
    public static List<ActivityConfigTemplate> configs = new ArrayList<>();
    public static void addActivityConfigTemplate(ActivityConfigTemplate template) {
        if (1004==template.getActivity_sign()) {
            configs.add(template);
        }
    }

    private String record="null,null";  // 上次领取的时间

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
