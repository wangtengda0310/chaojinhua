package com.igame.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.core.data.DataManager;
import com.igame.core.data.template.CheckPointTemplate;
import com.igame.core.log.GoldLog;
import com.igame.work.checkpoint.dto.RewardDto;
import com.igame.work.checkpoint.dto.XingMoDto;
import com.igame.work.user.dto.Player;
import com.igame.work.user.dto.ResCdto;
import com.igame.work.user.dto.RobotDto;
import com.igame.work.user.load.ResourceService;
import com.igame.work.user.service.RobotService;

/**
 * 
 * @author Marcus.Z
 *
 */
public class PlayerTimeResCalUtil {
	
	 private static final PlayerTimeResCalUtil domain = new PlayerTimeResCalUtil();

	    public static final PlayerTimeResCalUtil ins() {
	        return domain;
	    }
	
	/**
	 * 
	 * @param player
	 */
    public  void calPlayerTimeRes(Player player){
    	synchronized (player.getTimeLock()) {
    		calRes(player);
	    	if(!player.getTimeResCheck().isEmpty()){
	        	long now = System.currentTimeMillis();	
    			for(Map.Entry<Integer, Integer> m : player.getTimeResCheck().entrySet()){
    				CheckPointTemplate ct = DataManager.ins().CheckPointData.getTemplate(m.getKey());
    				if(ct != null && !MyUtil.isNullOrEmpty(ct.getDropPoint())){		
    					int total = m.getValue();//原有分钟数
    					if(player.getLoginoutTime() != null){//计算新的
    						int timeAdd = (int)((now - player.getLoginoutTime().getTime())/60000);
    						total += timeAdd;
    						if(total > ct.getMaxTime() * 60){
    							total = ct.getMaxTime()  * 60;
    						}
    						player.getTimeResCheck().put(m.getKey(), total);
    					}
    					RewardDto dto = ResourceService.ins().getResRewardDto(ct.getDropPoint(), total, ct.getMaxTime() * 60);
//    					MessageUtil.notiyTimeResToPlayer(player, m.getKey(), dto);
    					player.getResC().put(ct.getChapterId(),new ResCdto(ct.getChapterId(), ResourceService.ins().getRewardString(dto)));
    					
    				}
	    		}
	        }
	    	initXinMo(player);
	    	
    	}

    }
    
	//登录时候初始化
	public  void initXinMo(Player player){
		
    	if(!MyUtil.isNullOrEmpty(player.getCheckPoint()) && player.getCheckPoint().split(",").length >= 30){
    		
    		long now = System.currentTimeMillis();
    		//先是删掉24小时没打的
    		List<Integer> remove = Lists.newArrayList();   			
			for(XingMoDto xin :player.getXinMo().values()){
				if(xin.getStatTime() == 0 || now - xin.getStatTime()  >= 24 * 3600 * 1000){
					remove.add(xin.getCheckPiontId());
				}
			}
			if(!remove.isEmpty()){
				for(Integer id :remove){
					player.getXinMo().remove(id);
					GoldLog.info("remove xinMo:" + id);
				}
			}
			
			//判断是否刷新新的
			if(player.getLoginoutTime() != null){
				int timeAdd = (int)((now - player.getLoginoutTime().getTime())/60000);//分钟数
				int total = player.getXinMoMinuts() +  timeAdd;
				int count  = total /60; //几个小时-可刷几个
				if(count > 0){
					String[] ccs =  player.getCheckPoint().split(",");
					List<Integer> ls = Collections.synchronizedList(Lists.newArrayList());					
					for(String cc : ccs){
						if(!player.getXinMo().containsKey(Integer.parseInt(cc)) && DataManager.ins().CheckPointData.getTemplate(Integer.parseInt(cc)).getChapterType() != 2 && Integer.parseInt(cc) <=140){//有心魔的关卡不在生成列表中
							ls.add(Integer.parseInt(cc));
						}
					}
					if(!ls.isEmpty()){
						if(count > 25-player.getXinMo().size()){
							count = 25-player.getXinMo().size();
						}
						while(!ls.isEmpty() && count > 0){
							int index = GameMath.getRandInt(ls.size());
							Integer cid = ls.remove(index);
							XingMoDto xx = new XingMoDto();
							xx.setCheckPiontId(cid);
							Map<String, RobotDto> robs = RobotService.ins().getRobot().get(player.getSeverId());
							if(!robs.isEmpty()){
								RobotDto rb = robs.values().stream().collect(Collectors.toList()).get(GameMath.getRandInt(robs.size()));
								xx.setPlayerId(rb.getPlayerId());
								xx.setMid(rb.getName());
								xx.setPlayerFrameId(rb.getPlayerFrameId());
								xx.setPlayerHeadId(rb.getPlayerHeadId());
							}else{
								xx.setMid("");//待改变和生成具体机器人数据
							}
							xx.setStatTime(now);
							player.getXinMo().put(cid, xx);
							count--;
							GoldLog.info("add xinMo:" + cid);
							if(player.getXinMo().size() >= 25){
								break;
							}
						}

					}
					player.setXinMoMinuts(total % 60);//保存剩余分钟数
				}else{
					player.setXinMoMinuts(total);//保存剩余分钟数
				}
				
			}
    	}
    }
	
	public  void calRes(Player player){
		if(player.getLoginoutTime() != null){
			long now = System.currentTimeMillis();
			int timeAdd = (int)((now - player.getLoginoutTime().getTime())/60000);
			for(Map.Entry<Integer, Integer> m : player.getResMintues().entrySet()){
				player.getResMintues().put(m.getKey(), m.getValue() + timeAdd);
			}
	    	if(player.getResMintues().get(3) != null){
	    		if(player.getResMintues().get(3) >=6){
	    			ResourceService.ins().addPhysica(player, player.getResMintues().get(3)/6);
	    			player.getResMintues().put(3, player.getResMintues().get(3)%6);
	    		}  		
	    	}
	    	if(player.getResMintues().get(4) != null){
	    		if(player.getResMintues().get(4) >=60){
	    			ResourceService.ins().addSao(player, player.getResMintues().get(4)/60);
	    			player.getResMintues().put(4, player.getResMintues().get(4)%60);
	    		}
	    		
	    	}
	    	if(player.getResMintues().get(6) != null){
	    		if(player.getResMintues().get(6) >=120 && player.getTongRes() < 15){
	    			int add = player.getResMintues().get(6)/120;
	    			if(add > 15 - player.getTongRes()){
	    				add = 15 - player.getTongRes();
	    			}
	    			ResourceService.ins().addTongRes(player, add);
	    			player.getResMintues().put(6, player.getResMintues().get(6)%120);
	    		}
	    		
	    	}
	    	if(player.getResMintues().get(7) != null){
	    		if(player.getResMintues().get(7) >=120 && player.getXing() < 10){
	    			int add = player.getResMintues().get(7)/120;
	    			if(add > 10 - player.getXing()){
	    				add = 10 - player.getXing();
	    			}
	        		ResourceService.ins().addXing(player, add);
	    			player.getResMintues().put(7, player.getResMintues().get(7)%120);
	    		}
	    		
	    	}
		}
		
	}
    


}
