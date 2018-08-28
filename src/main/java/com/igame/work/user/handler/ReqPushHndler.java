package com.igame.work.user.handler;






import com.google.common.collect.Lists;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.user.dto.MessageCache;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import javax.naming.OperationNotSupportedException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 * @author Marcus.Z
 *
 */
public class ReqPushHndler extends ReconnectedHandler {
	

	@Override
	public void handleClientRequest(User user, ISFSObject params) {
		// 这个方法跟ReconnectedHandler不太一样 需要先判断player是否为空 在重发消息
		// TODO 有机会找客户端问下能不能调整一下协议, 这样就能复用ReconnectedHandler了


		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);
		int index = jsonObject.getInt("index");
		RetVO vo = new RetVO();
		Player player = SessionManager.ins().getSession(Long.parseLong(user.getName()));
		if(player == null){
			this.getLogger().error(this.getClass().getSimpleName()," get player failed Name:" +user.getName());
			vo.addData("index", index);
			vo.addData("isReLogin", 1);
			sendClient(MProtrol.REQ_PUSH, vo, user);
			return;
		}
		player.setUser(user);
		
		if(cachedResponse(user,params)){
			return;
		}

		// TODO 这段代码跟ReconnectedHandler的cacheResponse方法很像呀
		synchronized (player.getProPushLock()) {
//			MessageCache mc = player.getProTuiMap().get(index);
//			if(mc != null || mc == null){
				List<MessageCache> ls = Lists.newArrayList();
				List<Integer> key = player.getProTuiMap().keySet().stream()
						.sorted(Comparator.comparingInt(h -> h))
						.collect(Collectors.toList());
			for(Integer id : key){
        			if(id > index){
        				ls.add(player.getProTuiMap().get(id));
        			}
        		}
        		for(MessageCache mm : ls){
        			MessageUtil.sendMessageToPlayerNoCache(player, mm);
        		}
//			}
		}
		
		vo.addData("index", index);
		vo.addData("isReLogin", 0);
		sendClient(MProtrol.REQ_PUSH, vo, user);
		
	}

	@Override
    public int protocolId() {
		return MProtrol.REQ_PUSH;
	}

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) throws OperationNotSupportedException {
		// 因为重写了handleClientRequest方法 这里不应该能走进来
		throw new OperationNotSupportedException();
	}


}
