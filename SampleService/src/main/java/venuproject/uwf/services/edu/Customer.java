package venuproject.uwf.services.edu;

public class Customer {

	private String name;
	private String type;
	private String lineOfBusiness;
	private String salesRepresentative;
	private String customerId;

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLineOfBusiness() {
		return lineOfBusiness;
	}

	public void setLineOfBusiness(String lineOfBusiness) {
		this.lineOfBusiness = lineOfBusiness;
	}

	public String getSalesRepresentative() {
		return salesRepresentative;
	}

	public void setSalesRepresentative(String salesRepresentative) {
		this.salesRepresentative = salesRepresentative;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Customer(String id, String customerName, String customerType, String lob, String salesRep) {
		name = customerName;
		customerId = id;
		type = customerType;
		lineOfBusiness = lob;
		salesRepresentative = salesRep;
	}
}
