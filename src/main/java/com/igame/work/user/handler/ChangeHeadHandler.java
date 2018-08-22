package com.igame.work.user.handler;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.work.user.PlayerDataManager;
import com.igame.work.user.data.HeadTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * @author xym
 *
 * 更换头像
 */
public class ChangeHeadHandler extends BaseHandler {

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

        int headId = jsonObject.getInt("headId");

        vo.addData("headId",headId);

        //校验头像是否存在
        HeadTemplate template = PlayerDataManager.headData.getTemplate(headId);
        if (template == null){
            sendError(ErrorCode.PARAMS_INVALID,MProtrol.toStringProtrol(MProtrol.HEAD_CHANGE),vo,user);
            return;
        }

        //校验头像是否解锁
        if (!player.getUnlockHead().contains(headId)){
            sendError(ErrorCode.HEAD_UNLOCK,MProtrol.toStringProtrol(MProtrol.HEAD_CHANGE),vo,user);
            return;
        }

        //更换头像
        player.setPlayerHeadId(headId);

        sendSucceed(MProtrol.toStringProtrol(MProtrol.HEAD_CHANGE),vo,user);
    }
}