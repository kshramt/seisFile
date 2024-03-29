package edu.sc.seis.seisFile.fdsnws.stationxml;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.martiansoftware.jsap.JSAPException;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.StationClient;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;
import edu.sc.seis.seisFile.fdsnws.quakeml.Quakeml;
import edu.sc.seis.seisFile.fdsnws.stationxml.Network;
import edu.sc.seis.seisFile.fdsnws.stationxml.NetworkIterator;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLException;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLTagNames;

public class FDSNStationXML {

    public FDSNStationXML(final XMLEventReader reader) throws XMLStreamException, StationXMLException {
        this.reader = reader;
        StaxUtil.skipToStartElement(reader);
        StartElement startE = StaxUtil.expectStartElement(StationXMLTagNames.FDSNSTATIONXML, reader);
        Attribute schemaLocAttr = startE.getAttributeByName(new QName("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation"));
        xmlSchemaLocation = schemaLocAttr.getValue();
        schemaVersion = StaxUtil.pullAttribute(startE, StationXMLTagNames.SCHEMAVERSION);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(StationXMLTagNames.SOURCE)) {
                    source = StaxUtil.pullText(reader, StationXMLTagNames.SOURCE);
                } else if (elName.equals(StationXMLTagNames.SENDER)) {
                    sender = StaxUtil.pullText(reader, StationXMLTagNames.SENDER);
                } else if (elName.equals(StationXMLTagNames.MODULE)) {
                    module = StaxUtil.pullText(reader, StationXMLTagNames.MODULE);
                } else if (elName.equals(StationXMLTagNames.MODULEURI)) {
                    moduleUri = StaxUtil.pullText(reader, StationXMLTagNames.MODULEURI);
                } else if (elName.equals(StationXMLTagNames.CREATED)) {
                    created = StaxUtil.pullText(reader, StationXMLTagNames.CREATED);
                } else if (elName.equals(StationXMLTagNames.NETWORK)) {
                    networks = new NetworkIterator(reader);
                    break;
                } else {
                    StaxUtil.skipToMatchingEnd(reader);
                }
            } else if (e.isEndElement()) {
                return;
            } else {
                e = reader.nextEvent();
            }
        }
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public NetworkIterator getNetworks() {
        if (networks == null) {
            networks = new NetworkIterator(null) {

                @Override
                public boolean hasNext() throws XMLStreamException {
                    return false;
                }

                @Override
                public Network next() throws XMLStreamException, StationXMLException {
                    throw new StationXMLException("No mo networks");
                }
                
            };
        }
        return networks;
    }
    
    public void closeReader() {
        try {
            reader.close();
        } catch(XMLStreamException e) {
            logger.warn("problem closing underlying XMLEventReader.", e);
        }
    }
    
    
    public String getXmlSchemaLocation() {
        return xmlSchemaLocation;
    }

    
    public void setXmlSchemaLocation(String xmlns) {
        this.xmlSchemaLocation = xmlns;
    }
    
    
    public XMLEventReader getReader() {
        return reader;
    }

    
    public String getModuleUri() {
        return moduleUri;
    }

    
    public String getSchemaVersion() {
        return schemaVersion;
    }

    
    public static org.slf4j.Logger getLogger() {
        return logger;
    }

    public boolean checkSchemaVersion() {
        if ( ! xmlSchemaLocation.split(" ")[0].equals(StationXMLTagNames.CURRENT_SCHEMALOCATION_VERSION)) {
            return false;
        }
        if ( ! StationXMLTagNames.CURRENT_SCHEMA_VERSION.equals(getSchemaVersion())) {
            return false;
        }
        return true;
    }
    
    XMLEventReader reader;

    String source;

    String sender;

    String module;

    String moduleUri;

    String created;

    String xmlSchemaLocation;
    
    String schemaVersion;
    
    NetworkIterator networks;

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(FDSNStationXML.class);

    public static FDSNStationXML createEmpty() {
        try {
            URL url = FDSNStationXML.class.getClassLoader().getResource("edu/sc/seis/seisFile/stationxml/empty.stationxml");
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLEventReader r;
            r = factory.createXMLEventReader(url.toString(), url.openStream());
            return new FDSNStationXML(r);
        } catch(Exception e) {
            throw new RuntimeException("Should not happen", e);
        }
    }

    public static URL loadSchema() {
        return FDSNStationXML.class.getClassLoader().getResource("edu/sc/seis/seisFile/stationxml/fdsn-station-1.0.xsd");
    }
    

    
    public static FDSNStationXML loadStationXML(String filename) throws XMLStreamException, IOException, SeisFileException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLEventReader r = factory.createXMLEventReader(filename, new FileInputStream(filename));
        XMLEvent e = r.peek();
        while (!e.isStartElement()) {
            e = r.nextEvent(); // eat this one
            e = r.peek(); // peek at the next
        }
        System.out.println("StaMessage");
        FDSNStationXML fdsnStationXML = new FDSNStationXML(r);
        return fdsnStationXML;
    }
    
    public static void main(String[] args) throws XMLStreamException, IOException, SeisFileException, JSAPException {
        final FDSNStationXML stationXml = loadStationXML(args[0]);
        StationClient sc = new StationClient(new String[0]) {
            public void run()  {
                try {
                    handleResults(stationXml);
                } catch(XMLStreamException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch(SeisFileException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        sc.run();
    }
}
