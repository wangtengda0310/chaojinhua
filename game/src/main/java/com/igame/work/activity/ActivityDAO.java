package com.igame.work.activity;

import com.igame.core.db.AbsDao;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.UpdateOperations;

public class ActivityDAO extends AbsDao {

    public ActivityDto getActivityById(int activityId){

        return getDatastore().find(ActivityDto.class,"activityId",activityId).get();
    }

    public ActivityDto saveActivity(ActivityDto activity){

        getDatastore().save(activity);

        return activity;
    }

    public void remove(ActivityDto dto) {
        getDatastore().delete(dto);
    }

    public void updatePlayer(ActivityDto dto, long playerId, ActivityOrderDto field){
        Datastore ds = getDatastore();
        UpdateOperations<ActivityDto> up = ds.createUpdateOperations(ActivityDto.class)
                .set("orderData."+playerId, field);

        ds.update(dto, up);
    }
}
