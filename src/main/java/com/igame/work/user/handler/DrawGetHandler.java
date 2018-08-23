package com.igame.work.user.handler;


import com.google.common.collect.Lists;
import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.work.user.PlayerDataManager;
import com.igame.work.user.data.DrawdataTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.core.log.GoldLog;
import com.igame.core.handler.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.checkpoint.guanqia.RewardDto;
import com.igame.work.item.dto.Item;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.PlayerService;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * 
 * @author Marcus.Z
 *
 */
public class DrawGetHandler extends BaseHandler{
	

	@Override
	public void handleClientRequest(User user, ISFSObject params) {
		RetVO vo = new RetVO();
		if(reviceMessage(user,params,vo)){
			return;
		}
		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);
		Player player = SessionManager.ins().getSession(Long.parseLong(user.getName()));
		if(player == null){
			this.getLogger().error(this.getClass().getSimpleName()," get player failed Name:" +user.getName());
			return;
		}
		int drawType = jsonObject.getInt("drawType");
		int type = jsonObject.getInt("type");
		int number = 0;
		if(jsonObject.containsKey("number")){
			number = jsonObject.getInt("number");
		}
		int drawLv = player.getDraw().getDrawLv();
		String reward = null;
		int ret = 0;
		if(!MyUtil.hasCheckPoint(player.getDraw().getDrawList(), String.valueOf(drawType)) || (type != 1 && type != 2)){
			ret = ErrorCode.ERROR;
		}else if (player.getItems().size() >= player.getBagSpace()){
			ret = ErrorCode.BAGSPACE_ALREADY_FULL;
		}else{
			DrawdataTemplate dt  =  PlayerDataManager.DrawdataData.getTemplate(drawType+"_"+player.getDraw().getDrawLv());
			if(dt == null){
				ret = ErrorCode.ERROR;
			}else{
//				if(type == 1){//消耗道具方式
//					boolean can = true;
//					List<Item> ll = Lists.newArrayList();	
//					if(!MyUtil.isNullOrEmpty(dt.getItem())){
//											
//						for(String ii : dt.getItem().split(";")){
//							String[] item = ii.split(",");
//							if(!player.getItems().containsKey(Integer.parseInt(item[0])) || player.getItems().get(Integer.parseInt(item[0])).getBallisticCount() < Integer.parseInt(item[1])){
//								can = false;
//								break;
//							}
//						}
//					}
//					if(!can){
//						ret = ErrorCode.ITEM_NOT_ENOUGH;//道具不足
//					}else{
//						if(!MyUtil.isNullOrEmpty(dt.getItem())){
//							
//							for(String ii : dt.getItem().split(";")){
//								String[] item = ii.split(",");
//								Item old = ResourceService.ins().addItem(player, Integer.parseInt(item[0]), 0-Integer.parseInt(item[1]), false);
//								ll.add(old);
//							}
//						}
//						
//						RewardDto dto = ResourceService.ins().getRewardDto("1,2,100;3,200001,10", "100");
//						reward = ResourceService.ins().getRewardString(dto);
//						ResourceService.ins().addRewarToPlayer(player, dto);
//						MessageUtil.notiyItemChange(player, ll);
//						ResourceService.ins().addDrawExp(player, dt.getDrawExp());
//					}
//				}else 
				if(type == 1){
					if(number == 0){
						number = 1;
					}
					boolean can = true;
					List<Item> ll = Lists.newArrayList();	
					if(!MyUtil.isNullOrEmpty(dt.getItem())){
											
						for(String ii : dt.getItem().split(";")){
							String[] item = ii.split(",");
							if(!player.getItems().containsKey(Integer.parseInt(item[0])) ||
									player.getItems().get(Integer.parseInt(item[0])).getUsableCount(-1) < Integer.parseInt(item[1]) * number){
								can = false;
								break;
							}
						}
					}
					if(!can){
						ret = ErrorCode.ITEM_NOT_ENOUGH;//道具不足
					}else{
						if(!MyUtil.isNullOrEmpty(dt.getItem())){
							
							for(String ii : dt.getItem().split(";")){
								String[] item = ii.split(",");
								Item old = ResourceService.ins().addItem(player, Integer.parseInt(item[0]), 0-Integer.parseInt(item[1]) * number, false);
								ll.add(old);
							}
						}
						RewardDto dto = new RewardDto();
						reward = "";
						for(int i = 1;i<=number;i++){
							List<RewardDto> ls = PlayerService.couKa(dt.getRewardId(), dt.getDrawValue());
							for(RewardDto temp : ls){
								reward += ";" + ResourceService.ins().getRewardString(temp);
								ResourceService.ins().getTotalRewardDto(dto, temp);								
							}
						}
//						reward = ResourceService.ins().getRewardString(dto);
						if(reward.length() > 0){
							reward = reward.substring(1);
						}
						ResourceService.ins().addRewarToPlayer(player, dto);
						MessageUtil.notiyItemChange(player, ll);
						ResourceService.ins().addDrawExp(player, dt.getDrawExp() * number);
					}
				}else{
					if(player.getDiamond() < dt.getDiamond()){
						ret = ErrorCode.DIAMOND_NOT_ENOUGH;//钻石不足
					}else{
						ResourceService.ins().addDiamond(player, 0-dt.getDiamond());
						RewardDto dto = new RewardDto();
						List<RewardDto> ls  =  PlayerService.couKa(dt.getRewardId(), dt.getDrawValue());
						for(RewardDto temp : ls){
							reward += ";" + ResourceService.ins().getRewardString(temp);
							ResourceService.ins().getTotalRewardDto(dto, temp);							
						}
//						reward = ResourceService.ins().getRewardString(dto);
						if(reward.length() > 0){
							reward = reward.substring(1);
						}
						ResourceService.ins().addRewarToPlayer(player, dto);
						ResourceService.ins().addDrawExp(player, dt.getDrawExp());
					}

				}
			}
		}
		
		GoldLog.info(player.getSeverId(), player.getUserId(), player.getPlayerId(), GoldLog.DRAWGET, "#drawType:" + drawType+"#type:"+type+"#drawLv:"+drawLv);

		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}
		vo.addData("drawType", drawType);
		vo.addData("type", type);
		vo.addData("number", number);
		vo.addData("reward", reward);

		send(MProtrol.toStringProtrol(MProtrol.DRAW_GET), vo, user);
	}

	
}
