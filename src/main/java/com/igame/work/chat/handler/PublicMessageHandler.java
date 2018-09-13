package com.igame.work.chat.handler;

import com.igame.core.di.Inject;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.MProtrol;
import com.igame.work.chat.dto.PublicMessageDto;
import com.igame.work.chat.service.PublicMessageService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.service.PlayerCacheService;
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

    @Inject private PublicMessageService publicMessageService;
    @Inject private PlayerCacheService playerCacheService;

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        //世界消息
        List<PublicMessageDto> worldMsg = new ArrayList<>();

        publicMessageService.getWorldMessage().forEach(message -> {
            Player playerCache = playerCacheService.getPlayerById(message.getSender());
            if (playerCache == null) {
                extensionHolder.SFSExtension.getLogger().warn("{} player cache is null",message);
            }
            worldMsg.add(new PublicMessageDto(message,playerCache));
        });

        //喇叭消息
        List<PublicMessageDto> hornMsg = new ArrayList<>();
        publicMessageService.getHornMessage().forEach(message -> {
            Player playerCache = playerCacheService.getPlayerById(message.getSender());
            if (playerCache == null) {
                extensionHolder.SFSExtension.getLogger().warn("{} player cache is null",message);
            } else {
                hornMsg.add(new PublicMessageDto(message,playerCache));
            }
        });

        RetVO vo = new RetVO();
        vo.addData("worldMsg", worldMsg);
        vo.addData("hornMsg", hornMsg);
        vo.addData("clubMsg", new ArrayList<>());
        return vo;
    }

    @Override
    public int protocolId() {
        return MProtrol.MESSAGE_WORLD;
    }

}
