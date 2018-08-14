package com.igame.work.friend.handler;

import java.io.IOException;
import java.util.List;

import com.igame.work.friend.dto.FriendBuddyVariable;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.api.ISFSBuddyApi;
import com.smartfoxserver.v2.buddylist.Buddy;
import com.smartfoxserver.v2.buddylist.BuddyVariable;
import com.smartfoxserver.v2.buddylist.SFSBuddy;
import com.smartfoxserver.v2.buddylist.SFSBuddyEventParam;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.variables.VariableType;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;


/**
 * 
 * @author Marcus.Z
 *
 */
public class BuddyInitEventHandler extends BaseServerEventHandler{

	@Override
	public void handleServerEvent(ISFSEvent event) throws SFSException {
		trace("Start BuddyAddEventHandler!");
		
		User sender = (User) event.getParameter(SFSEventParam.USER);
		Buddy buddy = (Buddy) event.getParameter(SFSBuddyEventParam.BUDDY);
		trace("BuddyAdd sender name : "+sender.getName());
		trace("BuddyAdd recipient name : "+buddy.getName());

		
		
		SFSBuddy b = new SFSBuddy("ssss");


		
		FriendBuddyVariable bb= new FriendBuddyVariable();
		bb.setFriednDto();

//		buddys.setVariable();
//		buddy.setVariable();
	
		SmartFoxServer server = SmartFoxServer.getInstance();
		ISFSBuddyApi buddyApi = server.getAPIManager().getBuddyApi();
		
		
		try {
			List<Buddy> bs = buddyApi.initBuddyList(sender, true).getBuddies();

		} catch (IOException e) {
			e.printStackTrace();
		}
		buddyApi.addBuddy(sender, buddy.getName(), false, false, true);
		
	}
	
}
