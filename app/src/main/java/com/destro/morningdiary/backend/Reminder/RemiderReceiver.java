package com.destro.morningdiary.backend.Reminder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.destro.morningdiary.MainActivity;
import com.destro.morningdiary.R;
import com.destro.morningdiary.backend.DBManager;
import com.destro.morningdiary.backend.TaskModel.Task;

import static android.content.Context.NOTIFICATION_SERVICE;

public class RemiderReceiver extends BroadcastReceiver {

    private String DEFAULT_ID = "DEFAULT";
    public static final String CHANNEL_ID = "MD_1108";

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Receiver", Toast.LENGTH_SHORT).show();
        Task task = DBManager.getInstance(context).getTask(intent.getLongExtra("id",-1));
        Intent notIntent = new Intent(context, MainActivity.class);
        notIntent.putExtra("fromNot",true);
        notIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,notIntent,0);
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setContentTitle(task.getTitle());
        builder.setContentText(task.getDesc());
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        builder.setAutoCancel(true);
        manager.notify(0,builder.build());
    }
}
