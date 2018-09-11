package com.igame.work.chat.handler;

import com.igame.core.di.Inject;
import com.igame.work.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.chat.service.MessageBoardService;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * @author xym
 *
 * 修改留言板(点赞、取消点赞、反对、取消反对)
 */
public class MessageBoardModifyHandler extends ReconnectedHandler {

    @Inject private MessageBoardService messageBoardService;

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        String likeModify = jsonObject.getString("likeModify");
        String disLikeModify = jsonObject.getString("disLikeModify");

        vo.addData("likeModify",likeModify);
        vo.addData("disLikeModify",disLikeModify);

        for (String messageId : likeModify.split(",")) {
            messageBoardService.likeMessageBoard(player,messageId);
        }

        for (String messageId : disLikeModify.split(",")) {
            messageBoardService.dislikeMessageBoard(player,messageId);
        }

        return vo;
    }

    @Override
    public int protocolId() {
        return MProtrol.MESSAGE_BOARD_MODIFY;
    }

}
