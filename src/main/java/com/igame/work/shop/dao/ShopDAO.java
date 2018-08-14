package com.igame.work.shop.dao;

import com.igame.core.data.DataManager;
import com.igame.core.db.AbsDao;
import com.igame.work.shop.ShopConstants;
import com.igame.work.shop.dto.ShopInfo;
import com.igame.work.shop.service.ShopService;
import com.igame.work.user.dto.Player;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.UpdateOperations;

public class ShopDAO extends AbsDao {

	@Override
	public String getTableName() {
		return "Shops";
	}
	
    private static final ShopDAO domain = new ShopDAO();

    public static final ShopDAO ins() {
        return domain;
    }


    /**
     * 根据 玩家ID 获取 玩家商店信息
     * @param severId 服务器ID
     * @param playerId 玩家ID
     */
    public ShopInfo getShopInfoByPlayerId(int severId,long playerId){

        return getDatastore(severId).find(ShopInfo.class,"playerId",playerId).get();
    }

    /**
     * 初始化 玩家商店信息
     * @param serverId 服务器ID
     * @param shopInfo 商店信息
     */
    public ShopInfo saveShopInfo(int serverId, ShopInfo shopInfo){

        getDatastore(serverId).save(shopInfo);

        return shopInfo;
    }

    /**
     * 更新 玩家商店信息
     * @param serverId 服务器ID
     * @param shopInfo 玩家
     */
    public void updateShopInfo(int serverId, ShopInfo shopInfo){

        Datastore ds = getDatastore(serverId);
        UpdateOperations<ShopInfo> up = ds.createUpdateOperations(ShopInfo.class)
                .set("mysticalShop",shopInfo.getMysticalShop())
                .set("wujinShop",shopInfo.getWujinShop())
                .set("doujiShop",shopInfo.getDoujiShop())
                .set("qiyuanShop",shopInfo.getQiyuanShop())
                .set("buluoShop",shopInfo.getBuluoShop());

        ds.update(shopInfo,up);
    }

    public void updatePlayer(Player player) {

        if(player.getShopInfo().getDtate() == 1){
            saveShopInfo(player.getSeverId(), player.getShopInfo());
        }else if(player.getShopInfo().getDtate() == 2){
            updateShopInfo(player.getSeverId(), player.getShopInfo());
        }
    }
}
