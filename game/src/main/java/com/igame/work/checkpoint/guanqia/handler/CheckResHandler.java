package com.igame.work.checkpoint.guanqia.handler;


import com.igame.core.di.Inject;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.work.checkpoint.guanqia.CheckPointService;
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

	@Inject
	private ResourceService resourceService;
	@Inject
	private CheckPointService checkPointService;

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		String chapterIdstr = jsonObject.getString("chapterId");
		StringBuilder reward = new StringBuilder();
		for(String temp: chapterIdstr.split(",")){
			int chapterId = Integer.parseInt(temp);
			CheckPointTemplate ct = checkPointService.checkPointData.getTemplate(chapterId);
			synchronized (checkPointService.getTimeLock(player)) {
				if(!player.getTimeResCheck().containsKey(chapterId) || ct == null || ct.getChapterType() != 2){
//					ret = ErrorCode.CHECKPOINT_RESNOT_EXIT;
				}else{
					int timeCount = player.getTimeResCheck().get(chapterId);
					if(timeCount < 60){
//						ret = ErrorCode.CHECKPOINT_RESNOT_EXIT;
					}else{
						RewardDto dto = checkPointService.getResRewardDto(ct.getDropPoint(), timeCount, ct.getMaxTime() * 60);
						reward.append(";").append(ResourceService.getRewardString(dto));//返回字符串
						resourceService.addRewarToPlayer(player, dto);//真实给玩家加东西
						player.getTimeResCheck().put(chapterId, timeCount % 60);
						MessageUtil.notifyTimeResToPlayer(player, chapterId, new RewardDto());
					}
				}
			}
		}


		if(reward.length() > 0){
			reward = new StringBuilder(reward.substring(1));
		}

		vo.addData("reward", reward.toString());
		vo.addData("chapterId", chapterIdstr);

		return vo;
	}

	@Override
    public int protocolId() {
		return MProtrol.CHECKPOINT_RES_GET;
	}

}
