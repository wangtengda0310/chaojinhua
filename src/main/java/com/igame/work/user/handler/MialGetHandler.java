package com.igame.work.user.handler;


import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.core.log.GoldLog;
import com.igame.core.handler.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.checkpoint.guanqia.RewardDto;
import com.igame.work.user.dto.Mail;
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
public class MialGetHandler extends BaseHandler{
	

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
		int id = jsonObject.getInt("id");
		int type = jsonObject.getInt("type");
		int ret = 0;
		String reward = null;
		Mail mail = player.getMail().get(id);
		if(mail == null || mail.getState() == 2 || mail.getType() != 1 || MyUtil.isNullOrEmpty(mail.getAttach())){
			ret = ErrorCode.ERROR;
		}else if (player.getItems().size() >= player.getBagSpace()){
			ret = ErrorCode.BAGSPACE_ALREADY_FULL;
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

		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}
		vo.addData("id", id);
		vo.addData("type", type);
		vo.addData("state", mail.getState());
		vo.addData("reward", reward);

		send(MProtrol.toStringProtrol(MProtrol.MAIL_GET), vo, user);
	}

	
}
