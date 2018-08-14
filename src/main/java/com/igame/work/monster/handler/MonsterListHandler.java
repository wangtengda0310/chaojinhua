package com.igame.work.monster.handler;


import com.google.common.collect.Lists;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * 
 * @author Marcus.Z
 *
 */
public class MonsterListHandler extends BaseHandler{
	

	@Override
	public void handleClientRequest(User user, ISFSObject params) {
		
		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);
		Player player = SessionManager.ins().getSession(Long.parseLong(user.getName()));
		if(player == null){
			this.getLogger().error(this.getClass().getSimpleName()," get player failed Name:" +user.getName());
			return;
		}
		
		
		RetVO vo = new RetVO();
		if(reviceMessage(user,params,vo)){
			return;
		}



		int ret = 0;

		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}
		
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
		send(MProtrol.toStringProtrol(MProtrol.MONSTER_INFO), vo, user);
	}

	
}
