package edu.sc.seis.seisFile.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class StaMessage {

    public StaMessage(final XMLEventReader reader) throws XMLStreamException, StationXMLException {
        StaxUtil.skipToStartElement(reader);
        StartElement startE = StaxUtil.expectStartElement(StationXMLTagNames.STAMESSAGE, reader);
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
                } else if (elName.equals(StationXMLTagNames.SENTDATE)) {
                    sentDate = StaxUtil.pullText(reader, StationXMLTagNames.SENTDATE);
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

    public String getSentDate() {
        return sentDate;
    }

    public void setSentDate(String sentDate) {
        this.sentDate = sentDate;
    }

    public NetworkIterator getNetworks() {
        return networks;
    }

    String source;

    String sender;

    String module;

    String sentDate;

    NetworkIterator networks;
}
