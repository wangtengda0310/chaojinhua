package com.igame.core.db;

import com.igame.core.ISFSModule;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.util.*;

/**
 * 
 * @author Marcus.Z
 *
 */
public class DBManager implements ISFSModule {
	
	public Properties p = new Properties();
	private Map<String,MongoDatabase> mongoDBs = new HashMap<>();
	private Map<String,Datastore> datastores = new HashMap<>();
	private MongoClient mclient = null;

	private final String dbPer = "igame";

	public Datastore getDatastore(){
		String dbName = dbPer+p.getProperty("serverId");
		return getDatastore(dbName);
	}
	public Datastore getDatastore(String dbName){
		if(datastores.containsKey(dbName)){
			return datastores.get(dbName);
		}else{
			Morphia morphia = new Morphia();
			MongoDatabase mongoDB = mclient.getDatabase(dbName);
			Datastore ds = morphia.createDatastore(mclient, dbName);
			datastores.put(dbName, ds);
			ds.ensureIndexes();
			mongoDBs.put(dbName,mongoDB);
			return datastores.get(dbName);
		}

	}

	public void init() {
		this.p = extensionHolder.SFSExtension.getConfigProperties();
		String DBName = p.getProperty("DBName");
		String[] DBNames = DBName.split(",");
		MongoClient client = getConnect();
		mclient = client;
		
//		 MorphiaLoggerFactory.registerLogger(MorphiaLoggerFactory.class);//干掉Morphia自己的日志
		for(String key : DBNames){
			Morphia morphia = new Morphia();
			MongoDatabase mongoDB = client.getDatabase(key);
			Datastore ds = morphia.createDatastore(client, key);
			datastores.put(key, ds);
			ds.ensureIndexes();
			mongoDBs.put(key,mongoDB);
		}		
		
	}

	private MongoClient getConnect(){
		MongoClient mongoClient = null;
		try {

			String DBUrl = p.getProperty("DBUrl");
			int DBPort = Integer.parseInt(p.getProperty("DBPort"));
			String server = p.getProperty("server");
			
			MongoClientOptions.Builder buide = new MongoClientOptions.Builder();
			buide.connectionsPerHost(Integer.parseInt(p.getProperty("connectionsPerHost")));// 与目标数据库可以建立的最大链接数
			buide.connectTimeout(Integer.parseInt(p.getProperty("connectTimeout")));// 与数据库建立链接的超时时间
			buide.maxWaitTime(Integer.parseInt(p.getProperty("maxWaitTime")));// 一个线程成功获取到一个可用数据库之前的最大等待时间
			buide.threadsAllowedToBlockForConnectionMultiplier(100);
			buide.maxConnectionIdleTime(0);
			buide.maxConnectionLifeTime(Integer.parseInt(p.getProperty("maxConnectionLifeTime")));
			buide.socketTimeout(Integer.parseInt(p.getProperty("socketTimeout")));
			buide.socketKeepAlive(true);
			MongoClientOptions myOptions = buide.build();
			
			if(server.equals("public")){//正式服要用用户名密码				
				mongoClient = createMongoDBClient(DBUrl, DBPort,myOptions);				
			}else{
				ServerAddress serverAddress = new ServerAddress(DBUrl, DBPort);
				mongoClient = new MongoClient(serverAddress, myOptions);
			}
		} catch (Exception e) {
		   e.printStackTrace();
		   System.out.println("连接超时！");
		   mongoClient.close();
		}
		return mongoClient;
	}
	
	/**
	 * 有用户名密码链接数据库
	 */
	private MongoClient createMongoDBClient(String DBUrl, int DBPort,MongoClientOptions myOptions){
		ServerAddress serverAddress = new ServerAddress(DBUrl, DBPort);  
        List<ServerAddress> addrs = new ArrayList<ServerAddress>();  
        addrs.add(serverAddress);  
          
        String username = p.getProperty("username");
		String password = p.getProperty("password");
		String defaultDB = p.getProperty("DefaultDB");
        //MongoCredential.createScramSha1Credential()三个参数分别为 用户名 数据库名称 密码  
        MongoCredential credential = MongoCredential.createScramSha1Credential(username, defaultDB, password.toCharArray());
        List<MongoCredential> credentials = new ArrayList<MongoCredential>();  
        credentials.add(credential);  
        
        return new MongoClient(addrs,credentials,myOptions);  
	}

}
