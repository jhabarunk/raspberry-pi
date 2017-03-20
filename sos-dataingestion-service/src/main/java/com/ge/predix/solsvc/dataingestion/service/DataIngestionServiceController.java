package com.ge.predix.solsvc.dataingestion.service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ge.predix.entity.asset.Asset;
import com.ge.predix.entity.asset.AssetTag;
import com.ge.predix.entity.asset.TagDatasource;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.Body;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.DatapointsIngestion;
import com.ge.predix.solsvc.dataingestion.api.DataIngestionServiceAPI;
import com.ge.predix.solsvc.dataingestion.handler.DataIngestionHandler;
import com.ge.predix.solsvc.dataingestion.service.type.*;

/**
 * 
 * @author predix -
 */
@RestController
@SuppressWarnings("nls")
public class DataIngestionServiceController implements DataIngestionServiceAPI {
	private static Logger log = LoggerFactory.getLogger(DataIngestionServiceController.class);

	private ObjectMapper mapper = new ObjectMapper();

	public DataIngestionServiceController() {
		mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
		// mapper.configure(SerializationFeature.INDENT_OUTPUT,true);
		mapper.setSerializationInclusion(Include.NON_NULL);

	}

	@Autowired
	private DataIngestionHandler dataIngestionHandler;

	/**
	 * @return -
	 */
	@RequestMapping(value = "/ping", method = RequestMethod.GET)
	public String ping() {
		return "SUCCESS"; //$NON-NLS-1$
	}

	@Override
	@RequestMapping(value = "/ingestdata", method = RequestMethod.POST)
	public @ResponseBody String ingestFileData(
			@RequestHeader(value = "authorization", required = false) String authorization,
			@RequestParam("filename") String fileName, @RequestParam("clientId") String clientId,
			@RequestParam("tenantId") String tenantId, @RequestParam("file") MultipartFile file) {
		try {
			if (!file.isEmpty()) {
				try {
					byte[] bytes = file.getBytes();

					// authorization ="Bearer
					// eyJhbGciOiJSUzI1NiIsImtpZCI6ImxlZ2FjeS10b2tlbi1rZXkiLCJ0eXAiOiJKV1QifQ.eyJqdGkiOiI2YTFhODgzNzdhY2Y0NjQzYTRhMDlkODI4MGM0OGMyMyIsInN1YiI6ImFwcF9jbGllbnRfMSIsInNjb3BlIjpbInRpbWVzZXJpZXMuem9uZXMuZTUxN2VlZWYtMDJiYy00NTQ4LWFmZWEtMmQxNmVjYzI1YTNmLnF1ZXJ5IiwidGltZXNlcmllcy56b25lcy5lNTE3ZWVlZi0wMmJjLTQ1NDgtYWZlYS0yZDE2ZWNjMjVhM2YudXNlciIsInVhYS5yZXNvdXJjZSIsInRpbWVzZXJpZXMuem9uZXMuZTUxN2VlZWYtMDJiYy00NTQ4LWFmZWEtMmQxNmVjYzI1YTNmLmluZ2VzdCIsIm9wZW5pZCIsInVhYS5ub25lIiwicHJlZGl4LWFzc2V0LnpvbmVzLjkyNTZmY2ZlLTQ3MmMtNDJmZi05ZmM5LTAxMTkzMDlkMmM0OS51c2VyIl0sImNsaWVudF9pZCI6ImFwcF9jbGllbnRfMSIsImNpZCI6ImFwcF9jbGllbnRfMSIsImF6cCI6ImFwcF9jbGllbnRfMSIsImdyYW50X3R5cGUiOiJjbGllbnRfY3JlZGVudGlhbHMiLCJyZXZfc2lnIjoiNTExY2U1ZjkiLCJpYXQiOjE0ODc1ODg2MzIsImV4cCI6MTQ4NzYzMTgzMiwiaXNzIjoiaHR0cHM6Ly81ZGYwODkxOS0xYWQ0LTQ5YmQtOTc1YS1kZDgzZjcwNmY3OGUucHJlZGl4LXVhYS5ydW4uYXdzLXVzdzAyLXByLmljZS5wcmVkaXguaW8vb2F1dGgvdG9rZW4iLCJ6aWQiOiI1ZGYwODkxOS0xYWQ0LTQ5YmQtOTc1YS1kZDgzZjcwNmY3OGUiLCJhdWQiOlsiYXBwX2NsaWVudF8xIiwicHJlZGl4LWFzc2V0LnpvbmVzLjkyNTZmY2ZlLTQ3MmMtNDJmZi05ZmM5LTAxMTkzMDlkMmM0OSIsInVhYSIsIm9wZW5pZCIsInRpbWVzZXJpZXMuem9uZXMuZTUxN2VlZWYtMDJiYy00NTQ4LWFmZWEtMmQxNmVjYzI1YTNmIl19.kq0KYoLSlPlf_2D2iESA3jyU5yfMNGzW0QPFf8Z8Zn104gKSyjozGuG6bXEFiIU4JQlV92Jy9R2ChGSt6P04O9EYve0B6E3p4wY_oS3R4pi6ZxK-8KAOzN6zEytdmJJxYb_q2FEfZlEF_eKrHeiwq2jX109vVecKRhgOf-LUP8ozNRTYDKqaith7Md-LAhvVcXB7ovP5Wu7a0WvZLOp3H95n_DWp_vPItX54Dd3Z1BdpbFmda_QKQn2sLGOpeYkjdxlLamnxrZM0_1lP9KV7WjK9cjv886vkh_7LF3IRvFYPCh_1yYPEHoJK63bS7o0zCJi_b6F7PO2hRI06i5vCXw";
					this.dataIngestionHandler.handleData(tenantId, clientId, new String(bytes), authorization);
					return "You successfully uploaded " + fileName + "!";
				} catch (Throwable e) {
					log.error("Unable to Upload " + fileName, e);
					return "You failed to upload " + fileName + " => " + e.getMessage();
				}
			}
			return "You failed to upload " + fileName + " because the file was empty.";
		} catch (Throwable e) {
			log.error("Unable to Upload " + fileName, e);
			return "Request failed. " + e.getMessage();
		}
	}

