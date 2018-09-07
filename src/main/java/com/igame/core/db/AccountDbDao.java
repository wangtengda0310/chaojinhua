package com.igame.core.db;

import com.igame.server.GameServerExtension;
import org.mongodb.morphia.Datastore;


/**
 * 
 * @author Marcus.Z
 *
 */
public class AccountDbDao {

	public  Datastore getAccountDatastore(){
		return GameServerExtension.dbManager.getDatastore("accounts");
	}

}
