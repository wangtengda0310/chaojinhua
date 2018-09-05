package com.igame.work.shop.service;

import com.igame.core.ISFSModule;
import com.igame.core.quartz.TimeListener;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.core.handler.RetVO;
import com.igame.util.DateUtil;
import com.igame.util.GameMath;
import com.igame.work.shop.ShopConstants;
import com.igame.work.shop.ShopDataManager;
import com.igame.work.shop.dao.ShopDAO;
import com.igame.work.shop.data.ShopData;
import com.igame.work.shop.data.ShopOutPutTemplate;
import com.igame.work.shop.data.ShopRandomTemplate;
import com.igame.work.shop.data.ShopTemplate;
import com.igame.work.shop.dto.GeneralShop;
import com.igame.work.shop.dto.MysticalShop;
import com.igame.work.shop.dto.ShopInfo;
import com.igame.work.user.dto.Player;

import java.util.*;

/**
 * @author xym
 */
public class ShopService implements ISFSModule, TimeListener {
    @Override
    public void twenty() {
        reloadGeneralOnline();
    }

    @Override
    public void fourteen() {
        reloadGeneralOnline();
    }

    @Override
    public void twelve() {
        reloadMysticalOnline();
    }

    @Override
    public void nine() {
        reloadGeneralOnline();
    }

    @Override
    public void zero() {
        reloadMysticalOnline();
    }

    private static final ShopService domain = new ShopService();

    public static final ShopService ins() {
        return domain;
    }

    /**
     * 初始化商店
     * @param player 玩家信息
     */
    public ShopInfo initShop(Player player){

        ShopData shopData = ShopDataManager.shopData;
        ShopInfo shopInfo = new ShopInfo();
        shopInfo.setPlayerId(player.getPlayerId());
        shopInfo.setDtate(1);   //新增

        //神秘商店
        ShopTemplate mysticalTemplate = shopData.getTemplate(ShopConstants.ID_MysticalShop);

        if (player.getPlayerLevel() >= mysticalTemplate.getUnlock()){
            MysticalShop mysticalShop = new MysticalShop();
            mysticalShop.setShopId(ShopConstants.ID_MysticalShop);
            mysticalShop.setShopLv(1);
            mysticalShop.setReloadCount(0);

            shopInfo.setMysticalShop(mysticalShop);
        }else{
        	shopInfo.setMysticalShop(new MysticalShop());
        }

        //无尽商店
        GeneralShop wujinShop = new GeneralShop();
        wujinShop.setShopId(ShopConstants.ID_WUJINShop);
        wujinShop.setReloadCount(0);

        shopInfo.setWujinShop(wujinShop);

        //斗技场商店
        GeneralShop doujiShop = new GeneralShop();
        doujiShop.setShopId(ShopConstants.ID_DOUJIShop);
        doujiShop.setReloadCount(0);

        shopInfo.setDoujiShop(doujiShop);

        //起源商店
        GeneralShop qiyuanShop = new GeneralShop();
        qiyuanShop.setShopId(ShopConstants.ID_QIYUANShop);
        qiyuanShop.setReloadCount(0);

        shopInfo.setQiyuanShop(qiyuanShop);

        //部落商店
        GeneralShop buluoShop = new GeneralShop();
        buluoShop.setShopId(ShopConstants.ID_BULUOShop);
        buluoShop.setReloadCount(0);

        shopInfo.setBuluoShop(buluoShop);

        //刷新商店
        reloadAll(shopInfo);

        player.setShopInfo(shopInfo);

        return shopInfo;
    }

    /**
     * 初始化神秘商店 升级触发
     * @param player 玩家信息
     */
    public void initMysticalShop(Player player){

        if (player.getPlayerLevel() < ShopConstants.SHOP_MYSTICAL_LOCKLV)	//初始化神秘商店
            return;

        ShopInfo shopInfo = player.getShopInfo();

        MysticalShop mysticalShop = new MysticalShop();
        mysticalShop.setShopId(ShopConstants.ID_MysticalShop);
        mysticalShop.setShopLv(ShopConstants.MYSTICAL_DEFAULT_SHOPLV);

        shopInfo.setMysticalShop(mysticalShop);

        //刷新神秘商店
        reloadShop(ShopConstants.ID_MysticalShop,shopInfo, false);

        shopInfo.setDtate(2);   //更新

        //推送
        RetVO vo = new RetVO();
        vo.addData("mysticalShop",player.getShopInfo().getMysticalShop());
        MessageUtil.sendMessageToPlayer(player, MProtrol.MYSTICAL_SHOP_UPDATE, vo);

    }

