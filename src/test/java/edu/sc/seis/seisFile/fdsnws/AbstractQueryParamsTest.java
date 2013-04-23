package edu.sc.seis.seisFile.fdsnws;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;


public class AbstractQueryParamsTest {

    @Test
    public void testAppendToParam() throws URISyntaxException {
        String T = "TEST";
        String first = "first";
        String second = "second";
        String third = "third";
        AbstractQueryParams aqp = new AbstractQueryParams(new URI("http://test.seis.sc.edu/fdsnws/event/query/1?")) {};
        aqp.appendToParam(T, first);
        assertEquals("first", first, aqp.getParam(T));
        aqp.appendToParam(T, second);
        assertEquals("first", first+","+second, aqp.getParam(T));
        aqp.appendToParam(T, third);
        assertEquals("first", first+","+second+","+third, aqp.getParam(T));
    }
}