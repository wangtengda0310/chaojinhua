package com.igame.work.chat.handler;

import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.chat.dto.MessageBoard;
import com.igame.work.chat.service.MessageBoardService;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.Date;

import static com.igame.work.chat.MessageContants.MSG_LENGTH_MAX;

/**
 * @author xym
 *
 * 留言板新增留言
 */
public class MessageBoardAddHandler extends ReconnectedHandler {

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

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
            return error(ErrorCode.PARAMS_INVALID);
        }

        //校验发送间隔
        /*Date date = player.getLastMessageBoard().get(sType);
        if (date != null && new Date().getTime() - date.getTime() < 60*60*1000){
            return error(ErrorCode.SHORT_INTERVAL_TIME, MProtrol.toStringProtrol(MProtrol.MESSAGE_BOARD_ADD),vo,user);

        }*/

        //校验消息字节
        byte[] buff = content.getBytes();
        if(buff.length > MSG_LENGTH_MAX){
            return error(ErrorCode.MESSAGE_TOO_LONG);
        }

        MessageBoard messageBoard = MessageBoardService.ins().addMessageBoard(player.getSeverId(), sType, player.getPlayerId(), player.getUserId(),content);

        //更新当前留言板留言时间
        player.getLastMessageBoard().put(sType,new Date());

        vo.addData("objectId",messageBoard.get_id().toHexString());
        return vo;
    }

    @Override
    protected int protocolId() {
        return MProtrol.MESSAGE_BOARD_ADD;
    }

}