    /**
     * 刷新全部商店 玩家登录触发
     * @param shopInfo 商店信息
     */
    public ShopInfo reloadAll(ShopInfo shopInfo){

        if (shopInfo.getMysticalShop().getShopId() != 0
                && needRealod(shopInfo.getMysticalShop().getLastReload(),ShopConstants.ID_MysticalShop))
            reloadShop(ShopConstants.ID_MysticalShop,shopInfo, false);

        //无尽商店
        if (needRealod(shopInfo.getWujinShop().getLastReload(),ShopConstants.ID_MysticalShop))
            reloadShop(ShopConstants.ID_WUJINShop,shopInfo, false);

        //斗技商店
        if (needRealod(shopInfo.getDoujiShop().getLastReload(),ShopConstants.ID_MysticalShop))
            reloadShop(ShopConstants.ID_DOUJIShop,shopInfo, false);

        //起源商店
        if (needRealod(shopInfo.getQiyuanShop().getLastReload(),ShopConstants.ID_MysticalShop))
            reloadShop(ShopConstants.ID_QIYUANShop,shopInfo, false);

        //部落商店
        if (needRealod(shopInfo.getBuluoShop().getLastReload(),ShopConstants.ID_MysticalShop))
            reloadShop(ShopConstants.ID_BULUOShop,shopInfo, false);

        shopInfo.setDtate(2);   //更新

        return shopInfo;
    }

    /**
     * 判断是否需要刷新
     * @param lastReload 上次刷新时间
     * @param shopId 商店ID
     * @return true = 需要刷新
     */
    public boolean needRealod(Date lastReload,int shopId){

        if (lastReload == null)
            return true;

        ShopTemplate template = ShopDataManager.shopData.getTemplate(shopId);

        String[] resestTimes = template.getResestTime().split(ShopConstants.SPLIT_ONE);

        int curHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int tempHour = 0;
        for (String s : resestTimes) {

            int resestTime = Integer.parseInt(s);
            if (tempHour <= resestTime && resestTime <= curHour)
                tempHour = resestTime;
        }

        return DateUtil.isNeedFushDate(lastReload, tempHour);
    }

    /**
     * 刷新商店
     * @param shopId 要刷新的商店ID
     * @param shopInfo 商店信息
     * @param isManual 是否手动刷新
     */
    public ShopInfo reloadShop(int shopId, ShopInfo shopInfo, boolean isManual){

        ShopTemplate shopTemplate = ShopDataManager.shopData.getTemplate(shopId);

        int shopType = shopTemplate.getShopType();

        if (shopType == 1)     //如果是神秘商店
            reloadMysticalShop(shopId, shopInfo, isManual);

        if (shopType == 2)     //如果是一般商店
            reloadGeneralShop(shopId, shopInfo, isManual);

        shopInfo.setDtate(2);   //更新

        return shopInfo;
    }

    /**
     * 定时任务 刷新所有在线玩家的神秘商店 并推送
     */
    public void reloadMysticalOnline() {
        for(Player player : SessionManager.ins().getSessions().values()){
            ShopService.ins().reloadShop(ShopConstants.ID_MysticalShop,player.getShopInfo(), false);

            //推送
            RetVO vo = new RetVO();
            vo.addData("mysticalShop",player.getShopInfo().getMysticalShop());
            MessageUtil.sendMessageToPlayer(player, MProtrol.MYSTICAL_SHOP_UPDATE, vo);
        }
    }

    /**
     * 定时任务 刷新所有在线玩家的一般商店 并推送
     */
    public void reloadGeneralOnline() {
        for(Player player : SessionManager.ins().getSessions().values()){
            ShopService.ins().reloadShop(ShopConstants.ID_WUJINShop,player.getShopInfo(), false);
            ShopService.ins().reloadShop(ShopConstants.ID_DOUJIShop,player.getShopInfo(), false);
            ShopService.ins().reloadShop(ShopConstants.ID_QIYUANShop,player.getShopInfo(), false);
            ShopService.ins().reloadShop(ShopConstants.ID_BULUOShop,player.getShopInfo(), false);

            //推送
            RetVO vo = new RetVO();
            vo.addData("wujinShop",player.getShopInfo().getWujinShop());
            vo.addData("doujiShop",player.getShopInfo().getDoujiShop());
            vo.addData("qiyuanShop",player.getShopInfo().getQiyuanShop());
            vo.addData("buluoShop",player.getShopInfo().getBuluoShop());
            MessageUtil.sendMessageToPlayer(player, MProtrol.GENERAL_SHOP_UPDATE, vo);
        }
    }

