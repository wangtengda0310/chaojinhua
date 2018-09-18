package com.igame.work.gm.service;

import com.google.common.collect.Lists;
import com.igame.core.ISFSModule;
import com.igame.core.handler.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.work.item.dto.Item;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;

import java.util.List;

/**
 * 
 * @author Marcus.Z
 *
 */
public class GMService implements ISFSModule {
	private ResourceService resourceService;

	public boolean processGM(Player player,String gm){
		if(!MyUtil.isNullOrEmpty(gm)){
			String[] tels = gm.split(";");
			List<Item> items = Lists.newArrayList();
			for(String tel : tels){
				String[] temp  = tel.split(",");
				switch (temp[0]){
					case "1":
						if(Integer.parseInt(temp[1]) == 1){//金币
							resourceService.addGold(player, Long.parseLong(temp[2]));
							break;
						} else if(Integer.parseInt(temp[1]) == 2){//钻石
							resourceService.addDiamond(player, Integer.parseInt(temp[2]));
							break;
						}else if(Integer.parseInt(temp[1]) == 3){//体力
							resourceService.addPhysica(player, Integer.parseInt(temp[2]));
							break;
						}else if(Integer.parseInt(temp[1]) == 4){//扫荡券
							resourceService.addSao(player, Integer.parseInt(temp[2]));
							break;
						}else if(Integer.parseInt(temp[1]) == 5){//同化经验
							resourceService.addTongExp(player, Integer.parseInt(temp[2]));
							break;
						}else if(Integer.parseInt(temp[1]) == 6){//同化点
							resourceService.addTongRes(player, Integer.parseInt(temp[2]));
							break;
						}else if(Integer.parseInt(temp[1]) == 7){//星能
							resourceService.addXing(player, Integer.parseInt(temp[2]));
							break;
						}else if(Integer.parseInt(temp[1]) == 8){//无尽积分
							resourceService.addWuScore(player, Integer.parseInt(temp[2]));
							break;
						}else if(Integer.parseInt(temp[1]) == 9){//斗技积分
							resourceService.addDoujiScore(player, Integer.parseInt(temp[2]));
							break;
						}else if(Integer.parseInt(temp[1]) == 10){//起源积分
							resourceService.addQiyuanScore(player, Integer.parseInt(temp[2]));
							break;
						}else if(Integer.parseInt(temp[1]) == 11){//部落积分
							resourceService.addBuluoScore(player, Integer.parseInt(temp[2]));
							break;
						}else if(Integer.parseInt(temp[1]) == 14){//充值金额
							resourceService.addMoney(player, Integer.parseInt(temp[2]));
							break;
						} else{
							break;
						}
					case "2"://怪物
						resourceService.addMonster(player, Integer.parseInt(temp[1]), Integer.parseInt(temp[2]), true);
						break;
					case "3"://道具
						Item item = resourceService.addItem(player, Integer.parseInt(temp[1]), Integer.parseInt(temp[2]), false);
						if(item != null){
							items.add(item);
						}
						break;
					case "4"://exp
						resourceService.addExp(player, Integer.parseInt(temp[1]));
						break;
					case "5"://怪兽exp
						int ret = resourceService.addMonsterExp(player, Long.parseLong(temp[1]), Integer.parseInt(temp[2]), true);
						if (ret != 0)
							return false;
						break;
					case "100"://exp
						player.getTimeResCheck().put(Integer.parseInt(temp[1]), 0);
						player.setCheckPoint(player.getCheckPoint() + "," +Integer.parseInt(temp[1]));
						break;
					default:
						break;

				}
			}
			if(!items.isEmpty()){
				RetVO vo = new RetVO();
	        	vo.addData("items", items);
	    		MessageUtil.sendMessageToPlayer(player, MProtrol.ITEM_UPDATE, vo);
			}
			return true;
		}else{
			return false;
		}		
		
	}
	
	

}
