package com.destro.morningdiary;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.destro.morningdiary.backend.DBManager;
import com.destro.morningdiary.backend.DayModel.Day;
import com.destro.morningdiary.backend.Reminder.RemiderReceiver;
import com.destro.morningdiary.backend.TaskModel.Task;
import com.destro.morningdiary.ui.home.HomeFragment;
import com.destro.morningdiary.ui.newTask.NewTask;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private HomeFragment homeFragment;
    private DBManager dbManager;
    private Day today;
    public static ArrayList<Task> taskArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.AppTheme_NoActionBar);
        //overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        createNotificationChannel();
        //Toast.makeText(this, "ExT", Toast.LENGTH_SHORT).show();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dbManager = DBManager.getInstance(this);
        String todayDate = Task.dateToString(Calendar.getInstance());
        today = dbManager.getDay(todayDate);
        if(today != null)
            taskArray = dbManager.getAllTasksinDay(today.get_id());
        else{
            taskArray = new ArrayList<>();
            today = Day.getToday();
        }
        FloatingActionButton fab = findViewById(R.id.fab);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_day_view, R.id.nav_settings)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                 */
                //listener.onAddTask();
                Intent intent = new Intent(getApplicationContext(), NewTask.class);
                startActivity(intent);
            }
        });
    }

    public ArrayList<Task> getTasks(){
        return taskArray;
    }

    public void setTasks(ArrayList<Task> tasks){
        taskArray = tasks;
    }


    public DBManager getDbManager(){
        return dbManager;
    }

    public Day getToday() {
        return today;
    }

    public void setToday(Day today){
        this.today = today;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ReminderChannel";
            String description = "Reminder Notification Channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(RemiderReceiver.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            //Toast.makeText(this, "Channel Created", Toast.LENGTH_SHORT).show();
        }
    }
}