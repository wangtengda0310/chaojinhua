package com.igame.work.checkpoint.wujinZhiSen.handler;


import com.google.common.collect.Lists;
import com.igame.core.di.Inject;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.util.GameMath;
import com.igame.util.MyUtil;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.work.checkpoint.wujinZhiSen.EndlessService;
import com.igame.work.checkpoint.wujinZhiSen.EndlessdataTemplate;
import com.igame.work.fight.dto.MatchMonsterDto;
import com.igame.work.monster.dto.Monster;
import com.igame.work.monster.dto.WuEffect;
import com.igame.work.monster.service.MonsterService;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Marcus.Z
 *
 */
public class EndlessEnterHandler extends ReconnectedHandler {


	@Inject private EndlessService endlessService;
	@Inject private MonsterService monsterService;

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int buffer = jsonObject.getInt("buffer");

		int currIndex = 0;
		List<String> ll = Lists.newArrayList();
		ll.addAll(player.getWuMap().values());
		ll.sort(Comparator.comparingInt(h -> Integer.parseInt(h.split(";")[1])));
		int i = Integer.parseInt(ll.get(0).split(";")[0]);
		currIndex = i;
		int total = 0;
		List<MatchMonsterDto> lb = Lists.newArrayList();
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
		if(currIndex == 0 || player.getWuZheng().isEmpty()
				|| (currIndex != i && !"101,105,127,107".contains(String.valueOf(buffer)))){	// todo 1107 contains 107
			return error(ErrorCode.ERROR);
		}else{
			String[] ct = player.getWuMap().get(currIndex).split(";");
			String meetM = ct[3];

			EndlessdataTemplate edt = endlessService.endlessData.getTemplate(currIndex);

			if(!MyUtil.isNullOrEmpty(meetM)){
				boolean change = monsterService.isChange(player, meetM);
				if(change){
					MessageUtil.notifyMeetM(player);
				}

			}

			lb = monsterService.createMonsterDtoOfAll(Integer.parseInt(meetM));

			List<WuEffect> ls = Lists.newArrayList();
	    	if(buffer > 0 && currIndex != i && player.getWuEffect().size() < total){
				endlessService.tempBufferId.put(player.getPlayerId(), buffer);
	    		ls.addAll(player.getWuEffect());
	    		ls.add(new WuEffect(buffer));
				endlessService.notifyWuBufferChange(player,ls);
	    	}
	    	for(MatchMonsterDto mdt : player.getWuZheng().values()){
	    		mdt.reCalValue(ls);
	    	}


		}

		vo.addData("a", player.getWuZheng().values());
		vo.addData("m", lb);

		return vo;
	}

	@Override
    public int protocolId() {
		return MProtrol.WU_ENTER;
	}

}
