package com.igame.work.checkpoint.xingheZhiYan.handler;


import com.igame.core.di.Inject;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.checkpoint.guanqia.RewardDto;
import com.igame.work.checkpoint.xingheZhiYan.TrialdataTemplate;
import com.igame.work.checkpoint.xingheZhiYan.XingheZhiYanService;
import com.igame.work.quest.service.QuestService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class TrialEndHandler extends ReconnectedHandler {
	@Inject private QuestService questService;
	@Inject private ResourceService resourceService;
	@Inject private XingheZhiYanService xingheZhiYanService;

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int win = jsonObject.getInt("win");
		String reward = "";
		TrialdataTemplate ct = xingheZhiYanService.trialData.getTemplate(player.getTowerId() + 1);
		if(ct == null){
			return error(ErrorCode.CHECKPOINT_END_ERROR);
		}else{
			if(win == 1){
				RewardDto rt = resourceService.getRewardDto(ct.getReward(),"100");
				resourceService.addRewarToPlayer(player, rt);
				reward = resourceService.getRewardString(rt);
//				resourceService.addExp(player, wt.getPhysical() * 5);
//				playerExp = wt.getPhysical() * 5;
				player.setTowerId(player.getTowerId() + 1);
				MessageUtil.notifyTrialChange(player);
			}
			questService.processTask(player, 12, 1);
			
			//怪物经验
//			List<Monster> ll = Lists.newArrayList();
//			String[] monsters = player.getTeams()[0].split(",");				
//			for(String mid : monsters){
//				if(!"-1".equals(mid)){
//					Monster mm = player.getMonsters().get(Long.parseLong(mid));					
//					if(mm != null){	
//						int mmExp = CheckPointService.getTotalExp(mm, wt.getPhysical() * 5);
//						monsterExpStr += mid;
//						if(win == 1 && resourceService.addMonsterExp(player, Long.parseLong(mid), mmExp, false) == 0){
//							ll.add(mm);
//							monsterExpStr += ("," + mmExp +";");
//						}else{
//							monsterExpStr += ",0;";
//						}
//					}
//				}
//			}
//			MessageUtil.notifyMonsterChange(player, ll);
//			if(monsterExpStr.lastIndexOf(";") >0){
//				monsterExpStr = monsterExpStr.substring(0,monsterExpStr.lastIndexOf(";"));
//			}

			
		}

		vo.addData("win", win);
//		vo.addData("playerExp", playerExp);
//		vo.addData("monsterExp", monsterExpStr);
		vo.addData("reward", reward);

		return vo;
	}

	@Override
    public int protocolId() {
		return MProtrol.TRIAL_END;
	}
}
