package com.destro.morningdiary.backend;

import androidx.appcompat.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.destro.morningdiary.backend.Reminder.ReminderService;
import com.destro.morningdiary.backend.TaskModel.Task;

import java.text.ParseException;

public class Utils {

    public static void createNotification (Context context, Task task) {
        Intent myIntent = new Intent(context, ReminderService.class);
        myIntent.putExtra("notId",task.get_id());
        myIntent.setAction(ReminderService.CREATE);
        context.startService(myIntent);
        /*
        myIntent.putExtra("Task",new ParcelableTask(task));
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent. getService (context, 0 , myIntent , 0 ) ;
        Calendar calendar = Task.datetimeStringToCalendar(task.getDatetime());
        //Toast.makeText(context, task.getDatetime(), Toast.LENGTH_SHORT).show();
        assert calendar != null;
        //alarmManager.setRepeating(AlarmManager. RTC_WAKEUP , calendar.getTimeInMillis() , 1000 * 60 * 60 * 24 , pendingIntent) ;
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

         */
    }

    public static AlertDialog.Builder getDialogBuilder(Context cx, String title, String subTitle, String positiveBttnText, DialogInterface.OnClickListener positiveBttnListener,
                                                                      String negativeBttnText, DialogInterface.OnClickListener negativeBttnListener){
        return new AlertDialog.Builder(cx)
                .setTitle(title)
                .setMessage(subTitle)
                .setPositiveButton(positiveBttnText, positiveBttnListener)
                .setNegativeButton(negativeBttnText, negativeBttnListener!=null ? negativeBttnListener : new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
    }
}
