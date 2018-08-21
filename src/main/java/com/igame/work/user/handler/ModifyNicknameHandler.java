package com.igame.work.user.handler;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * @author xym
 * <p>
 * 更换头像框
 */
public class ModifyNicknameHandler extends BaseHandler {

    @Override
    public void handleClientRequest(User user, ISFSObject params) {

        RetVO vo = new RetVO();
        if (reviceMessage(user, params, vo)) {
            return;
        }

        Player player = SessionManager.ins().getSession(Long.parseLong(user.getName()));
        if (player == null) {
            this.getLogger().error(this.getClass().getSimpleName(), " get player failed Name:" + user.getName());
            return;
        }

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        String nickname = jsonObject.getString("nickname");

        vo.addData("nickname", nickname);

        if (player.getLastNickname() != null) {
            if (player.getDiamond() < 100) {
                sendError(ErrorCode.DIAMOND_NOT_ENOUGH, MProtrol.toStringProtrol(MProtrol.MODIFY_NICKNAME), vo, user);
                return;
            }
            ResourceService.ins().addDiamond(player, -100);
        }

        //更换头像框
        player.setLastNickname(player.getNickname());
        player.setNickname(nickname);

        vo.addData("diamond", player.getDiamond());
        fireEvent(user, "diamond");

        sendSucceed(MProtrol.toStringProtrol(MProtrol.MODIFY_NICKNAME), vo, user);
    }
}
