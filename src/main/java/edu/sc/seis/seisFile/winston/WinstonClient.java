package edu.sc.seis.seisFile.winston;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.BasicConfigurator;

import edu.sc.seis.seisFile.BuildVersion;
import edu.sc.seis.seisFile.QueryParams;
import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.earthworm.EarthwormExport;
import edu.sc.seis.seisFile.earthworm.TraceBuf2;
import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.syncFile.SyncFileWriter;

public class WinstonClient {

    protected WinstonClient(String[] args) throws SeisFileException, FileNotFoundException, IOException {
        QueryParams defaults = new QueryParams(new String[] {"-n", "*", "-s", "*", "-l", "*", "-c", "*"});
        params = new QueryParams(args, defaults);
        List<String> leftOverArgs = params.getUnknownArgs();
        winstonConfig.put("winston.driver", WinstonUtil.MYSQL_DRIVER);
        winstonConfig.put("winston.prefix", "W");
        winstonConfig.put("winston.url", "jdbc:mysql://localhost/?user=wwsuser&password=");
        Iterator<String> it = leftOverArgs.iterator();
        while (it.hasNext()) {
            String nextArg = it.next();
            if (nextArg.equals("--sync")) {
                doSync = true;
            } else if (nextArg.equals("--steim1")) {
                doSteim1 = true;
            } else if (nextArg.equals("--heartbeatverbose")) {
                heartbeatverbose = true;
            } else if (nextArg.equals("--tbzip")) {
                doTbZip = true;
            } else if (it.hasNext()) {
                // arg with value
                if (nextArg.equals("-p")) {
                    winstonConfig.load(new BufferedReader(new FileReader(it.next())));
                } else if (nextArg.equals("-u")) {
                    winstonConfig.put("winston.url", it.next());
                } else if (nextArg.equals("--recLen")) {
                    recordSize = Integer.parseInt(it.next());
                } else if (nextArg.equals("--export")) {
                    doExport = true;
                    exportPort = Integer.parseInt(it.next());
                } else if (nextArg.equals("--chunk")) {
                    chunkSeconds = Integer.parseInt(it.next());
                } else if (nextArg.equals("--module")) {
                    module = Integer.parseInt(it.next());
                } else if (nextArg.equals("--inst")) {
                    institution = Integer.parseInt(it.next());
                } else if (nextArg.equals("--heartbeat")) {
                    heartbeat = Integer.parseInt(it.next());
                } else if (nextArg.equals("--sleepmillis")) {
                    sleepMillis = Integer.parseInt(it.next());
                } else {
                    throw new IllegalArgumentException("Unknown argument: "+nextArg);
                }
            } else {
                throw new IllegalArgumentException("Unknown argument: "+nextArg);
            }
        }
        if (!doSync && params.getOutFile() == null) {
            params.setOutFile("output.mseed");
        }
    }

    QueryParams params;

    Properties winstonConfig = new Properties();

    boolean doSync = false;

    boolean doExport = false;

    boolean doTbZip = false;

    boolean heartbeatverbose = false;

    int exportPort = -1;

    int recordSize = 12;

