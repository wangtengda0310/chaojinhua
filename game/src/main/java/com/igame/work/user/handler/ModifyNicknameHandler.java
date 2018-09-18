package com.igame.work.user.handler;

import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.PlayerEvents;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * @author xym
 * <p>
 * 更换头像框
 */
public class ModifyNicknameHandler extends ReconnectedHandler {

    private ResourceService resourceService;

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {

        RetVO vo = new RetVO();

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        String nickname = jsonObject.getString("nickname");

        vo.addData("nickname", nickname);

        if (player.getLastNickname() != null && !"".equals(player.getLastNickname())) {
            if (player.getDiamond() < 100) {
                return error(ErrorCode.DIAMOND_NOT_ENOUGH);
            }
            resourceService.addDiamond(player, -100);
        }

        //更换头像框
        player.setLastNickname(player.getNickname());
        player.setNickname(nickname);

        fireEvent(player, PlayerEvents.MODIFY_NAME, nickname);

        return vo;
    }

    @Override
    public int protocolId() {
        return MProtrol.MODIFY_NICKNAME;
    }

}
