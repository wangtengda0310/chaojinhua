package com.igame.work.quest.handler;


import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.work.quest.QuestDataManager;
import com.igame.work.quest.data.QuestTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.quest.dto.TaskDayInfo;
import com.igame.work.quest.service.QuestService;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class QuestRewardHandler extends BaseHandler{
	

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

		int questId = jsonObject.getInt("questId");
		vo.addData("questId", questId);

		TaskDayInfo td = player.getAchievement().get(questId);
		QuestTemplate qt = QuestDataManager.QuestData.getTemplate(questId);
		if(td == null || qt == null || td.getVars() < qt.getFinish() || td.getStatus() == 3){
			sendError(ErrorCode.QUEST_REWARD_NOT,MProtrol.toStringProtrol(MProtrol.QUEST_REWARD), vo, user);
			return;
		}

		//校验背包
		if (player.getItems().size() >= player.getBagSpace()){
			sendError(ErrorCode.BAGSPACE_ALREADY_FULL,MProtrol.toStringProtrol(MProtrol.QUEST_REWARD), vo, user);
			return;
		}

		String reward = QuestService.getReward(player, td);

		vo.addData("reward", reward);

		sendSucceed(MProtrol.toStringProtrol(MProtrol.QUEST_REWARD), vo, user);
	}

	
}
