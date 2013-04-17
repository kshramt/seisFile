
package edu.sc.seis.seisFile.fdsnws;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

/** Autogenerated by groovy FDSNQueryParamGenerator.groovy in src/metacode/groovy
 */
public class FDSNDataSelectQueryParams extends AbstractQueryParams {

    public FDSNDataSelectQueryParams() {
        this(IRIS_BASE_URI);
    }
    
    public FDSNDataSelectQueryParams(URI baseUri) {
        super(baseUri);
    }


    public static final String STARTTIME = "starttime";

    /** Limit results to time series samples on or after the specified start time
     */
    public FDSNDataSelectQueryParams setStartTime(Date value) {
        setParam(STARTTIME, value);
        return this;
    }

    public FDSNDataSelectQueryParams clearStartTime() {
        clearParam(STARTTIME);
        return this;
    }


    public static final String ENDTIME = "endtime";

    /** Limit results to time series samples on or before the specified end time
     */
    public FDSNDataSelectQueryParams setEndTime(Date value) {
        setParam(ENDTIME, value);
        return this;
    }

    public FDSNDataSelectQueryParams clearEndTime() {
        clearParam(ENDTIME);
        return this;
    }


    public static final String NETWORK = "network";

    /** Select one or more network codes. Can be SEED network codes or data center defined codes. Multiple codes are comma-separated.
     */
    public FDSNDataSelectQueryParams appendToNetwork(String value) {
        appendToParam(NETWORK, value);
        return this;
    }

    public FDSNDataSelectQueryParams clearNetwork() {
        clearParam(NETWORK);
        return this;
    }


    public static final String STATION = "station";

    /** Select one or more SEED station codes. Multiple codes are comma-separated.
     */
    public FDSNDataSelectQueryParams appendToStation(String value) {
        appendToParam(STATION, value);
        return this;
    }

    public FDSNDataSelectQueryParams clearStation() {
        clearParam(STATION);
        return this;
    }


    public static final String LOCATION = "location";

    /** Select one or more SEED location identifiers. Multiple identifiers are comma-separated. As a special case �--� (two dashes) will be translated to a string of two space characters to match blank location IDs.
     */
    public FDSNDataSelectQueryParams appendToLocation(String value) {
        appendToParam(LOCATION, value);
        return this;
    }

    public FDSNDataSelectQueryParams clearLocation() {
        clearParam(LOCATION);
        return this;
    }


    public static final String CHANNEL = "channel";

    /** Select one or more SEED channel codes. Multiple codes are comma-separated.
     */
    public FDSNDataSelectQueryParams appendToChannel(String value) {
        appendToParam(CHANNEL, value);
        return this;
    }

    public FDSNDataSelectQueryParams clearChannel() {
        clearParam(CHANNEL);
        return this;
    }


    public static final String QUALITY = "quality";

    /** Select a specific SEED quality indicator, handling is data center dependent.
     */
    public FDSNDataSelectQueryParams setQuality(String value) {
        setParam(QUALITY, value);
        return this;
    }

    public FDSNDataSelectQueryParams clearQuality() {
        clearParam(QUALITY);
        return this;
    }


    public static final String MINIMUMLENGTH = "minimumlength";

    /** Limit results to continuous data segments of a minimum length specified in seconds.
     */
    public FDSNDataSelectQueryParams setMinimumLength(int value) {
        setParam(MINIMUMLENGTH, value);
        return this;
    }

    public FDSNDataSelectQueryParams clearMinimumLength() {
        clearParam(MINIMUMLENGTH);
        return this;
    }


    public static final String LONGESTONLY = "longestonly";

    /** Limit results to the longest continuous segment per channel.
     */
    public FDSNDataSelectQueryParams setLongestOnly(boolean value) {
        setParam(LONGESTONLY, value);
        return this;
    }

    public FDSNDataSelectQueryParams clearLongestOnly() {
        clearParam(LONGESTONLY);
        return this;
    }


    

    public static final String IRIS_BASE_URL = "http://service.iris.edu/fdsnws/dataselect/1/query?";
    
    public static final URI IRIS_BASE_URI;
    
    static {
        try {
            IRIS_BASE_URI = new URI(IRIS_BASE_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Should no happen, bad default uri string"+IRIS_BASE_URL);
        }
    }
}

