package com.igame.work.checkpoint.xinmo;


import com.google.common.collect.Lists;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.util.GameMath;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.work.checkpoint.guanqia.RewardDto;
import com.igame.work.user.dto.Player;
import com.igame.work.user.dto.RobotDto;
import com.igame.work.user.load.ResourceService;
import com.igame.work.user.service.RobotService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Marcus.Z
 *
 */
public class XinmoEndHandler extends ReconnectedHandler {

	private ResourceService resourceService;
	private RobotService robotService;

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int win = jsonObject.getInt("win");
		int chapterId = jsonObject.getInt("chapterId");
		String reward = "";

		if(player.getXinMo().get(chapterId) == null){
			return error(ErrorCode.ERROR);
		}else{
			RewardDto rt = new RewardDto();
			if(win == 1){
				String removeId = "";
				List<XingMoDto> ls = Lists.newArrayList();
				resourceService.addRewarToPlayer(player, rt);
				reward = resourceService.getRewardString(rt);
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
					Map<String, RobotDto> robs = robotService.getRobot().get(player.getSeverId());

					if(!robs.isEmpty()){
						RobotDto rb = new ArrayList<>(robs.values()).get(GameMath.getRandInt(robs.size()));
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
				MessageUtil.notifyXingMoChange(player,removeId,ls);
			}
		}

		vo.addData("chapterId", chapterId);
		vo.addData("win", win);
		vo.addData("reward", reward);

		return vo;
	}

	@Override
	public int protocolId() {
		return MProtrol.XINGMO_END;
	}

}
