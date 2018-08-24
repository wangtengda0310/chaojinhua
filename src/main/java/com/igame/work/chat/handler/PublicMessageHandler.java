package com.igame.work.chat.handler;

import com.igame.work.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.chat.dto.PublicMessageDto;
import com.igame.work.chat.service.PublicMessageService;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xym
 *
 * 获取世界消息
 */
public class PublicMessageHandler extends ReconnectedHandler {

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        //世界消息
        List<PublicMessageDto> worldMsg = new ArrayList<>();
        PublicMessageService.ins().getWorldMessage(player.getSeverId()).forEach(message -> worldMsg.add(new PublicMessageDto(message)));

        //喇叭消息
        List<PublicMessageDto> hornMsg = new ArrayList<>();
        PublicMessageService.ins().getHornMessage(player.getSeverId()).forEach(message -> hornMsg.add(new PublicMessageDto(message)));

        RetVO vo = new RetVO();
        vo.addData("worldMsg", worldMsg);
        vo.addData("hornMsg", hornMsg);
        vo.addData("clubMsg", new ArrayList<>());
        return vo;
    }

    @Override
    protected int protocolId() {
        return MProtrol.MESSAGE_WORLD;
    }

}
