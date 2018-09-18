package com.igame.work.shop.handler;

import com.igame.core.di.Inject;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.shop.dto.ShopInfo;
import com.igame.work.shop.service.ShopService;
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
    @Inject
    ShopService shopService;

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

        ShopInfo shopInfo = shopService.getShopInfo(player);

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
    public int protocolId() {
        return MProtrol.SHOP_INFO;
    }

}
