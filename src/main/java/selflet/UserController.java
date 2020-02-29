package selflet;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import selflet.bean.CreateUser;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Component;
import org.springframework.http.client.ClientHttpResponse;
import java.io.IOException;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.HashMap;
//import java.util.Map;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;


@RestController
@RequestMapping(value = "/user")
public class UserController {

	@CrossOrigin(origins = "http://localhost:3000")
	@PostMapping("/user")
	public ResponseEntity<String> createUser(@RequestBody CreateUser createUser) {
		
		String token = createUser.getToken();
		String username = createUser.getUsername();
		String firstName = createUser.getFirstName();
		String lastName = createUser.getLastName();
		String agency = createUser.getAgency();
		String landlord = createUser.getLandlord();
		String tenant = createUser.getTenant();
		String agencyId = createUser.getAgencyId();
		
		String uri = "http://localhost:8080/auth/admin/realms/selflet/users";
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Authorization", "Bearer " + token);
		
		RestTemplate restTemplate = new RestTemplate();
		
		//add custom attributes
		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("agency", agency);
		attributes.put("landlord", landlord);
		attributes.put("tenant", tenant);
		attributes.put("agencyId", agencyId);	
		
		JSONObject requestJSON = new JSONObject();
		//requestJSON.put("token", token);
		requestJSON.put("username", username);
		requestJSON.put("email", username);
		requestJSON.put("firstName", firstName);
		requestJSON.put("lastName", lastName);
		requestJSON.put("enabled", true);
		requestJSON.put("attributes", attributes);

		httpHeaders.set("Content-Type", "application/json");
		HttpEntity<String> entity = new HttpEntity<String>(requestJSON.toString(),httpHeaders);	
		
		ResponseEntity<String> result = null;
		try {
			//String answer = restTemplate.postForObject(uri, entity, String.class);
			result = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
			
		//System.out.println(answer);
		} catch (HttpStatusCodeException ex) {
		    // raw http status code e.g `404`
		    if (ex.getRawStatusCode() ==409) {
		    	return new ResponseEntity<String>("Hello World!", HttpStatus.CONFLICT);
		    }		    
		}
		
		return result;
		
	}
	
	@CrossOrigin(origins = "http://localhost:3000")
	@PostMapping(value="/users", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> listUsers(@RequestBody CreateUser createUser) {
		String uri = "http://localhost:8080/auth/admin/realms/selflet/users";
		String agencyId = createUser.getAgencyId();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Authorization", "Bearer " + createUser.getToken());
		
		httpHeaders.set("Content-Type", "application/json");
		HttpEntity<String> entity = new HttpEntity<String>(httpHeaders);	
		
		RestTemplate restTemplate = new RestTemplate();
		
		try {
			ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
			
			//filter on agencyId
			JSONArray users = new JSONArray(result.getBody());
			for (int i=0; i < users.length(); i++) {
				JSONObject user = users.getJSONObject(i);
				JSONObject attribs = user.getJSONObject("attributes");
				if (!attribs.getJSONArray("agencyId").getString(0).equals(agencyId)) {
					users.remove(i);
				}
			}
			
			ResponseEntity<String> response = new ResponseEntity<String>(users.toString(), HttpStatus.OK);
			
			return response;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
		
	}
	
	@CrossOrigin(origins = "http://localhost:3000")
	@DeleteMapping(value="/users", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> deleteUser(@RequestBody CreateUser createUser) {
		String uri = "http://localhost:8080/auth/admin/realms/selflet/users/" + createUser.getId();
		
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Authorization", "Bearer " + createUser.getToken());		
		httpHeaders.set("Content-Type", "application/json");
		HttpEntity<String> entity = new HttpEntity<String>(httpHeaders);
		
		RestTemplate restTemplate = new RestTemplate();
		
		try {
			ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.DELETE, entity, String.class);
			System.out.println(result.getBody());
			return result;
		} catch(Exception ex) {
			return null;
		}		
	}
	
	@CrossOrigin(origins = "http://localhost:3000")
	@PutMapping(value="/users", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> editUser(@RequestBody CreateUser createUser) {
		String uri = "http://localhost:8080/auth/admin/realms/selflet/users/" + createUser.getId();
		
		String token = createUser.getToken();
		String username = createUser.getUsername();
		String firstName = createUser.getFirstName();
		String lastName = createUser.getLastName();
		String agency = createUser.getAgency();
		String landlord = createUser.getLandlord();
		String tenant = createUser.getTenant();
		String agencyId = createUser.getAgencyId();
		String email = createUser.getEmail();
		
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Authorization", "Bearer " + token);		
		httpHeaders.set("Content-Type", "application/json");
		
		RestTemplate restTemplate = new RestTemplate();
		
		//add custom attributes
		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("agency", agency);
		attributes.put("landlord", landlord);
		attributes.put("tenant", tenant);
		attributes.put("agencyId", agencyId);
		
		JSONObject requestJSON = new JSONObject();
		//requestJSON.put("token", token);
		requestJSON.put("username", username);
		requestJSON.put("email", email);
		requestJSON.put("firstName", firstName);
		requestJSON.put("lastName", lastName);
		requestJSON.put("enabled", true);
		requestJSON.put("attributes", attributes);
		
		HttpEntity<String> entity = new HttpEntity<String>(requestJSON.toString(),httpHeaders);	
		
		try {
			ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.PUT, entity, String.class);
			return result;
		} catch(Exception ex) {
			return null;
		}		
	}
	

}

