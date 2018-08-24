package com.igame.work.shop.handler;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.shop.dto.ShopInfo;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author xym
 *
 * 商店信息
 */
public class ShopInfoHandler extends ReconnectedHandler {

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

        ShopInfo shopInfo = player.getShopInfo();
        if (shopInfo == null){
            return error(ErrorCode.ERROR);
        }

   
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        vo.addData("serverTime", format.format(new Date()));
        vo.addData("mysticalShop",shopInfo.getMysticalShop());
        vo.addData("wujinShop",shopInfo.getWujinShop());
        vo.addData("doujiShop",shopInfo.getDoujiShop());
        vo.addData("qiyuanShop",shopInfo.getQiyuanShop());
        vo.addData("buluoShop",shopInfo.getBuluoShop());

        return vo;
    }

    @Override
    protected int protocolId() {
        return MProtrol.SHOP_INFO;
    }

}