    boolean doSteim1 = false;

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();
        WinstonClient client = new WinstonClient(args);
        client.readData();
    }

    public void readData() throws SeisFileException, SQLException, DataFormatException, FileNotFoundException,
            IOException, URISyntaxException {
        if (params.isPrintHelp()) {
            System.out.println(getHelp());
            return;
        } else if (params.isPrintVersion()) {
            System.out.println("Version: " + BuildVersion.getDetailedVersion());
            return;
        } else if (params.getNetwork() == null || params.getStation() == null || params.getChannel() == null) {
            System.out.println(BuildVersion.getDetailedVersion() + " one of scnl is null: n=" + params.getNetwork()
                    + " s=" + params.getStation() + " l=" + params.getLocation() + " c=" + params.getChannel());
            System.out.println("LocId null is ok for scn, but needed for scnl");
            return;
        }
        if (params.isVerbose()) {
            WinstonUtil.setVerbose(true);
        }
        WinstonUtil winston = new WinstonUtil(getDbURL(),
                                              getUser(),
                                              getPassword(),
                                              winstonConfig.getProperty("winston.prefix"));
        List<WinstonSCNL> allChannels = winston.listChannelDatabases();
        Pattern staPattern = Pattern.compile("*".equals(params.getStation()) ? ".*" : params.getStation());
        Pattern chanPattern = Pattern.compile("*".equals(params.getChannel()) ? ".*" : params.getChannel());
        Pattern netPattern = Pattern.compile("*".equals(params.getNetwork()) ? ".*" : params.getNetwork());
        Pattern locPattern = Pattern.compile("*".equals(params.getLocation()) ? ".*" : params.getLocation());
        if (doSync) {
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(params.getDataOutputStream())));
            SyncFileWriter syncOut = new SyncFileWriter("winston", out);
            for (WinstonSCNL scnl : allChannels) {
                if (staPattern.matcher(scnl.getStation()).matches() && chanPattern.matcher(scnl.getChannel()).matches()
                        && netPattern.matcher(scnl.getNetwork()).matches()
                        && locPattern.matcher(scnl.getLocId() == null ? "" : scnl.getLocId()).matches()) {
                    syncChannel(winston, scnl, syncOut);
                }
            }
            syncOut.close();
        } else if (doTbZip) {
            File f = new File(params.getOutFile());
            String dirName = f.getName();
            if (dirName.endsWith(".zip")) {
                dirName = dirName.substring(0, dirName.length()-4);
            } else {
                dirName = dirName+"_TraceBufs";
            }
            ZipOutputStream zip = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
            ZipEntry tbzip = new ZipEntry(dirName+"/");
            zip.putNextEntry(tbzip);
            zip.closeEntry();
            for (WinstonSCNL scnl : allChannels) {
                if (staPattern.matcher(scnl.getStation()).matches() && chanPattern.matcher(scnl.getChannel()).matches()
                        && netPattern.matcher(scnl.getNetwork()).matches()
                        && locPattern.matcher(scnl.getLocId() == null ? "" : scnl.getLocId()).matches()) {
                    outputRawTraceBuf2s(winston, scnl, zip, dirName);
                }
            }
            zip.close();
        } else if (doExport) {
            EarthwormExport exporter = new EarthwormExport(exportPort, module, institution, "heartbeat", heartbeat);
            if (heartbeatverbose) {
                exporter.getHeartbeater().setVerbose(heartbeatverbose);
            }
            if (params.isVerbose()) {
                exporter.setVerbose(true);
                System.out.println("Waiting for client connect, port: " + exportPort);
            }
            exporter.waitForClient();
            Date startTime = params.getBegin();
            Date chunkBegin, chunkEnd;
            HashMap<WinstonSCNL, Date> lastSent = new HashMap<WinstonSCNL, Date>();
            for (WinstonSCNL scnl : allChannels) {
                if (staPattern.matcher(scnl.getStation()).matches() && chanPattern.matcher(scnl.getChannel()).matches()
                        && netPattern.matcher(scnl.getNetwork()).matches()
                        && locPattern.matcher(scnl.getLocId() == null ? "" : scnl.getLocId()).matches()) {
                    lastSent.put(scnl, startTime);
                }
            }
            while (startTime.before(params.getEnd())) {
                chunkEnd = new Date(startTime.getTime() + chunkSeconds * 1000);
                for (WinstonSCNL scnl : lastSent.keySet()) {
                    chunkBegin = lastSent.get(scnl);
                    if (chunkBegin.before(chunkEnd)) {
                        Date sentEnd = exportChannel(winston, scnl, chunkBegin, chunkEnd, exporter);
                        // sendEnd is expected time of next sample, ie 1 samp period after end time of last tb
                        lastSent.put(scnl, new Date(sentEnd.getTime()+1));
                    }
                }
                startTime = new Date(chunkEnd.getTime() + 1);
            }
            exporter.closeSocket();
            if (params.isVerbose()) {
                System.out.println("Done sending, " + exporter.getNumTraceBufSent() + " ("
                        + exporter.getNumSplitTraceBufSent() + " too big so split)");
            }
        } else {
            for (WinstonSCNL scnl : allChannels) {
                if (staPattern.matcher(scnl.getStation()).matches() && chanPattern.matcher(scnl.getChannel()).matches()
                        && netPattern.matcher(scnl.getNetwork()).matches()
                        && locPattern.matcher(scnl.getLocId() == null ? "" : scnl.getLocId()).matches()) {
                    processChannel(winston, scnl);
                }
            }
            params.getDataOutputStream().close();
        }
        winston.close();
    }

    void syncChannel(WinstonUtil winston, WinstonSCNL channel, SyncFileWriter syncOut) throws SeisFileException,
            SQLException, DataFormatException, FileNotFoundException, IOException, URISyntaxException {
        Calendar cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.setTime(params.getBegin());
        int startYear = cal.get(Calendar.YEAR);
        int startMonth = cal.get(Calendar.MONTH) + 1; // why are Calendar
                                                      // months zero based,
                                                      // but days are one
                                                      // based???
        int startDay = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(params.getEnd());
        int endYear = cal.get(Calendar.YEAR);
        int endMonth = cal.get(Calendar.MONTH) + 1; // why are Calendar
                                                    // months zero based,
                                                    // but days are one
                                                    // based???
        int endDay = cal.get(Calendar.DAY_OF_MONTH);
        winston.writeSyncBetweenDates(channel, startYear, startMonth, startDay, endYear, endMonth, endDay, syncOut);
    }

    Date exportChannel(WinstonUtil winston, WinstonSCNL channel, Date begin, Date end, EarthwormExport exporter)
            throws SeisFileException, SQLException, DataFormatException, FileNotFoundException, IOException,
            URISyntaxException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        List<TraceBuf2> tbList = winston.extractData(channel, begin, end);
        Date lastSentEnd = end;
        double sampRate = 1;
        TraceBuf2 prev = null;
        for (TraceBuf2 traceBuf2 : tbList) {
            if (params.isVerbose()) {
                System.out.println("Tracebuf: " + traceBuf2.getNetwork() + "." + traceBuf2.getStation() + "."
                        + traceBuf2.getLocId() + "." + traceBuf2.getChannel() + " "
                        + sdf.format(traceBuf2.getStartDate()) + " " + traceBuf2.getNumSamples() + " "
                        + sdf.format(traceBuf2.getEndDate()));
            }
            if (prev != null && prev.getEndDate().after(traceBuf2.getStartDate())) {
                System.out.println("WARNING: current tracebuf overlaps previous: ");
                System.out.println("  prev: " + prev);
                System.out.println("  curr: " + traceBuf2);
            }
            boolean notSent = true;
            while (notSent) {
                try {
                    exporter.export(traceBuf2);
                    notSent = false;
                } catch(IOException e) {
                    exporter.closeClient();
                    if (params.isVerbose()) {
                        System.out.println("Caught exception, waiting for reconnect, will resend tracebuf" + e);
                    }
                    logger.warn("Caught exception, waiting for reconnect, will resend tracebuf", e);
                    exporter.waitForClient();
                    if (params.isVerbose()) {
                        System.out.println("Resend Tracebuf: " + traceBuf2.getNetwork() + "." + traceBuf2.getStation()
                                + "." + traceBuf2.getLocId() + "." + traceBuf2.getChannel() + " "
                                + sdf.format(traceBuf2.getStartDate()) + " " + traceBuf2.getNumSamples() + " "
                                + sdf.format(traceBuf2.getEndDate()));
                    }
                }
            }
            if (lastSentEnd.before(traceBuf2.getPredictedNextStartDate())) {
                lastSentEnd = traceBuf2.getPredictedNextStartDate();
                sampRate = traceBuf2.getSampleRate();
            }
            if (params.isVerbose()) {
                System.out.print("sleep: " + sleepMillis + " milliseconds " + sdf.format(new Date()) + " ...");
            }
            try {
                Thread.sleep(sleepMillis);
            } catch(InterruptedException e) {}
            if (params.isVerbose()) {
                System.out.println("...back to work at " + sdf.format(new Date()) + ".");
            }
        }
        return lastSentEnd;
    }

    void processChannel(WinstonUtil winston, WinstonSCNL channel) throws SeisFileException, SQLException,
            DataFormatException, FileNotFoundException, IOException, URISyntaxException {
        List<TraceBuf2> tbList = winston.extractData(channel, params.getBegin(), params.getEnd());
        for (TraceBuf2 traceBuf2 : tbList) {
            List<DataRecord> mseedList = traceBuf2.toMiniSeed(recordSize, doSteim1);
            for (DataRecord dr : mseedList) {
                dr.write(params.getDataOutputStream());
            }
        }
    }

    void outputRawTraceBuf2s(WinstonUtil winston, WinstonSCNL channel, ZipOutputStream zip, String dir) throws SeisFileException, SQLException,
            DataFormatException, FileNotFoundException, IOException, URISyntaxException {
        List<TraceBuf2> tbList = winston.extractData(channel, params.getBegin(), params.getEnd());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss.SSS");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        for (TraceBuf2 tb : tbList) {
            ZipEntry tbzip = new ZipEntry(dir+"/"+tb.getNetwork().trim()+"."
                                          +tb.getStation().trim()+"."
                                          +tb.getLocId().trim()+"."
                                          +tb.getChannel().trim()+"."
                                          +sdf.format(tb.getStartDate())+".tb");
            zip.putNextEntry(tbzip);
            byte[] outBytes = tb.toByteArray();
            zip.write(outBytes, 0, outBytes.length);
            zip.closeEntry();
        }
    }

    String getDbURL() {
        return winstonConfig.getProperty("winston.url");
    }

    String getUser() throws URISyntaxException, SeisFileException {
        return getUrlQueryParam("user");
    }

    String getPassword() throws URISyntaxException, SeisFileException {
        return getUrlQueryParam("password");
    }

    String getUrlQueryParam(String name) throws SeisFileException, URISyntaxException {
        String[] urlParts = getDbURL().split("\\?")[1].split("\\&");
        for (int i = 0; i < urlParts.length; i++) {
            if (urlParts[i].startsWith(name + "=")) {
                return urlParts[i].substring((name + "=").length());
            }
        }
        throw new SeisFileException("Unable to find '" + name + "' query param in database url: " + getDbURL());
    }

    public String getHelp() {
        return "java "
                + WinstonClient.class.getName()
                + " "
                + QueryParams.getStandardHelpOptions()
                + "[-p <winston.config file>][-u databaseURL][--sync][--steim1][--recLen len(8-12)][[--export port][--chunk sec][--module modNum][--inst institutionNum][--heartbeat sec][--heartbeatverbose]]";
    }

    int heartbeat = DEFAULT_HEARTBEAT;

    int module = DEFAULT_MODULE;

    int institution = DEFAULT_INSTITUTION;

    int chunkSeconds = DEFAULT_CHUNK_SECONDS;

    int sleepMillis = 0;

    public static final int DEFAULT_CHUNK_SECONDS = 60;

    public static final int DEFAULT_HEARTBEAT = 5;

    public static final int DEFAULT_MODULE = 255;

    public static final int DEFAULT_INSTITUTION = 255;

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(WinstonClient.class);
}
