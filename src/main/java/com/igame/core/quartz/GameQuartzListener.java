package com.igame.core.quartz;


import com.igame.core.SessionManager;
import com.igame.core.log.ExceptionLog;
import com.igame.work.fight.dto.FightBase;
import com.igame.work.fight.service.PVPFightService;
import com.igame.work.user.dto.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Marcus.Z
 *
 */
public class GameQuartzListener {
    public GameQuartzListener(){}


    public void minute(){

		//ExceptionLog.error("minute execute");

//    	for(Player player : SessionManager.ins().getSessions().values()){
//			PlayerLoad.ins().savePlayer(player,false);
//		}

		ExceptionLog.error("minute");
		JobManager.listeners.values().forEach(jobListener -> {
			try{

				jobListener.minute();
			}catch (Exception e){
				ExceptionLog.error("minute");
				e.printStackTrace();
			}
		});
    }

    public void minute5(){

		ExceptionLog.error("minute5 execute");
    	
    	long now = System.currentTimeMillis();
    	for(FightBase fb : PVPFightService.ins().fights.values()){
    		if(now - fb.getStartTime() >= 300000){
    			PVPFightService.ins().fights.remove(fb.getId());
    		}  		
    	}
    	

    }


	public void minute180(){

		ExceptionLog.error("minute180 execute");
		JobManager.listeners.values().forEach(jobListener -> {
			try{

				//刷新所有在线玩家的一般商店
				jobListener.zero();
			}catch (Exception e){
				ExceptionLog.error("minute180");
				e.printStackTrace();
			}
		});
	}

    //零点执行
    public void zero(){
		ExceptionLog.error("zero");
		JobManager.listeners.values().forEach(jobListener -> {
			try{

				//刷新所有在线玩家的一般商店
				jobListener.zero();
			}catch (Exception e){
				ExceptionLog.error("zero");
				e.printStackTrace();
			}
		});
        try{
        	List<String> palyers = new ArrayList<>();
			for (Player player : SessionManager.ins().getSessions().values()) {
				palyers.add(player.getNickname());
			}
			ExceptionLog.error("zero:"+palyers);

        }catch (Exception e){
           ExceptionLog.error("zero");
            e.printStackTrace();
        }
    }

	//九点执行
	public void nine(){
		ExceptionLog.error("nine");
		JobManager.listeners.values().forEach(jobListener -> {
			try{

				//刷新所有在线玩家的一般商店
				jobListener.nine();
			}catch (Exception e){
				ExceptionLog.error("nine");
				e.printStackTrace();
			}
		});
	}

	//十二点执行
	public void twelve(){
		ExceptionLog.error("twelve");
		JobManager.listeners.values().forEach(jobListener -> {
			try{

				jobListener.twelve();
			}catch (Exception e){
				ExceptionLog.error("twelve");
				e.printStackTrace();
			}
		});
	}

	//十四点执行
	public void fourteen(){
		ExceptionLog.error("fourteen");
		JobManager.listeners.values().forEach(jobListener -> {
			try{

				jobListener.fourteen();
			}catch (Exception e){
				ExceptionLog.error("fourteen");
				e.printStackTrace();
			}
		});
	}

	//二十点执行
	public void twenty(){
		ExceptionLog.error("twenty");

	}

	//每周一执行
	public void week7(){
    	JobManager.listeners.values().forEach(jobListener -> {
			try{
				jobListener.week7();
			}catch (Exception e){
				ExceptionLog.error("week7");
				e.printStackTrace();
			}
		});

	}


}
