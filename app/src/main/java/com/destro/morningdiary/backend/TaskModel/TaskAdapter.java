package com.destro.morningdiary.backend.TaskModel;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.destro.morningdiary.R;
import com.destro.morningdiary.backend.DBManager;
import com.destro.morningdiary.ui.home.HomeFragment;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private ArrayList<Task> tasks;
    private Context cx;
    private ArrayList<Task> updatedTasks;
    private HomeFragment.SetChangeVisible changeVisible;
    private DBManager dbManager;

    public TaskAdapter(Context cx, ArrayList<Task> tasks, HomeFragment.SetChangeVisible changeVisible){
        this.cx = cx;
        this.tasks = tasks;
        this.changeVisible = changeVisible;
        updatedTasks = new ArrayList<>();
        dbManager = DBManager.getInstance(cx);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        MaterialTextView title;
        MaterialTextView desc;
        MaterialTextView time;
        MaterialCheckBox done;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tasktitle);
            desc = itemView.findViewById(R.id.desc);
            time = itemView.findViewById(R.id.tasktime);
            done = itemView.findViewById(R.id.done);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Task task = tasks.get(position);
        holder.title.setText(task.getTitle());
        holder.desc.setText(task.getDesc());
        holder.time.setText(task.getTime());
        //holder.time.setText("Time");
        holder.done.setChecked(task.isDone());
        holder.done.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, final boolean b) {
                task.setDone(b);
                dbManager.updateTaskDone(task.get_id(),b);
                new Handler(cx.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        dbManager.updateTaskDone(task.get_id(),b);
                    }
                });
                //if(updatedTasks.contains(task)) updatedTasks.remove(task);
                //updatedTasks.add(task);
                //changeVisible.onChangeVisibility();

            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks.clear();
        this.tasks.addAll(tasks);
    }

    public void deleteItem(int position){
        dbManager.deleteTask(tasks.remove(position).get_id());
    }

    public Task getItem(int position){
        return tasks.get(position);
    }

    public ArrayList<Task> getUpdatedTasks(){
        return updatedTasks;
    }

    public void clearChanges(){
        updatedTasks.clear();
    }
}

