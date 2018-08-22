package com.igame.work.chat.handler;

import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.work.chat.dto.PublicMessageDto;
import com.igame.work.chat.service.PublicMessageService;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xym
 *
 * 获取世界消息
 */
public class PublicMessageHandler extends BaseHandler{

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

        //世界消息
        List<PublicMessageDto> worldMsg = new ArrayList<>();
        PublicMessageService.ins().getWorldMessage(player.getSeverId()).forEach(message -> worldMsg.add(new PublicMessageDto(message)));

        //喇叭消息
        List<PublicMessageDto> hornMsg = new ArrayList<>();
        PublicMessageService.ins().getHornMessage(player.getSeverId()).forEach(message -> hornMsg.add(new PublicMessageDto(message)));

        vo.addData("worldMsg", worldMsg);
        vo.addData("hornMsg", hornMsg);
        vo.addData("clubMsg", new ArrayList<>());
        sendSucceed(MProtrol.toStringProtrol(MProtrol.MESSAGE_WORLD),vo,user);
    }
}
