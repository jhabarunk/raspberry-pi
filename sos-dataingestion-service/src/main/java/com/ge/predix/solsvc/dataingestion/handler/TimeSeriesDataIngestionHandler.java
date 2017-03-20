package com.ge.predix.solsvc.dataingestion.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ge.predix.entity.asset.Asset;
import com.ge.predix.entity.asset.AssetTag;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.Body;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.DatapointsIngestion;
import com.ge.predix.solsvc.dataingestion.api.Constants;
import com.ge.predix.solsvc.dataingestion.service.type.JSONData;
import com.ge.predix.solsvc.dataingestion.websocket.WebSocketClient;
import com.ge.predix.solsvc.dataingestion.websocket.WebSocketConfig;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.timeseries.bootstrap.config.TimeseriesRestConfig;
import com.ge.predix.solsvc.timeseries.bootstrap.config.TimeseriesWSConfig;
import com.ge.predix.solsvc.timeseries.bootstrap.factories.TimeseriesFactory;

/**
 * 
 * @author predix -
 */
@Component
public class TimeSeriesDataIngestionHandler extends BaseFactory {
	private static Logger log = Logger.getLogger(TimeSeriesDataIngestionHandler.class);
	@Autowired
	private TimeseriesFactory timeSeriesFactory;

	@Autowired
	private AssetDataHandler assetDataHandler;

	@Autowired
	private TimeseriesWSConfig tsInjectionWSConfig;

	@Autowired
	private WebSocketConfig wsConfig;

	@Autowired
	private WebSocketClient wsClient;

	@Autowired
	private JsonMapper jsonMapper;

	@Autowired
	private TimeseriesRestConfig timeseriesRestConfig;

	@Autowired
	Environment evn;

	/**
	 * -
	 */
	@SuppressWarnings("nls")
	@PostConstruct
	public void intilizeDataIngestionHandler() {
		log.info("*******************TimeSeriesDataIngestionHandler Initialization complete*********************");
	}

