package com.ge.sos.rmd.datasource.utils;


public class Constants {
	
    /**
     * List of Timeseries Tags
     */
    
	public static final String WELDING_WIRE_SPEED = ".WIRE_SPEED";
	public static final String WELDING_ERROR_CODE=".ERROR_CODE";
	public static final String WELDING_OUTPUT_VOL="OUTPUT_VOL";
    public static final String WELDING_OUTPUT_CUR="OUTPUT_CUR";
    
	public static final String WELDING_ATTRIBUTE_STATUS_ARC="STATUSARC";
	public static final String WELDING_ATTRIBUTE_WORKING_MODE="WORKINGMODE";
	
	public static final String WELDING_ATTRIBUTE_WORKING_MODE_VALUE1="6108";
	public static final String WELDING_ATTRIBUTE_WORKING_MODE_VALUE2="8108";
	
	public static final String WELDING_AGGRIGATION_INTERVAL ="60s";
	public static final String WELDING_AGGRIGATION_SUM_FUNCTION ="sum";
	public static final String WELDING_AGGRIGATION_COUNT_FUNCTION ="count";
	public static final String WELDING_AGGRIGATION_COUNT_INTERVAL ="30s";
	public static final String WELDING_QUERY_FOR_ATTRIBUTE ="attribute";
	
	public static final String WELDING_MESURMENT_CRITERIA ="gt";
	public static final String WELDING_FEEDER_RESULT_TYPE_DEPOSIT_VOLUME="Deposit Volume";
	public static final String WELDING_FEEDER_RESULT_TYPE_WIRE_USAGE="Wire Usage";
	
	public static final String HOUR="3600s";
	public static final String DAY="86400s";
	public static final String WEEK="604800s";
	public static final String MONTH="2678400s";
	public static final String QUARTER="8035200s";
	public static final String YEAR="32140800s";
   
}