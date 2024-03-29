
package edu.sc.seis.seisFile.fdsnws;

import java.util.Date;

import edu.sc.seis.seisFile.ChannelTimeWindow;

/** Autogenerated by groovy FDSNQueryParamGenerator.groovy in src/metacode/groovy
 */
public class FDSNStationQueryParams extends AbstractQueryParams implements Cloneable {

    public FDSNStationQueryParams() {
        this(IRIS_HOST);
    }
    
    public FDSNStationQueryParams(String host) {
        super(host==null?IRIS_HOST:host);
    }

    public FDSNStationQueryParams clone() {
        FDSNStationQueryParams out = new FDSNStationQueryParams(getHost());
        for (String key : params.keySet()) {
            out.setParam(key, params.get(key));
        }
        return out;
    }

    public FDSNStationQueryParams setHost(String host) {
        this.host = host;
        return this;
    }


    public static final String STARTTIME = "starttime";

    /** Limit to metadata epochs startingon or after the specified start time.
     */
    public FDSNStationQueryParams setStartTime(Date value) {
        setParam(STARTTIME, value);
        return this;
    }

    public FDSNStationQueryParams clearStartTime() {
        clearParam(STARTTIME);
        return this;
    }


    public static final String ENDTIME = "endtime";

    /** Limit to metadata epochs ending on or before the specified end time.
     */
    public FDSNStationQueryParams setEndTime(Date value) {
        setParam(ENDTIME, value);
        return this;
    }

    public FDSNStationQueryParams clearEndTime() {
        clearParam(ENDTIME);
        return this;
    }


    public static final String STARTBEFORE = "startbefore";

    /** Limit to metadata epochs starting before specified time.
     */
    public FDSNStationQueryParams setStartBefore(Date value) {
        setParam(STARTBEFORE, value);
        return this;
    }

    public FDSNStationQueryParams clearStartBefore() {
        clearParam(STARTBEFORE);
        return this;
    }


    public static final String STARTAFTER = "startafter";

    /** Limit to metadata epochs starting after specified time.
     */
    public FDSNStationQueryParams setStartAfter(Date value) {
        setParam(STARTAFTER, value);
        return this;
    }

    public FDSNStationQueryParams clearStartAfter() {
        clearParam(STARTAFTER);
        return this;
    }


    public static final String ENDBEFORE = "endbefore";

    /** Limit to metadata epochs ending before specified time.
     */
    public FDSNStationQueryParams setEndBefore(Date value) {
        setParam(ENDBEFORE, value);
        return this;
    }

    public FDSNStationQueryParams clearEndBefore() {
        clearParam(ENDBEFORE);
        return this;
    }


    public static final String ENDAFTER = "endafter";

    /** Limit to metadata epochs ending after specified time.
     */
    public FDSNStationQueryParams setEndAfter(Date value) {
        setParam(ENDAFTER, value);
        return this;
    }

    public FDSNStationQueryParams clearEndAfter() {
        clearParam(ENDAFTER);
        return this;
    }


    public static final String NETWORK = "network";

    /** Select one or more network codes. Can be SEED network codes or data center defined codes. Multiple codes are comma-separated.
     */
    public FDSNStationQueryParams appendToNetwork(String value) {
        appendToParam(NETWORK, value);
        return this;
    }

    public FDSNStationQueryParams clearNetwork() {
        clearParam(NETWORK);
        return this;
    }


    public static final String STATION = "station";

    /** Select one or more SEED station codes. Multiple codes are comma-separated.
     */
    public FDSNStationQueryParams appendToStation(String value) {
        appendToParam(STATION, value);
        return this;
    }

    public FDSNStationQueryParams clearStation() {
        clearParam(STATION);
        return this;
    }


    public static final String LOCATION = "location";

    /** Select one or more SEED location identifiers. Multiple identifiers are comma-separated. As a special case "--" (two dashes) will be translated to a string of two space characters to match blank location IDs.
     */
    public FDSNStationQueryParams appendToLocation(String value) {
        if ("  ".equals(value)) { value = "--";}
        appendToParam(LOCATION, value);
        return this;
    }

    public FDSNStationQueryParams clearLocation() {
        clearParam(LOCATION);
        return this;
    }


    public static final String CHANNEL = "channel";

    /** Select one or more SEED channel codes. Multiple codes are comma-separated.
     */
    public FDSNStationQueryParams appendToChannel(String value) {
        appendToParam(CHANNEL, value);
        return this;
    }

    public FDSNStationQueryParams clearChannel() {
        clearParam(CHANNEL);
        return this;
    }


    public static final String MINLATITUDE = "minlatitude";

    /** Limit to stations with a latitude larger than the specified minimum.
     */
    public FDSNStationQueryParams setMinLatitude(float value) {
        setParam(MINLATITUDE, value);
        return this;
    }

    public FDSNStationQueryParams clearMinLatitude() {
        clearParam(MINLATITUDE);
        return this;
    }


    public static final String MAXLATITUDE = "maxlatitude";

