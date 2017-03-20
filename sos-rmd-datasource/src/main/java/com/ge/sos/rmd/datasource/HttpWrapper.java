package com.ge.sos.rmd.datasource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class HttpWrapper {
	private static Logger log = LoggerFactory.getLogger(HttpWrapper.class);
	
	public static Map<String,String> getRESTAPIforMachine(String authorizationToken ,String   RESTURL) throws Exception {

  		log.info("::propertyConfig RESTURL is :::"+RESTURL);
		HashMap<String,String> map=null;
		HttpClient httpClient = HttpClientBuilder.create().build();
	    try
	    {
	        HttpGet getRequest = new HttpGet(RESTURL);
	        //HttpGet getRequest = new HttpGet(RESTSERVER1+"/"+areaId+"/"+deptId);

	        getRequest.addHeader("accept", "application/json");
		    getRequest.addHeader("Authorization",authorizationToken);
	        HttpResponse response = httpClient.execute(getRequest);
	         
	        int statusCode = response.getStatusLine().getStatusCode();
	        if (statusCode != 200) 
	        {
	            throw new RuntimeException("Failed with HTTP error code : " + statusCode);
	        }
	    	BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
			String output;
			StringBuffer data = new StringBuffer();

			if(br!=null)
			while ((output = br.readLine()) != null) {
				data.append(output);
		  		log.info(output);
			}
		      //Type listType = new TypeToken<HashMap<String, String>>(){}.getType();
			br.close();
			map = new Gson().fromJson(data.toString(), new TypeToken<HashMap<String, String>>(){}.getType());

	    }
	    catch (IOException e) {
	  		log.error("error in getRESTAPIforMachine",e);
		  }
	    return map;
	}
	
	
	public static String postHttpResource(String authorizationToken , String entity,String RESTSERVER) throws ClientProtocolException, IOException {

		HttpClient client = HttpClientBuilder.create().build();
		HttpPost postRequest = new HttpPost(RESTSERVER);
		postRequest.addHeader("Content-Type","application/json");
		postRequest.addHeader("Authorization",authorizationToken);
		if(entity!=null && !entity.isEmpty()){
			
			HttpEntity httpEntity = new StringEntity(entity);
			postRequest.setEntity(httpEntity);
		}
		
		HttpResponse response = client.execute(postRequest);


		log.info("Response Code : "
	                + response.getStatusLine().getStatusCode());

		BufferedReader rd = new BufferedReader(
			new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		if(rd!=null)
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		rd.close();
		return result.toString();

	}

	
	/*public static Map<String,FeederWelderRelVO> getRESTAPIforWMachine(String authorizationToken ,String   RESTURL) throws Exception {
		
  		log.info("::propertyConfig RESTURL is :::"+RESTURL);
		Map<String, FeederWelderRelVO> map=null;
		HttpClient httpClient = HttpClientBuilder.create().build();
		BufferedReader br =null;
	    try
	    {
	        HttpGet getRequest = new HttpGet(RESTURL);
	        //HttpGet getRequest = new HttpGet(RESTSERVER1+"/"+areaId+"/"+deptId);

	        getRequest.addHeader("accept", "application/json");
		    getRequest.addHeader("Authorization",authorizationToken);
	          
	        HttpResponse response = httpClient.execute(getRequest);
	         
	        int statusCode = response.getStatusLine().getStatusCode();
	        if (statusCode != 200) 
	        {
	            throw new RuntimeException("Failed with HTTP error code : " + statusCode);
	        }
	    	br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
			String output;
			StringBuffer data = new StringBuffer();
			if(br!=null)
			while ((output = br.readLine()) != null) {
				data.append(output);
		  		log.info(output);
			}
		      //Type listType = new TypeToken<HashMap<String, String>>(){}.getType();

			map = new Gson().fromJson(data.toString(), new TypeToken<HashMap<String,FeederWelderRelVO>>(){}.getType());

	    }
	    catch (IOException e) {
	    	log.error(":::exception in  getRESTAPIforWMachine::-->"+e);
	    }
	    finally
	    {
	    	if(br!=null)
	    		br.close();
	    }
	    return map;
	}*/
}
