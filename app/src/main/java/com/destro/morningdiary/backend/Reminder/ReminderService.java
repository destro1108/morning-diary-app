package com.destro.morningdiary.backend.Reminder;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import com.destro.morningdiary.backend.DBManager;
import com.destro.morningdiary.backend.TaskModel.Task;

import java.text.ParseException;
import java.util.Calendar;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class ReminderService extends IntentService {

    public static final String CREATE = "CREATE";
    public static final String CANCEL = "CANCEL";

    private IntentFilter matcher;

    public ReminderService() {
        super("ReminderService");
        matcher = new IntentFilter();
        matcher.addAction(CREATE);
        matcher.addAction(CANCEL);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            long notId = intent.getLongExtra("notId",-1);
            if(matcher.matchAction(action)){
                try {
                    execute(action, notId);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void execute(String action, long notId) throws ParseException {
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Task task = DBManager.getInstance(this).getTask(notId);
        Intent intent = new Intent(this, RemiderReceiver.class);
        intent.putExtra("id",notId);

        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if(CREATE.equals(action)){
            Calendar cal = Task.datetimeStringToCalendar(task.getDatetime());
            assert cal != null;
            manager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
            Toast.makeText(this, "ALARM SET "+cal.get(Calendar.HOUR_OF_DAY), Toast.LENGTH_SHORT).show();
        }
        else if(CANCEL.equals(action))
            manager.cancel(pi);
    }
}
