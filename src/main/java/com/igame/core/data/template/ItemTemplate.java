package com.igame.core.data.template;



import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * 
 *
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "low")
public class ItemTemplate {

	
	@XmlAttribute(name = "item_id", required = true)
	private int itemId;//物品ID
	
	@XmlAttribute(name = "item_type")
	private int itemType;//大类型
	
	@XmlAttribute(name = "subtype")
	private int subtype;//小类型

	@XmlAttribute(name = "iten_rarity")
	private int itenRarity;//品质
	
	@XmlAttribute(name = "sale")
	private int sale;//售价
	
	@XmlAttribute(name = "effect")
	private int effect;//效果值
	
	@XmlAttribute(name = "value")
	private float value;//数值

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public int getItemType() {
		return itemType;
	}

	public void setItemType(int itemType) {
		this.itemType = itemType;
	}

	public int getSubtype() {
		return subtype;
	}

	public void setSubtype(int subtype) {
		this.subtype = subtype;
	}

	public int getItenRarity() {
		return itenRarity;
	}

	public void setItenRarity(int itenRarity) {
		this.itenRarity = itenRarity;
	}

	public int getSale() {
		return sale;
	}

	public void setSale(int sale) {
		this.sale = sale;
	}

	public int getEffect() {
		return effect;
	}

	public void setEffect(int effect) {
		this.effect = effect;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}
	
	
	
	
	

}