	/**
	 * @param data
	 *            -
	 * @param authorization
	 *            -
	 */
	@SuppressWarnings("nls")
	public void handleData(String data, String authorization) {
		log.info("Data from Simulator : " + data);
		List<Header> headers = this.restClient.getSecureTokenForClientId();
		this.restClient.addZoneToHeaders(headers, this.timeseriesRestConfig.getZoneId());
		headers.add(new BasicHeader("Origin", "http://localhost"));
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
			mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
			mapper.setSerializationInclusion(Include.NON_NULL);
			// Changes Start Sipra
			@SuppressWarnings("unchecked")
			DatapointsIngestion dpIngestion = (DatapointsIngestion) mapper.readValue(data, new com.fasterxml.jackson.core.type.TypeReference<DatapointsIngestion>() {});
			log.info("inside hanleData");
			log.info("TimeSeries URL : " + this.tsInjectionWSConfig.getInjectionUri());
			log.info("WebSocket URL : " + this.wsConfig.getPredixWebSocketURI());
			// Sipra
			/*String machineValue = evn.getProperty("user.asset.machineValue");
			Map<String, AssetObject> assetObjectMap = this.assetMap
					.get(machineValue);
			if (assetObjectMap == null) {
				assetObjectMap = assetDataHandler
						.retriveAssetObjects(authorization);
				this.assetMap.put(machineValue, assetObjectMap);
			}*/
			log.info("TimeSeries JSON : " + this.jsonMapper.toJson(dpIngestion));
			if (dpIngestion.getBody() != null && dpIngestion.getBody().size() > 0) {
				// commented by sipra
				//this.wsClient.postToWebSocketServer(this.jsonMapper.toJson(dpIngestion));
				log.info("Posted Data to Predix Websocket Server");

				this.timeSeriesFactory.createConnectionToTimeseriesWebsocket(headers);
				this.timeSeriesFactory.postDataToTimeseriesWebsocket(dpIngestion, headers);
				this.timeSeriesFactory.closeConnectionToTimeseriesWebsocket();
				log.info("Posted Data to Timeseries");
			}

		} catch (JsonParseException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param json
	 * @param i
	 * @param asset
	 * @param assetTag
	 * @return -
	 */
	@SuppressWarnings({ "unchecked", "unused", "nls" })
	private DatapointsIngestion createTimeseriesDataBody(JSONData json, Long i, Asset asset, AssetTag assetTag) {
		DatapointsIngestion dpIngestion = new DatapointsIngestion();
		dpIngestion.setMessageId(String.valueOf(System.currentTimeMillis()));

		Body body = new Body();
		body.setName(assetTag.getSourceTagId());
		// Sipra
		// attributes
		com.ge.predix.entity.util.map.Map map = new com.ge.predix.entity.util.map.Map();
		map.put("assetId", asset.getAssetId());
		if (!StringUtils.isEmpty(assetTag.getSourceTagId())) {
			String sourceTagAttribute = assetTag.getSourceTagId().split(":")[1];
			map.put("sourceTagId", sourceTagAttribute);
		}
		body.setAttributes(map);
		// Sipra
		// datapoints
		List<Object> datapoint1 = new ArrayList<Object>();
		datapoint1.add(converLocalTimeToUtcTime(json.getTimestamp().getTime()));
		Double convertedValue = getConvertedValue(assetTag.getTagDatasource().getNodeName(), Double.parseDouble(json.getValue().toString()));
		datapoint1.add(convertedValue);

		List<Object> datapoints = new ArrayList<Object>();
		datapoints.add(datapoint1);
		body.setDatapoints(datapoints);

		List<Body> bodies = new ArrayList<Body>();
		bodies.add(body);

		dpIngestion.setBody(bodies);

		return dpIngestion;
	}
	
	
	@SuppressWarnings("nls")
	public Double getConvertedValue(String nodeName, Double value) {
		Double convValue = null;
		switch (nodeName.toLowerCase()) {
		case Constants.COMPRESSION_RATIO:
			convValue = value * 9.0 / 65535.0 + 1;
			break;
		case Constants.DISCHG_PRESSURE:
			convValue = value * 100.0 / 65535.0;
			break;
		case Constants.SUCT_PRESSURE:
			convValue = value * 100.0 / 65535.0;
			break;
		case Constants.MAX_PRESSURE:
			convValue = value * 100.0 / 65535.0;
			break;
		case Constants.MIN_PRESSURE:
			convValue = value * 100.0 / 65535.0;
			break;
		case Constants.VELOCITY:
			convValue = value * 0.5 / 65535.0;
			break;
		case Constants.TEMPERATURE:
			convValue = value * 200.0 / 65535.0;
			break;
		default:
			throw new UnsupportedOperationException("nameName=" + nodeName + " not found");
		}
		return convValue;
	}

	private long converLocalTimeToUtcTime(long timeSinceLocalEpoch) {
		return timeSinceLocalEpoch + getLocalToUtcDelta();
	}

	private long getLocalToUtcDelta() {
		Calendar local = Calendar.getInstance();
		local.clear();
		local.set(1970, Calendar.JANUARY, 1, 0, 0, 0);
		return local.getTimeInMillis();
	}

	@SuppressWarnings("nls")
	private AssetTag getAssetTag(LinkedHashMap<String, AssetTag> tags, String nodeName) {
		AssetTag ret = null;
		if (tags != null) {
			for (Entry<String, AssetTag> entry : tags.entrySet()) {
				AssetTag assetTag = entry.getValue();
				// TagDatasource dataSource = assetTag.getTagDatasource();
				if (assetTag != null && !assetTag.getSourceTagId().isEmpty() && nodeName != null && nodeName.toLowerCase().contains(assetTag.getSourceTagId().toLowerCase())) {
					ret = assetTag;
					return ret;
				}
			}
		} else {
			log.warn("2. asset has no assetTags with matching nodeName" + nodeName);
		}
		return ret;
	}
}
