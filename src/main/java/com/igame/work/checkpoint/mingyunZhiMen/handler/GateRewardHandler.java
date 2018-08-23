package com.igame.work.checkpoint.mingyunZhiMen.handler;


import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.core.log.GoldLog;
import com.igame.core.handler.RetVO;
import com.igame.work.checkpoint.guanqia.RewardDto;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class GateRewardHandler extends BaseHandler{
	

	@Override
	public void handleClientRequest(User user, ISFSObject params) {
		RetVO vo = new RetVO();
		if(reviceMessage(user,params,vo)){
			return;
		}

		Player player = SessionManager.ins().getSession(Long.parseLong(user.getName()));
		if(player == null){
			this.getLogger().error(this.getClass().getSimpleName()," get player failed Name:" +user.getName());
			return;
		}

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int ret = 0;
		String reward = "";
//		if(player.getPlayerLevel() <18){
//			ret = ErrorCode.LEVEL_NOT;
//		}else{
			if(player.getFateData().getGetReward() == 1){
				ret = ErrorCode.GATE_NOT;
			}else if (player.getItems().size() >= player.getBagSpace()){
				ret = ErrorCode.BAGSPACE_ALREADY_FULL;
			}else{
//				if(player.getFateData().getTempBoxCount() != -1){
//					ret = ErrorCode.ERROR;
//				}else{
					GoldLog.info(player.getSeverId(), player.getUserId(), player.getPlayerId(), GoldLog.GATEREWARD, "#FateLevel:" + player.getFateData().getTodayFateLevel()+"#BoxCount:"+player.getFateData().getTodayBoxCount());
					int fl = (int)(player.getFateData().getTodayFateLevel() * 0.9);
					if(fl < 1){
						fl = 1;
					}
//					RankService.ins().setMRank(player);//排行榜
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
					ResourceService.ins().addRewarToPlayer(player, rt);
					reward = ResourceService.ins().getRewardString(rt);
					MessageUtil.notiyDeInfoChange(player);
					
//				}                                                                                                                                                                                                                                                                                                             
				
			}

//		}

		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}
		vo.addData("reward", reward);

		send(MProtrol.toStringProtrol(MProtrol.GATE_REWARD), vo, user);
	}

	
}
