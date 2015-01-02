package venuproject.uwf.edu;

import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.ws.http.HTTPException;

import org.apache.camel.Exchange;
import org.apache.camel.component.http.HttpOperationFailedException;
import org.restlet.util.NamedValue;
import org.restlet.util.Series;

public class TokenManager {

	private static final ConcurrentHashMap<String, Long> cache = new ConcurrentHashMap();

	/**
	 * @return the cache
	 */
	public static ConcurrentHashMap<String, Long> getCache() {
		return cache;
	}

	public boolean validateSession(Exchange exchange)  {
		String token = (String) exchange.getIn()
				.getHeader("Authorization");
		
		Map<String, Object> map = exchange.getIn().getHeaders();
		Object map2 = map.get("org.restlet.http.headers");
		Series headers = (Series) map2;
		token = headers.getFirstValue("Authorization");
		
		boolean invalidToken = true;
		if (token != null) {
			Long sessionTime = getCache().get(token);
			if (sessionTime != null) {
				long sessionExpiryTime = sessionTime.longValue() + 86400000;
				long currentTime = Calendar.getInstance().getTimeInMillis();
				if (currentTime <= sessionExpiryTime)
					invalidToken = false;
			}
		}
		if (invalidToken) {
			if (token != null) {
				getCache().remove(token);
			}

			exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 401);
			exchange.getIn().setBody("Requires New Login");
			
		}
		
		return !invalidToken;
	}
	
	public void cleanUpSession(Exchange exchange) {
		exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 401);
		exchange.getIn().setBody("Requires New Login");
	}

	public String createSession(Exchange exchange) {
		String token = (String) exchange.getIn()
				.getHeader("Authorization");
		if (token != null) {
			Long sessionTime = getCache().get(token);
			long currentTime = Calendar.getInstance().getTimeInMillis();
			sessionTime = new Long(currentTime);
			getCache().put(token, sessionTime);
		}
		String authToken = "{ \"authorizationToken\":\"" + token + "\" }";
		return authToken;
	}
	
	public void invalidateToken(Exchange exchange) {
		String token = (String) exchange.getIn()
				.getHeader("authorizationToken");
		if (token != null) {
			getCache().remove(token);
		}
	}
}
