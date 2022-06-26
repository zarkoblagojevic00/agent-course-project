package model.harvester;

import java.io.Serializable;

public class SearchDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8341993478133725966L;
	private InstrumentType type;
	private String keyword;
	private double fromPrice;
	private double toPrice;
	
	public SearchDTO() {
		super();
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public InstrumentType getType() {
		return type;
	}

	public String getKeyword() {
		return keyword;
	}

	public double getFromPrice() {
		return fromPrice;
	}

	public double getToPrice() {
		return toPrice;
	}
}
