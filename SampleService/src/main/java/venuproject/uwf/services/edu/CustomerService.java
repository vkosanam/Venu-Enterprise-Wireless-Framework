package venuproject.uwf.services.edu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.camel.Exchange;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CustomerService {
	
	
    public String retrieveCustomers(Exchange exchange) throws Exception {
		
    	Object mandatoryBody = exchange.getIn().getMandatoryBody();
    	ArrayList<Customer> responseList = null;
		if (mandatoryBody instanceof ArrayList) {
			ArrayList listOfresults = (ArrayList) mandatoryBody;
			responseList = new  ArrayList<Customer>();
			for (Object result : listOfresults) {
				if (result instanceof HashMap<?,?>) {
					HashMap<String, String> dbObject = (HashMap<String, String>) result;
					Customer customer = new Customer(dbObject.get("customerId"), 
							dbObject.get("name"), dbObject.get("type"), dbObject.get("lineOfBusiness"), dbObject.get("salesRepresentative"));					
					responseList.add(customer);
				}
			}
		}
		 GsonBuilder builder = new GsonBuilder();
	     Gson gson = builder.create();
	    String  jsonStr = gson.toJson(responseList);

        return jsonStr;
    }

}
