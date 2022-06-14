package com.example.doantest.Function;

import com.zomato.photofilters.geometry.Point;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ToneCurveSubFilter;

public class PhotoLabFilter {

    // 1
    public  Filter getStarLitFilter() {
        Point[] rgbKnots;
        rgbKnots = new Point[8];
        rgbKnots[0] = new Point(0, 0);
        rgbKnots[1] = new Point(34, 6);
        rgbKnots[2] = new Point(69, 23);
        rgbKnots[3] = new Point(100, 58);
        rgbKnots[4] = new Point(150, 154);
        rgbKnots[5] = new Point(176, 196);
        rgbKnots[6] = new Point(207, 233);
        rgbKnots[7] = new Point(255, 255);
        Filter filter = new Filter();
        filter.addSubFilter(new ToneCurveSubFilter(rgbKnots, null, null, null));
        return filter;
    }
    //2
    public  Filter getBlueMessFilter() {
        Point[] redKnots;
        redKnots = new Point[8];
        redKnots[0] = new Point(0, 0);
        redKnots[1] = new Point(86, 34);
        redKnots[2] = new Point(117, 41);
        redKnots[3] = new Point(146, 80);
        redKnots[4] = new Point(170, 151);
        redKnots[5] = new Point(200, 214);
        redKnots[6] = new Point(225, 242);
        redKnots[7] = new Point(255, 255);
        Filter filter = new Filter();
        filter.addSubFilter(new ToneCurveSubFilter(null, redKnots, null, null));
        filter.addSubFilter(new BrightnessSubFilter(30));
        filter.addSubFilter(new ContrastSubFilter(1f));
        return filter;
    }
    //3
    public  Filter getAweStruckVibeFilter() {
        Point[] rgbKnots;
        Point[] redKnots;
        Point[] greenKnots;
        Point[] blueKnots;

        rgbKnots = new Point[5];
        rgbKnots[0] = new Point(0, 0);
        rgbKnots[1] = new Point(80, 43);
        rgbKnots[2] = new Point(149, 102);
        rgbKnots[3] = new Point(201, 173);
        rgbKnots[4] = new Point(255, 255);

        redKnots = new Point[5];
        redKnots[0] = new Point(0, 0);
        redKnots[1] = new Point(125, 147);
        redKnots[2] = new Point(177, 199);
        redKnots[3] = new Point(213, 228);
        redKnots[4] = new Point(255, 255);


        greenKnots = new Point[6];
        greenKnots[0] = new Point(0, 0);
        greenKnots[1] = new Point(57, 76);
        greenKnots[2] = new Point(103, 130);
        greenKnots[3] = new Point(167, 192);
        greenKnots[4] = new Point(211, 229);
        greenKnots[5] = new Point(255, 255);


        blueKnots = new Point[7];
        blueKnots[0] = new Point(0, 0);
        blueKnots[1] = new Point(38, 62);
        blueKnots[2] = new Point(75, 112);
        blueKnots[3] = new Point(116, 158);
        blueKnots[4] = new Point(171, 204);
        blueKnots[5] = new Point(212, 233);
        blueKnots[6] = new Point(255, 255);

        Filter filter = new Filter();
        filter.addSubFilter(new ToneCurveSubFilter(rgbKnots, redKnots, greenKnots, blueKnots));
        return filter;
    }
    //4
    public  Filter getLimeStutterFilter() {
        Point[] blueKnots;
        blueKnots = new Point[3];
        blueKnots[0] = new Point(0, 0);
        blueKnots[1] = new Point(165, 114);
        blueKnots[2] = new Point(255, 255);
        // Check whether output is null or not.
        Filter filter = new Filter();
        filter.addSubFilter(new ToneCurveSubFilter(null, null, null, blueKnots));
        return filter;
    }
    //5
    public  Filter getNightWhisperFilter() {
        Point[] rgbKnots;
        Point[] redKnots;
        Point[] greenKnots;
        Point[] blueKnots;

        rgbKnots = new Point[3];
        rgbKnots[0] = new Point(0, 0);
        rgbKnots[1] = new Point(174, 109);
        rgbKnots[2] = new Point(255, 255);

        redKnots = new Point[4];
        redKnots[0] = new Point(0, 0);
        redKnots[1] = new Point(70, 114);
        redKnots[2] = new Point(157, 145);
        redKnots[3] = new Point(255, 255);

        greenKnots = new Point[3];
        greenKnots[0] = new Point(0, 0);
        greenKnots[1] = new Point(109, 138);
        greenKnots[2] = new Point(255, 255);

        blueKnots = new Point[3];
        blueKnots[0] = new Point(0, 0);
        blueKnots[1] = new Point(113, 152);
        blueKnots[2] = new Point(255, 255);

        Filter filter = new Filter();
        filter.addSubFilter(new ToneCurveSubFilter(rgbKnots, redKnots, greenKnots, blueKnots));
        return filter;
    }

