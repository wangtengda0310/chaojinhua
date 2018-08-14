package com.igame.work.gm.handler;



import java.util.List;

import net.sf.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.log.GoldLog;
import com.igame.dto.RetVO;
import com.igame.work.gm.service.GMService;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.api.ISFSApi;
import com.smartfoxserver.v2.api.ISFSGameApi;
import com.smartfoxserver.v2.api.SFSApi;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.entities.invitation.Invitation;
import com.smartfoxserver.v2.entities.invitation.InvitationCallback;
import com.smartfoxserver.v2.entities.invitation.SFSInvitation;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

/**
 * 
 * @author Marcus.Z
 *
 */
public class TestHandler extends BaseClientRequestHandler{
	

	@Override
	public void handleClientRequest(User user, ISFSObject params) {
		
		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		Player player = SessionManager.ins().getSession(Long.parseLong(user.getName()));
		if(player == null){
			this.getLogger().error(this.getClass().getSimpleName()," get player failed Name:" +user.getName());
			return;
		}

//		String test = jsonObject.getString("test");
		//请求
		/*ISFSObject friendReq = new SFSObject();
		friendReq.putByte("type", (byte)1);
		ISFSGameApi gameAPI = SmartFoxServer.getInstance().getAPIManager().getGameApi();
		ISFSApi sFSApi = SmartFoxServer.getInstance().getAPIManager().getSFSApi();
		Invitation invitation = new SFSInvitation(user, user, 10,friendReq);


        gameAPI.sendInvitation(invitation, new InvitationCallback()
        {
            @Override
            public void onRefused(Invitation invObj, ISFSObject params)
            {
            	trace("onRefusedonRefusedonRefused");
            }

            @Override
            public void onExpired(Invitation invObj)
            {
            	trace("onExpiredonExpiredonExpired");
            }

            @Override
            public void onAccepted(Invitation invObj, ISFSObject params)
            {
            	trace("onAcceptedonAcceptedonAccepted");
            }
        });

        User sender = this.getApi().getUserByName("1000112");


        sFSApi.sendPrivateMessage(sender, user, "message", friendReq);
        List<User> ls = sender.getLastJoinedRoom().getUserList();*/


		RetVO vo = new RetVO();
//		vo.addData("test", test);
		ObjectMapper mapper = new ObjectMapper();
		String json = null;
		ISFSObject res = new SFSObject();

		try {
			json = mapper.writeValueAsString(vo);
		} catch (JsonProcessingException e) {
			this.getLogger().warn("TestHandler  error", e);
		}

		res.putUtfString("infor", json);
//		GoldLog.info("TestHandlerTestHandlerTestHandlerTestHandlerTestHandlerTestHandler");
//		trace("TestHandlerTestHandlerTestHandlerTestHandlerTestHandlerTestHandler");
//		send(MProtrol.toStringProtrol(MProtrol.TEST), res, user,true);
		send(MProtrol.toStringProtrol(MProtrol.TEST), res, user);
	}

	
}
