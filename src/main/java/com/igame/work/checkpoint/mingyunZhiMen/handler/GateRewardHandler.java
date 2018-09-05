package com.igame.work.checkpoint.mingyunZhiMen.handler;


import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.core.log.GoldLog;
import com.igame.work.checkpoint.guanqia.RewardDto;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class GateRewardHandler extends ReconnectedHandler {

	private ResourceService resourceService;

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		String reward;
//		if(player.getPlayerLevel() <18){
//			ret = ErrorCode.LEVEL_NOT;
//		}else{
			if(player.getFateData().getGetReward() == 1){
				return error(ErrorCode.GATE_NOT);
			}else if (player.getItems().size() >= player.getBagSpace()){
				return error(ErrorCode.BAGSPACE_ALREADY_FULL);
			}else{
//				if(player.getFateData().getTempBoxCount() != -1){
//					ret = ErrorCode.ERROR;
//				}else{
					GoldLog.info(player.getSeverId(), player.getUserId(), player.getPlayerId(), GoldLog.GATEREWARD, "#FateLevel:" + player.getFateData().getTodayFateLevel()+"#BoxCount:"+player.getFateData().getTodayBoxCount());
					int fl = (int)(player.getFateData().getTodayFateLevel() * 0.9);
					if(fl < 1){
						fl = 1;
					}
//					RankServiceDto.ins().setMRank(player);//排行榜
					player.getFateData().setFateLevel(fl);//更新历史最高层数
					player.getFateData().setTodayFateLevel(player.getFateData().getFateLevel());//设置当天更新层数
					player.getFateData().setGetReward(1);//更新是否领取宝箱
//					player.getFateData().setTodayBoxCount(0);//更新当天最大宝箱数
					player.getFateData().setTempBoxCount(-1);//更新今天临时宝箱数
					if(player.getFateData().getFirst() == 0){//更新是否第一次命运之门
						player.getFateData().setFirst(1);
					}
					player.getFateData().setTempSpecialCount(0);//更新临时特殊门刷数
					player.getFateData().setAddRate(0);//更新临时特殊门几率增长
					RewardDto rt = new RewardDto();
					resourceService.addRewarToPlayer(player, rt);
					reward = resourceService.getRewardString(rt);
					MessageUtil.notifyDeInfoChange(player);
					
//				}                                                                                                                                                                                                                                                                                                             
				
			}

//		}

		vo.addData("reward", reward);

		return vo;
	}

	@Override
    public int protocolId() {
		return MProtrol.GATE_REWARD;
	}

}
