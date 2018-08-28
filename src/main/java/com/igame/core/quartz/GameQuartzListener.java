package com.igame.core.quartz;


import com.google.common.collect.Lists;
import com.igame.core.SessionManager;
import com.igame.core.log.ExceptionLog;
import com.igame.core.log.GoldLog;
import com.igame.server.GameServer;
import com.igame.util.GameMath;
import com.igame.util.MyUtil;
import com.igame.work.MessageUtil;
import com.igame.work.checkpoint.guanqia.GuanQiaDataManager;
import com.igame.work.checkpoint.guanqia.RewardDto;
import com.igame.work.checkpoint.guanqia.data.CheckPointTemplate;
import com.igame.work.checkpoint.xinmo.XingMoDto;
import com.igame.work.fight.dto.FightBase;
import com.igame.work.fight.service.PVPFightService;
import com.igame.work.turntable.service.TurntableService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.dto.RobotDto;
import com.igame.work.user.load.ResourceService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

		for(Player player : SessionManager.ins().getSessions().values()){

			if (player.getTurntable() != null){
				//更新大转盘
				TurntableService.ins().reloadTurntable(player);
				//推送更新
				MessageUtil.notifyTurntableChange(player);
			}
		}

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