    /** Limit to stations with a latitude smaller than the specified maximum.
     */
    public FDSNStationQueryParams setMaxLatitude(float value) {
        setParam(MAXLATITUDE, value);
        return this;
    }

    public FDSNStationQueryParams clearMaxLatitude() {
        clearParam(MAXLATITUDE);
        return this;
    }


    public static final String MINLONGITUDE = "minlongitude";

    /** Limit to stations with a longitude larger than the specified minimum.
     */
    public FDSNStationQueryParams setMinLongitude(float value) {
        setParam(MINLONGITUDE, value);
        return this;
    }

    public FDSNStationQueryParams clearMinLongitude() {
        clearParam(MINLONGITUDE);
        return this;
    }


    public static final String MAXLONGITUDE = "maxlongitude";

    /** Limit to stations with a longitude smaller than the specified maximum.
     */
    public FDSNStationQueryParams setMaxLongitude(float value) {
        setParam(MAXLONGITUDE, value);
        return this;
    }

    public FDSNStationQueryParams clearMaxLongitude() {
        clearParam(MAXLONGITUDE);
        return this;
    }


    public static final String LATITUDE = "latitude";

    /** Specify the latitude to be used for a radius search.
     */
    public FDSNStationQueryParams setLatitude(float value) {
        setParam(LATITUDE, value);
        return this;
    }

    public FDSNStationQueryParams clearLatitude() {
        clearParam(LATITUDE);
        return this;
    }


    public static final String LONGITUDE = "longitude";

    /** Specify the longitude to the used for a radius search.
     */
    public FDSNStationQueryParams setLongitude(float value) {
        setParam(LONGITUDE, value);
        return this;
    }

    public FDSNStationQueryParams clearLongitude() {
        clearParam(LONGITUDE);
        return this;
    }


    public static final String MINRADIUS = "minradius";

    /** Limit results to stations within the specified minimum number of degrees from the geographic point defined by the latitude and longitude parameters.
     */
    public FDSNStationQueryParams setMinRadius(float value) {
        setParam(MINRADIUS, value);
        return this;
    }

    public FDSNStationQueryParams clearMinRadius() {
        clearParam(MINRADIUS);
        return this;
    }


    public static final String MAXRADIUS = "maxradius";

    /** Limit results to stations within the specified maximum number of degrees from the geographic point defined by the latitude and longitude parameters.
     */
    public FDSNStationQueryParams setMaxRadius(float value) {
        setParam(MAXRADIUS, value);
        return this;
    }

    public FDSNStationQueryParams clearMaxRadius() {
        clearParam(MAXRADIUS);
        return this;
    }


    public static final String LEVEL = "level";

    /** Specify the level of detail for the results.
     */
    public FDSNStationQueryParams setLevel(String value) {
        setParam(LEVEL, value);
        return this;
    }

    public FDSNStationQueryParams clearLevel() {
        clearParam(LEVEL);
        return this;
    }


    public static final String INCLUDERESTRICTED = "includerestricted";

    /** Specify if results should include information for restricted stations.
     */
    public FDSNStationQueryParams setIncludeRestricted(boolean value) {
        setParam(INCLUDERESTRICTED, value);
        return this;
    }

    public FDSNStationQueryParams clearIncludeRestricted() {
        clearParam(INCLUDERESTRICTED);
        return this;
    }


    public static final String INCLUDEAVAILABILITY = "includeavailability";

    /** Specify if results should include information about time series data availability.
     */
    public FDSNStationQueryParams setIncludeAvailability(boolean value) {
        setParam(INCLUDEAVAILABILITY, value);
        return this;
    }

    public FDSNStationQueryParams clearIncludeAvailability() {
        clearParam(INCLUDEAVAILABILITY);
        return this;
    }


    public static final String UPDATEDAFTER = "updatedafter";

    /** Limit to metadata updated after specified date; updates are data center specific.
     */
    public FDSNStationQueryParams setUpdatedAfter(Date value) {
        setParam(UPDATEDAFTER, value);
        return this;
    }

    public FDSNStationQueryParams clearUpdatedAfter() {
        clearParam(UPDATEDAFTER);
        return this;
    }


    


    public FDSNStationQueryParams area(float minLat, float maxLat, float minLon, float maxLon) {
        return setMinLatitude(minLat).setMaxLatitude(maxLat).setMinLongitude(minLon).setMaxLongitude(maxLon);
    }

    public FDSNStationQueryParams ring(float lat, float lon, float maxRadius) {
        return setLatitude(lat).setLongitude(lon).setMaxRadius(maxRadius);
    }

    public FDSNStationQueryParams donut(float lat, float lon, float minRadius, float maxRadius) {
        return ring(lat, lon, maxRadius).setMinRadius(minRadius);
    }

    public static final String LEVEL_NETWORK = "network";

    public static final String LEVEL_STATION = "station";

    public static final String LEVEL_CHANNEL = "channel";

    public static final String LEVEL_RESPONSE = "response";

    @Override
    public String getServiceName() {
        return STATION_SERVICE;
    }

    public static final String STATION_SERVICE = "station";


}

