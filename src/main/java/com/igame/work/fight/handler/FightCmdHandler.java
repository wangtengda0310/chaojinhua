package com.igame.work.fight.handler;






import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.fight.dto.FightCmd;
import com.igame.work.fight.dto.RetFightCmd;
import com.igame.work.fight.service.FightProcessser;
import com.igame.work.fight.service.PVPFightService;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

/**
 * 
 * @author Marcus.Z
 *
 */
public class FightCmdHandler extends BaseHandler{
	

	
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
		
		int ret = 0;

		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}
		vo.addData("c", ls);
		if(player.getFightBase() == null){
			vo.addData("winner", -1);
		}else{
			vo.addData("winner", player.getFightBase().getWinner());
		}
		

		send(MProtrol.toStringProtrol(MProtrol.F_CMD), vo, user);

	}

	
}
