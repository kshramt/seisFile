package edu.sc.seis.fissuresUtil.sac;

import junit.framework.TestCase;

/**
 * SacTimeSeriesTest.java
 *
 * @author Created by Philip Oliver-Paull
 */

public class SacTimeSeriesTest extends TestCase{

    public void testSwapBytesShort(){
        short s1 = Short.MIN_VALUE;
        short s2 = Short.MAX_VALUE;
        short s3 = 0;

        short s4 = SacTimeSeries.swapBytes(SacTimeSeries.swapBytes(s1));
        short s5 = SacTimeSeries.swapBytes(SacTimeSeries.swapBytes(s2));
        short s6 = SacTimeSeries.swapBytes(SacTimeSeries.swapBytes(s3));

        assertEquals(s1, s4);
        assertEquals(s2, s5);
        assertEquals(s3, s6);
    }

    public void testSwapBytesFloat(){
        float f1 = Float.MIN_VALUE*2;
        float f2 = Float.MAX_VALUE/2;
        float f3 = 0f;

        float f4 = SacTimeSeries.swapBytes(SacTimeSeries.swapBytes(f1));
        float f5 = SacTimeSeries.swapBytes(SacTimeSeries.swapBytes(f2));
        float f6 = SacTimeSeries.swapBytes(SacTimeSeries.swapBytes(f3));

        assertEquals(f1, f4, 0.01f);

    }

    public void testSwapBytesInt(){
        int i1 = Integer.MIN_VALUE + 1;
        int i2 = Integer.MAX_VALUE - 1;
        int i3 = 0;

        int i4 = SacTimeSeries.swapBytes(SacTimeSeries.swapBytes(i1));
        int i5 = SacTimeSeries.swapBytes(SacTimeSeries.swapBytes(i2));
        int i6 = SacTimeSeries.swapBytes(SacTimeSeries.swapBytes(i3));

        assertEquals(i1, i4);
        assertEquals(i2, i5);
        assertEquals(i3, i6);
    }

    public void testSwapBytesDouble(){
        double d1 = Double.MIN_VALUE;
        double d2 = Double.MAX_VALUE;
        double d3 = Math.PI;
        double d4 = 0.0;

        double d5 = SacTimeSeries.swapBytes(SacTimeSeries.swapBytes(d1));
        double d6 = SacTimeSeries.swapBytes(SacTimeSeries.swapBytes(d2));
        double d7 = SacTimeSeries.swapBytes(SacTimeSeries.swapBytes(d3));
        double d8 = SacTimeSeries.swapBytes(SacTimeSeries.swapBytes(d4));

        assertEquals(d1, d5, 0.01);
        assertEquals(d2, d6, 0.01);
        assertEquals(d3, d7, 0.01);
        assertEquals(d4, d8, 0.01);
    }

    public void testSwapBytesLong(){
        long l1 = Long.MIN_VALUE;
        long l2 = Long.MAX_VALUE;
        long l3 = 0l;

        long l4 = SacTimeSeries.swapBytes(SacTimeSeries.swapBytes(l1));
        long l5 = SacTimeSeries.swapBytes(SacTimeSeries.swapBytes(l2));
        long l6 = SacTimeSeries.swapBytes(SacTimeSeries.swapBytes(l3));

        assertEquals(l1, l4);
        assertEquals(l2, l5);
        assertEquals(l3, l6);
    }

}

