package model.harvester;

import java.io.Serializable;

public class Hit implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4585809721704156651L;
	
	private String item;
	private double price;
	private InstrumentType instrumentType;
	private String link;
	private String website;
	
	public Hit() {
		super();
	}

	public Hit(String item, double price, InstrumentType instrumentType, String link, String website) {
		super();
		this.item = item;
		this.price = price;
		this.instrumentType = instrumentType;
		this.link = link;
		this.website = website;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getItem() {
		return item;
	}

	public double getPrice() {
		return price;
	}

	public InstrumentType getInstrumentType() {
		return instrumentType;
	}

	public String getLink() {
		return link;
	}

	public String getWebsite() {
		return website;
	}
}
