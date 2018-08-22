package com.igame.work.shop.handler;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.shop.dto.ShopInfo;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author xym
 *
 * 商店信息
 */
public class ShopInfoHandler extends BaseHandler{

    @Override
    public void handleClientRequest(User user, ISFSObject params) {
		RetVO vo = new RetVO();
		if(reviceMessage(user,params,vo)){
			return;
		}
        Player player = SessionManager.ins().getSession(Long.parseLong(user.getName()));
        if(player == null){
            this.getLogger().error(this.getClass().getSimpleName()," get player failed Name:" +user.getName());
            return;
        }

        ShopInfo shopInfo = player.getShopInfo();
        if (shopInfo == null){
            sendError(ErrorCode.ERROR, MProtrol.toStringProtrol(MProtrol.SHOP_INFO), new RetVO(), user);
            return;
        }

   
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        vo.addData("serverTime", format.format(new Date()));
        vo.addData("mysticalShop",shopInfo.getMysticalShop());
        vo.addData("wujinShop",shopInfo.getWujinShop());
        vo.addData("doujiShop",shopInfo.getDoujiShop());
        vo.addData("qiyuanShop",shopInfo.getQiyuanShop());
        vo.addData("buluoShop",shopInfo.getBuluoShop());

        sendSucceed(MProtrol.toStringProtrol(MProtrol.SHOP_INFO),vo,user);
    }
}