	@Override
	@RequestMapping(value = "/saveTimeSeriesData", method = RequestMethod.POST)
	public @ResponseBody String saveTimeSeriesData(
			@RequestHeader(value = "authorization", required = false) String authorization,
			@RequestParam("clientId") String clientId, @RequestParam("tenantId") String tenantId,
			@RequestParam("content") String content) {

		try {
			log.debug("Content : " + content);
			log.error("Client id" + clientId);

			this.dataIngestionHandler.handleData(tenantId, clientId, content, authorization);
			return "You successfully posted data";
		} catch (Throwable e) {
			log.error("Unable to Save " + content, e);
			throw new RuntimeException(e);
		}

	}

	@RequestMapping(value = "/saveTImeSeriesDataWifi", method = RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Integer> saveTImeSeriesDataWifi(@RequestHeader Map<String, String> header,
			@RequestBody Map<String, String> message) {
		System.out.println(header);
		System.out.println(message);
		Long timestamp = System.currentTimeMillis();
		String content = "";
		log.info("Messgae " + message);
		try {
			content = generateAndPushData(timestamp, Long.parseLong(message.get("wattage")), message.get("zone_name"),
					message.get("floor_id"));
			log.debug("Content : " + content);
			this.dataIngestionHandler.handleData("", "idTimeSeries", content, "");
			return new ResponseEntity<Integer>(200, HttpStatus.OK);
		} catch (Throwable e) {
			log.error("Unable to Save " + content, e);
			throw new RuntimeException(e);
		}
		//return new ResponseEntity<Integer>(200, HttpStatus.OK);
	}

	private String generateAndPushData(Long currentTimeMillis, long value, String tagName, String floor_id)
			throws JsonGenerationException, JsonMappingException, IOException {
		ObjectVO objectVO = new ObjectVO();
		List<BodyVO> list = new ArrayList<>();
		BodyVO bodyVO = getRandomDataVO(currentTimeMillis, value, tagName, true, floor_id);
		list.add(bodyVO);
		objectVO.setBody(list);
		objectVO.setMessageId(currentTimeMillis);
		DatapointsIngestion dataPtIngest = createDataIngestionRequest(objectVO);
		StringWriter writer = new StringWriter();
		mapper.writeValue(writer, dataPtIngest);
		System.out.println("Final JSON sending to saveTimeSeries >> " + writer.toString());
		return writer.toString();

	}

	private BodyVO getRandomDataVO(Long currentTimeMillis, long value, String bodyName, boolean b, String floor_id) {
		BodyVO bodyVO = new BodyVO();
		bodyVO.setName(bodyName);
		ArrayList<Long> datapoint = new ArrayList<>();
		datapoint.add(currentTimeMillis);
		datapoint.add(value);
		datapoint.add(3l);
		ArrayList<ArrayList<Long>> datapoints = new ArrayList<>();
		datapoints.add(datapoint);
		bodyVO.setDatapoints(datapoints);
		AttributesVO attributesVO = new AttributesVO();
		attributesVO.setFloor_id(floor_id);
		bodyVO.setAttributesVO(attributesVO);
		return bodyVO;
	}

	@SuppressWarnings("unchecked")
	private DatapointsIngestion createDataIngestionRequest(ObjectVO objectVO) {
		DatapointsIngestion dpIngestion = new DatapointsIngestion();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		dpIngestion.setMessageId(String.valueOf(calendar.getTimeInMillis()));
		List<Body> bodies = new ArrayList<Body>();
		List<BodyVO> bodiesVO = objectVO.getBody();

		for (BodyVO bodyVO : bodiesVO) {
			AttributesVO attributesVO = bodyVO.getAttributesVO();
			ArrayList<ArrayList<Long>> datapoint = bodyVO.getDatapoints();
			String bodyName = bodyVO.getName();
			Body body = new Body();
			body.setName(bodyName);
			List<Object> datapoints = new ArrayList<Object>();
			List<Object> assetDatapoint = new ArrayList<Object>();
			assetDatapoint.add(String.valueOf(calendar.getTimeInMillis()));
			assetDatapoint.add(datapoint.get(0).get(1));
			assetDatapoint.add(datapoint.get(0).get(2));
			datapoints.add(assetDatapoint);
			body.setDatapoints(datapoints);
			com.ge.predix.entity.util.map.Map map = new com.ge.predix.entity.util.map.Map();
			map.put("floor_id", attributesVO.getFloor_id());
			body.setAttributes(map);
			bodies.add(body);
		}

		dpIngestion.setBody(bodies);
		return dpIngestion;
	}

	@Override
	@RequestMapping(value = "/ingestdata", method = RequestMethod.GET)
	public @ResponseBody String handleRequest() {
		try {
			return this.dataIngestionHandler.listAssets();
		} catch (Throwable e) {
			log.error("Failure in /ingestdata GET ", e);
			return "Request failed. " + e.getMessage();
		}

	}

	/**
	 * @return -
	 */
	@RequestMapping(value = "/retrieveasset", method = RequestMethod.GET)
	public @ResponseBody String retrieveAsset() {
		try {
			return this.dataIngestionHandler.retrieveAsset();
		} catch (Throwable e) {
			log.error("Failure in /retrieveasset GET ", e);
			return "Request failed. " + e.getMessage();
		}

	}

	/**
	 * @param asset
	 *            -
	 * @return -
	 */
	protected String getTimeSeriesTag(Asset asset) {
		@SuppressWarnings("unchecked")
		LinkedHashMap<String, AssetTag> tags = asset.getAssetTag();
		if (tags != null) {
			for (Entry<String, AssetTag> entry : tags.entrySet()) {
				AssetTag assetTag = entry.getValue();
				TagDatasource dataSource = assetTag.getTagDatasource();
				if (dataSource != null && !dataSource.getNodeName().isEmpty()) {
					return assetTag.getSourceTagId();
				}
			}
		}
		return null;
	}

}
