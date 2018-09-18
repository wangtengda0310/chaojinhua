package com.igame.work.turntable.dao;

import com.igame.core.db.AbsDao;
import com.igame.work.turntable.dto.Turntable;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.UpdateOperations;

public class TurntableDAO extends AbsDao {

    /**
     * 根据 玩家ID 获取 玩家转盘信息
     * @param playerId 玩家ID
     */
    public Turntable getTurntableByPlayerId(long playerId){

        return getDatastore().find(Turntable.class,"playerId",playerId).get();
    }

    /**
     * 保存 玩家转盘信息
     * @param turntable 转盘信息
     */
    public Turntable saveTurntable(Turntable turntable){

        getDatastore().save(turntable);

        return turntable;
    }

    /**
     * 更新 玩家转盘信息
     * @param turntable 玩家
     */
    public void updateTurntable(Turntable turntable){

        Datastore ds = getDatastore();
        UpdateOperations<Turntable> up = ds.createUpdateOperations(Turntable.class)
                .set("lastUpdate",turntable.getLastUpdate())
                .set("rewards",turntable.getRewards())
                .set("results",turntable.getResults());

        ds.update(turntable,up);
    }

}
