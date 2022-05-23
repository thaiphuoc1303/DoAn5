package com.example.doantest;

public class PhotoLabMatrix {
    public double[] MedianBlur(PhotoLabPixel[][] pixels){
        int chanel = pixels[0][0].getChanel();
        double[] s = new double[chanel];
        for (int i = 0; i < chanel; i++) {
            s[i] = 0;
        }
        int m =pixels.length, n =pixels.length;
        for(int i = 0; i < m; i++){
            for(int j = 0; j < n; j++){
                for (int k = 0; k<chanel; k++){
                    s[k] += pixels[i][j].getPixel(k);
                }
            }
        }
        for (int i = 0; i < chanel; i++) {
            s[i] = s[i]/ (m*n);
        }
        return s;
    }
    public double[] Sharpen(PhotoLabPixel [][] pixels){
        int chanel = pixels[0][0].getChanel();
        double[] s = new double[chanel];
        for (int i = 0; i < chanel; i++) {
            s[i] = 0;
        }
        int m =pixels.length, n =pixels.length, avg = m/2;
        for(int i = 0; i < m; i++){
            for(int j = 0; j < n; j++){
                for (int k = 0; k<chanel; k++){
                    if(i==avg && j == avg){
                        s[k] += m*n*pixels[i][j].getPixel(k);
                    }
                    else s[k] -= pixels[i][j].getPixel(k);
                }
            }
        }
        return s;
    }
}
