package com.igame.work.activity;

import com.igame.core.db.AbsDao;

public class ActivityDAO extends AbsDao {

    public ActivityDto getActivityById(int activityId){

        return getDatastore().find(ActivityDto.class,"activityId",activityId).get();
    }

    public ActivityDto saveActivity(ActivityDto activity){

        getDatastore().save(activity);

        return activity;
    }

}
