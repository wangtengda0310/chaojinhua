package com.igame.work.shop.handler;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.data.DataManager;
import com.igame.core.data.template.ShopOutPutTemplate;
import com.igame.core.data.template.ShopRandomLvTemplate;
import com.igame.core.data.template.ShopRandomTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.work.shop.ShopConstants;
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
 * 购买商品
 */
public class ShopBuyHandler extends BaseHandler{

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
        int type = jsonObject.getInt("itemType");
        int itemId = jsonObject.getInt("itemId");
        int count = jsonObject.getInt("count");

        vo.addData("shopId",shopId);
        vo.addData("itemType",type);
        vo.addData("itemId",itemId);

        //入参校验
        if (shopId < 101 || shopId > 108){
            sendError(ErrorCode.PARAMS_INVALID, MProtrol.toStringProtrol(MProtrol.SHOP_BUY), vo, user);
            return;
        }
        if (type != 2 && type != 3){
            sendError(ErrorCode.PARAMS_INVALID, MProtrol.toStringProtrol(MProtrol.SHOP_BUY), vo, user);
            return;
        }

        if (count <= 0){
            sendError(ErrorCode.PARAMS_INVALID, MProtrol.toStringProtrol(MProtrol.SHOP_BUY), vo, user);
            return;
        }

        ShopInfo shopInfo = player.getShopInfo();
        int maxCount = shopInfo.getMaxCount(shopId, itemId);

        //校验商店是否解锁
        if (shopInfo.isLock(shopId)){
            sendError(ErrorCode.SHOP_LOCK, MProtrol.toStringProtrol(MProtrol.SHOP_BUY), vo, user);
            return;
        }

        //校验库存
        if (count > maxCount){
            sendError(ErrorCode.SHOP_STOCK, MProtrol.toStringProtrol(MProtrol.SHOP_BUY), vo, user);
            return;
        }

        //校验积分
        ShopOutPutTemplate outPutTemplate = DataManager.shopOutPutData.getTemplate(shopId);
        ShopRandomTemplate randomTemplate = DataManager.shopRandomData.getTemplate(shopInfo.getMysticalShop().getShopLv());
        int price;

        switch (shopId){
            case ShopConstants.ID_MysticalShop:   //神秘商店

                price = randomTemplate.getPrice(type, itemId);
                int sale = shopInfo.getMysticalShop().getSale();
                int discountedPrice = price / 10 * sale * count;

                if (player.getDiamond() < discountedPrice) {
                    sendError(ErrorCode.DIAMOND_NOT_ENOUGH, MProtrol.toStringProtrol(MProtrol.SHOP_BUY), vo, user);
                }else {     //减少商品数量，减少钻石

                    //增加神秘商店经验
                    int exp = DataManager.shopRandomLvData.getTemplate(shopInfo.getMysticalShop().getShopLv())
                            .getUnitExp() * discountedPrice;
                    ShopService.ins().addMysticalExp(player,exp);

                    shopInfo.addMaxCount(shopId,itemId,-count);
                    ResourceService.ins().addDiamond(player,-discountedPrice);

                    vo.addData("count",maxCount - count);
                    sendSucceed(MProtrol.toStringProtrol(MProtrol.SHOP_BUY),vo,user);
                }
                break;

            case ShopConstants.ID_WUJINShop:     //无尽商店

                price = outPutTemplate.getPrice(itemId) * count;

                if (player.getWuScore() < price) {
                    sendError(ErrorCode.WUJIN_NOT_ENOUGH, MProtrol.toStringProtrol(MProtrol.SHOP_BUY), vo, user);
                }else {     //减少商品数量，减少积分
                    shopInfo.addMaxCount(shopId,itemId,-count);
                    ResourceService.ins().addWuScore(player,-price);

                    vo.addData("count",maxCount - count);
                    sendSucceed(MProtrol.toStringProtrol(MProtrol.SHOP_BUY),vo,user);
                }
                break;

            case ShopConstants.ID_DOUJIShop:     //斗技商店

                price = outPutTemplate.getPrice(itemId) * count;

                if (player.getDoujiScore() < price) {
                    sendError(ErrorCode.DOUJI_NOT_ENOUGH, MProtrol.toStringProtrol(MProtrol.SHOP_BUY), vo, user);
                }else {     //减少商品数量，减少积分
                    shopInfo.addMaxCount(shopId,itemId,-count);
                    ResourceService.ins().addDoujiScore(player,-price);

                    vo.addData("count",maxCount - count);
                    sendSucceed(MProtrol.toStringProtrol(MProtrol.SHOP_BUY),vo,user);
                }
                break;

            case ShopConstants.ID_QIYUANShop:    //起源商店

                price = outPutTemplate.getPrice(itemId) * count;

                if (player.getQiyuanScore() < price) {
                    sendError(ErrorCode.QIYUAN_NOT_ENOUGH, MProtrol.toStringProtrol(MProtrol.SHOP_BUY), vo, user);
                }else {     //减少商品数量，减少积分
                    shopInfo.addMaxCount(shopId,itemId,-count);
                    ResourceService.ins().addQiyuanScore(player,-price);

                    vo.addData("count",maxCount - count);
                    sendSucceed(MProtrol.toStringProtrol(MProtrol.SHOP_BUY),vo,user);
                }
                break;

            case ShopConstants.ID_BULUOShop:     //部落商店

                price = outPutTemplate.getPrice(itemId) * count;

                if (player.getBuluoScore() < price) {
                    sendError(ErrorCode.BULUO_NOT_ENOUGH, MProtrol.toStringProtrol(MProtrol.SHOP_BUY), vo, user);
                }else {     //减少商品数量，减少积分
                    shopInfo.addMaxCount(shopId,itemId,-count);
                    ResourceService.ins().addBuluoScore(player,-price);

                    vo.addData("count",maxCount - count);
                    sendSucceed(MProtrol.toStringProtrol(MProtrol.SHOP_BUY),vo,user);
                }
                break;

            default:
                sendError(ErrorCode.SHOP_LOCK, MProtrol.toStringProtrol(MProtrol.SHOP_BUY), vo, user);
                break;
        }


    }
}
