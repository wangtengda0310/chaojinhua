package com.igame.work.checkpoint.handler.xingheZhiYan;




import com.igame.work.checkpoint.GuanQiaDataManager;
import net.sf.json.JSONObject;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.work.checkpoint.data.TrialdataTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.checkpoint.dto.RewardDto;
import com.igame.work.quest.service.QuestService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class TrialEndHandler extends BaseHandler{
	

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
		int ret = 0;
		int playerExp = 0;
		String monsterExpStr = null;
		String reward = "";
		TrialdataTemplate ct = GuanQiaDataManager.TrialData.getTemplate(player.getTowerId() + 1);
		if(ct == null){
			ret = ErrorCode.CHECKPOINT_END_ERROR;
		}else{
			RewardDto rt = new RewardDto();
			if(win == 1){
				rt = ResourceService.ins().getRewardDto(ct.getReward(),"100");
				ResourceService.ins().addRewarToPlayer(player, rt);
				reward = ResourceService.ins().getRewardString(rt);
//				ResourceService.ins().addExp(player, wt.getPhysical() * 5);
//				playerExp = wt.getPhysical() * 5;
				player.setTowerId(player.getTowerId() + 1);
				MessageUtil.notiyTrialChange(player);
			}
			QuestService.processTask(player, 12, 1);
			
			//怪物经验
//			List<Monster> ll = Lists.newArrayList();
//			String[] monsters = player.getTeams()[0].split(",");				
//			for(String mid : monsters){
//				if(!"-1".equals(mid)){
//					Monster mm = player.getMonsters().get(Long.parseLong(mid));					
//					if(mm != null){	
//						int mmExp = CheckPointService.getTotalExp(mm, wt.getPhysical() * 5);
//						monsterExpStr += mid;
//						if(win == 1 && ResourceService.ins().addMonsterExp(player, Long.parseLong(mid), mmExp, false) == 0){
//							ll.add(mm);
//							monsterExpStr += ("," + mmExp +";");
//						}else{
//							monsterExpStr += ",0;";
//						}
//					}
//				}
//			}
//			MessageUtil.notiyMonsterChange(player, ll);
//			if(monsterExpStr.lastIndexOf(";") >0){
//				monsterExpStr = monsterExpStr.substring(0,monsterExpStr.lastIndexOf(";"));
//			}

			
		}
		
		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}
		vo.addData("win", win);
//		vo.addData("playerExp", playerExp);
//		vo.addData("monsterExp", monsterExpStr);
		vo.addData("reward", reward);

		send(MProtrol.toStringProtrol(MProtrol.TRIAL_END), vo, user);
	}

	
}
