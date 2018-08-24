package com.igame.work.monster.handler;


import com.google.common.collect.Lists;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * 
 * @author Marcus.Z
 *
 */
public class MonsterListHandler extends ReconnectedHandler {
	

	@Override
	public RetVO handleClientRequest(Player player, ISFSObject params) {
		
		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);
		
		RetVO vo = new RetVO();

		int perSize = 100;
		
		int index = 0;
		int total = player.getMonsters().size() >=perSize ? (player.getMonsters().size()%perSize==0?player.getMonsters().size()/perSize:player.getMonsters().size()/perSize+1):1;
		
		int size = 0;
		List<Monster> ls = Lists.newArrayList();
		for(Monster monster :  player.getMonsters().values()){
			size++;
			ls.add(monster);
			if((size >=perSize && size%perSize==0) || size == player.getMonsters().size()){
				index++;
				MessageUtil.notiyMonsterList(player, total, index, ls);
				ls.clear();
			}
		}
		
		vo.addData("total", total);
		return vo;
	}

	@Override
	protected int protocolId() {
		return MProtrol.MONSTER_INFO;
	}

}
