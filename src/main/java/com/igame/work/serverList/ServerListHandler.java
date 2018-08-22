package com.igame.work.serverList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.igame.core.MProtrol;
import com.igame.dto.RetVO;
import com.igame.work.user.dao.PlayerDAO;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author Marcus.Z
 *
 */
public class ServerListHandler extends BaseClientRequestHandler{
	
	public static Map<Integer,ServerInfo> servers = Maps.newHashMap();
	
	private static String notice = "公告信息公告信息";
	
	static {
		servers.put(1,new ServerInfo(1, "一服", 1,1));
		servers.put(2,new ServerInfo(2, "二服", 2,2));
	}
	
	@Override
	public void handleClientRequest(User user, ISFSObject params) {
		
		trace("选择服务器----- userId:" + user.getName());
		RetVO vo = new RetVO();
		int index = params.getInt("index");
		vo.setIndex(index);
		//获取所有服务器此用户ID下的角色
		Map<Integer,Player> all = PlayerDAO.ins().getAllUser(Long.parseLong(user.getName()));
		ServerInfo curr = new ServerInfo(servers.get(1).getServerId(),servers.get(1).getServerName(),servers.get(1).getStatus(),servers.get(1).getWorldRoomId());
		if(all.get(servers.get(1).getServerId()) != null){
			curr.setHas(true);
		}
		vo.addData("currentInfo", curr); 
		List<ServerInfo> ss = Lists.newArrayList();
		for(ServerInfo si : servers.values()){
			ServerInfo temp = new ServerInfo(si.getServerId(),si.getServerName(),si.getStatus(),si.getWorldRoomId());
			if(all.get(si.getServerId()) != null){
				temp.setHas(true);
			}
			ss.add(si);
		}
		vo.addData("serverList", ss);
		vo.addData("notice", notice);
		ObjectMapper mapper = new ObjectMapper();
		String json = null;
		try {
			json = mapper.writeValueAsString(vo);
		} catch (JsonProcessingException e) {
			this.getLogger().warn("mapper RetVO error", e);
		}
		ISFSObject res = new SFSObject();
		res.putUtfString("infor", json);
//		JSONObject jo = new JSONObject();
//		jo.put("elves", "");

		send(MProtrol.toStringProtrol(MProtrol.SERVER_LIST), res, user);
	}
	
}
