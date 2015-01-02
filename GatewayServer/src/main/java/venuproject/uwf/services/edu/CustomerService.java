package venuproject.uwf.services.edu;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CustomerService {
	
	public static List<Customer> setTestData() {
		ArrayList<Customer> customers = new ArrayList<Customer>();
		Customer customer = new Customer("Venu");
		customers.add(customer);
		customers.add(new Customer("uwf"));
		customers.add(new Customer("aurora"));
		return customers;
	}
	
    public String retrieveCustomers(Exchange exchange) {
		
		 GsonBuilder builder = new GsonBuilder();
	     Gson gson = builder.create();
	    String  jsonStr = gson.toJson(setTestData());

        return jsonStr;
	}

}
