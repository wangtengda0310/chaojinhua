package com.igame.work.shop.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.igame.core.db.BasicDto;
import com.igame.work.shop.ShopConstants;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Transient;


/**
 * @author xym
 *
 * 商店信息
 */
@Entity(value = "Shops", noClassnameStored = true)
public class ShopInfo extends BasicDto {
	
	@Indexed
	@JsonIgnore
	private long playerId;//所属角色ID

	@JsonIgnore
	private MysticalShop mysticalShop;	//神秘商店

	@JsonIgnore
	private GeneralShop wujinShop;	//无尽商店

	@JsonIgnore
	private GeneralShop doujiShop;	//斗技场商店

	@JsonIgnore
	private GeneralShop qiyuanShop;	//起源商店

	@JsonIgnore
	private GeneralShop buluoShop;	//部落商店

	@Transient
	@JsonIgnore
	private int dtate;//数据库状态 0-NO 1-新增 2-更新 3-删除



	/**
	 * 获取商品 剩余可购买数量
	 * @param shopId 商店ID
	 * @param itemId 商品ID
	 */
	public int getMaxCount(int shopId,int itemId){
		switch (shopId){
			case ShopConstants.ID_MysticalShop:
				if (itemId == this.mysticalShop.getId())
					return this.mysticalShop.getCount();
			case ShopConstants.ID_WUJINShop:
				return this.wujinShop.getItemCount(itemId);
			case ShopConstants.ID_DOUJIShop:
				return this.doujiShop.getItemCount(itemId);
			case ShopConstants.ID_QIYUANShop:
				return this.qiyuanShop.getItemCount(itemId);
			case ShopConstants.ID_BULUOShop:
				return this.buluoShop.getItemCount(itemId);
			default:
				return 0;
		}
	}

	/**
	 * 增加商品 剩余可购买数量
	 * @param shopId 商店ID
	 * @param itemId 商品ID
	 * @param count 增加的商品数量
	 */
	public void addMaxCount(int shopId, int itemId, int count){
		switch (shopId){
			case ShopConstants.ID_MysticalShop:
				if (itemId == this.mysticalShop.getId())
					this.mysticalShop.addCount(count);
				break;
			case ShopConstants.ID_WUJINShop:
				this.wujinShop.addItemCount(itemId,count);
				break;
			case ShopConstants.ID_DOUJIShop:
				this.doujiShop.addItemCount(itemId,count);
				break;
			case ShopConstants.ID_QIYUANShop:
				this.qiyuanShop.addItemCount(itemId,count);
				break;
			case ShopConstants.ID_BULUOShop:
				this.buluoShop.addItemCount(itemId,count);
				break;
			default:
				break;
		}
	}

	/**
	 * 获取商店对象
	 * @param shopId 商店ID
	 */
	public Object getShopObj(int shopId){
		switch (shopId){
			case ShopConstants.ID_MysticalShop:
				return this.mysticalShop;
			case ShopConstants.ID_WUJINShop:
				return this.wujinShop;
			case ShopConstants.ID_DOUJIShop:
				return this.doujiShop;
			case ShopConstants.ID_QIYUANShop:
				return this.qiyuanShop;
			case ShopConstants.ID_BULUOShop:
				return this.buluoShop;
			default:
				return null;
		}
	}

	/**
	 * 获取商店剩余刷新次数
	 * @param shopId 商店ID
	 */
	public int getReloadCount(int shopId) {
		switch (shopId){
			case ShopConstants.ID_MysticalShop:
				return this.mysticalShop.getReloadCount();
			case ShopConstants.ID_WUJINShop:
				return this.wujinShop.getReloadCount();
			case ShopConstants.ID_DOUJIShop:
				return this.doujiShop.getReloadCount();
			case ShopConstants.ID_QIYUANShop:
				return this.qiyuanShop.getReloadCount();
			case ShopConstants.ID_BULUOShop:
				return this.buluoShop.getReloadCount();
			default:
				return 0;
		}
	}

	/**
	 * 判断商店是否解锁
	 * @param shopId 商店ID
	 * @return true = 未解锁
	 */
	public boolean isLock(int shopId) {

		switch (shopId){
			case ShopConstants.ID_MysticalShop:
				return this.getMysticalShop() == null;
			case ShopConstants.ID_WUJINShop:
				return this.getWujinShop() == null;
			case ShopConstants.ID_DOUJIShop:
				return this.getDoujiShop() == null;
			case ShopConstants.ID_QIYUANShop:
				return this.getQiyuanShop() == null;
			case ShopConstants.ID_BULUOShop:
				return this.getBuluoShop() == null;
			default:
				return true;
		}
	}



	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	public MysticalShop getMysticalShop() {
		return mysticalShop;
	}

	public void setMysticalShop(MysticalShop mysticalShop) {
		this.mysticalShop = mysticalShop;
	}

	public GeneralShop getWujinShop() {
		return wujinShop;
	}

	public void setWujinShop(GeneralShop wujinShop) {
		this.wujinShop = wujinShop;
	}

	public GeneralShop getDoujiShop() {
		return doujiShop;
	}

	public void setDoujiShop(GeneralShop doujiShop) {
		this.doujiShop = doujiShop;
	}

	public GeneralShop getQiyuanShop() {
		return qiyuanShop;
	}

	public void setQiyuanShop(GeneralShop qiyuanShop) {
		this.qiyuanShop = qiyuanShop;
	}

	public GeneralShop getBuluoShop() {
		return buluoShop;
	}

	public void setBuluoShop(GeneralShop buluoShop) {
		this.buluoShop = buluoShop;
	}

	public int getDtate() {
		return dtate;
	}

	public void setDtate(int ndtate) {
		switch (ndtate) {
			case 1://添加
				if (this.dtate == 2 || this.dtate == 3)
					this.dtate = 2;
				else
					this.dtate = 1;
				break;
			case 2://更新
				if(this.dtate == 1){
					this.dtate = 1;
				} else{
					this.dtate = 2;
				}
				break;
			case 3://删除
				if (this.dtate == 1)
					this.dtate = 0;
				else {
					this.dtate = 3;
				}
				break;
			default:
				this.dtate = ndtate;
				break;
		}
	}
}
