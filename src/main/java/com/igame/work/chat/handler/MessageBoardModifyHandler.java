package com.igame.work.chat.handler;

import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.chat.service.MessageBoardService;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * @author xym
 *
 * 修改留言板(点赞、取消点赞、反对、取消反对)
 */
public class MessageBoardModifyHandler extends BaseHandler{

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

        String likeModify = jsonObject.getString("likeModify");
        String disLikeModify = jsonObject.getString("disLikeModify");

        vo.addData("likeModify",likeModify);
        vo.addData("disLikeModify",disLikeModify);

        for (String messageId : likeModify.split(",")) {
            MessageBoardService.ins().likeMessageBoard(player,messageId);
        }

        for (String messageId : disLikeModify.split(",")) {
            MessageBoardService.ins().dislikeMessageBoard(player,messageId);
        }

        sendSucceed(MProtrol.toStringProtrol(MProtrol.MESSAGE_BOARD_MODIFY),vo,user);
    }
}
