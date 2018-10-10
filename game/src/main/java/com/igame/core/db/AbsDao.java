package com.igame.core.db;

import com.igame.server.GameServerExtension;
import org.mongodb.morphia.Datastore;



/**
 * @author Marcus.Z
 *
 */
public abstract class AbsDao {
	
	/**
	 * 根据服务器ID获取数据库Datastore对象
	 */
	public  Datastore getDatastore(){
		return GameServerExtension.dbManager.getDatastore();
	}

}
