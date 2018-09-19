package com.igame.work.vip;

import com.igame.core.db.AbsDao;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.UpdateOperations;

public class VipDAO extends AbsDao {
    public VipDto get(long playerId){
		return getDatastore().find(VipDto.class, "playerId", playerId).get();
    }

	public void save(VipDto vipData) {
		getDatastore().save(vipData);
	}

	public void update(VipDto vipDto) {

		Datastore ds = getDatastore();
		UpdateOperations<VipDto> up = ds.createUpdateOperations(VipDto.class)
				.set("firstPack",vipDto.firstPack)
				.set("lastDailyPack",vipDto.lastDailyPack);

		ds.update(vipDto,up);
	}
}
