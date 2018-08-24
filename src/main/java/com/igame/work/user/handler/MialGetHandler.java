package com.igame.work.user.handler;


import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.core.log.GoldLog;
import com.igame.util.MyUtil;
import com.igame.work.checkpoint.guanqia.RewardDto;
import com.igame.work.user.dto.Mail;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class MialGetHandler extends ReconnectedHandler {


	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int id = jsonObject.getInt("id");
		int type = jsonObject.getInt("type");
		String reward;
		Mail mail = player.getMail().get(id);
		if(mail == null || mail.getState() == 2 || mail.getType() != 1 || MyUtil.isNullOrEmpty(mail.getAttach())){
			return error(ErrorCode.ERROR);
		}else if (player.getItems().size() >= player.getBagSpace()){
			return error(ErrorCode.BAGSPACE_ALREADY_FULL);
		}else{
			
			RewardDto rto = ResourceService.ins().getRewardDto(mail.getAttach(), "100");
			ResourceService.ins().addRewarToPlayer(player, rto);
			reward = mail.getAttach();
			mail.setState(2);
			if(type == 1){
				mail.setDtate(3);
			}else{
				mail.setDtate(2);
			}
			GoldLog.info(player.getSeverId(), player.getUserId(), player.getPlayerId(), GoldLog.MAILGET, "#mailId:" + mail.getId() +"#attach:"+mail.getAttach());
		}

		vo.addData("id", id);
		vo.addData("type", type);
		vo.addData("state", mail.getState());
		vo.addData("reward", reward);

		return vo;
	}

	@Override
	protected int protocolId() {
		return MProtrol.MAIL_GET;
	}

}
