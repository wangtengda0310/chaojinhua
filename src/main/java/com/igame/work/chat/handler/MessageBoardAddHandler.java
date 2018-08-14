package com.igame.work.chat.handler;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.work.chat.dto.MessageBoard;
import com.igame.work.chat.service.MessageBoardService;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.Date;

import static com.igame.work.chat.MessageContants.MSG_LENGTH_MAX;

/**
 * @author xym
 *
 * 留言板新增留言
 */
public class MessageBoardAddHandler extends BaseHandler{

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

        int type = jsonObject.getInt("type");
        int id = jsonObject.getInt("id");
        int difficulty = jsonObject.getInt("difficulty");
        String content = jsonObject.getString("content");

        vo.addData("type", type);
        vo.addData("id", id);
        vo.addData("difficulty", difficulty);
        vo.addData("content", content);

        String sType = MessageBoardService.ins().getSType(type,id,difficulty);
        if (sType.isEmpty()){
            sendError(ErrorCode.PARAMS_INVALID,MProtrol.toStringProtrol(MProtrol.MESSAGE_BOARD_ADD),vo,user);
            return;
        }

        //校验发送间隔
        /*Date date = player.getLastMessageBoard().get(sType);
        if (date != null && new Date().getTime() - date.getTime() < 60*60*1000){
            sendError(ErrorCode.SHORT_INTERVAL_TIME, MProtrol.toStringProtrol(MProtrol.MESSAGE_BOARD_ADD),vo,user);
            return;
        }*/

        //校验消息字节
        byte[] buff = content.getBytes();
        if(buff.length > MSG_LENGTH_MAX){
            sendError(ErrorCode.MESSAGE_TOO_LONG, MProtrol.toStringProtrol(MProtrol.MESSAGE_BOARD_ADD),vo,user);
            return;
        }

        MessageBoard messageBoard = MessageBoardService.ins().addMessageBoard(player.getSeverId(), sType, player.getPlayerId(), player.getUserId(),content);

        //更新当前留言板留言时间
        player.getLastMessageBoard().put(sType,new Date());

        vo.addData("objectId",messageBoard.get_id().toHexString());
        sendSucceed(MProtrol.toStringProtrol(MProtrol.MESSAGE_BOARD_ADD),vo,user);
    }


}
