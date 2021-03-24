package com.sasr.medbudfinal.view.reminder;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sasr.medbudfinal.R;
import com.sasr.medbudfinal.data.reminder.ReminderDao;
import com.sasr.medbudfinal.data.reminder.ReminderPresenterDao;
import com.sasr.medbudfinal.model.ReminderPresenter;

import java.util.ArrayList;


public class ReminderDisplayFragment extends Fragment {

    private RecyclerView remDisplayRecycler;

    public ReminderDisplayFragment() {

    }


    public static ReminderDisplayFragment newInstance() {
        ReminderDisplayFragment fragment = new ReminderDisplayFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_reminder_display, container, false);
        remDisplayRecycler = view.findViewById(R.id.remDisplayRecycler);
        ArrayList<ReminderPresenter> reminderPresenterArrayList = new ReminderPresenterDao(getContext()).getOrganizedReminders();
        if(!reminderPresenterArrayList.isEmpty()){
            ReminderDisplayAdapter reminderDisplayAdapter = new ReminderDisplayAdapter(getContext(),reminderPresenterArrayList);
            remDisplayRecycler.setVisibility(View.VISIBLE);
            remDisplayRecycler.setAdapter(reminderDisplayAdapter);
            remDisplayRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
            TextView remDisplayNoRemMessage = view.findViewById(R.id.remDisplayNoRemMessage);
            remDisplayNoRemMessage.setVisibility(View.GONE);
        } else {
            remDisplayRecycler.setVisibility(View.GONE);
            TextView remDisplayNoRemMessage = view.findViewById(R.id.remDisplayNoRemMessage);
            remDisplayNoRemMessage.setVisibility(View.VISIBLE);
        }
        return view;
    }
}
