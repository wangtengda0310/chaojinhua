package com.igame.core.quartz;





import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.core.data.DataManager;
import com.igame.core.data.template.CheckPointTemplate;
import com.igame.core.log.ExceptionLog;
import com.igame.core.log.GoldLog;
import com.igame.util.GameMath;
import com.igame.util.MyUtil;
import com.igame.util.SystemService;
import com.igame.work.chat.service.PublicMessageService;
import com.igame.work.checkpoint.dto.RewardDto;
import com.igame.work.checkpoint.dto.XingMoDto;
import com.igame.work.checkpoint.service.BallisticService;
import com.igame.work.fight.dto.FightBase;
import com.igame.work.fight.service.ArenaService;
import com.igame.work.fight.service.PVPFightService;
import com.igame.work.shop.service.ShopService;
import com.igame.work.system.BallisticRank;
import com.igame.work.system.RankService;
import com.igame.work.turntable.service.TurntableService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.dto.RobotDto;
import com.igame.work.user.load.ResourceService;
import com.igame.work.user.service.PlayerCacheService;
import com.igame.work.user.service.RobotService;
import com.igame.work.user.service.VIPService;

/**
 * 
 * @author Marcus.Z
 *
 */
public class GameQuartzListener {
    public GameQuartzListener(){}


    public void minute(){

		//ExceptionLog.error("minute execute");
    	
    	calPlayerTimeRes();
//    	for(Player player : SessionManager.ins().getSessions().values()){
//			PlayerLoad.ins().savePlayer(player,false);
//		}
    	PlayerCacheService.checkPlayer();
    	SystemService.ins().saveData();

    }
    
    public void minute5(){

		//ExceptionLog.error("minute5 execute");
    	
    	long now = System.currentTimeMillis();
    	for(FightBase fb : PVPFightService.ins().fights.values()){
    		if(now - fb.getStartTime() >= 300000){
    			PVPFightService.ins().fights.remove(fb.getId());
    		}  		
    	}
    	RankService.ins().sort();
    	RankService.ins().saveData();
    	BallisticService.ins().saveData();
    	RobotService.ins().ref();
    	RobotService.ins().save();
		ArenaService.ins().saveRobot();
		PublicMessageService.ins().save();
    	if(ArenaService.ins().isUp()){
        	ArenaService.ins().saveRank();
    		ArenaService.ins().setUp(false);
    	}
    	

    }


	public void minute180(){

		ExceptionLog.error("minute180 execute");

		for(Player player : SessionManager.ins().getSessions().values()){

			if (player.getTurntable() != null){
				//更新大转盘
				TurntableService.ins().reloadTurntable(player);
				//推送更新
				MessageUtil.notiyTurntableChange(player);
			}
		}

	}
    
    //定时执行玩家金币关卡计算
    private void calPlayerTimeRes(){
    	for(Player player : SessionManager.ins().getSessions().values()){
    		synchronized (player.getTimeLock()) {
    			if(!player.getTimeResCheck().isEmpty()){
		    		for(Map.Entry<Integer, Integer> m : player.getTimeResCheck().entrySet()){
		    			CheckPointTemplate ct = DataManager.CheckPointData.getTemplate(m.getKey());
		    			if(ct != null && !MyUtil.isNullOrEmpty(ct.getDropPoint())){
	        				if(m.getValue() < ct.getMaxTime() * 60){//没到上限
	        					player.getTimeResCheck().put(m.getKey(), m.getValue()+ 1);
	        					if(m.getValue() >= 60 && m.getValue() % 60 == 0){//到了一小时更新，推送金币数
	        						RewardDto dto = ResourceService.ins().getResRewardDto(ct.getDropPoint(), m.getValue(), ct.getMaxTime() * 60);
	        						MessageUtil.notiyTimeResToPlayer(player, m.getKey(), dto);      						
	        					}
	        					
	        					//零时测试
//        						RewardDto dto = ResourceService.ins().getResRewardDto(ct.getDropPoint(), m.getValue(), ct.getMaxTime() * 60);
//        						MessageUtil.notiyTimeResToPlayer(player, m.getKey(), dto);     
	        				} 				
		    			}
		    		}
	    		}
    			processXinMo(player);
    			processRes(player);
    		}		
		}
    }
    
    private void processRes(Player player){
		for(Map.Entry<Integer, Integer> m : player.getResMintues().entrySet()){
			if(m.getKey() == 6 && player.getTongRes() >= 15 || m.getKey() == 7 && player.getXing() >= 10){
				continue;
			}else{
				player.getResMintues().put(m.getKey(), m.getValue() + 1);
			}
			
		}
    	if(player.getResMintues().get(3) != null){
    		if(player.getResMintues().get(3) >=6){
    			ResourceService.ins().addPhysica(player, 1);
    			MessageUtil.notiyCDDown(player, 3);
    			player.getResMintues().put(3, 0);
    		}
    		
    	}
    	if(player.getResMintues().get(4) != null){
    		if(player.getResMintues().get(4) >=60){
    			ResourceService.ins().addSao(player, 1);
    			player.getResMintues().put(4, 0);
    		}
    		
    	}
    	if(player.getResMintues().get(6) != null){
    		if(player.getResMintues().get(6) >=120 && player.getTongRes() < 15){
    			ResourceService.ins().addTongRes(player, 1);
    			player.getResMintues().put(6, 0);
    		}
    		
    	}
    	if(player.getResMintues().get(7) != null){
    		if(player.getResMintues().get(7) >=120  && player.getXing() < 10){
        		ResourceService.ins().addXing(player, 1);
    			player.getResMintues().put(7, 0);
    		}
    		
    	}

    }
    
