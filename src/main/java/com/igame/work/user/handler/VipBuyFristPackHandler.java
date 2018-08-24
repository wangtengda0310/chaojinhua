package com.igame.work.user.handler;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.gm.service.GMService;
import com.igame.work.user.PlayerDataManager;
import com.igame.work.user.data.VipPackTemplate;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import static com.igame.work.user.VIPConstants.KEY_FIRST_PACK;

/**
 * @author xym
 *
 * 会员购买礼包
 */
public class VipBuyFristPackHandler extends ReconnectedHandler {

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {

        RetVO vo = new RetVO();

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        int vipLv = jsonObject.getInt("vipLv");
        vo.addData("vipLv",vipLv);

        //校验vip等级
        int vip = player.getVip();
        if (vip < vipLv){
            return error(ErrorCode.VIP_LV_LACK);
        }

        //校验是否已购买
        String s = (String) player.getVipPrivileges().get(KEY_FIRST_PACK);
        if (!s.contains(String.valueOf(vipLv))){
            return error(ErrorCode.PACK_PURCHASED);
        }

        VipPackTemplate template = PlayerDataManager.vipPackData.getTemplate(vipLv);
        if (template == null){
            return error(ErrorCode.PARAMS_INVALID);
        }

        //校验钻石
        int gem = template.getGem();
        if (player.getDiamond() < gem){
            return error(ErrorCode.DIAMOND_NOT_ENOUGH);
        }

        //扣除钻石
        ResourceService.ins().addDiamond(player,-gem);

        //发放礼包
        String firstPack = template.getFirstPack();
        GMService.processGM(player,firstPack);

        //标记已购买
        if (MyUtil.isNullOrEmpty(s)){
            s = String.valueOf(vipLv);
        }else {
            s = s+","+String.valueOf(vipLv);
        }
        player.getVipPrivileges().put(KEY_FIRST_PACK,s);

        vo.addData("reward",firstPack);
        return vo;
    }

    @Override
    protected int protocolId() {
        return MProtrol.VIP_FRIST_PACK;
    }

}
