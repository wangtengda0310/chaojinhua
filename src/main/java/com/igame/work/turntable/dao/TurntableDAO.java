package com.igame.work.turntable.dao;

import com.igame.core.db.AbsDao;
import com.igame.work.turntable.dto.Turntable;
import com.igame.work.shop.dto.ShopInfo;
import com.igame.work.user.dto.Player;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.UpdateOperations;

public class TurntableDAO extends AbsDao {

	@Override
	public String getTableName() {
		return "Shops";
	}
	
    private static final TurntableDAO domain = new TurntableDAO();

    public static final TurntableDAO ins() {
        return domain;
    }


    /**
     * 根据 玩家ID 获取 玩家转盘信息
     * @param severId 服务器ID
     * @param playerId 玩家ID
     */
    public Turntable getTurntableByPlayerId(int severId, long playerId){

        return getDatastore(severId).find(Turntable.class,"playerId",playerId).get();
    }

    /**
     * 保存 玩家转盘信息
     * @param serverId 服务器ID
     * @param turntable 转盘信息
     */
    public Turntable saveTurntable(int serverId, Turntable turntable){

        getDatastore(serverId).save(turntable);

        return turntable;
    }

    /**
     * 更新 玩家转盘信息
     * @param serverId 服务器ID
     * @param turntable 玩家
     */
    public void updateTurntable(int serverId, Turntable turntable){

        Datastore ds = getDatastore(serverId);
        UpdateOperations<Turntable> up = ds.createUpdateOperations(Turntable.class)
                .set("lastUpdate",turntable.getLastUpdate())
                .set("rewards",turntable.getRewards())
                .set("results",turntable.getResults());

        ds.update(turntable,up);
    }

    public void updatePlayer(Player player) {

        if(player.getTurntable().getDtate() == 1){
            saveTurntable(player.getSeverId(), player.getTurntable());
        }else if(player.getTurntable().getDtate() == 2){
            updateTurntable(player.getSeverId(), player.getTurntable());
        }
    }
}
