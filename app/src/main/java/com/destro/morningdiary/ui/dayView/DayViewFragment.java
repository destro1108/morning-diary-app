package com.destro.morningdiary.ui.dayView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.destro.morningdiary.MainActivity;
import com.destro.morningdiary.R;
import com.destro.morningdiary.backend.DBManager;
import com.destro.morningdiary.backend.DayModel.Day;
import com.destro.morningdiary.backend.DayModel.DayAdapter;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class DayViewFragment extends Fragment{

    private DBManager dbManager;
    private ArrayList<Day> days;
    private AlertDialog dialog;
    private ProgressBar progressBar;

    private int OPERATION_FETCH_DAYS = 10;

    private ListView dayList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final MainActivity activity = (MainActivity) getActivity();
        dbManager = DBManager.getInstance(getActivity());
        View root = inflater.inflate(R.layout.fragment_day_view, container, false);
        dayList = root.findViewById(R.id.day_list);
        createProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                days = dbManager.getAllDays();
                DayAdapter adapter = new DayAdapter(getActivity(),R.layout.day_view, days);
                dayList.setAdapter(adapter);
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                });
            }
        }).start();
        dayList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                assert activity != null;
                activity.setToday(days.get(i));
                activity.setTasks(dbManager.getAllTasksinDay(days.get(i).get_id()));
                Navigation.findNavController(view).navigate(R.id.action_nav_day_view_to_nav_home);
            }
        });

        /*
        final TextView textView = root.findViewById(R.id.text_gallery);
        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
         */
        return root;
    }

    public void createProgressDialog(){
        int padding = 45;
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setPadding(padding, padding, padding, padding);
        layout.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        layout.setLayoutParams(params);

        progressBar = new ProgressBar(getActivity());
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, padding, 0);
        progressBar.setLayoutParams(params);
        params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        MaterialTextView tv = new MaterialTextView(requireActivity());
        tv.setText(R.string.loading);
        tv.setTextColor(getResources().getColor(R.color.textColor));
        tv.setTextSize(20);
        tv.setLayoutParams(params);

        layout.addView(progressBar);
        layout.addView(tv);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .setView(layout);
        dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        if(window != null){
            WindowManager.LayoutParams params1 = new WindowManager.LayoutParams();
            params1.copyFrom(window.getAttributes());
            params1.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            params1.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            window.setAttributes(params1);
        }
    }
}