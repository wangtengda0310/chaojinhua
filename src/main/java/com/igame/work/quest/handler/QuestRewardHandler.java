package com.igame.work.quest.handler;


import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.quest.QuestDataManager;
import com.igame.work.quest.data.QuestTemplate;
import com.igame.work.quest.dto.TaskDayInfo;
import com.igame.work.quest.service.QuestService;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class QuestRewardHandler extends ReconnectedHandler {
	

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int questId = jsonObject.getInt("questId");
		vo.addData("questId", questId);

		TaskDayInfo td = player.getAchievement().get(questId);
		QuestTemplate qt = QuestDataManager.QuestData.getTemplate(questId);
		if(td == null || qt == null || td.getVars() < qt.getFinish() || td.getStatus() == 3){
			return error(ErrorCode.QUEST_REWARD_NOT);
		}

		//校验背包
		if (player.getItems().size() >= player.getBagSpace()){
			return error(ErrorCode.BAGSPACE_ALREADY_FULL);
		}

		String reward = QuestService.getReward(player, td);

		vo.addData("reward", reward);

		return vo;
	}

	@Override
    public int protocolId() {
		return MProtrol.QUEST_REWARD;
	}

}
