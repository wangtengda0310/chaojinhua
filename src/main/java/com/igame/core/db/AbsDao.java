package com.igame.core.db;

import java.util.Map;

import org.bson.Document;
import org.mongodb.morphia.Datastore;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;



/**
 * 
 * @author Marcus.Z
 *
 */
public abstract class AbsDao {
	
	public String dbPer = "igame";
	
	public abstract String getTableName();
	
	public  Datastore getDatastore(String dbName){
		return DBManager.getInstance().getDatastore(dbName);
	}
	
	/**
	 * 根据服务器ID获取数据库Datastore对象
	 * @param serverId
	 * @return
	 */
	public  Datastore getDatastore(int serverId){
		return DBManager.getInstance().getDatastore(dbPer+serverId);
	}
	
	
	private Map<String,Datastore> getAllDatastore(){
		return DBManager.getInstance().getDatastores();
	}
	
//	public Datastore getDatastore(){
//		return DBManager.getInstance().getDatastoreOne();
//	}
	
	public MongoDatabase getDB(){
		return DBManager.getInstance().getGameDBOne();
	}
	
	public MongoCollection<Document> getTable(){
		return getDB().getCollection(getTableName());
	}
	

}
