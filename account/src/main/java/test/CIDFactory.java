package test;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * @author Marcus.Z
 *
 */
public class CIDFactory extends CAbsDao {

	
	private static final Logger Log = LogManager.getLogger(CIDFactory.class);
	
	public static final String AT = "AT";//账号ID
	


	@Override
	public String getTableName() {
		return "IDSeq";
	}
	
	 
	public synchronized long getNewIdByType(String type){
		

		
		IDSeq id = getDatastore().find(IDSeq.class, "type", AT).get();
		if(id == null){
			id = new IDSeq(AT,1);
			getDatastore().save(id);
			return Long.valueOf("10001"+id.getValue());
		}else{
			getDatastore().update(id, getDatastore().createUpdateOperations(IDSeq.class).inc("value"));
//			id = getDatastore().find(IDSeq.class, "type", AT).get();
			return Long.valueOf("10001"+(id.getValue()+1));
		}
		
	    	
	}

    private static final CIDFactory domain = new CIDFactory();
    public static final CIDFactory ins(){return domain;}
   
    
    private CIDFactory(){}

    
    
    

}
