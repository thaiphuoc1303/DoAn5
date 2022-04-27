package com.example.doantest;

public class PhotoLabPixel {
    public double[] pixel;

    public PhotoLabPixel(double[] pixel) {
        this.pixel = pixel;
    }
    public PhotoLabPixel() {
    }

    public double[] getPixel() {
        return pixel;
    }
    public double getPixel(int i) {
        return pixel[i];
    }
    public int getChanel(){
        return pixel.length;
    }
    public void setPixel(double[] pixel) {
        this.pixel = pixel;
    }
    public void setPixel(double pixel, int i) {
        this.pixel[i] = pixel;
    }
}
