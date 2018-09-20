package test;

import org.bson.Document;
import org.mongodb.morphia.Datastore;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;



/**
 * 
 * @author Marcus.Z
 *
 */
public abstract class CAbsDao {
	
	public abstract String getTableName();
	
	public Datastore getDatastore(){
		return CDBManager.ins().getDatastores();
	}
	
	public MongoDatabase getDB(){
		return CDBManager.ins().getGameDBS();
	}
	
	public MongoCollection<Document> getTable(){
		return getDB().getCollection(getTableName());
	}
	

}
