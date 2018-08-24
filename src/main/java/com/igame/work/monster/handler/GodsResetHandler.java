package com.igame.work.monster.handler;


import com.google.common.collect.Lists;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.core.log.GoldLog;
import com.igame.work.checkpoint.guanqia.RewardDto;
import com.igame.work.fight.FightDataManager;
import com.igame.work.fight.data.GodsdataTemplate;
import com.igame.work.monster.dto.Gods;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author Marcus.Z
 *
 */
public class GodsResetHandler extends ReconnectedHandler {
	

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int godsType = jsonObject.getInt("godsType");
		String reward;
		Gods gods = player.getGods().get(godsType);

		if(gods == null){
			return error(ErrorCode.ERROR);
		}else{
			List<GodsdataTemplate> preList = FightDataManager.GodsData.getPreList(gods.getGodsType(), gods.getGodsLevel());
			if(preList == null || preList.isEmpty()){
				return error(ErrorCode.GODS_ZERO);
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
				MessageUtil.notifyGodsChange(player, ll);
				GoldLog.info(player.getSeverId(), player.getUserId(), player.getPlayerId(), GoldLog.GODSRESET, "#godsType:" + godsType +"#lvl:"+lvl);

			}
		}

		vo.addData("gods", gods);
		vo.addData("reward", reward);

		return vo;
	}

	@Override
	protected int protocolId() {
		return MProtrol.Gods_RESET;
	}
}
