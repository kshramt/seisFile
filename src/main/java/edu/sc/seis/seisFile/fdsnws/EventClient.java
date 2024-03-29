package edu.sc.seis.seisFile.fdsnws;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.stream.XMLStreamException;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;

import edu.sc.seis.seisFile.BuildVersion;
import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.client.BoxAreaParser;
import edu.sc.seis.seisFile.client.DonutParser;
import edu.sc.seis.seisFile.client.ISOTimeParser;
import edu.sc.seis.seisFile.client.RangeParser;
import edu.sc.seis.seisFile.fdsnws.quakeml.Event;
import edu.sc.seis.seisFile.fdsnws.quakeml.EventIterator;
import edu.sc.seis.seisFile.fdsnws.quakeml.Magnitude;
import edu.sc.seis.seisFile.fdsnws.quakeml.Origin;
import edu.sc.seis.seisFile.fdsnws.quakeml.QuakeMLTagNames;
import edu.sc.seis.seisFile.fdsnws.quakeml.Quakeml;

public class EventClient extends AbstractFDSNClient {

    private static final String DEPTH = "depth";

    private static final String CONTRIBUTORS = "contributors";

    private static final String CATALOGS = "catalogs";

    private static final String MAGNITUDE = "magnitude";

    private static final String TYPES = "types";

    private static final String INCLUDEALLORIGINS = "includeallorigins";

    private static final String INCLUDEALLMAGNITUDES = "includeallmagnitudes";

    private static final String INCLUDEARRIVALS = "includearrivals";

    @Override
    protected void addParams() throws JSAPException {
        super.addParams();
        add(BoxAreaParser.createParam("Event constraining box as west/east/south/north"));
        add(DonutParser.createParam("Event constraining donut as lat/lon/minRadius/maxRadius"));
        add(ISOTimeParser.createYesterdayParam(BEGIN, "The earliest time for an accepted event", false));
        add(ISOTimeParser.createParam(END, "now", "The latest time for an accepted event", true));
        add(RangeParser.createParam(MAGNITUDE, "0", "10", "The range of acceptable magnitudes"));
        add(createListOption(TYPES, 't', "types", "The types of magnitudes to retrieve."));
        add(RangeParser.createParam(DEPTH, "0", "10000", "The range of acceptable depths in kilometers", 'D'));
        add(createListOption(CATALOGS, 'c', CATALOGS, "A comma separated list of catalogs to search"));
        add(createListOption(CONTRIBUTORS, 'C', CONTRIBUTORS, "A comma separated list of contributors to search"));

        add(new Switch(INCLUDEALLORIGINS, JSAP.NO_SHORTFLAG, INCLUDEALLORIGINS, "Retrieve all origins instead of only the primary origin associated with each event."));
        add(new Switch(INCLUDEALLMAGNITUDES, JSAP.NO_SHORTFLAG, INCLUDEALLMAGNITUDES, "Retrieve all magnitudes for the event instead of only the primary magnitude."));
        add(new Switch(INCLUDEARRIVALS, JSAP.NO_SHORTFLAG, INCLUDEARRIVALS, "Specify if phase arrivals should be included."));
        
        add(new Switch(VALIDATE, JSAP.NO_SHORTFLAG, VALIDATE, "Validate XML against schema"));
    }

    public EventClient(String[] args) throws JSAPException {
        super(args);
    }

