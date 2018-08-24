package com.igame.work.checkpoint.wujinZhiSen.handler;


import com.google.common.collect.Lists;
import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.checkpoint.guanqia.RewardDto;
import com.igame.work.monster.dto.WuEffect;
import com.igame.work.quest.service.QuestService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.Comparator;
import java.util.List;

/**
 * 
 * @author Marcus.Z
 *
 */
public class EndlessEndHandler extends ReconnectedHandler {
	

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int win = jsonObject.getInt("win");
		String monsHp = jsonObject.getString("monsHp");

		String reward = "";
		
		int currIndex;
		List<String> ll = Lists.newArrayList();
		ll.addAll(player.getWuMap().values());
		ll.sort(Comparator.comparingInt(h -> Integer.parseInt(h.split(";")[1])));
		currIndex = Integer.parseInt(ll.get(0).split(";")[0]);
		int total = 0;
		for(String m : ll){
			int num = Integer.parseInt(m.split(";")[0]);
			int guo = Integer.parseInt(m.split(";")[2]);
			if(guo == 0){
				currIndex = num;
				break;
			}
			total++;
		}
		if(total >= ll.size()){//已结全部过关
			currIndex = 0;
		}	
		
		if(currIndex == 0|| player.getWuZheng().isEmpty()){
			return error(ErrorCode.CHECKPOINT_END_ERROR);
		}else{
			RewardDto rt = new RewardDto();
			if(win == 1){
//				rt = ResourceService.ins().getRewardDto(ct.getReward(),"100");
				
				rt.addItem(200006, 2 * (total + 1));
				ResourceService.ins().addRewarToPlayer(player, rt);
				ResourceService.ins().addWuScore(player, 20 + 5 * total);
				String[] ct = player.getWuMap().get(currIndex).split(";");
				for(String mh : monsHp.split(";")){
					long oId = Long.parseLong(mh.split(",")[0]);
					int hp = Integer.parseInt(mh.split(",")[1]);
					if(player.getWuZheng().get(oId) != null && hp < player.getWuZheng().get(oId).getHpInit()){
						player.getWuZheng().get(oId).setHp(hp);
					}
				}
				reward = ResourceService.ins().getRewardString(rt);
				ct[2] = "1";
				player.getWuMap().put(currIndex, MyUtil.toString(ct, ";"));
				MessageUtil.notiyWuZhengChange(player);
				MessageUtil.notiyWuChange(player);
				if(player.getTempBufferId() > 0){
		    		player.getWuEffect().add(new WuEffect(player.getTempBufferId()));
		    		player.setTempBufferId(0);
				}
			}else{
				MessageUtil.notiyWuBufferChange(player,player.getWuEffect());
			}
			QuestService.processTask(player, 11, 1);

		}

		vo.addData("win", win);
		vo.addData("reward", reward);

		return vo;
	}

	@Override
	protected int protocolId() {
		return MProtrol.WU_END;
	}

}
