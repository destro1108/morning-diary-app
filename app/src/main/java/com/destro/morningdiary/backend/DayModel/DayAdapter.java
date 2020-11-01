package com.destro.morningdiary.backend.DayModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.destro.morningdiary.R;
import com.destro.morningdiary.backend.TaskModel.Task;
import com.google.android.material.textview.MaterialTextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class DayAdapter extends ArrayAdapter<Day> {

    private ArrayList<Day> days;
    private Context context;

    public DayAdapter(Context context, int resoueceId, ArrayList<Day> days){
        super(context, resoueceId, days);
        this.context = context;
        this.days = days;
    }

    static class ViewHolder{
        MaterialTextView dayDate;
        MaterialTextView dayName;
        MaterialTextView nooftasks;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        Day day = days.get(position);
        if(convertView == null){
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.day_view,parent,false);
            holder.dayDate = convertView.findViewById(R.id.day_date);
            holder.dayName = convertView.findViewById(R.id.day_name);
            holder.nooftasks = convertView.findViewById(R.id.nooftasks);
            convertView.setTag(holder);
        } else holder = (ViewHolder) convertView.getTag();

        try {
            holder.dayDate.setText(Day.getFormattedDate(Objects.requireNonNull(Task.dateStringtoCalendar(day.getDate())),"dd MMM"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.dayName.setText(day.getDayName());
        holder.nooftasks.setText(String.format(Locale.ENGLISH,"Tasks : %d", day.getNooftasks()));

        return convertView;
    }
}
