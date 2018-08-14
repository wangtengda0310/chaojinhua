package com.igame.work.shop.handler;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.data.DataManager;
import com.igame.core.data.template.ShopTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.work.shop.ShopConstants;
import com.igame.work.shop.dto.GeneralShop;
import com.igame.work.shop.dto.MysticalShop;
import com.igame.work.shop.dto.ShopInfo;
import com.igame.work.shop.service.ShopService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * @author xym
 *
 * 刷新商店
 */
public class ReloadShopHandler extends BaseHandler{

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

        int shopId = jsonObject.getInt("shopId");

        vo.addData("shopId",shopId);

        if (shopId < 101 || 108 < shopId){
            sendError(ErrorCode.PARAMS_INVALID, MProtrol.toStringProtrol(MProtrol.SHOP_BUY), vo, user);
            return;
        }

        ShopInfo shopInfo = player.getShopInfo();
        ShopTemplate shopTemplate = DataManager.shopData.getTemplate(shopId);

        //校验刷新次数
        if (shopTemplate.getResestMax() != -1 && shopTemplate.getResestMax() <= shopInfo.getReloadCount(shopId)){
            sendError(ErrorCode.SHOP_NOT_RELOAD, MProtrol.toStringProtrol(MProtrol.SHOP_Reload),vo,user);
            return;
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
            sendError(ErrorCode.DIAMOND_NOT_ENOUGH, MProtrol.toStringProtrol(MProtrol.SHOP_Reload),vo,user);
            return;
        }

        //校验商店是否解锁
        if (shopInfo.isLock(shopId)){
            sendError(ErrorCode.SHOP_LOCK, MProtrol.toStringProtrol(MProtrol.SHOP_Reload),vo,user);
            return;
        }

        //刷新商店
        ShopService.ins().reloadShop(shopId,shopInfo,true);
        //减少钻石
        ResourceService.ins().addDiamond(player,-gem);

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

        sendSucceed(MProtrol.toStringProtrol(MProtrol.SHOP_Reload),vo,user);
    }
}
