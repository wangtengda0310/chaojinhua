package com.igame.work.draw;


import com.google.common.collect.Lists;
import com.igame.core.di.Inject;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.core.log.GoldLog;
import com.igame.util.MyUtil;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.work.PlayerEvents;
import com.igame.work.checkpoint.guanqia.RewardDto;
import com.igame.work.item.dto.Item;
import com.igame.work.user.PlayerService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * 
 * @author Marcus.Z
 *
 */
public class DrawGetHandler extends ReconnectedHandler {

	@Inject private ResourceService resourceService;
	@Inject private PlayerService playerService;
	@Inject private DrawService drawService;

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int drawType = jsonObject.getInt("drawType");
		int type = jsonObject.getInt("type");
		int number = 0;
		if(jsonObject.containsKey("number")){
			number = jsonObject.getInt("number");
		}
		int drawLv = player.getDraw().getDrawLv();
		StringBuilder reward;
		if(!player.hasCheckPointDraw(String.valueOf(drawType)) || (type != 1 && type != 2)){
			return error(ErrorCode.ERROR);
		}else if (player.getItems().size() >= player.getBagSpace()){
			return error(ErrorCode.BAGSPACE_ALREADY_FULL);
		}else{
			DrawdataTemplate dt  =  drawService.drawdataData.getTemplate(drawType+"_"+player.getDraw().getDrawLv());
			if(dt == null){
				return error(ErrorCode.ERROR);
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
//								Item old = resourceService.addItem(player, Integer.parseInt(item[0]), 0-Integer.parseInt(item[1]), false);
//								ll.add(old);
//							}
//						}
//						
//						RewardDto dto = resourceService.getRewardDto("1,2,100;3,200001,10", "100");
//						checkReward = resourceService.getRewardString(dto);
//						resourceService.addRewarToPlayer(player, dto);
//						MessageUtil.notifyItemChange(player, ll);
//						resourceService.addDrawExp(player, dt.getDrawExp());
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
						return error(ErrorCode.ITEM_NOT_ENOUGH);//道具不足
					}else{
						if(!MyUtil.isNullOrEmpty(dt.getItem())){
							
							for(String ii : dt.getItem().split(";")){
								String[] item = ii.split(",");
								Item old = resourceService.addItem(player, Integer.parseInt(item[0]), 0-Integer.parseInt(item[1]) * number, false);
								ll.add(old);
							}
						}
						RewardDto dto = new RewardDto();
						reward = new StringBuilder();
						for(int i = 1;i<=number;i++){
							List<RewardDto> ls = playerService.couKa(dt.getRewardId(), dt.getDrawValue());
							for(RewardDto temp : ls){
								reward.append(";").append(ResourceService.getRewardString(temp));
								resourceService.getTotalRewardDto(dto, temp);
							}
						}
//						checkReward = resourceService.getRewardString(dto);
						if(reward.length() > 0){
							reward = new StringBuilder(reward.substring(1));
						}
						resourceService.addRewarToPlayer(player, dto);
						MessageUtil.notifyItemChange(player, ll);
						resourceService.addDrawExp(player, dt.getDrawExp() * number);
					}
				}else{
					if(player.getDiamond() < dt.getDiamond()){
						return error(ErrorCode.DIAMOND_NOT_ENOUGH);//钻石不足
					}else{
						resourceService.addDiamond(player, 0-dt.getDiamond());
						RewardDto dto = new RewardDto();
						List<RewardDto> ls  =  playerService.couKa(dt.getRewardId(), dt.getDrawValue());
						reward = new StringBuilder();
						for(RewardDto temp : ls){
							reward.append(";").append(ResourceService.getRewardString(temp));
							resourceService.getTotalRewardDto(dto, temp);
						}
//						checkReward = resourceService.getRewardString(dto);
						if(reward.length() > 0){
							reward = new StringBuilder(reward.substring(1));
						}
						resourceService.addRewarToPlayer(player, dto);
						resourceService.addDrawExp(player, dt.getDrawExp());

						fireEvent(player, PlayerEvents.DRAW_BY_DIAMOND, number);
					}

				}
			}
		}
		
		GoldLog.info(player.getSeverId(), player.getUserId(), player.getPlayerId(), GoldLog.DRAWGET, "#drawType:" + drawType+"#type:"+type+"#drawLv:"+drawLv);

		vo.addData("drawType", drawType);
		vo.addData("type", type);
		vo.addData("number", number);
		vo.addData("reward", reward.toString());

		return vo;
	}

	@Override
    public int protocolId() {
		return MProtrol.DRAW_GET;
	}
}