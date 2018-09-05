package com.igame.work.checkpoint.xingheZhiYan.handler;


import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.checkpoint.guanqia.RewardDto;
import com.igame.work.checkpoint.xingheZhiYan.TrialdataTemplate;
import com.igame.work.checkpoint.xingheZhiYan.XingheZhiYanDataManager;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class WaKuangHandler extends ReconnectedHandler {

	private ResourceService resourceService;

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		String reward;
		TrialdataTemplate ct = XingheZhiYanDataManager.TrialData.getTemplate(player.getTowerId());
		if(ct == null){
			return error(ErrorCode.ERROR);
		}else{
			if(player.getOreCount() >= 1){
				return error(ErrorCode.WA_NOT_ENOUGH);
			}else{
				RewardDto rt = new RewardDto();
				rt.addGold(ct.getGold());
				resourceService.addRewarToPlayer(player, rt);
				reward = resourceService.getRewardString(rt);
				player.setOreCount(player.getOreCount() + 1);
				MessageUtil.notifyTrialChange(player);
			}
		}

		vo.addData("reward", reward);

		return vo;
	}

	@Override
    public int protocolId() {
		return MProtrol.TRIAL_WA;
	}

}