    //6
    public   Filter getDarker(){
        Point[] rgbKnots = new Point[3];
        rgbKnots[0] = new Point(0, 0);
        rgbKnots[1] = new Point(128, 100);
        rgbKnots[2] = new Point(255, 255);

        Point[] redKnots = new Point[2];
        redKnots[0] = new Point(0, 0);
        redKnots[1] = new Point(255, 255);

        Point[] greenKnots = new Point[2];
        greenKnots[0] = new Point(0, 0);
        greenKnots[1] = new Point(255, 255);

        Point[] blueKnots = new Point[2];
        blueKnots[0] = new Point(0, 0);
        blueKnots[1] = new Point(255, 255);


        Filter filter = new Filter();
        filter.addSubFilter(new ToneCurveSubFilter( rgbKnots, redKnots, greenKnots, blueKnots));
        return filter;
    }

    //7
    public Filter getIncreaseContrast(){
        Point[] rgbKnots;
        rgbKnots = new Point[6];
        rgbKnots[0] = new Point(0, 0);
        rgbKnots[1] = new Point(36, 16);
        rgbKnots[2] = new Point(155, 155);
        rgbKnots[3] = new Point(210, 230);
        rgbKnots[4] = new Point(230, 250);
        rgbKnots[5] = new Point(255, 255);

        Filter filter = new Filter();
        filter.addSubFilter(new ToneCurveSubFilter( rgbKnots, null, null, null));
        return filter;
    }

    //8
    public Filter getBrighten(){
        Point[] rgbKnots;
        rgbKnots = new Point[3];
        rgbKnots[0] = new Point(0, 0);
        rgbKnots[1] = new Point(212, 219);
        rgbKnots[2] = new Point(255, 255);

        Filter filter = new Filter();
        filter.addSubFilter(new ToneCurveSubFilter( rgbKnots, null, null, null));
        return filter;
    }

    //9
    public Filter getFade(){
        Point[] rgbKnots;
        rgbKnots = new Point[6];
        rgbKnots[0] = new Point(0, 0);
        rgbKnots[1] = new Point(73, 65);
        rgbKnots[2] = new Point(93, 79);
        rgbKnots[3] = new Point(121, 140);
        rgbKnots[4] = new Point(207, 219);
        rgbKnots[5] = new Point(255, 236);

        Point[] redKnots;
        redKnots= new Point[2];
        redKnots[0] = new Point(0, 0);
        redKnots[1] = new Point(255, 255);

        Point[] greenKnots;
        greenKnots = new Point[2];
        greenKnots[0] = new Point(0, 0);
        greenKnots[1] = new Point(255, 255);

        Point[] blueKnots;
        blueKnots = new Point[2];
        blueKnots[0] = new Point(0, 0);
        blueKnots[1] = new Point(255, 255);


        Filter filter = new Filter();
        filter.addSubFilter(new ToneCurveSubFilter( rgbKnots, null, null, null));
        return filter;
    }

    //10
    public Filter getPTL1(){
        Point[] rgbKnots;
        rgbKnots = new Point[2];
        rgbKnots[0] = new Point(0, 21);
        rgbKnots[1] = new Point(255, 238);

        Point[] redKnots;
        redKnots= new Point[2];
        redKnots[0] = new Point(21, 0);
        redKnots[1] = new Point(239, 255);

        Point[] greenKnots;
        greenKnots = new Point[2];
        greenKnots[0] = new Point(18, 0);
        greenKnots[1] = new Point(240, 255);

        Point[] blueKnots;
        blueKnots = new Point[2];
        blueKnots[0] = new Point(19, 0);
        blueKnots[1] = new Point(234, 255);


        Filter filter = new Filter();
        filter.addSubFilter(new ToneCurveSubFilter( rgbKnots, redKnots, greenKnots, blueKnots));
        return filter;
    }

    //11
    public Filter getPTL2(){
        Point[] rgbKnots;
        rgbKnots = new Point[5];
        rgbKnots[0] = new Point(0, 21);
        rgbKnots[1] = new Point(76, 57);
        rgbKnots[2] = new Point(126, 129);
        rgbKnots[3] = new Point(188, 202);
        rgbKnots[4] = new Point(255, 255);

        Point[] redKnots;
        redKnots= new Point[5];
        redKnots[0] = new Point(0, 0);
        redKnots[1] = new Point(64, 46);
        redKnots[2] = new Point(126, 126);
        redKnots[3] = new Point(193, 178);
        redKnots[4] = new Point(255, 255);

        Point[] greenKnots;
        greenKnots = new Point[6];
        greenKnots[0] = new Point(0, 0);
        greenKnots[1] = new Point(71, 53);
        greenKnots[2] = new Point(126, 130);
        greenKnots[3] = new Point(160, 163);
        greenKnots[4] = new Point(193, 211);
        greenKnots[5] = new Point(255, 255);

        Point[] blueKnots;
        blueKnots = new Point[5];
        blueKnots[0] = new Point(0, 0);
        blueKnots[1] = new Point(64, 83);
        blueKnots[2] = new Point(125, 137);
        blueKnots[3] = new Point(188, 212);
        blueKnots[4] = new Point(255, 255);


        Filter filter = new Filter();
        filter.addSubFilter(new ToneCurveSubFilter( rgbKnots, redKnots, greenKnots, blueKnots));
        return filter;
    }
}
