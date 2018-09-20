package test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;




import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

/**
 * 
 * @author Marcus.Z
 *
 */
public class CDBManager {
	
	private  Properties pp = new Properties();
	private  MongoDatabase mongoDBs;
	private  Datastore datastores; 
	

	public MongoDatabase getGameDBS(){
		return mongoDBs;
		
	}

	
	public Datastore getDatastores() {
		return datastores;
	}

	private CDBManager() {
		
//		try {
//			pp.load(ClassLoader.getSystemResourceAsStream("resource/db.properties"));
////			p = ResourceBundle.getBundle("resource/db.properties");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		String DBName = "accounts";
		MongoClient client = getConnect();
		Morphia morphia = new Morphia();
		MongoDatabase mongoDB = client.getDatabase(DBName);
		Datastore ds = morphia.createDatastore(client, DBName);
		this.datastores = ds;
		ds.ensureIndexes();
		this.mongoDBs = mongoDB;

	}

	private  MongoClient getConnect(){	
		MongoClient mongoClient = null;
		try {

			String DBUrl = "127.0.0.1";
			int DBPort = 27017;
			String server = "test";
			
			MongoClientOptions.Builder buide = new MongoClientOptions.Builder();
			buide.connectionsPerHost(100);// 与目标数据库可以建立的最大链接数
			buide.connectTimeout(25000);// 与数据库建立链接的超时时间
			buide.maxWaitTime(300000);// 一个线程成功获取到一个可用数据库之前的最大等待时间
			buide.threadsAllowedToBlockForConnectionMultiplier(100);
			buide.maxConnectionIdleTime(0);
			buide.maxConnectionLifeTime(150000);
			buide.socketTimeout(100000);
			buide.socketKeepAlive(true);
			MongoClientOptions myOptions = buide.build();

			ServerAddress serverAddress = new ServerAddress(DBUrl, DBPort);
			mongoClient = new MongoClient(serverAddress, myOptions);
		} catch (Exception e) {
		   e.printStackTrace();
		   System.out.println("连接超时！");
		   mongoClient.close();
		}
		return mongoClient;
	}

    private static final CDBManager domain = new CDBManager();
    public static final CDBManager ins(){return domain;}

	
}
