package com.destro.morningdiary.backend.DayModel;

import com.destro.morningdiary.backend.TaskModel.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Day {
    long _id;
    private String dayName;
    private String date;
    private int nooftasks;

    public Day(long _id,String dayName, String date, int nooftasks) {
        this._id = _id;
        this.dayName = dayName;
        this.date = date;
        this.nooftasks = nooftasks;
    }

    public String getDayName() {
        return dayName;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getNooftasks() {
        return nooftasks;
    }

    public void setNooftasks(int nooftasks) {
        this.nooftasks = nooftasks;
    }

    public static Day getToday(){
        Calendar cal = Calendar.getInstance();
        return new Day(0,cal.getDisplayName(Calendar.DAY_OF_WEEK,Calendar.LONG, Locale.ENGLISH), Task.dateToString(cal),0);
    }

    public static String getFormattedDate(Calendar cal, String format){
        Date date = cal.getTime();
        return new SimpleDateFormat(format, Locale.ENGLISH).format(date);
    }

    public static Day getOffsetDay(Day day, int offset) throws ParseException {
        Calendar cal = Task.dateStringtoCalendar(day.getDate());
        assert cal != null;
        cal.set(Calendar.DAY_OF_MONTH,cal.get(Calendar.DAY_OF_MONTH) + offset);
        return new Day(0,cal.getDisplayName(Calendar.DAY_OF_WEEK,Calendar.LONG, Locale.ENGLISH),Task.dateToString(cal),0);
    }
}
