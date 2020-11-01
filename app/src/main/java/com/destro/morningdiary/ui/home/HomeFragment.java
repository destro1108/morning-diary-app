package com.destro.morningdiary.ui.home;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.destro.morningdiary.MainActivity;
import com.destro.morningdiary.R;
import com.destro.morningdiary.backend.DBManager;
import com.destro.morningdiary.backend.DayModel.Day;
import com.destro.morningdiary.backend.RecyclerItemHelper;
import com.destro.morningdiary.backend.TaskModel.Task;
import com.destro.morningdiary.backend.TaskModel.TaskAdapter;
import com.destro.morningdiary.ui.newTask.NewTask;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private ArrayList<Task> tasks;
    private DBManager dbManager;
    private Task currentSelectedTask;
    private Day today, currentDay;
    private TaskAdapter adapter;

    private MainActivity activity;

    private MaterialTextView taskTitle;
    private ImageButton prevBttn;
    private MaterialTextView todayText;
    private ImageButton nextBttn;
    private MaterialTextView noTaskText;
    private RecyclerView tasklist;
    private LinearLayout saveChangeLayout;
    private MaterialButton saveChangeBttn;
    private MaterialButton cancelChangeBttn;
    private AlertDialog dialog;

    public interface SetChangeVisible{
        void onChangeVisibility();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        activity =(MainActivity) getActivity();
        dbManager = DBManager.getInstance(getActivity());

        /*
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
         */
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        taskTitle = root.findViewById(R.id.task_title_home);
        prevBttn = root.findViewById(R.id.bttn_prev);
        nextBttn = root.findViewById(R.id.bttn_next);
        noTaskText = root.findViewById(R.id.no_task_text);
        tasklist = root.findViewById(R.id.tasklist);
        todayText = root.findViewById(R.id.today_text);
        saveChangeLayout = root.findViewById(R.id.save_change_layout);
        saveChangeBttn = root.findViewById(R.id.bttn_save_change);
        cancelChangeBttn = root.findViewById(R.id.bttn_cancel_change);
        today = activity.getToday();
        if(tasks != null &&  !tasks.isEmpty()){
            Toast.makeText(activity, "XSS", Toast.LENGTH_SHORT).show();
            currentDay = dbManager.getDay(tasks.get(0).getDayID());
        }else currentDay = today;
        todayText.setText(String.format("%s, %s %s", today.getDayName(), today.getDate().split("-")[0], Day.getFormattedDate(Calendar.getInstance(), "MMM")));
        tasks = activity.getTasks();
        tasklist.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new TaskAdapter(getContext(), tasks, new SetChangeVisible() {
            @Override
            public void onChangeVisibility() {
                saveChangeLayout.setVisibility(View.VISIBLE);
            }
        });
        //saveChangeLayout.setVisibility(adapter.getUpdatedTasks().size()==0 ? View.GONE : View.VISIBLE );
        tasklist.setAdapter(adapter);

        ItemTouchHelper touchHelper = new ItemTouchHelper(new RecyclerItemHelper(adapter,getActivity()));
        touchHelper.attachToRecyclerView(tasklist);
        /*
        tasklist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(activity, "XP", Toast.LENGTH_SHORT).show();
                currentSelectedTask = tasks.get(i);
                createDialog();
            }
        });

         */
        prevBttn.setOnClickListener(this);
        nextBttn.setOnClickListener(this);
        cancelChangeBttn.setOnClickListener(this);
        saveChangeBttn.setOnClickListener(this);

        if(!tasks.isEmpty()){
            noTaskText.setVisibility(View.INVISIBLE);
            tasklist.setVisibility(View.VISIBLE);
        }
        
        /*
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
         */
        return root;
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.bttn_cancel_change:
                adapter.clearChanges();
                Toast.makeText(getActivity(), "Changes Not Saved", Toast.LENGTH_SHORT).show();
                saveChangeLayout.setVisibility(View.INVISIBLE);
                break;
            case R.id.bttn_save_change:
                dbManager.updateBatch(adapter.getUpdatedTasks());
                adapter.clearChanges();
                Toast.makeText(getActivity(), "Changes Saved", Toast.LENGTH_SHORT).show();
                saveChangeLayout.setVisibility(View.INVISIBLE);
                break;
            case R.id.bttn_next:
                if(currentDay!=null)
                    try {
                        //if(currentDay==null) currentDay = today;
                        Day nextDay = Day.getOffsetDay(currentDay, 1);
                        ArrayList<Task> list = dbManager.getAllTasksinDay(nextDay.getDate());
                        if(!list.isEmpty()){
                            tasklist.setVisibility(View.VISIBLE);
                            adapter.setTasks(list);
                            adapter.notifyDataSetChanged();
                            tasklist.setVisibility(View.VISIBLE);
                            noTaskText.setVisibility(View.INVISIBLE);
                        }else {
                            //Toast.makeText(activity, "No Tasks Available for Next Day", Toast.LENGTH_SHORT).show();
                            tasklist.setVisibility(View.INVISIBLE);
                            noTaskText.setVisibility(View.VISIBLE);
                        }
                        if(nextDay.getDate().equals(today.getDate()))
                            taskTitle.setText(R.string.todays_tasks);
                        else
                            taskTitle.setText(String.format("%s %s's Tasks",nextDay.getDate().split("-")[0],
                                Day.getFormattedDate(Objects.requireNonNull(Task.dateStringtoCalendar(nextDay.getDate())),"MMM")));
                        todayText.setText(String.format("%s, %s %s", nextDay.getDayName(), nextDay.getDate().split("-")[0],
                                Day.getFormattedDate(Objects.requireNonNull(Task.dateStringtoCalendar(nextDay.getDate())), "MMM")));
                        currentDay = nextDay;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                else{
                    Toast.makeText(activity, "No Tasks Available for Next Day", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bttn_prev:
                if(currentDay!=null)
                    try {
                        //if(currentDay==null) currentDay = today;
                        Day prevDay = Day.getOffsetDay(currentDay, -1);
                        ArrayList<Task> list = dbManager.getAllTasksinDay(prevDay.getDate());
                        if(!list.isEmpty()) {
                            tasklist.setVisibility(View.VISIBLE);
                            //  Toast.makeText(getActivity(), list.toString(), Toast.LENGTH_SHORT).show();
                            adapter.setTasks(list);
                            adapter.notifyDataSetChanged();
                            tasklist.setVisibility(View.VISIBLE);
                            noTaskText.setVisibility(View.INVISIBLE);
                        }else{
                            //Toast.makeText(activity, "No Tasks Available for Previous Day", Toast.LENGTH_SHORT).show();
                            tasklist.setVisibility(View.INVISIBLE);
                            noTaskText.setVisibility(View.VISIBLE);
                            tasklist.setVisibility(View.INVISIBLE);
                        }
                        if(prevDay.getDate().equals(today.getDate()))
                            taskTitle.setText(R.string.todays_tasks);
                        else
                            taskTitle.setText(String.format("%s %s's Tasks",prevDay.getDate().split("-")[0],
                                Day.getFormattedDate(Objects.requireNonNull(Task.dateStringtoCalendar(prevDay.getDate())),"MMM")));
                        todayText.setText(String.format("%s, %s %s", prevDay.getDayName(), prevDay.getDate().split("-")[0],
                                Day.getFormattedDate(Objects.requireNonNull(Task.dateStringtoCalendar(prevDay.getDate())), "MMM")));
                        currentDay = prevDay;
                        saveChangeLayout.setVisibility(View.INVISIBLE);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                else{
                    Toast.makeText(activity, "No Tasks Available for Previous Day", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    /*
    public void createDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View dialogView = inflater.inflate(R.layout.edit_delete_task_dialog,null), titleView = inflater.inflate(R.layout.edit_delete_task_dialog_title,null);
        builder.setCustomTitle(titleView);
        builder.setView(dialogView);
        MaterialButton editBttn, deleteBttn, cancelBttn;
        editBttn = dialogView.findViewById(R.id.bttn_edit);
        deleteBttn = dialogView.findViewById(R.id.bttn_delete);
        cancelBttn = dialogView.findViewById(R.id.bttn_cancel);
        editBttn.setOnClickListener(this);
        deleteBttn.setOnClickListener(this);
        cancelBttn.setOnClickListener(this);
        dialog = builder.create();
        dialog.show();
    }
     */

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}