    public void run() {
        JSAPResult result = getResult();
        if (shouldPrintHelp()) {
            System.out.println(jsap.getHelp());
            return;
        }
        if (shouldPrintVersion()) {
            System.out.println(BuildVersion.getVersion());
            return;
        }
        if (!isSuccess()) {
            for (Iterator errs = result.getErrorMessageIterator(); errs.hasNext();) {
                System.err.println("Error: " + errs.next());
            }
            System.err.println();
            System.err.println("Usage: java " + this.getClass().getName());
            System.err.println("                " + jsap.getUsage());
            System.err.println();
            System.err.println(jsap.getHelp());
            return;
        }
        FDSNEventQueryParams queryParams;
        try {
            queryParams = configureQuery(result);
            if (getResult().getBoolean(PRINTURL)) {
                System.out.println(queryParams.formURI());
                return;
            } else {
                FDSNEventQuerier querier = new FDSNEventQuerier(queryParams);
                if (getResult().getBoolean(VALIDATE)) {
                    querier.validateQuakeML();
                    System.out.println("Valid");
                } else if (getResult().getBoolean(RAW)) {
                    querier.outputRaw(System.out);
                } else {
                    Quakeml quakeml = querier.getQuakeML();
                    if (!quakeml.checkSchemaVersion()) {
                        System.out.println("");
                        System.out.println("WARNING: XmlSchema of this document does not match this code, results may be incorrect.");
                        System.out.println("XmlSchema (code): " + QuakeMLTagNames.CODE_MAIN_SCHEMA_VERSION);
                        System.out.println("XmlSchema (doc): " + quakeml.getSchemaVersion());
                    }
                    handleResults(quakeml);
                }
            }
        } catch(Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    FDSNEventQueryParams configureQuery(JSAPResult result) throws SeisFileException {
        FDSNEventQueryParams queryParams = new FDSNEventQueryParams();
        if (result.contains(BoxAreaParser.NAME)) {
            HashMap<String, String> box = (HashMap<String, String>)result.getObject(BoxAreaParser.NAME);
            queryParams.area(Float.parseFloat(box.get("west")),
                             Float.parseFloat(box.get("east")),
                             Float.parseFloat(box.get("south")),
                             Float.parseFloat(box.get("north")));
        }
        if (result.contains(DonutParser.NAME)) {
            HashMap<String, String> donut = (HashMap<String, String>)result.getObject(DonutParser.NAME);
            queryParams.donut(Float.parseFloat(donut.get("lat")),
                              Float.parseFloat(donut.get("lon")),
                              Float.parseFloat(donut.get("min")),
                              Float.parseFloat(donut.get("max")));
        }
        if (result.contains(BEGIN)) {
            queryParams.setStartTime((Date)result.getObject(BEGIN));
        }
        if (result.contains(END)) {
            queryParams.setEndTime((Date)result.getObject(END));
        }
        if (result.contains(DEPTH)) {
            HashMap<String, String> depthRange = (HashMap<String, String>)result.getObject(DEPTH);
            queryParams.setMinDepth(Float.parseFloat(depthRange.get("min")))
                    .setMaxDepth(Float.parseFloat(depthRange.get("max")));
        }
        if (result.contains(MAGNITUDE)) {
            HashMap<String, String> magRange = (HashMap<String, String>)result.getObject(MAGNITUDE);
            queryParams.setMinMagnitude(Float.parseFloat(magRange.get("min")))
                    .setMaxMagnitude(Float.parseFloat(magRange.get("max")));
            if (result.contains(TYPES)) {
                queryParams.setMagnitudeType(result.getString(TYPES));
            }
        }
        if (result.contains(CATALOGS)) {
            queryParams.setCatalog(result.getString(CATALOGS));
        }
        if (result.contains(CONTRIBUTORS)) {
            queryParams.setContributor(result.getString(CONTRIBUTORS));
        }
        if (result.getBoolean(INCLUDEARRIVALS)) {
            queryParams.setIncludeArrivals(true);
        }
        if (result.getBoolean(INCLUDEALLMAGNITUDES)) {
            queryParams.setIncludeAllMagnitudes(true);
        }
        if (result.getBoolean(INCLUDEALLORIGINS)) {
            queryParams.setIncludeAllOrigins(true);
        }
        if (result.contains(HOST)) {
            queryParams.setHost(result.getString(HOST));
        }
        if (result.contains(BASEURL)) {
            try {
                queryParams.internalSetBaseURI(new URI(result.getString(BASEURL)));
            } catch(URISyntaxException e) {
                throw new SeisFileException("Unable to parse URI: "+result.getString(BASEURL), e);
            }
        }
        return queryParams;
    }


    public void handleResults(Quakeml quakeml) throws XMLStreamException, SeisFileException {
        EventIterator eIt = quakeml.getEventParameters().getEvents();
        while (eIt.hasNext()) {
            Event e = eIt.next();
            Origin o = e.getOriginList().get(0);
            Magnitude m = e.getMagnitudeList().get(0);
            System.out.println(o.getLatitude() + "/" + o.getLongitude() + " " + m.getMag().getValue() + " "
                    + m.getType() + " " + o.getTime().getValue());
        }
    }

    /**
     * @param args
     * @throws JSAPException
     */
    public static void main(String[] args) throws JSAPException {
        new EventClient(args).run();
    }
}
