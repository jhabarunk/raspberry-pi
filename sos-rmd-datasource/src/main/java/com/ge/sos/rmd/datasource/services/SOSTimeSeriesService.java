package com.ge.sos.rmd.datasource.services;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ge.predix.solsvc.restclient.config.IOauthRestConfig;
import com.ge.sos.rmd.datasource.PropertyConfig;
import com.ge.sos.rmd.datasource.data.QueryCollatedValueDTO;
import com.ge.sos.rmd.datasource.handlers.SOSDatasourceHandler;
import com.ge.sos.rmd.datasource.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "sos")
public class SOSTimeSeriesService {
	private static Logger log = LoggerFactory
			.getLogger(SOSTimeSeriesService.class);

	@Autowired
	SOSDatasourceHandler sosDatasourceHandler;

	@Autowired
	ObjectMapper mapper;

	@Autowired
	PropertyConfig propertyConfig;

	@Autowired
	protected IOauthRestConfig restConfig;

	public SOSTimeSeriesService() {
		super();
	}

	@SuppressWarnings("nls")
	@RequestMapping(value = "/ping", method = RequestMethod.GET)
	public String ping() {

		return "SUCCESS";
	}

	private Gson getMyGson() {

		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Double.class,
				new JsonDeserializer<Double>() {
					@Override
					public Double deserialize(final JsonElement json,
							final Type typeOfT,
							final JsonDeserializationContext context)
							throws JsonParseException {

						if (json.getAsString() == null
								|| json.getAsString().trim().length() == 0) {
							return null;
						} else {
							return Double.parseDouble(json.getAsString());

						}

					}
				});
		Gson gson = gsonBuilder.create();

		return gson;
	}

	// TODO: Hardcoded need to fix
	@RequestMapping(value = "/getSOSCollatedValue", method = RequestMethod.POST)
	public Map<String,String> getSOSCollatedResponse(
			@RequestHeader(value = "authorization") String authorization,
			@RequestBody QueryCollatedValueDTO queryCollatedValueDTO) {
		String value = null;
		if (null != queryCollatedValueDTO) {
			String floor_id=queryCollatedValueDTO.getFloor_id();
			String interval=queryCollatedValueDTO.getInterval();
			List<String> zoneNames = new ArrayList<String>();
			zoneNames.addAll(queryCollatedValueDTO.getZoneNames());
			//Long currentTimeMillis = System.currentTimeMillis();
			try {
				if (interval.equalsIgnoreCase("h")) {
					return sosDatasourceHandler.getSOSCollatedResponse(
							authorization, "1h-ago",floor_id, zoneNames, Constants.HOUR);
				}
				if (interval.equalsIgnoreCase("d")) {
					return sosDatasourceHandler.getSOSCollatedResponse(
							authorization, "1d-ago", floor_id, zoneNames, Constants.DAY);
					//currentTimeMillis - 86400000,
							
				}
				if (interval.equalsIgnoreCase("w")) {
					return sosDatasourceHandler.getSOSCollatedResponse(
							authorization, "1w-ago", floor_id, zoneNames, Constants.WEEK);
						//currentTimeMillis - 604800000,
				}
				if (interval.equalsIgnoreCase("m")) {
					return sosDatasourceHandler.getSOSCollatedResponse(
							authorization, "1mm-ago", floor_id, zoneNames, Constants.MONTH);
					//currentTimeMillis - 2628000 * 1000,
				}
				if (interval.equalsIgnoreCase("q")) {
					return sosDatasourceHandler.getSOSCollatedResponse(
							authorization, "3mm-ago", floor_id, zoneNames,Constants.QUARTER);
					//currentTimeMillis - 7884000 * 1000,
				}
				if (interval.equalsIgnoreCase("y")) {
					return sosDatasourceHandler.getSOSCollatedResponse(
							authorization,"1y-ago", floor_id , zoneNames, Constants.YEAR);
					//currentTimeMillis - 31540000 * 1000,
				}
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	@SuppressWarnings("nls")
	private OAuth2RestTemplate getRestTemplate(String clientId,
			String clientSecret) {
		ClientCredentialsResourceDetails clientDetails = new ClientCredentialsResourceDetails();
		clientDetails.setClientId(clientId);
		clientDetails.setClientSecret(clientSecret);
		String url = this.restConfig.getOauthResourceProtocol() + "://"
				+ this.restConfig.getOauthRestHost()
				+ this.restConfig.getOauthResource();
		clientDetails.setAccessTokenUri(url);
		clientDetails.setGrantType("client_credentials");
		OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(clientDetails);

		return restTemplate;
	}

	private String getToken() {
		String[] oauthClient = restConfig.getOauthClientId().split(":");
		String authorizationToken = "Bearer "
				+ getRestTemplate(oauthClient[0], oauthClient[1])
						.getAccessToken().getValue();
		return authorizationToken;
	}
}
