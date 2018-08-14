package com.igame.dto;

import com.igame.core.db.AbsDao;




/**
 * 
 * @author Marcus.Z
 *
 */
public class IDFactory extends AbsDao {

	
	
	public static final String PL = "PL";//角色ID
	
	public static final String MA = "MA";//怪物
	


	@Override
	public String getTableName() {
		return "IDSeq";
	}
	
	 
	public  long getNewIdByType(String type,int serverId){
		synchronized(DBSeqcLock.getLock(serverId -1)){
			IDSeq id = getDatastore(serverId).find(IDSeq.class, "type", PL).get();
			if(id == null){
				id = new IDSeq(PL,1);
				getDatastore(serverId).save(id);
				return Long.valueOf(String.valueOf(10000+serverId)+id.getValue());
			}else{
				getDatastore(serverId).update(id, getDatastore(serverId).createUpdateOperations(IDSeq.class).inc("value"));
//				id = getDatastore().find(IDSeq.class, "type", AT).get();
				return Long.valueOf(String.valueOf(10000+serverId)+(id.getValue()+1));
			}
		}
    	
	}
	
	public long getNewIdMonster(int serverId){
		synchronized(DBSeqcLock.getLock(serverId -1)){
			IDSeq id = getDatastore(serverId).find(IDSeq.class, "type", MA).get();
			if(id == null){
				id = new IDSeq(MA,1);
				getDatastore(serverId).save(id);
				return Long.valueOf(String.valueOf(10000+serverId)+id.getValue());
			}else{
				getDatastore(serverId).update(id, getDatastore(serverId).createUpdateOperations(IDSeq.class).inc("value"));
	//			id = getDatastore().find(IDSeq.class, "type", AT).get();
				return Long.valueOf(String.valueOf(10000+serverId)+(id.getValue()+1));
			}
		}
		
	    	
	}

    private static final IDFactory domain = new IDFactory();
    public static final IDFactory ins(){return domain;}
   
    
    private IDFactory(){}

    
    
    

}
