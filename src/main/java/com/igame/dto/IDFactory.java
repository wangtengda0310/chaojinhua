package com.igame.dto;

import com.igame.core.db.AbsDao;




/**
 * 
 * @author Marcus.Z
 *
 */
public class IDFactory extends AbsDao {

	
	
	public static final String PL = "PL";//角色ID
	
	static final String MA = "MA";//怪物

	public  long getNewIdByType(String type,int serverId){
		return getNewId(serverId, type);

	}

	private long getNewId(int serverId, String type) {
		synchronized (DBSeqcLock.getLock(serverId - 1)) {
			IDSeq id = getDatastore().find(IDSeq.class, "type", type).get();
			if (id == null) {
				id = new IDSeq(type, 1);
				getDatastore().save(id);
				return Long.valueOf(String.valueOf(10000 + serverId) + id.getValue());
			} else {
				getDatastore().update(id, getDatastore().createUpdateOperations(IDSeq.class).inc("value"));
//				id = getAccountDatastore().find(IDSeq.class, "type", AT).get();
				return Long.valueOf(String.valueOf(10000 + serverId) + (id.getValue() + 1));
			}
		}
	}

	public long getNewIdMonster(int serverId){
		return getNewId(serverId, MA);


	}

    private static final IDFactory domain = new IDFactory();
    public static IDFactory ins(){return domain;}
   
    
    private IDFactory(){}


	/**
	 *
	 * @author Marcus.Z
	 *
	 */
	public static class DBSeqcLock {
		private static final int DEFAULT_SIZE = 100;
		private static Object[] locks = new Object[100];
		private static Object oo = new Object();


		static Object getLock(int index) {
			if (index < 0 || index >= locks.length) {
				return oo;
			}
			return locks[index];
		}

		static {
			for (int i = 0; i < DEFAULT_SIZE; i++)
				locks[i] = new Object();
		}
	}
}
