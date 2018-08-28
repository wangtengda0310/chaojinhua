package com.igame.work.checkpoint.guanqia.handler;


import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.checkpoint.guanqia.GuanQiaDataManager;
import com.igame.work.checkpoint.guanqia.RewardDto;
import com.igame.work.checkpoint.guanqia.data.CheckPointTemplate;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class CheckResHandler extends ReconnectedHandler {
	

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		String chapterIdstr = jsonObject.getString("chapterId");
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
						MessageUtil.notifyTimeResToPlayer(player, chapterId, new RewardDto());
					}
				}
			}
		}


		if(reward.length() > 0){
			reward = reward.substring(1);
		}
		vo.addData("chapterId", chapterIdstr);
		vo.addData("reward", reward);

		return vo;
	}

	@Override
    public int protocolId() {
		return MProtrol.CHECKPOINT_RES_GET;
	}

}
