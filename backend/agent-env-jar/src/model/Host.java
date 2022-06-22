package model;

import java.io.Serializable;

public class Host implements Serializable {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = -1615215171263602343L;
	private String address;
	private String alias;
	private String masterAlias;
	
	public Host() {};
	
	public Host(String address, String alias, String masterAlias) {
		super();
		this.address = address;
		this.alias = alias;
		this.masterAlias = masterAlias;
	}

	public String getAddress() {
		return address;
	}
	
	public String getAlias() {
		return alias;
	}

	
	public String getMasterAlias() {
		return masterAlias;
	}

	@Override
	public String toString() {
		return "Host [address=" + address + ", alias=" + alias + ", masterAlias=" + masterAlias + "]";
	}
	
	

}
