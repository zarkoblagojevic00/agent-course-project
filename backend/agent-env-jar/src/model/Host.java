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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((alias == null) ? 0 : alias.hashCode());
		result = prime * result + ((masterAlias == null) ? 0 : masterAlias.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Host other = (Host) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (alias == null) {
			if (other.alias != null)
				return false;
		} else if (!alias.equals(other.alias))
			return false;
		if (masterAlias == null) {
			if (other.masterAlias != null)
				return false;
		} else if (!masterAlias.equals(other.masterAlias))
			return false;
		return true;
	}
	
	
	
	

}
