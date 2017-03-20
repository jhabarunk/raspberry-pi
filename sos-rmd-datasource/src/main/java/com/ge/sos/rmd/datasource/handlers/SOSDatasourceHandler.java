package com.ge.sos.rmd.datasource.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ge.predix.entity.timeseries.datapoints.queryresponse.DatapointsResponse;
import com.ge.predix.solsvc.restclient.impl.RestClient;
import com.ge.predix.solsvc.timeseries.bootstrap.config.TimeseriesRestConfig;
import com.ge.predix.solsvc.timeseries.bootstrap.factories.TimeseriesFactory;
import com.ge.sos.rmd.datasource.data.Result;
import com.ge.sos.rmd.datasource.data.SOSFloor;
import com.ge.sos.rmd.datasource.data.SOSLight;
import com.ge.sos.rmd.datasource.data.SOSSwitch;
import com.ge.sos.rmd.datasource.data.SOSZone;
import com.ge.sos.rmd.datasource.data.Tag;
import com.ge.sos.rmd.datasource.data.Timeseries;
import com.google.gson.Gson;

@Component
public class SOSDatasourceHandler {

	private static Logger log = LoggerFactory
			.getLogger(SOSDatasourceHandler.class);

	@Autowired
	private TimeseriesFactory timeSeriesFactory;

	@Autowired
	private TimeseriesRestConfig timeseriesRestConfig;

	@Autowired
	protected RestClient restClient;

	// @Autowired
	// protected IOauthRestConfig restConfig;

	// @SuppressWarnings("nls")
	// private OAuth2RestTemplate getRestTemplate(String clientId, String
	// clientSecret)
	// {
	// ClientCredentialsResourceDetails clientDetails = new
	// ClientCredentialsResourceDetails();
	// clientDetails.setClientId(clientId);
	// clientDetails.setClientSecret(clientSecret);
	// String url = this.restConfig.getOauthResourceProtocol() + "://" +
	// this.restConfig.getOauthRestHost()
	// + this.restConfig.getOauthResource();
	// clientDetails.setAccessTokenUri(url);
	// clientDetails.setGrantType("client_credentials");
	// OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(clientDetails);
	//
	// return restTemplate;
	// }

	public Map<String, String> getSOSCollatedResponse(String authorization,
			String start_time, String floorId, List<String> zoneNames,
			String interval) throws JsonProcessingException {
		long actualUsage=0;
		List<Header> headers = new ArrayList<Header>();
		restClient.addSecureTokenToHeaders(headers, authorization);
		restClient.addZoneToHeaders(headers, timeseriesRestConfig.getZoneId());
		List<com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag> tags = new ArrayList<com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag>();
		com.ge.predix.entity.timeseries.datapoints.queryrequest.DatapointsQuery datapoints = new com.ge.predix.entity.timeseries.datapoints.queryrequest.DatapointsQuery();

		datapoints.setStart(start_time);
		log.info("Interval: " + interval);
		// datapoints.setEnd(end_time);
		com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag tag = null;
		for (String zoneName : zoneNames) {
			tag = new com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag();
			tag.setName(zoneName);
			do {

				com.ge.predix.entity.timeseries.datapoints.queryrequest.Aggregation agg = new com.ge.predix.entity.timeseries.datapoints.queryrequest.Aggregation();
				agg.setType("sum");
				agg.setInterval(interval);

				List<com.ge.predix.entity.timeseries.datapoints.queryrequest.Aggregation> aggs = new ArrayList<com.ge.predix.entity.timeseries.datapoints.queryrequest.Aggregation>();
				aggs.add(agg);
				tag.setAggregations(aggs);

				com.ge.predix.entity.timeseries.datapoints.queryrequest.Filters filters = new com.ge.predix.entity.timeseries.datapoints.queryrequest.Filters();

				com.ge.predix.entity.util.map.Map map = new com.ge.predix.entity.util.map.Map();
				map.put("floor_id", floorId);

				List<Integer> intList = new ArrayList<Integer>();
				com.ge.predix.entity.timeseries.datapoints.queryrequest.Measurements measurement = new com.ge.predix.entity.timeseries.datapoints.queryrequest.Measurements();
				measurement.setCondition("ge");
				intList.add(1);
				measurement.setValues(intList);

				filters.setAttributes(map);
				filters.setMeasurements(measurement);

				tag.setFilters(filters);

			} while (false);
			tags.add(tag);
		}
		datapoints.setTags(tags);
		DatapointsResponse response = this.timeSeriesFactory
				.queryForDatapoints(this.timeseriesRestConfig.getBaseUrl(),
						datapoints, headers);
		String gson;
		gson = new Gson().toJson(response);
		Timeseries timeseries = new Timeseries();
		timeseries = new Gson().fromJson(gson, Timeseries.class);
		actualUsage = parseCollatedResult(timeseries);
		RestTemplate restTemplate = new RestTemplate();
	    SOSFloor floor = restTemplate.getForObject("https://sos-db-service.run.aws-usw02-pr.ice.predix.io/sos/findOneFloor?id="+floorId, SOSFloor.class);
	    long theroticalWattage=0l;
	    if(null!=floor.getZones() && floor.getZones().size()>0){
	    	for(SOSZone sosZone:floor.getZones()){
	    		if(null!=sosZone.getSwitchs() && sosZone.getSwitchs().size()>0){
	    			for(SOSSwitch sosSwitch:sosZone.getSwitchs()){
	    				if(null!=sosSwitch.getLights() && sosSwitch.getLights().size()>0){
	    					for(SOSLight sosLight:sosSwitch.getLights())
	    						theroticalWattage+=(long)sosLight.getWattage();
	    				}
	    			}
	    		}
	    	}
	    }
	    Map<String, String> result=new HashMap<String, String>();
	    int totalHrs = 0;
	    if("1d-ago".equalsIgnoreCase(start_time))
	    	totalHrs=24;
	    else if("1w-ago".equalsIgnoreCase(start_time))
		    totalHrs=168;
	    else if("1mm-ago".equalsIgnoreCase(start_time))
		    totalHrs=730;
	    else if("3mm-ago".equalsIgnoreCase(start_time))
		    totalHrs=2190;
	    else if("1y-ago".equalsIgnoreCase(start_time))
		    totalHrs=8760;
	    log.info("theroticalWattage"+theroticalWattage);
	    log.info("totalHrs"+totalHrs);
	    long totalBaseline=theroticalWattage*totalHrs;
	    log.info("totalBaseline="+totalBaseline);
	    long savings=totalBaseline-actualUsage;
	    log.info("savings="+savings);
	    float percentageSaving=(float) ((savings*100.0)/totalBaseline);
	    log.info("percentageSaving="+percentageSaving);
	    result.put("savings", String.format("%.2f", percentageSaving));
	    result.put("enrgyUsed", String.valueOf(actualUsage));
		return result;
	}

	private long parseCollatedResult(Timeseries timeseries) {
		long value = 0;
		for (Tag tag : timeseries.getTags()) {
			for (Result result : tag.getResults()) {
				if (null != result.getValues()  && result.getValues().size()>0) {
					List<List<Long>> values = result.getValues();
					List<Long> data = values.get(0);
					value = value + data.get(1);
				}
			}
		}
		return value;
	}
}
