package com.igame.work.user.handler;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.work.user.PlayerDataManager;
import com.igame.work.user.data.VipPackTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.gm.service.GMService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import static com.igame.work.user.VIPConstants.KEY_FIRST_PACK;

/**
 * @author xym
 *
 * 会员购买礼包
 */
public class VipBuyFristPackHandler extends BaseHandler{

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

        int vipLv = jsonObject.getInt("vipLv");
        vo.addData("vipLv",vipLv);

        //校验vip等级
        int vip = player.getVip();
        if (vip < vipLv){
            sendError(ErrorCode.VIP_LV_LACK,MProtrol.toStringProtrol(MProtrol.VIP_FRIST_PACK),vo,user);
            return;
        }

        //校验是否已购买
        String s = (String) player.getVipPrivileges().get(KEY_FIRST_PACK);
        if (!s.contains(String.valueOf(vipLv))){
            sendError(ErrorCode.PACK_PURCHASED,MProtrol.toStringProtrol(MProtrol.VIP_FRIST_PACK),vo,user);
            return;
        }

        VipPackTemplate template = PlayerDataManager.vipPackData.getTemplate(vipLv);
        if (template == null){
            sendError(ErrorCode.PARAMS_INVALID,MProtrol.toStringProtrol(MProtrol.VIP_FRIST_PACK),vo,user);
            return;
        }

        //校验钻石
        int gem = template.getGem();
        if (player.getDiamond() < gem){
            sendError(ErrorCode.DIAMOND_NOT_ENOUGH,MProtrol.toStringProtrol(MProtrol.VIP_FRIST_PACK),vo,user);
            return;
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
        sendSucceed(MProtrol.toStringProtrol(MProtrol.VIP_FRIST_PACK),vo,user);
    }
}
