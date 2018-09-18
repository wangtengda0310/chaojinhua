package com.igame.work.sign;

import com.igame.core.di.Inject;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.Map;

/**
 * 玩家创建账号开始算登录时间
 * 签完30天再读下一个30天的配置
 * 累积签到奖励如果没有领取，到下一个签到周期数据会被清掉
 */
public class SignHandler extends ReconnectedHandler {
    @Inject private SignService signService;

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {

            String infor = params.getUtfString("infor");
            JSONObject jsonObject = JSONObject.fromObject(infor);

            int index = jsonObject.getInt("index");

            if(player.getSign()==null) {
                player.setSign(new SignData());
            }

            RetVO vo = new RetVO();

            if (index == 0) {
                Map<String, Object> ret = signService.signToday(player);
                if (ret == null) {
                    return error(ErrorCode.PACK_PURCHASED);
                }
                vo.setData(ret);
            } else if (index < 5) {
                Map<String, Object> ret = signService.signTotal(player,index);
                if (ret == null) {
                    return error(ErrorCode.PACK_PURCHASED);
                }
                vo.setData(ret);
            } else {
                return error(ErrorCode.PARAMS_INVALID);
            }

            vo.addData("index", index);
            return vo;
    }

    @Override
    public int protocolId() {
        return MProtrol.SIGN;
    }
}
