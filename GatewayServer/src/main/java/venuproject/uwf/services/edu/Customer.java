package venuproject.uwf.services.edu;

public class Customer {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Customer(String customerName) {
		name = customerName;
	}
}
