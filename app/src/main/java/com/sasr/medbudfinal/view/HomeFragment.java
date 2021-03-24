package com.sasr.medbudfinal.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.sasr.medbudfinal.R;
import com.sasr.medbudfinal.model.MedDisplay;
import com.sasr.medbudfinal.view.history.HistoryManageActivity;
import com.sasr.medbudfinal.view.info.MyInfoActivity;
import com.sasr.medbudfinal.view.inv.InvManageActivity;
import com.sasr.medbudfinal.view.reminder.ReminderManageActivity;
import com.sasr.medbudfinal.view.sos.SosActivity;

public class HomeFragment extends Fragment {
    private RecyclerView homeRecycleDisplay;
    private HomeRecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ImageButton homeTvSos;

    private int[] imageArr={R.drawable.user_profile,R.drawable.inventory,R.drawable.reminder,R.drawable.history};

    public HomeFragment() {}

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        String[] nameArr = {
                requireActivity().getResources().getString(R.string.strUserInfo),
                requireActivity().getResources().getString(R.string.strInventory),
                requireActivity().getResources().getString(R.string.strReminders),
                requireActivity().getResources().getString(R.string.strHistory)
        };
        homeRecycleDisplay =  view.findViewById(R.id.homeRecycleDisplay);
        recyclerViewAdapter = new HomeRecyclerViewAdapter(getContext(),imageArr, nameArr);

        layoutManager = new GridLayoutManager(getContext(),2);
        homeRecycleDisplay.setLayoutManager(layoutManager);
        homeRecycleDisplay.setHasFixedSize(true);
        homeRecycleDisplay.setAdapter(recyclerViewAdapter);

        homeTvSos = view.findViewById(R.id.home_SOS_Button);
        homeTvSos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SosActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
