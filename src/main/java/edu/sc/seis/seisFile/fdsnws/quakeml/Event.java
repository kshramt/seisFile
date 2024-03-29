package edu.sc.seis.seisFile.fdsnws.quakeml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;
import edu.sc.seis.seisFile.fdsnws.quakeml.Comment;
import edu.sc.seis.seisFile.fdsnws.quakeml.EventDescription;
import edu.sc.seis.seisFile.fdsnws.quakeml.Magnitude;
import edu.sc.seis.seisFile.fdsnws.quakeml.Origin;
import edu.sc.seis.seisFile.fdsnws.quakeml.Pick;
import edu.sc.seis.seisFile.fdsnws.quakeml.QuakeMLTagNames;

public class Event {

    public static final String ELEMENT_NAME = QuakeMLTagNames.event;

    public Event(final XMLEventReader reader) throws XMLStreamException, SeisFileException {
        StaxUtil.skipToStartElement(reader);
        StartElement startE = StaxUtil.expectStartElement(ELEMENT_NAME, reader);
        publicId = StaxUtil.pullAttribute(startE, QuakeMLTagNames.publicId);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(QuakeMLTagNames.description)) {
                    descriptionList.add(new EventDescription(reader));
                } else if (elName.equals(QuakeMLTagNames.comment)) {
                    commentList.add(new Comment(reader));
                    // } else if (elName.equals(QuakeMLTagNames.amplitude)) {
                    // amplitudeList.add(new Amplitude(reader));
                    // } else if
                    // (elName.equals(QuakeMLTagNames.stationMagnitude)) {
                    // stationMagnitudeList.add(new StationMagnitude(reader));
                } else if (elName.equals(QuakeMLTagNames.type)) {
                    type = StaxUtil.pullText(reader, QuakeMLTagNames.type);
                } else if (elName.equals(QuakeMLTagNames.origin)) {
                    originList.add(new Origin(reader));
                } else if (elName.equals(QuakeMLTagNames.focalMechanism)) {
                    focalMechanismList.add(new FocalMechanism(reader));
                } else if (elName.equals(QuakeMLTagNames.magnitude)) {
                    magnitudeList.add(new Magnitude(reader));
                } else if (elName.equals(QuakeMLTagNames.stationMagnitude)) {
                    stationMagnitudeList.add(new StationMagnitude(reader));
                } else if (elName.equals(QuakeMLTagNames.amplitude)) {
                    amplitudeList.add(new Amplitude(reader));
                } else if (elName.equals(QuakeMLTagNames.pick)) {
                    pickList.add(new Pick(reader));
                } else if (elName.equals(QuakeMLTagNames.preferredOriginID)) {
                    preferredOriginID = StaxUtil.pullText(reader, QuakeMLTagNames.preferredOriginID);
                } else if (elName.equals(QuakeMLTagNames.preferredMagnitudeID)) {
                    preferredMagnitudeID = StaxUtil.pullText(reader, QuakeMLTagNames.preferredMagnitudeID);
                } else if (elName.equals(QuakeMLTagNames.preferredFocalMechanismID)) {
                    preferredFocalMechanismID = StaxUtil.pullText(reader, QuakeMLTagNames.preferredFocalMechanismID);
                } else if (elName.equals(QuakeMLTagNames.creationInfo)) {
                    creationInfo = new CreationInfo(reader);
                } else {
                    System.err.println("Event skipping " + elName);
                    StaxUtil.skipToMatchingEnd(reader);
                }
            } else if (e.isEndElement()) {
                reader.nextEvent();
                return;
            } else {
                e = reader.nextEvent();
            }
        }
    }

    public String getPreferredOriginID() {
        return preferredOriginID;
    }

    public String getPreferredMagnitudeID() {
        return preferredMagnitudeID;
    }

    public String getPreferredFocalMechanismID() {
        return preferredFocalMechanismID;
    }

    public List<EventDescription> getDescriptionList() {
        return descriptionList;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public List<Origin> getOriginList() {
        return originList;
    }

    public String getPublicId() {
        return publicId;
    }

    public List<Magnitude> getMagnitudeList() {
        return magnitudeList;
    }

    public List<StationMagnitude> getStationMagnitudeList() {
        return stationMagnitudeList;
    }

    public int getIrisFECode() {
        return irisFECode;
    }

    public List<Pick> getPickList() {
        return pickList;
    }

    public String getType() {
        return type;
    }

    public CreationInfo getCreationInfo() {
        return creationInfo;
    }

    public List<FocalMechanism> getFocalMechanismList() {
        return focalMechanismList;
    }

    public List<Amplitude> getAmplitudeList() {
        return amplitudeList;
    }

    private String preferredOriginID, preferredMagnitudeID, preferredFocalMechanismID;

    private String publicId;

    private List<EventDescription> descriptionList = new ArrayList<EventDescription>();

    private List<Magnitude> magnitudeList = new ArrayList<Magnitude>();

    private List<StationMagnitude> stationMagnitudeList = new ArrayList<StationMagnitude>();

    private List<Amplitude> amplitudeList = new ArrayList<Amplitude>();

    private List<Comment> commentList = new ArrayList<Comment>();

    private List<Pick> pickList = new ArrayList<Pick>();

    private String type;

    private int irisFECode = -1;

    /*
     * List<StationMagnitude> stationMagnitudeList = new
     * ArrayList<StationMagnitude>();
     */
    private List<Origin> originList = new ArrayList<Origin>();

    private List<FocalMechanism> focalMechanismList = new ArrayList<FocalMechanism>();

    private CreationInfo creationInfo;
}
