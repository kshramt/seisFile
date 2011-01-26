package edu.sc.seis.seisFile.stationxml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class StyxPrint {
    
    public static void main(String[] args) throws FileNotFoundException, XMLStreamException, StationXMLException {
        if (args.length == 0) {
            System.out.println("Usage: styxprint filename.xml");
            return;
        }
        String filename = args[0];
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLEventReader r = factory.createXMLEventReader(filename,
                                                        new FileInputStream(filename));
        XMLEvent e = r.peek();
        while(! e.isStartElement()) {
            System.out.println(e);
            e = r.nextEvent(); // eat this one
            e = r.peek();  // peek at the next
        }
        System.out.println("begin StaMessage");
        StaMessage staMessage = new StaMessage(r);
        System.out.println("Source: "+staMessage.getSource());
        System.out.println("Sender: "+staMessage.getSender());
        System.out.println("Module: "+staMessage.getModule());
        System.out.println("SentDate: "+staMessage.getSentDate());
        StationIterator it = staMessage.getStations();
        while(it.hasNext()) {
            Station s = it.next();
            System.out.println("Station: "+s.getNetCode()+"."+s.getStaCode()+" "+s.getStaList().size());
        }
        System.out.println("Done station iterate");
    }
}