    private void processXinMo(Player player){
    	
    	boolean change = false;
    	String removeId = "";
    	List<XingMoDto> lx = Lists.newArrayList();
    	if(!MyUtil.isNullOrEmpty(player.getCheckPoint()) && player.getCheckPoint().split(",").length >= 30){
    		
    		long now = System.currentTimeMillis();
    		//先是删掉24小时没打的
    		List<Integer> remove = Lists.newArrayList();   			
			for(XingMoDto xin :player.getXinMo().values()){
				if(xin.getStatTime() == 0 || now - xin.getStatTime() >= 24 * 3600 * 1000){
					remove.add(xin.getCheckPiontId());
					removeId += "," + xin.getCheckPiontId();
				}
			}
			if(!remove.isEmpty()){
				for(Integer id :remove){
					player.getXinMo().remove(id);
					GoldLog.info("remove xinMo:" + id);
				}
				change = true;
			}
			//判断是否刷新新的
			if(player.getXinMoMinuts() < 60){
				player.setXinMoMinuts(player.getXinMoMinuts() + 1);
			}else{
				if(player.getXinMoMinuts() >= 60){//到了一小时  可能刷新
					String[] ccs =  player.getCheckPoint().split(",");
					List<Integer> ls = Lists.newArrayList();
					for(String cc : ccs){
						if(!player.getXinMo().containsKey(Integer.parseInt(cc)) && DataManager.ins().CheckPointData.getTemplate(Integer.parseInt(cc)).getChapterType() != 2 && Integer.parseInt(cc) <=140){//有心魔的关卡不在生成列表中
							ls.add(Integer.parseInt(cc));
						}
					}
					if(!ls.isEmpty() && player.getXinMo().size() < 25){
						Integer cid = ls.get(GameMath.getRandInt(ls.size()));
						XingMoDto xx = new XingMoDto();
						xx.setCheckPiontId(cid);
						Map<String, RobotDto> robs = RobotService.ins().getRobot().get(player.getSeverId());
						if(!robs.isEmpty()){
							RobotDto rb = robs.values().stream().collect(Collectors.toList()).get(GameMath.getRandInt(robs.size()));
							xx.setPlayerId(rb.getPlayerId());
							xx.setMid(rb.getName());
							xx.setPlayerFrameId(rb.getPlayerFrameId());
							xx.setPlayerHeadId(rb.getPlayerHeadId());
							xx.setStatTime(now);
							player.getXinMo().put(cid, xx);
							player.setXinMoMinuts(0);//重新累算
							change = true;
							lx.add(xx);
							GoldLog.info("add xinMo:" + cid);
						}else{
							xx.setMid("");
						}					
						
					}
				}
			}
    	}
    	if(change){
    		if(removeId.length() > 0){
    			removeId = removeId.substring(1);
    		}
    		MessageUtil.notiyXingMoChange(player,removeId,lx);//推送更新
    	}
    	
    }

    //零点执行
    public void zero(){
        try{
        	List<String> palyers = new ArrayList<>();
			for (Player player : SessionManager.ins().getSessions().values()) {
				palyers.add(player.getNickname());
			}
			ExceptionLog.error("zero:"+palyers);

            SystemService.ins().getClock().clear();
            SystemService.ins().resetOnline();
            SystemService.ins().refFateMap();

			//刷新所有在线玩家的神秘商店
			ShopService.ins().reloadMysticalOnline();

			//暴走时刻 结算奖励 && 清空排行榜 && 随机当日buff
			BallisticService.ins().zero();

			//重置命运之门排行榜
			RankService.ins().zero();
			
			ArenaService.ins().giveReward();
        }catch (Exception e){
           ExceptionLog.error("SystemService.ins().zero");
            e.printStackTrace();
        }
    }

	//九点执行
	public void nine(){
		try{
			ExceptionLog.error("nine");

			//刷新所有在线玩家的一般商店
			ShopService.ins().reloadGeneralOnline();

		}catch (Exception e){
			ExceptionLog.error("SystemService.ins().zero");
			e.printStackTrace();
		}
	}

	//十二点执行
	public void twelve(){
		try{
			ExceptionLog.error("twelve");

			//刷新所有在线玩家的神秘商店
			ShopService.ins().reloadMysticalOnline();
		}catch (Exception e){
			ExceptionLog.error("ShopService.ins().twelve");
			e.printStackTrace();
		}
	}

	//十四点执行
	public void fourteen(){
		try{
			ExceptionLog.error("fourteen");

			//刷新所有在线玩家的一般商店
			ShopService.ins().reloadGeneralOnline();
		}catch (Exception e){
			ExceptionLog.error("SystemService.ins().fourteen");
			e.printStackTrace();
		}
	}

	//二十点执行
	public void twenty(){
		try{
			ExceptionLog.error("twenty");

			//刷新所有在线玩家的一般商店
			ShopService.ins().reloadGeneralOnline();
		}catch (Exception e){
			ExceptionLog.error("SystemService.ins().twenty");
			e.printStackTrace();
		}
	}

	//每周一执行
	public void week7(){
		try{
			ArenaService.ins().clearRank();
		}catch (Exception e){
			ExceptionLog.error("ArenaService.ins().week7");
			e.printStackTrace();
		}
	}


}
