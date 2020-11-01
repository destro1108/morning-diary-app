package com.destro.morningdiary.backend.TaskModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Task {

    protected long _id;
    protected String title;
    protected String desc;
    protected long dayID;
    protected String datetime;
    protected Boolean done;

    public Task(String title, String desc, String datetime, Boolean done, long dayID) {
        this.title = title;
        this.desc = desc;
        this.datetime = datetime;
        this.done = done;
        this.dayID = dayID;
    }

    public Task(long _id, String title, String desc, String datetime, Boolean done, long dayID) {
        this._id = _id;
        this.title = title;
        this.desc = desc;
        this.dayID = dayID;
        this.datetime = datetime;
        this.done = done;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public long getDayID() {
        return dayID;
    }

    public void setDayID(long dayID) {
        this.dayID = dayID;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public Boolean isDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public static String dateToString(Calendar cal){
        Date date = cal.getTime();
        return new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(date);
    }

    public static String dateTimeToString(Calendar cal){
        Date date = cal.getTime();
        return new SimpleDateFormat("dd-MM-yyyy hh:MM", Locale.ENGLISH).format(date);
    }

    /*
    public static Calendar dateStringtoCalendar(String date){
        if(!date.equalsIgnoreCase("") || date.equalsIgnoreCase("")){
            String[] fields = date.split("-");
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH,Integer.parseInt(fields[0]));
            cal.set(Calendar.MONTH,Integer.parseInt(fields[1]));
            cal.set(Calendar.YEAR,Integer.parseInt(fields[2]));
            return cal;
        }
        return null;
    }
     */

    public static Calendar dateStringtoCalendar(String date) throws ParseException {
        if(!date.equalsIgnoreCase("") || date.equalsIgnoreCase("")){
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy",Locale.ENGLISH);
            Calendar cal = Calendar.getInstance();
            cal.setTime(Objects.requireNonNull(dateFormat.parse(date)));
            return cal;
        }
        return null;
    }
    /*
    public static Calendar datetimeStringToCalendar(String datetime){
        if(!datetime.equalsIgnoreCase("") || datetime.equalsIgnoreCase("")){
            String[] fields = datetime.split(" ");
            String[] dateFields = fields[0].split("-");
            String[] timeFields = fields[1].split(":");
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH,Integer.parseInt(dateFields[0]));
            cal.set(Calendar.MONTH,Integer.parseInt(dateFields[1]));
            cal.set(Calendar.YEAR,Integer.parseInt(dateFields[2]));
            cal.set(Calendar.HOUR, Integer.parseInt(timeFields[0]));
            cal.set(Calendar.MINUTE, Integer.parseInt(timeFields[1]));
            return cal;
        }
        return null;
    }
     */

    public static Calendar datetimeStringToCalendar(String datetime) throws ParseException{
        if(!datetime.equalsIgnoreCase("") || datetime.equalsIgnoreCase("")){
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:MM",Locale.ENGLISH);
            Calendar cal = Calendar.getInstance();
            cal.setTime(Objects.requireNonNull(dateFormat.parse(datetime)));
            return cal;
        }
        return null;
    }

    public String getTime(){
        String[] timeFields = datetime.split(" ")[1].split(":");
        int hr = Integer.parseInt(timeFields[0]), min = Integer.parseInt(timeFields[1]), ampm=0;
        if(hr>12){
            hr = hr-12;
            ampm = 1;
        }
        return (hr<10 ? "0"+hr : hr) + ":" + (min<10 ? "0"+min : min) + " " + (ampm==0 ? "AM" : "PM");
    }

}
