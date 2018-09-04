package com.igame.core.db;

import com.igame.server.GameServerExtension;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.mongodb.morphia.Datastore;

import java.util.Map;



/**
 * 
 * @author Marcus.Z
 *
 */
public abstract class AbsDao {
	
	public String dbPer = "igame";
	
	public abstract String getTableName();
	
	public  Datastore getDatastore(String dbName){
		return GameServerExtension.dbManager.getDatastore(dbName);
	}
	
	/**
	 * 根据服务器ID获取数据库Datastore对象
	 */
	public  Datastore getDatastore(int serverId){
		return GameServerExtension.dbManager.getDatastore(dbPer+serverId);
	}
	
	
	private Map<String,Datastore> getAllDatastore(){
		return GameServerExtension.dbManager.getDatastores();
	}
	
//	public Datastore getDatastore(){
//		return GameServerExtension.dbManager.getDatastoreOne();
//	}
	
	public MongoDatabase getDB(){
		return GameServerExtension.dbManager.getGameDBOne();
	}
	
	public MongoCollection<Document> getTable(){
		return getDB().getCollection(getTableName());
	}
	

}
