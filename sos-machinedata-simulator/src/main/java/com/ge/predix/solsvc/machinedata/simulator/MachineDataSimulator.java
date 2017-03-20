package com.ge.predix.solsvc.machinedata.simulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.Body;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.DatapointsIngestion;
import com.ge.predix.solsvc.machinedata.simulator.vo.AttributesVO;
import com.ge.predix.solsvc.machinedata.simulator.vo.BodyVO;
import com.ge.predix.solsvc.machinedata.simulator.vo.ObjectVO;
import com.ge.predix.solsvc.restclient.impl.RestClient;

/**
 * 
 * @author predix -
 */
@Component
public class MachineDataSimulator {
   
    static final Logger log = LoggerFactory.getLogger(MachineDataSimulator.class);
    private ObjectMapper mapper = new ObjectMapper();

    public MachineDataSimulator() {
        mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        // mapper.configure(SerializationFeature.INDENT_OUTPUT,true);
        mapper.setSerializationInclusion(Include.NON_NULL);

    }

    @Autowired
    ApplicationProperties applicationProperties;

    @Autowired
    private RestClient restClient;
    int value=0;
    @Scheduled(fixedDelayString = "${dataingestion.sleepTimeMillis}")
    public void run() {

        try {
        	Long currentTimeMillis = System.currentTimeMillis();
        	value=(int) Math.round((Math.random()*1));
        	generateAndPushData(currentTimeMillis,value,"Zone 1","5");
        	value=(int) Math.round((Math.random()*1));	
        	generateAndPushData(currentTimeMillis,value,"Zone 2","5");
        	value=(int) Math.round((Math.random()*1));
        	generateAndPushData(currentTimeMillis,value,"Zone 3","5");
        	value=(int) Math.round((Math.random()*1));
        	generateAndPushData(currentTimeMillis,value,"Zone 4","5");
        	value=(int) Math.round((Math.random()*1));	
        	generateAndPushData(currentTimeMillis,value,"Zone 5","5");
        } catch (Throwable e) {
            log.error("unable to run Machine DataSimulator Thread", e); //$NON-NLS-1$
        }
    }
	
    private String generateAndPushData(Long currentTimeMillis,long value,String tagName,String floor_id) throws JsonGenerationException, JsonMappingException, IOException {
    	ObjectVO objectVO=new ObjectVO();
    	List<BodyVO> list =new ArrayList<>();	
    	BodyVO bodyVO=getRandomDataVO(currentTimeMillis,value,tagName,true,floor_id); 	
    	list.add(bodyVO); 	
    	objectVO.setBody(list);
    	objectVO.setMessageId(currentTimeMillis);			
    	DatapointsIngestion dataPtIngest = createDataIngestionRequest(objectVO);
		StringWriter writer = new StringWriter();
		mapper.writeValue(writer, dataPtIngest);
        System.out.println("Final JSON sending to saveTimeSeries >> "+writer.toString());
		return postData(writer.toString());
		
	}

    private BodyVO getRandomDataVO(Long currentTimeMillis,long value,
			String bodyName, boolean b,String floor_id) {
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
	private DatapointsIngestion createDataIngestionRequest(
			ObjectVO objectVO) {
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
			map.put("floor_id",attributesVO.getFloor_id());
			body.setAttributes(map);
			bodies.add(body);
		}
		
		dpIngestion.setBody(bodies);
		return dpIngestion;
	}
    
    private String postData(String content) {
        HttpClient client = null;
        try {
            HttpClientBuilder builder = HttpClientBuilder.create();
            if (this.applicationProperties.getDiServiceProxyHost() != null && !"".equals(this.applicationProperties.getDiServiceProxyHost()) //$NON-NLS-1$
                    && this.applicationProperties.getDiServiceProxyPort() != null && !"".equals(this.applicationProperties.getDiServiceProxyPort())) //$NON-NLS-1$
            {
                HttpHost proxy = new HttpHost(this.applicationProperties.getDiServiceProxyHost(), Integer.parseInt(this.applicationProperties.getDiServiceProxyPort()));
                builder.setProxy(proxy);
            }
            client = builder.build();
            String serviceURL = null;
            if (this.applicationProperties.getPredixDataIngestionURL() == null) {
                serviceURL = "http://" + this.applicationProperties.getDiServiceHost() + ":" + this.applicationProperties.getDiServicePort() + "/saveTimeSeriesData"; //$NON-NLS-1$
                URLEncoder.encode(serviceURL, "UTF-8");
            } else {
                serviceURL = this.applicationProperties.getPredixDataIngestionURL() + "/saveTimeSeriesData"; //$NON-NLS-1$
                URLEncoder.encode(serviceURL, "UTF-8");

            }
            log.info("Service URL : " + serviceURL); //$NON-NLS-1$
            log.info("Data : " + content);
            log.info("Tenant ID "+this.applicationProperties.getTenantId());
            HttpPost request = new HttpPost(serviceURL);
            HttpEntity reqEntity = MultipartEntityBuilder.create().addTextBody("content", content) //$NON-NLS-1$
                    .addTextBody("destinationId", "TimeSeries").addTextBody("clientId", "TimeSeries") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    .addTextBody("tenantId", this.applicationProperties.getTenantId()).build(); //$NON-NLS-1$
            request.setEntity(reqEntity);
            HttpResponse response = client.execute(request);
            log.debug("Send Data to Ingestion Service : Response Code : " + response.getStatusLine().getStatusCode()); //$NON-NLS-1$
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer result = new StringBuffer();
            String line = ""; //$NON-NLS-1$
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            log.debug("Response : " + result.toString());
            if (result.toString().startsWith("You successfully posted")) { //$NON-NLS-1$
                return "SUCCESS : " + result.toString(); //$NON-NLS-1$
            }
            return "FAILED : " + result.toString(); //$NON-NLS-1$

        } catch (Throwable e) {
            log.error("unable to post data ", e); //$NON-NLS-1$
            return "FAILED : " + e.getLocalizedMessage(); //$NON-NLS-1$
        }
    }

}




