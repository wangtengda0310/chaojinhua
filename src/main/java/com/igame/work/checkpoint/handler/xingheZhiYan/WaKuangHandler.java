package com.igame.work.checkpoint.handler.xingheZhiYan;




import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.core.data.DataManager;
import com.igame.core.data.template.TrialdataTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.work.checkpoint.dto.RewardDto;
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
public class WaKuangHandler extends BaseHandler{
	

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
		int ret = 0;
		String reward = "";
		TrialdataTemplate ct = DataManager.ins().TrialData.getTemplate(player.getTowerId());
		if(ct == null){
			ret = ErrorCode.ERROR;
		}else{
			if(player.getOreCount() >= 1){
				ret = ErrorCode.WA_NOT_ENOUGH;
			}else{
				RewardDto rt = new RewardDto();
				rt.addGold(ct.getGold());
				ResourceService.ins().addRewarToPlayer(player, rt);
				reward = ResourceService.ins().getRewardString(rt);
				player.setOreCount(player.getOreCount() + 1);
				MessageUtil.notiyTrialChange(player);
			}
		}

		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}
		vo.addData("reward", reward);

		send(MProtrol.toStringProtrol(MProtrol.TRIAL_WA), vo, user);
	}

	
}
