package com.example.doantest;

import android.util.Log;

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
    public double[] LaplaceVariant(PhotoLabPixel [][] pixels){
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

    public double[] Laplace(PhotoLabPixel [][] pixels){
        int chanel = pixels[0][0].getChanel();
        double[] s = new double[chanel];
        for (int i = 0; i < chanel; i++) {
            s[i] = 0;
        }
        int m =pixels.length, n =pixels.length, avg = m/2;
        Log.e("avg", avg+"");
        for(int i = 0; i < m; i++){
            for(int j = 0; j < n; j++){
                for (int k = 0; k<chanel; k++){
                    if(i==avg && j == avg){
                        s[k] += 6*pixels[i][j].getPixel(k);
                    }
                    else if(i==avg || j == avg){
                        s[k] += pixels[i][j].getPixel(k);
                    }
                }
            }
        }
        for (int i = 0; i < chanel; i++) {
            s[i] = s[i]/ (m*n);
        }
        return s;
    }

    public double[] Sobel(PhotoLabPixel [][] pixels){
        int chanel = pixels[0][0].getChanel();
        double[] s1 = new double[chanel];
        double[] s2 = new double[chanel];

        for (int i = 0; i < chanel; i++) {
            s1[i] = 0;
            s2[i] = 0;
        }
        double[][] kenel = {{1, 2, 1}, {0, 0, 0}, {-1, -2, -1}};
        int m =pixels.length, n =pixels.length, avg = m/2;
        for(int i = 0; i < m; i++){
            for(int j = 0; j < n; j++){
                for (int k = 0; k<chanel; k++){
                    s1[k] += kenel[i][j]*pixels[i][j].getPixel(k);
                    s2[k] += kenel[j][i]*pixels[i][j].getPixel(k);
                }
            }
        }
        for (int i = 0; i < chanel; i++) {
            s1[i] += s2[i] + pixels[1][1].getPixel(i);
        }
        return s1;
    }
}
