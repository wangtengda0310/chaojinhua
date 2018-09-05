package com.igame.work.shop.handler;

import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.shop.ShopConstants;
import com.igame.work.shop.ShopDataManager;
import com.igame.work.shop.data.ShopOutPutTemplate;
import com.igame.work.shop.data.ShopRandomTemplate;
import com.igame.work.shop.dto.ShopInfo;
import com.igame.work.shop.service.ShopService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * @author xym
 *
 * 购买商品
 */
public class ShopBuyHandler extends ReconnectedHandler {
    private ResourceService resourceService;

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        int shopId = jsonObject.getInt("shopId");
        int type = jsonObject.getInt("itemType");
        int itemId = jsonObject.getInt("itemId");
        int count = jsonObject.getInt("count");

        vo.addData("shopId",shopId);
        vo.addData("itemType",type);
        vo.addData("itemId",itemId);

        //入参校验
        if (shopId < 101 || shopId > 108){
            return error(ErrorCode.PARAMS_INVALID);
        }
        if (type != 2 && type != 3){
            return error(ErrorCode.PARAMS_INVALID);
        }

        if (count <= 0){
            return error(ErrorCode.PARAMS_INVALID);
        }

        ShopInfo shopInfo = player.getShopInfo();
        int maxCount = shopInfo.getMaxCount(shopId, itemId);

        //校验商店是否解锁
        if (shopInfo.isLock(shopId)){
            return error(ErrorCode.SHOP_LOCK);
        }

        //校验库存
        if (count > maxCount){
            return error(ErrorCode.SHOP_STOCK);
        }

        //校验积分
        ShopOutPutTemplate outPutTemplate = ShopDataManager.shopOutPutData.getTemplate(shopId);
        ShopRandomTemplate randomTemplate = ShopDataManager.shopRandomData.getTemplate(shopInfo.getMysticalShop().getShopLv());
        int price;

        switch (shopId){
            case ShopConstants.ID_MysticalShop: {   //神秘商店

                price = randomTemplate.getPrice(type, itemId);
                int sale = shopInfo.getMysticalShop().getSale();
                int discountedPrice = price / 10 * sale * count;

                if (player.getDiamond() < discountedPrice) {
                    return error(ErrorCode.DIAMOND_NOT_ENOUGH);
                } else {     //减少商品数量，减少钻石

                    //增加神秘商店经验
                    int exp = ShopDataManager.shopRandomLvData.getTemplate(shopInfo.getMysticalShop().getShopLv())
                            .getUnitExp() * discountedPrice;
                    ShopService.ins().addMysticalExp(player, exp);

                    shopInfo.addMaxCount(shopId, itemId, -count);
                    resourceService.addDiamond(player, -discountedPrice);

                    vo.addData("count", maxCount - count);
                    return vo;
                }

            } case ShopConstants.ID_WUJINShop: {     //无尽商店

                price = outPutTemplate.getPrice(itemId) * count;

                if (player.getWuScore() < price) {
                    return error(ErrorCode.WUJIN_NOT_ENOUGH);
                } else {     //减少商品数量，减少积分
                    shopInfo.addMaxCount(shopId, itemId, -count);
                    resourceService.addWuScore(player, -price);

                    vo.addData("count", maxCount - count);
                    return vo;
                }

            } case ShopConstants.ID_DOUJIShop: {     //斗技商店

                price = outPutTemplate.getPrice(itemId) * count;

                if (player.getDoujiScore() < price) {
                    return error(ErrorCode.DOUJI_NOT_ENOUGH);
                } else {     //减少商品数量，减少积分
                    shopInfo.addMaxCount(shopId, itemId, -count);
                    resourceService.addDoujiScore(player, -price);

                    vo.addData("count", maxCount - count);
                    return vo;
                }

            } case ShopConstants.ID_QIYUANShop: {    //起源商店

                price = outPutTemplate.getPrice(itemId) * count;

                if (player.getQiyuanScore() < price) {
                    return error(ErrorCode.QIYUAN_NOT_ENOUGH);
                } else {     //减少商品数量，减少积分
                    shopInfo.addMaxCount(shopId, itemId, -count);
                    resourceService.addQiyuanScore(player, -price);

                    vo.addData("count", maxCount - count);
                    return vo;
                }

            } case ShopConstants.ID_BULUOShop: {     //部落商店

                price = outPutTemplate.getPrice(itemId) * count;

                if (player.getBuluoScore() < price) {
                    return error(ErrorCode.BULUO_NOT_ENOUGH);
                } else {     //减少商品数量，减少积分
                    shopInfo.addMaxCount(shopId, itemId, -count);
                    resourceService.addBuluoScore(player, -price);

                    vo.addData("count", maxCount - count);
                    return vo;
                }

            } default: {
                return error(ErrorCode.SHOP_LOCK);
            }
        }


    }

    @Override
    public int protocolId() {
        return MProtrol.SHOP_BUY;
    }

}
