package com.igame.work.monster.handler;



import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.core.data.DataManager;
import com.igame.core.data.template.GodsdataTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.core.log.GoldLog;
import com.igame.dto.RetVO;
import com.igame.work.checkpoint.dto.RewardDto;
import com.igame.work.monster.dto.Gods;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

/**
 * 
 * @author Marcus.Z
 *
 */
public class GodsResetHandler extends BaseHandler{
	

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
		
		int godsType = jsonObject.getInt("godsType");
		int ret = 0;
		String reward = "";
		Gods gods = player.getGods().get(godsType);

		if(gods == null){
			ret = ErrorCode.ERROR;
		}else{
			List<GodsdataTemplate> preList = DataManager.ins().GodsData.getPreList(gods.getGodsType(), gods.getGodsLevel());
			if(preList == null || preList.isEmpty()){
				ret = ErrorCode.GODS_ZERO;
			}else{
				RewardDto rt = new RewardDto();
				for(GodsdataTemplate gt : preList){
					rt.addGold(gt.getGold());
					for(Map.Entry<Integer,Integer> m : ResourceService.ins().getRewardDto(gt.getItem(), "100").getItems().entrySet()){
						rt.addItem(m.getKey(), m.getValue());
					}
				}
				int lvl = gods.getGodsLevel();
				ResourceService.ins().addRewarToPlayer(player, rt);
				gods.setGodsLevel(0);
				gods.setDtate(2);
				reward = ResourceService.ins().getRewardString(rt);
				List<Gods> ll = Lists.newArrayList();
				ll.add(gods);
				MessageUtil.notiyGodsChange(player, ll);		
				GoldLog.info(player.getSeverId(), player.getUserId(), player.getPlayerId(), GoldLog.GODSRESET, "#godsType:" + godsType +"#lvl:"+lvl);

			}
		}

		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}
		vo.addData("gods", gods);
		vo.addData("reward", reward);

		send(MProtrol.toStringProtrol(MProtrol.Gods_RESET), vo, user);
	}

	
}
