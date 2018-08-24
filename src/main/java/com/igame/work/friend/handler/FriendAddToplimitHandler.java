package com.igame.work.friend.handler;

import com.igame.work.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * @author xym
 *
 * 增加好友上限
 * todo 暂时不做
 */
public class FriendAddToplimitHandler extends ReconnectedHandler {

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {


        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        RetVO vo = new RetVO();

        //增加好友上限
        //增加体力领取上限
        //扣除钻石

        return vo;
    }

    @Override
    protected int protocolId() {
        return MProtrol.FRIEND_TOPLIMIT_ADD;
    }

}
