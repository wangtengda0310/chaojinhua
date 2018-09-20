package test;

import java.util.Date;

import org.mongodb.morphia.Key;

/**
 * 
 * @author Marcus.Z
 *
 */
public class AccountDao extends CAbsDao {

	@Override
	public String getTableName() {
		return "AccountDto";
	}
	
	public CRetVO getAccuont(String username,String password,boolean ip2){
		if(password == null){
			password = "";
		}
		CRetVO ret = new CRetVO(Protrol.ACCOUNT_LOGIN);
		AccountDto at =getDatastore().find(AccountDto.class, "username", username).get();
		if(at == null){
			AccountDto dto = new AccountDto(username,password);
			return saveOrUpdateAccuont(dto);
			
		}
		if(!password.equals(at.getPassword())){
			ret.setErrCode(ErrorCode.ACCOUNT_NOEXIT);
			return ret;
		}
		ret.setState(ErrorCode.SUCC);
		ret.setData(at);
		return ret;
		
	}
	
	public CRetVO saveOrUpdateAccuont(AccountDto dto){
		CRetVO ret = new CRetVO(Protrol.ACCOUNT_LOGIN);
		ret.setState(ErrorCode.SUCC);
		AccountDto at = getDatastore().find(AccountDto.class, "username", dto.getUsername()).get();
		if(at != null){
			ret.setErrCode(ErrorCode.ACCOUNT_EXIT);
			return ret;
		}
		dto.setUserId(CIDFactory.ins().getNewIdByType(CIDFactory.AT));
		dto.setRegDate(new Date());
		Key<AccountDto> re = getDatastore().save(dto);
		if(re == null){
			ret.setState(ErrorCode.ERR);
			ret.setErrCode(ErrorCode.ERR);
			return ret;
		}
		ret.setState(ErrorCode.SUCC);
		ret.setErrCode(ErrorCode.CODE_SUCC);
		ret.setData(dto);
		return ret;
		
	}
	
    private static final AccountDao domain = new AccountDao();
    public static final AccountDao ins(){return domain;}
   
    
    private AccountDao(){}
	
	

}