    /**
     * 增加神秘商店经验
     * @param player 角色
     * @param exp 增加的经验值
     */
    public void addMysticalExp(Player player, int exp) {

        MysticalShop mysticalShop = player.getShopInfo().getMysticalShop();

        int curLv = mysticalShop.getShopLv();
        int curExp = mysticalShop.getExp() + exp;
        int nextLevelNeedExp = ShopDataManager.shopRandomLvData.getTemplate(curLv).getExp();

        while (curExp > nextLevelNeedExp && curLv < 4){	//升级

            curLv++;
            curExp -= nextLevelNeedExp;
            nextLevelNeedExp = ShopDataManager.shopRandomLvData.getTemplate(curLv).getExp();
        }

        mysticalShop.setShopLv(curLv);
        mysticalShop.setExp(curExp);

        RetVO vo = new RetVO();
        vo.addData("shopLv",mysticalShop.getShopLv());
        vo.addData("exp",mysticalShop.getExp());
        MessageUtil.sendMessageToPlayer(player, MProtrol.MYSTICAL_SHOP_EXP, vo);

    }

    //刷新神秘商店
    private void reloadMysticalShop(int shopId, ShopInfo shopInfo, boolean isManual) {

        int shopLv = shopInfo.getMysticalShop().getShopLv();
        ShopRandomTemplate randomTemplate = ShopDataManager.shopRandomData.getTemplate(shopLv);
        if (shopLv == 0 || randomTemplate == null)
            return;

        //随机商品
        String[] items = randomTemplate.getItem().split(ShopConstants.SPLIT_TWO);
        String[] monsters = randomTemplate.getMonster().split(ShopConstants.SPLIT_TWO);

        List<String> all = new ArrayList<>();
        all.addAll(Arrays.asList(items));
        all.addAll(Arrays.asList(monsters));

        //2(类型),1001(商品ID),1(数量);
        String shops = all.get(new Random().nextInt(all.size()));

        //随机折扣
        String s = randomTemplate.getSale();
        String r = randomTemplate.getRate();

        String[] sales = s.split(ShopConstants.SPLIT_TWO);
        String[] rates = r.split(ShopConstants.SPLIT_TWO);

        int sale = 10;
        ok:
        while (true){

            if (rates.length == 0)
                break ok;

            for (int i = 0; i < rates.length; i++) {
                if (GameMath.hitRate100(Integer.parseInt(rates[i]))){   //如果命中
                    sale = Integer.parseInt(sales[i]);
                    break ok;
                }
            }
        }

        MysticalShop mysticalShop = (MysticalShop) shopInfo.getShopObj(shopId);
        //清除商品项
        mysticalShop.clearItems();
        //设置商品
        mysticalShop.parseShops(shops);

        int price = randomTemplate.getPrice(mysticalShop.getItemType(), mysticalShop.getId());
        mysticalShop.setPrice(price);
        mysticalShop.setSale(sale);
        mysticalShop.setDiscountedPrice(price/10 * sale);
        mysticalShop.setLastReload(new Date());
        if (isManual)
            mysticalShop.addReloadCount();
    }

    //刷新一般商店
    private void reloadGeneralShop(int shopId, ShopInfo shopInfo, boolean isManual) {

        ShopOutPutTemplate outPutTemplate = ShopDataManager.shopOutPutData.getTemplate(shopId);
        String s = outPutTemplate.getItem();
        String r = outPutTemplate.getRate();
        String c = outPutTemplate.getBuy();
        String p = outPutTemplate.getItemPrice();

        String[] shops = s.split(ShopConstants.SPLIT_ONE);
        String[] rates = r.split(ShopConstants.SPLIT_ONE);
        String[] counts = c.split(ShopConstants.SPLIT_ONE);
        String[] prices = p.split(ShopConstants.SPLIT_ONE);

        //清除商品项
        GeneralShop generalShop = (GeneralShop) shopInfo.getShopObj(shopId);
        generalShop.clearItems();

        //随机并添加新的商品项
        ok:
        while (true){

            for (int i = 0; i < rates.length; i++) {
                if (GameMath.hitRate100(Integer.parseInt(rates[i]))){   //如果命中

                    int shop = Integer.parseInt(shops[i]);
                    int count = Integer.parseInt(counts[i]);
                    int price = Integer.parseInt(prices[i]);
                    generalShop.addItem(shop,count,price);

                    rates = delete(i,rates);
                    shops = delete(i,shops);
                    counts = delete(i,counts);
                    prices = delete(i,prices);
                }

                if (generalShop.getItems().size() == 6)
                    break ok;

                if (rates.length == 0)
                    break ok;
            }
        }

        //更新
        generalShop.setLastReload(new Date());
        if (isManual)
            generalShop.addReloadCount();
    }

    //删除数组指定元素
    private String[] delete(int index,String array[]){
        array[index] = array[array.length-1];
        array = Arrays.copyOf(array, array.length-1);
        return array;
    }

    public void loadPlayer(Player player, int serverId) {
        player.setShopInfo(ShopDAO.ins().getShopInfoByPlayerId(serverId,player.getPlayerId()));
    }
}
