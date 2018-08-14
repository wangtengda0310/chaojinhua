package com.igame.work.checkpoint.handler;





import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.sf.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.util.GameMath;
import com.igame.work.checkpoint.dto.RewardDto;
import com.igame.work.checkpoint.dto.XingMoDto;
import com.igame.work.user.dto.Player;
import com.igame.work.user.dto.RobotDto;
import com.igame.work.user.load.ResourceService;
import com.igame.work.user.service.RobotService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

/**
 * 
 * @author Marcus.Z
 *
 */
public class XiongMoEndHandler extends BaseHandler{
	

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
		int win = jsonObject.getInt("win");
		int chapterId = jsonObject.getInt("chapterId");
		int ret = 0;
		String reward = "";

		if(player.getXinMo().get(chapterId) == null){
			ret = ErrorCode.ERROR;
		}else{
			RewardDto rt = new RewardDto();
			if(win == 1){
				String removeId = "";
				List<XingMoDto> ls = Lists.newArrayList();
				ResourceService.ins().addRewarToPlayer(player, rt);
				reward = ResourceService.ins().getRewardString(rt);
				player.getXinMo().remove(chapterId);
				int qiang = 0;
				for(XingMoDto xm : player.getXinMo().values()){
					if(xm.getType() == 1){
						qiang++;
					}
				}
				if(player.getXinMo().size() <25 && qiang < 2 && GameMath.hitRate100(5)){ //生成强化心魔
					XingMoDto xx = new XingMoDto();
					xx.setCheckPiontId(chapterId);
					Map<String, RobotDto> robs = RobotService.ins().getRobot().get(player.getSeverId());

					if(!robs.isEmpty()){
						RobotDto rb = robs.values().stream().collect(Collectors.toList()).get(GameMath.getRandInt(robs.size()));
						xx.setPlayerId(rb.getPlayerId());
						xx.setMid(rb.getName());
						xx.setPlayerFrameId(rb.getPlayerFrameId());
						xx.setPlayerHeadId(rb.getPlayerHeadId());
						xx.setType(1);
						xx.setStatTime(System.currentTimeMillis());
						player.getXinMo().put(chapterId, xx);
						ls.add(xx);
					}else{
						xx.setMid("");
					}
				
				}else{
					removeId = String.valueOf(chapterId);
				}
				MessageUtil.notiyXingMoChange(player,removeId,ls);
			}
		}

		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}
		vo.addData("chapterId", chapterId);
		vo.addData("win", win);
		vo.addData("reward", reward);

		send(MProtrol.toStringProtrol(MProtrol.XINGMO_END), vo, user);
	}

	
}
