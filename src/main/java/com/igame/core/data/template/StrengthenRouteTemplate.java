package com.igame.core.data.template;



import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * 
 * @author Marcus.Z
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "low")
public class StrengthenRouteTemplate {
	

	@XmlAttribute(name = "strengthen_route")
	private int strengthen_route;
	
	@XmlAttribute(name = "value")
	private int value;

	@XmlAttribute(name = "coordinate")
	private String coordinate;

	public int getStrengthen_route() {
		return strengthen_route;
	}

	public void setStrengthen_route(int strengthen_route) {
		this.strengthen_route = strengthen_route;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(String coordinate) {
		this.coordinate = coordinate;
	}
	
	
	
	
	
	
	
	
	
	
	
	

}
