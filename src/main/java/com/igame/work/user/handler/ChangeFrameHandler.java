package com.igame.work.user.handler;

import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.user.PlayerDataManager;
import com.igame.work.user.data.HeadFrameTemplate;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * @author xym
 *
 * 更换头像框
 */
public class ChangeFrameHandler extends ReconnectedHandler {

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        int headFrameId = jsonObject.getInt("headFrameId");

        vo.addData("headFrameId",headFrameId);

        //校验头像框是否存在
        HeadFrameTemplate template = PlayerDataManager.headFrameData.getTemplate(headFrameId);
        if (template == null){
            return error(ErrorCode.PARAMS_INVALID);
        }

        //校验头像框是否解锁
        if (!player.getUnlockFrame().contains(headFrameId)){
            return error(ErrorCode.FRAME_UNLOCK);
        }

        //更换头像框
        player.setPlayerFrameId(headFrameId);

        return vo;
    }

    @Override
    public int protocolId() {
        return MProtrol.FRAME_CHANGE;
    }
}
