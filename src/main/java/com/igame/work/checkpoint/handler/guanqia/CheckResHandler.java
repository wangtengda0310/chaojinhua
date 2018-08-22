package com.igame.work.checkpoint.handler.guanqia;



import com.igame.work.checkpoint.GuanQiaDataManager;
import net.sf.json.JSONObject;

import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.work.checkpoint.data.CheckPointTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.work.checkpoint.dto.RewardDto;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class CheckResHandler extends BaseHandler{
	

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
		String chapterIdstr = jsonObject.getString("chapterId");
		int ret = 0;
		String reward = "";
		for(String temp: chapterIdstr.split(",")){
			int chapterId = Integer.parseInt(temp);
			CheckPointTemplate ct = GuanQiaDataManager.CheckPointData.getTemplate(chapterId);
			synchronized (player.getTimeLock()) {
				if(!player.getTimeResCheck().containsKey(chapterId) || ct == null || ct.getChapterType() != 2){
//					ret = ErrorCode.CHECKPOINT_RESNOT_EXIT;
				}else{
					int timeCount = player.getTimeResCheck().get(chapterId);
					if(timeCount < 60){
//						ret = ErrorCode.CHECKPOINT_RESNOT_EXIT;
					}else{
						RewardDto dto = ResourceService.ins().getResRewardDto(ct.getDropPoint(), timeCount, ct.getMaxTime() * 60);
						reward += ";"+ ResourceService.ins().getRewardString(dto);//返回字符串
						ResourceService.ins().addRewarToPlayer(player, dto);//真实给玩家加东西
						player.getTimeResCheck().put(chapterId, timeCount % 60);
						MessageUtil.notiyTimeResToPlayer(player, chapterId, new RewardDto());	
					}
				}
			}
		}


		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}
		if(reward.length() > 0){
			reward = reward.substring(1);
		}
		vo.addData("chapterId", chapterIdstr);
		vo.addData("reward", reward);

		send(MProtrol.toStringProtrol(MProtrol.CHECKPOINT_RES_GET), vo, user);
	}

	
}
