package com.igame.work.shop.handler;

import com.igame.core.di.Inject;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.shop.ShopConstants;
import com.igame.work.shop.ShopDataManager;
import com.igame.work.shop.data.ShopTemplate;
import com.igame.work.shop.dto.GeneralShop;
import com.igame.work.shop.dto.MysticalShop;
import com.igame.work.shop.dto.ShopInfo;
import com.igame.work.shop.service.ShopService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * @author xym
 *
 * 刷新商店
 */
public class ReloadShopHandler extends ReconnectedHandler {
    @Inject private ResourceService resourceService;
    @Inject private ShopService shopService;

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        int shopId = jsonObject.getInt("shopId");

        vo.addData("shopId",shopId);

        if (shopId < 101 || 108 < shopId){
            return error(ErrorCode.PARAMS_INVALID);
        }

        ShopInfo shopInfo = player.getShopInfo();
        ShopTemplate shopTemplate = ShopDataManager.shopData.getTemplate(shopId);

        //校验刷新次数
        if (shopTemplate.getResestMax() != -1 && shopTemplate.getResestMax() <= shopInfo.getReloadCount(shopId)){
            return error(ErrorCode.SHOP_NOT_RELOAD);
        }

        //校验钻石
        int gem = 0;
        if (shopTemplate.getShopType() == 1){   //神秘商店

            int shopLv = shopInfo.getMysticalShop().getShopLv();
            String[] split = shopTemplate.getResestGem().split(ShopConstants.SPLIT_ONE);
            gem = Integer.parseInt(split[shopLv - 1]);

        }else if (shopTemplate.getShopType() == 2){ //一般商店
            gem = Integer.parseInt(shopTemplate.getResestGem());
        }

        if (gem == 0 || player.getDiamond() < gem){
            return error(ErrorCode.DIAMOND_NOT_ENOUGH);
        }

        //校验商店是否解锁
        if (shopInfo.isLock(shopId)){
            return error(ErrorCode.SHOP_LOCK);
        }

        //刷新商店
        shopService.reloadShop(shopId,shopInfo,true);
        //减少钻石
        resourceService.addDiamond(player,-gem);

        //return
        vo.addData("mysticalShop",shopInfo.getMysticalShop());
        switch (shopId){
            case ShopConstants.ID_MysticalShop:
                vo.addData("mysticalShop",shopInfo.getMysticalShop());
                vo.addData("generalShop",new GeneralShop());
                break;
            case ShopConstants.ID_WUJINShop:
                vo.addData("generalShop",shopInfo.getWujinShop());
                vo.addData("mysticalShop",new MysticalShop());
                break;
            case ShopConstants.ID_DOUJIShop:
                vo.addData("generalShop",shopInfo.getDoujiShop());
                vo.addData("mysticalShop",new MysticalShop());
                break;
            case ShopConstants.ID_QIYUANShop:
                vo.addData("generalShop",shopInfo.getQiyuanShop());
                vo.addData("mysticalShop",new MysticalShop());
                break;
            case ShopConstants.ID_BULUOShop:
                vo.addData("generalShop",shopInfo.getBuluoShop());
                vo.addData("mysticalShop",new MysticalShop());
                break;
            default:
                break;
        }

        return vo;
    }

    @Override
    public int protocolId() {
        return MProtrol.SHOP_Reload;
    }

}
