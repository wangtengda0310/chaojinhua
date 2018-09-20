package com.igame.work.serverList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.igame.core.di.Inject;
import com.igame.core.handler.ClientDispatcherHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.MProtrol;
import com.igame.work.user.dao.PlayerDAO;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * 
 * @author Marcus.Z
 *
 */
public class ServerListHandler extends ClientDispatcherHandler {

	private static String notice = "公告信息公告信息";

	@Inject private PlayerDAO playerDAO;
	@Inject private ServerManager serverManager;

	private ObjectMapper mapper = new ObjectMapper();

	@Override
	public void handleClientRequest(User user, ISFSObject params) {
		
		trace("选择服务器----- userId:" + user.getName());
		RetVO vo = new RetVO();
		int index = params.getInt("index");
		vo.setIndex(index);
		//获取所有服务器此用户ID下的角色
		Map<Integer,Player> all = playerDAO.getAllUser(Long.parseLong(user.getName()));

		serverManager.servers = getParentExtension().getParentZone().getZoneManager().getZoneList().stream()
                .filter(zone -> zone.getExtension()!=null && zone.getExtension().getConfigProperties()!=null)
				.filter(zone -> !zone.getRoomList().isEmpty())
				.map(zone -> {
					String serverId = zone.getExtension().getConfigProperties().getProperty("serverId");
					String serverName = getParentExtension().getConfigProperties().getProperty("serverName",zone.getName());
					int roomId = zone.getRoomList().stream().map(Room::getId).findAny().orElse(1);
					return new ServerInfo(serverId, serverName, zone.getName(), (int) (Math.random() * 3), roomId);
				})
				.peek(serverInfo-> {
					System.out.println(serverInfo.getServerId());
					System.out.println(serverInfo.serverId);
					System.out.println(serverInfo.serverName);
				})
				.peek(serverInfo -> serverInfo.setHas(all.containsKey(Integer.parseInt(serverInfo.getServerId()))))
				.peek(serverInfo -> {
					if (1 == Integer.parseInt(serverInfo.getServerId())) {
						vo.addData("currentInfo", serverInfo);
					}})
				.collect(Collectors.toList());
		vo.addData("serverList", serverManager.servers);
		vo.addData("notice", notice);

		String json = "";
		try {
			json = mapper.writeValueAsString(vo);
		} catch (JsonProcessingException e) {
			this.getLogger().warn("mapper RetVO error", e);
		}

		ISFSObject res = new SFSObject();
		res.putUtfString("infor", json);

		send(MProtrol.toStringProtrol(MProtrol.SERVER_LIST), res, user);
	}

	@Override
	public int protocolId() {
		return MProtrol.SERVER_LIST;
	}
}
