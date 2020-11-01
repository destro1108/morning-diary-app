package com.destro.morningdiary.backend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.destro.morningdiary.backend.DayModel.Day;
import com.destro.morningdiary.backend.TaskModel.Task;

import java.util.ArrayList;
import java.util.Arrays;

public class DBManager extends SQLiteOpenHelper {

    public static int entryCount = 0;
    public static int DB_VERSION = 2;

    public static final String DATABASE_NAME = "MorningDiary.db";
    public static final String DAYS_TABLE_NAME = "days";
    public static final String DAYS_COLUMN_ID = "_id";
    public static final String DAYS_COLUMN_NAME = "name";
    public static final String DAYS_COLUMN_DATE = "date";
    public static final String DAYS_COLUMN_NOOFTASKS = "tasks";

    public static final String TASKS_TABLE_NAME = "tasks";
    public static final String TASKS_COLUMN_ID = "_id";
    public static final String TASKS_COLUMN_DAY_ID = "day_id";
    public static final String TASKS_COLUMN_TITLE = "title";
    public static final String TASKS_COLUMN_DESC = "description";
    public static final String TASKS_COLUMN_TIME = "time";
    public static final String TASKS_COLUMN_ISDONE = "isdone";

    private static DBManager dbManager;

    public DBManager(Context context){
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    public static DBManager getInstance(Context context){
        if(dbManager==null){
            dbManager = new DBManager(context);
        }
        return dbManager;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DAYS_TABLE_NAME + " ("+ DAYS_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                DAYS_COLUMN_NAME + " TEXT, " + DAYS_COLUMN_DATE + " TEXT UNIQUE NOT NULL, " + DAYS_COLUMN_NOOFTASKS + " INTEGER)");
        db.execSQL("CREATE TABLE " + TASKS_TABLE_NAME + " (" + TASKS_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                TASKS_COLUMN_TITLE + " TEXT, " + TASKS_COLUMN_DESC + " TEXT, " + TASKS_COLUMN_TIME + " TEXT, " +
                TASKS_COLUMN_ISDONE + " BOOLEAN, " + TASKS_COLUMN_DAY_ID + " BIGINT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TASKS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DAYS_TABLE_NAME);
        onCreate(db);
    }

    public long addTask(Task task){
        if(task != null){
            SQLiteDatabase db = getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(TASKS_COLUMN_TITLE, task.getTitle());
            cv.put(TASKS_COLUMN_DESC, task.getDesc());
            cv.put(TASKS_COLUMN_TIME, task.getDatetime());
            cv.put(TASKS_COLUMN_ISDONE,task.isDone());
            cv.put(TASKS_COLUMN_DAY_ID, task.getDayID());
            db.execSQL("UPDATE " + DAYS_TABLE_NAME + " SET " + DAYS_COLUMN_NOOFTASKS + " = " + DAYS_COLUMN_NOOFTASKS + "+ 1 WHERE "
                + DAYS_COLUMN_ID + " = " + task.getDayID());
            entryCount++;
            return db.insert(TASKS_TABLE_NAME, null, cv);
        }else return -1;
    }

    public boolean isDBEmpty(){
        return entryCount == 0;
    }

    public Task getTask(long id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cs = db.rawQuery("SELECT * FROM "+ TASKS_TABLE_NAME + " WHERE "+TASKS_COLUMN_ID + " = "+id, null);
        cs.moveToFirst();
        Task task = new Task(id, cs.getString(1),cs.getString(2), cs.getString(3),
                cs.getString(4).equalsIgnoreCase("1"),cs.getLong(5));
        cs.close();
        return task;
    }

    public ArrayList<Task> getAllTasksinDay(long dayId){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cs = db.rawQuery("SELECT * FROM " + TASKS_TABLE_NAME + " WHERE " + TASKS_COLUMN_DAY_ID + " = " + dayId,null);
        ArrayList<Task> list = new ArrayList<>(cs.getCount());
        if(cs.getCount() == 0) return list;
        cs.moveToFirst();
        do{
            list.add(new Task(cs.getLong(0),cs.getString(1),cs.getString(2),cs.getString(3),
                    cs.getString(4).equalsIgnoreCase("1"),cs.getLong(5)));
        }
        while (cs.moveToNext());
        cs.close();
        return list;
    }

    public ArrayList<Task> getAllTasksinDay(String date){
        SQLiteDatabase db = getReadableDatabase();
        Day day = getDay(date);
        if(day != null) {
            Cursor cs = db.rawQuery("SELECT * FROM " + TASKS_TABLE_NAME + " WHERE " + TASKS_COLUMN_DAY_ID + " = " + day.get_id(), null);
            ArrayList<Task> list = new ArrayList<>(cs.getCount());
            cs.moveToFirst();
            do {
                list.add(new Task(cs.getLong(0), cs.getString(1), cs.getString(2), cs.getString(3),
                        cs.getString(4).equalsIgnoreCase("1"), cs.getLong(5)));
            }
            while (cs.moveToNext());
            cs.close();
            return list;
        }
        return new ArrayList<>();
    }

    public void updateTask(Task newTask){
        if(newTask == null) return;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TASKS_COLUMN_TITLE, newTask.getTitle());
        cv.put(TASKS_COLUMN_DESC, newTask.getDesc());
        cv.put(TASKS_COLUMN_TIME, newTask.getDatetime());
        cv.put(TASKS_COLUMN_ISDONE,newTask.isDone());
        cv.put(TASKS_COLUMN_DAY_ID, newTask.getDayID());
        db.update(TASKS_TABLE_NAME, cv, TASKS_COLUMN_ID + " = " + newTask.get_id(),null);
    }

