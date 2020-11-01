package com.destro.morningdiary.ui.newTask;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.preference.PreferenceManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.destro.morningdiary.MainActivity;
import com.destro.morningdiary.R;
import com.destro.morningdiary.backend.DBManager;
import com.destro.morningdiary.backend.DayModel.Day;
import com.destro.morningdiary.backend.TaskModel.Task;
import com.destro.morningdiary.backend.Utils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;

public class NewTask extends AppCompatActivity {

    private DBManager dbManager;
    private Calendar cal;
    private Boolean isEdit = false;
    private long editTaskId;

    private TextInputEditText title_input;
    private TextInputEditText desc_input;
    private RadioGroup dateSel;
    private TimePicker timeSel;
    private MaterialButton bttnOK;
    private MaterialButton bttnCancel;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        setTitle("Add Task");
        ActionBar bar = getSupportActionBar();
        //assert bar != null;
        //bar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_new_task);
        dbManager = DBManager.getInstance(this);
        cal = Calendar.getInstance();
        title_input = findViewById(R.id.title_input);
        desc_input = findViewById(R.id.desc_input);
        dateSel = findViewById(R.id.date_sel);
        timeSel = findViewById(R.id.time_sel);
        bttnOK = findViewById(R.id.dbttn_ok);
        bttnCancel = findViewById(R.id.dbttn_cancel);
        ArrayAdapter<String> dates = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new ArrayList<String>());
        dates.addAll("Today,Tomorrow".split(","));
        //dateSel.setAdapter(dates);
        timeSel.setIs24HourView(false);
        if(getIntent().getBooleanExtra("isEdit",false)){
            isEdit = true;
            setTitle("Edit Task");
            Task task = dbManager.getTask(getIntent().getLongExtra("editTaskId",-1));
            editTaskId = task.get_id();
            title_input.setText(task.getTitle());
            desc_input.setText(task.getDesc());
            try {
                cal = Task.datetimeStringToCalendar(task.getDatetime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            assert cal != null;
            timeSel.setHour(cal.get(Calendar.HOUR_OF_DAY));
            timeSel.setMinute(cal.get(Calendar.MINUTE));
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onClick(View v){
        //Toast.makeText(this, "Wk", Toast.LENGTH_SHORT).show();
        switch (v.getId()){
            case R.id.dbttn_cancel:
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
            case R.id.dbttn_ok:
                String title = Objects.requireNonNull(title_input.getText()).toString(), desc = Objects.requireNonNull(desc_input.getText()).toString(), dateStr, dayName, time;
                if(title.equals("")) {Toast.makeText(this, "Please input Task Title", Toast.LENGTH_SHORT).show();return;}
                if(desc.equals("")) {Toast.makeText(this, "Please input Task Description", Toast.LENGTH_LONG).show();return;}
                int dayOffset;
                Task task;
                dayOffset = dateSel.getCheckedRadioButtonId() == R.id.radio_tomorrow ? 1 : 0;
                cal.set(Calendar.DAY_OF_MONTH,cal.get(Calendar.DAY_OF_MONTH) + dayOffset);
                dateStr = Task.dateToString(cal);
                time = (timeSel.getHour()<10 ? "0" + timeSel.getHour() : timeSel.getHour()) + ":" + (timeSel.getMinute()<10 ? "0" + timeSel.getMinute() : timeSel.getMinute());
                if(dayOffset==0 && timeSel.getHour()<Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
                    Toast.makeText(this, "Enter Valid Time", Toast.LENGTH_SHORT).show();
                    return;
                }
                cal.set(Calendar.HOUR, timeSel.getHour());
                cal.set(Calendar.MINUTE, timeSel.getMinute());
                //cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1)  + "/" + cal.get(Calendar.YEAR);
                dayName = cal.getDisplayName(Calendar.DAY_OF_WEEK,Calendar.LONG, Locale.ENGLISH);
                Day day = dbManager.getDay(dateStr);
                if(day==null){
                    day = new Day(0,dayName,dateStr,0);
                    day.set_id(dbManager.addDay(day));
                }
                if(isEdit){
                    task = new Task(editTaskId, title, desc, dateStr + " " + time, false, day.get_id());
                    dbManager.updateTask(task);
                    Utils.createNotification(getApplicationContext(),task);
                }else{
                    task = new Task(title, desc, dateStr + " " + time, false, day.get_id());
                    long taskId = dbManager.addTask(task);
                    task.set_id(taskId);
                    Utils.createNotification(getApplicationContext(),task);
                }
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                if(preferences.getBoolean("saveEventToCal",false)) addEventToCalendar(task);
                //Utils.createNotification(getApplicationContext(), task);
                break;
            default:
                break;
        }
    }

    public void addEventToCalendar(Task task){
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra(CalendarContract.Events.TITLE,task.getTitle());
        intent.putExtra(CalendarContract.Events.DESCRIPTION,task.getDesc());
        intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY,false);
        GregorianCalendar calendar = new GregorianCalendar();
        //new GregorianCalendar(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH),
        //        cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE));
        calendar.setTimeInMillis(cal.getTimeInMillis());
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,calendar.getTimeInMillis() - 12 * 3600000);
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,calendar.getTimeInMillis() - 12 * 3600000);
        Toast.makeText(this, "Adding Event", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            NavUtils.navigateUpFromSameTask(this);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = Utils.getDialogBuilder(getApplicationContext(), "Cancel New Task", "Are you sure to cancel new task?", "Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                },"No",null);
    }
}