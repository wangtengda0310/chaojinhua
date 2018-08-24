package com.igame.work.fight.handler;


import com.google.common.collect.Lists;
import com.igame.work.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.fight.dto.FightCmd;
import com.igame.work.fight.dto.RetFightCmd;
import com.igame.work.fight.service.FightProcessser;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * 
 * @author Marcus.Z
 *
 */
public class FightCmdHandler extends ReconnectedHandler {
	

	
	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		List<RetFightCmd> ls = Lists.newArrayList();
		String cmdStr = jsonObject.getString("c");
		if(!MyUtil.isNullOrEmpty(cmdStr)){
			List<FightCmd> cmdList = Lists.newArrayList();
			String[] cc = cmdStr.split(";");
			for(String c : cc){
				String[] cd = c.split(",");
				cmdList.add(new FightCmd(Integer.parseInt(cd[0]), Long.parseLong(cd[1]), Integer.parseInt(cd[2]), Integer.parseInt(cd[3]),Long.parseLong(cd[4])));
			}
			ls =  FightProcessser.ins().processCmdList(player.getFightBase(), cmdList);
		}
//		@SuppressWarnings("unchecked")
//		List<FightCmd> cmdList = (List<FightCmd>)JSONArray.toCollection(jsonObject.getJSONArray("c"), FightCmd.class);
//		if(cmdList != null && !cmdList.isEmpty()){
//			
//		}

		vo.addData("c", ls);
		if(player.getFightBase() == null){
			vo.addData("winner", -1);
		}else{
			vo.addData("winner", player.getFightBase().getWinner());
		}
		

		return vo;

	}

	@Override
	protected int protocolId() {
		return MProtrol.F_CMD;
	}

}
