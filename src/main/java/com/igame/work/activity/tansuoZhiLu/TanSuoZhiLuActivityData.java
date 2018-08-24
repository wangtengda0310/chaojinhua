package com.igame.work.activity.tansuoZhiLu;

import com.igame.work.activity.ActivityConfigTemplate;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Transient;

import java.util.ArrayList;
import java.util.List;

@Entity(noClassnameStored = true)
public class TanSuoZhiLuActivityData {
    private String reveivedLeve;

    @Transient
    static List<ActivityConfigTemplate> configs = new ArrayList<>();
    public static void addActivityConfigTemplate(ActivityConfigTemplate template) {
        if (1005==template.getActivity_sign()) {
            configs.add(template);
        }
    }

    public String getReveivedLeve() {
        return reveivedLeve;
    }

    public void setReveivedLeve(String reveivedLeve) {
        this.reveivedLeve = reveivedLeve;
    }

    public JSONObject toClientData() {
        JSONObject object = new JSONObject();
        object.put("type", 1005);
        JSONArray array = new JSONArray();
        for (ActivityConfigTemplate config : configs) {
            JSONObject item = new JSONObject();
            item.put("id", config.getActivity_sign());
            item.put("needLevel", config.getGet_limit());
            item.put("state", 0);
            array.add(item);
        }
        return object;
    }
}
