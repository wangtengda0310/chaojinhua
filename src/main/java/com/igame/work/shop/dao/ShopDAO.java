package com.igame.work.shop.dao;

import com.igame.core.db.AbsDao;
import com.igame.work.shop.dto.ShopInfo;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.UpdateOperations;

public class ShopDAO extends AbsDao {

    /**
     * 根据 玩家ID 获取 玩家商店信息
     * @param playerId 玩家ID
     */
    public ShopInfo getShopInfoByPlayerId(long playerId){

        return getDatastore().find(ShopInfo.class,"playerId",playerId).get();
    }

    /**
     * 初始化 玩家商店信息
     * @param shopInfo 商店信息
     */
    public ShopInfo saveShopInfo(ShopInfo shopInfo){

        getDatastore().save(shopInfo);

        return shopInfo;
    }

    /**
     * 更新 玩家商店信息
     * @param shopInfo 玩家
     */
    public void updateShopInfo(ShopInfo shopInfo){

        Datastore ds = getDatastore();
        UpdateOperations<ShopInfo> up = ds.createUpdateOperations(ShopInfo.class)
                .set("mysticalShop",shopInfo.getMysticalShop())
                .set("wujinShop",shopInfo.getWujinShop())
                .set("doujiShop",shopInfo.getDoujiShop())
                .set("qiyuanShop",shopInfo.getQiyuanShop())
                .set("buluoShop",shopInfo.getBuluoShop());

        ds.update(shopInfo,up);
    }

    public void updatePlayer(ShopInfo shopInfo) {

        if(shopInfo.getDtate() == 1){
            saveShopInfo(shopInfo);
        }else if(shopInfo.getDtate() == 2){
            updateShopInfo(shopInfo);
        }
    }
}
