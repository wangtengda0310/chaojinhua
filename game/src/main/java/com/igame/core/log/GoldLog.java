package com.igame.core.log;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;





/**
 * 
 * @author Marcus.Z
 *
 */
public class GoldLog {
	
    private static Logger log = LogManager.getLogger(GoldLog.class);
    
    public static final  String ADDITEM = "addItem";
    
    public static final  String ADDRES = "addRes";
    
    public static final  String ADDMONSTER = "addMonster";
    
    public static final  String ENDC = "endC";
    
    public static final  String SAOC = "saoC";
    
    public static final  String MTUPOAll = "monsterTUPOAll";
    
    public static final  String MTUPOChange = "monsterTUPOChange";
    
    public static final  String MTUPO = "monsterTUPO";
    
    public static final  String MEV = "monsterEV";
    
    public static final  String CHEAT = "cheat";
    
    public static final String TONGHUAGET = "tongHuaGet";
    
    public static final String TONGHUAFIGHT = "tongHuaFight";
    
    public static final String BUYRES = "buyRes";
    
    public static final String LOGIN = "login";
    
    public static final String LOGINOUT = "loginOut";
    
    public static final String HECHENG = "hecheng";
    
    public static final String MAILGET = "mailget";
    
    public static final String GODSRESET = "godsReset";
    
    public static final String DRAWGET = "drawGet";
    
    public static final String QUESTREWARD = "questReward";
    
    public static final String GATEREWARD = "gateReward";
    
    public static void info(String msg){
        log.info(msg+"#Date:"+new Date());
    }
    
    public static void info(int serverId,long userId,long playerId,String act,String msg){
    	StringBuilder sb = new StringBuilder();
    	sb.append("#serverId:").append(serverId).append("#userId:").append(userId).append("#playerId:")
    		.append(playerId).append("#act:").append(act).append(msg).append("#Date:").append(new Date());
    	log.info(sb.toString());
    }

}
