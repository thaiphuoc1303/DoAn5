package com.example.doantest;

import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoLabDate {
    public Date date;
    SimpleDateFormat yFormat, MFormat;

    public PhotoLabDate() {
        yFormat = new SimpleDateFormat("HH:mm dd:MM:yyyy");
        MFormat = new SimpleDateFormat("HH:mm dd:MM");
    }

    public String compare(long t, Context context) {

        this.date = new Date(t);
        Date now = new Date();
        long s =  (now.getTime() - date.getTime())/1000;
        if(s> 31536000) {return yFormat.format(date);
        }
        else if(s>604800) {return MFormat.format(date);
        }
        else if (s > 86400){
            return s/86400 +" " + context.getString(R.string.day_ago);
        }
        else if (s>3600) {
            return s/3600 + " " + context.getString(R.string.hour_ago);
        }
        else if(s>60) {
            return s/60 + " " + context.getString(R.string.minute_ago);
        }
        return context.getString(R.string.just_now);
    }
}