    public void updateTaskDone(long taskId, boolean done){
        if(taskId == -1) return;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TASKS_COLUMN_ISDONE, done);
        db.update(TASKS_TABLE_NAME, cv, TASKS_COLUMN_ID + " = " + taskId,null);
    }

    public void deleteTask(long taskId){
        Task task = getTask(taskId);
        SQLiteDatabase db =         getWritableDatabase();
        db.execSQL("UPDATE " + DAYS_TABLE_NAME + " SET " + DAYS_COLUMN_NOOFTASKS + " = " + DAYS_COLUMN_NOOFTASKS + "- 1 WHERE "
                + DAYS_COLUMN_ID + " = " + task.getDayID());
        db.delete(TASKS_TABLE_NAME, TASKS_COLUMN_ID + " = " + taskId, null);
    }


    public void updateBatch(ArrayList<Task> batch){
        if(batch==null) return;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv;
        for(Task task : batch){
            cv = new ContentValues();
            cv.put(TASKS_COLUMN_TITLE, task.getTitle());
            cv.put(TASKS_COLUMN_DESC, task.getDesc());
            cv.put(TASKS_COLUMN_TIME, task.getDatetime());
            cv.put(TASKS_COLUMN_ISDONE,task.isDone());
            cv.put(TASKS_COLUMN_DAY_ID, task.getDayID());
            db.update(TASKS_TABLE_NAME, cv, TASKS_COLUMN_ID + " = " + task.get_id(),null);
        }
    }

    public long addDay(Day day){
        if(day != null){
            SQLiteDatabase db = getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(DAYS_COLUMN_NAME, day.getDayName());
            cv.put(DAYS_COLUMN_DATE, day.getDate());
            cv.put(DAYS_COLUMN_NOOFTASKS, day.getNooftasks());
            return db.insert(DAYS_TABLE_NAME, null, cv);
        }else return -1;
    }

    public Day getDay(long dayId){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cs = db.rawQuery("SELECT * FROM " + DAYS_TABLE_NAME + " WHERE " + DAYS_COLUMN_ID + " = " + dayId,null);
        if(cs.getCount()==0) return null;
        cs.moveToFirst();
        Day day =  new Day(cs.getLong(0),cs.getString(1),cs.getString(2),cs.getInt(3));
        cs.close();
        return day;
    }

    public Day getDay(String date){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cs = db.rawQuery("SELECT * FROM " + DAYS_TABLE_NAME + " WHERE " + DAYS_COLUMN_DATE + " = '" + date + "';",null);
        if(cs.getCount()==0) return null;
        cs.moveToFirst();
        Day day =  new Day(cs.getLong(0),cs.getString(1),cs.getString(2),cs.getInt(3));
        cs.close();
        return day;
    }

    public ArrayList<Day> getAllDays(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cs = db.rawQuery("SELECT * FROM " + DAYS_TABLE_NAME + ";",null);
        if(cs.getCount()==0) return null;
        cs.moveToFirst();
        ArrayList<Day> list = new ArrayList<>(cs.getCount());
        do list.add(new Day(cs.getLong(0), cs.getString(1), cs.getString(2), cs.getInt(3)));
        while (cs.moveToNext());
        cs.close();
        return list;
    }

    public ArrayList<String> getStructure(){
        Cursor cs = getReadableDatabase().rawQuery("SELECT name from sqlite_master WHERE type='table' AND name='"+TASKS_TABLE_NAME+"';",null);
        ArrayList<String> sx = new ArrayList<>(Arrays.asList(cs.getColumnNames()));
        cs.close();
        return sx;
    }

}
