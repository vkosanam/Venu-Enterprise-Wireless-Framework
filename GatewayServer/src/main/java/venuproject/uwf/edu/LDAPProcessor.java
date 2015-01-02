/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package venuproject.uwf.edu;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.ldap.LdapName;
import javax.servlet.http.HttpSession;

import org.apache.camel.Exchange;
import org.eclipse.jetty.client.HttpExchange;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.ldap.support.LdapUtils;

import sun.misc.BASE64Encoder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


/**
 *
 * @author Venu Kosanam
 */
public class LDAPProcessor implements PersonDao {

	private LdapTemplate ldapTemplate;

	org.slf4j.Logger logger = org.slf4j.LoggerFactory
			.getLogger(LDAPProcessor.class);

/*	public List<String> process(Exchange exchange) throws Exception {
		// String msg = exchange.getIn().getBody().toString();

		List<String> ldapResponse = ldapTemplate.search(query()
				.attributes("cn").where("objectclass").is("person"),
				new AttributesMapper<String>() {
					public String mapFromAttributes(Attributes attrs)
							throws NamingException {
						return attrs.get("cn").get().toString();
					}
				});

		exchange.getIn().setBody(ldapResponse);
		String msg = exchange.getIn().getBody().toString();
		logger.info("Sending Response: " + msg);
		return ldapResponse;
	}
	*/
	public List<String> process(Exchange exchange) throws Exception {
		// String msg = exchange.getIn().getBody().toString();

		List<String> ldapResponse = ldapTemplate.search(query()
				.attributes("cn").where("objectclass").is("person"),
				new AttributesMapper<String>() {
					public String mapFromAttributes(Attributes attrs)
							throws NamingException {
						return attrs.get("cn").get().toString();
					}
				});

		exchange.getIn().setBody(ldapResponse);
		String msg = exchange.getIn().getBody().toString();
		logger.info("Sending Response: " + msg);
		return ldapResponse;
	}
	
	public void processCORS(Exchange exchange) throws Exception {
	/*	
		String accessControlAllow = (String) exchange.getIn().getHeader("Access-Control-Allow-Origin");
		if ( accessControlAllow == null) {
			String origin = (String) exchange.getIn().getHeader("Origin");
			exchange.getIn().setHeader("Access-Control-Allow-Origin", "http://localhost:8100");
		}
		
		String credentials = (String) exchange.getIn().getHeader("Access-Control-Allow-Credentials");
		if ( credentials == null) {
			exchange.getIn().setHeader("Access-Control-Allow-Credentials", "true");
		}
		
		String methods = (String) exchange.getIn().getHeader("Access-Control-Allow-Methods");
		if ( methods == null) {
			exchange.getIn().setHeader("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT,HEAD");
		}
		
		String maxAge = (String) exchange.getIn().getHeader("Access-Control-Max-Age");
		if ( maxAge == null) {
			exchange.getIn().setHeader("Access-Control-Max-Age", "1800");
		}
		
		String allowHeaders = (String) exchange.getIn().getHeader("Access-Control-Allow-Headers");
		if ( allowHeaders == null) {
			exchange.getIn().setHeader("Access-Control-Allow-Headers", "origin, content-type, accept");
		}
		*/

	}
	

	public LdapTemplate getLdapTemplate() {
		return ldapTemplate;
	}

	public void setLdapTemplate(LdapTemplate ldapTemplate) {
		this.ldapTemplate = ldapTemplate;
	}

	@Override
	public void create(Person person) {
		Name dn = buildDn(person);
		DirContextAdapter context = new DirContextAdapter(dn);
		mapToContext(person, context);
		ldapTemplate.bind(dn, context, null);
	}

	@Override
	public void update(Person person) {
		Name dn = buildDn(person);
		DirContextAdapter context = (DirContextAdapter) ldapTemplate.lookup(dn);
		mapToContext(person, context);
		ldapTemplate.modifyAttributes(dn, context.getModificationItems());
	}

	@Override
	public void delete(Person person) {
		ldapTemplate.unbind(buildDn(person));
	}

	@Override
	public List<String> getAllPersonNames() {
		return ldapTemplate.search(query().attributes("cn")
				.where("objectclass").is("person"),
				new AttributesMapper<String>() {
					public String mapFromAttributes(Attributes attrs)
							throws NamingException {
						return attrs.get("cn").get().toString();
					}
				});
	}

	@Override
	public List<Person> findAll() {
		return ldapTemplate.search(query().where("objectclass").is("person"),
				PERSON_CONTEXT_MAPPER);
	}

	public Person getUser(Exchange exchange) {
		
		String username = (String) exchange.getIn().getHeader("personId");
		
		LdapName dn = buildDn("", "users", username);
		Person ldapResponse = (Person) ldapTemplate.lookup(dn, PERSON_CONTEXT_MAPPER);
		exchange.getIn().setBody(ldapResponse);
		String msg = exchange.getIn().getBody().toString();
		logger.info("Sending Response: " + msg);
		findGroupsByParam();
		return ldapResponse;
	}
	
	
	public void authenticate(Exchange exchange) {
		
		String msg = getStringFromInputStream((InputStream)exchange.getIn().getBody());
		 GsonBuilder builder = new GsonBuilder();
	        Gson gson = builder.create();
	    Map<String,Map<String,String>> user = gson.fromJson(msg, Map.class);
	    
	    Map<String,String> userInfo = user.get("user");
	    String userName = userInfo.get("username");
	    String password = userInfo.get("password");
	    
	    AndFilter filter = new AndFilter();
	    filter.and(new EqualsFilter("objectclass", "person")).and(new EqualsFilter("cn", userName));
	    boolean authenticated =  ldapTemplate.authenticate(LdapUtils.emptyLdapName(), filter.toString(),password);
	    
	    
	//    exchange.getIn().setBody("{ authorizationToken: \"NjMwNjM4OTQtMjE0Mi00ZWYzLWEzMDQtYWYyMjkyMzNiOGIy\" }");
     exchange.getIn().setHeader("Authorization", "NjMwNjM4OTQtMjE0Mi00ZWYzLWEzMDQtYWYyMjkyMzNiOGIy");
	//    exchange.get
	}
	
	 @Override
		public Person findByPrimaryKey(String country, String company, String fullname) {
			LdapName dn = buildDn(country, company, fullname);
			return ldapTemplate.lookup(dn, PERSON_CONTEXT_MAPPER);
		}

	private LdapName buildDn(Person person) {
		return buildDn(person.getCountry(), person.getCompany(),
				person.getFullName());
	}

	private LdapName buildDn(String country, String users, String fullname) {
		return LdapNameBuilder.newInstance().add("ou", users).add("cn", fullname).build();

//	return LdapNameBuilder.newInstance().add("dc", "com").add("dc", "uwf").add("ou", "groups").add("cn", "user").build();
	}

	private void mapToContext(Person person, DirContextAdapter context) {
		context.setAttributeValues("objectclass", new String[] { "top",
				"person" });
		context.setAttributeValue("cn", person.getFullName());
		context.setAttributeValue("sn", person.getLastName());
		context.setAttributeValue("description", person.getDescription());
		context.setAttributeValue("telephoneNumber", person.getPhone());
	}

	/**
	 * Maps from DirContextAdapter to Person objects. A DN for a person will be
	 * of the form <code>cn=[fullname],ou=[company],c=[country]</code>, so the
	 * values of these attributes must be extracted from the DN. For this, we
	 * use the LdapName along with utility methods in LdapUtils.
	 */
	private final static ContextMapper<Person> PERSON_CONTEXT_MAPPER = new AbstractContextMapper<Person>() {
		@Override
		public Person doMapFromContext(DirContextOperations context) {
			Person person = new Person();

			LdapName dn = LdapUtils.newLdapName(context.getDn());
			person.setCountry(LdapUtils.getStringValue(dn, 0));
			person.setCompany(LdapUtils.getStringValue(dn, 1));
			person.setFullName(context.getStringAttribute("cn"));
			person.setLastName(context.getStringAttribute("sn"));
			person.setDescription(context.getStringAttribute("description"));
			person.setPhone(context.getStringAttribute("telephoneNumber"));

			return person;
		}
	};
	
	public List<Person> findGroupsByParam() {
		// TODO Auto-generated method stub
		String filter=null;

				filter="(&(objectClass=groupOfNames)(member=cn=bob,ou=groups,dc=uwf,dc=com" + "))";

		List<Person> groups = ldapTemplate.search("cn=admin,cn=user,ou=groups", filter, PERSON_CONTEXT_MAPPER);

		return groups;
	}
	
	private static String getStringFromInputStream(InputStream is) {
 
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
 
		String line;
		try {
 
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
 
		return sb.toString();
 
	}
	
	private static String encrypt(String plaintext) {
		        MessageDigest md = null;
		        try {
		            md = MessageDigest.getInstance("SHA");
		        } catch (NoSuchAlgorithmException e) {
		            throw new RuntimeException(e.getMessage());
		        }
		        try {
		            md.update(plaintext.getBytes("UTF-8"));
		        } catch (UnsupportedEncodingException e) {
		            throw new RuntimeException(e.getMessage());
		        }
		        byte raw[] = md.digest();
		        String hash = "{SHA}" + (new BASE64Encoder()).encode(raw);
		        return hash;
		    }


}
