package com.sasr.medbudfinal.view.history;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sasr.medbudfinal.R;
import com.sasr.medbudfinal.data.history.HistoryDao;
import com.sasr.medbudfinal.model.History;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {
    private RecyclerView historyDisplayRecyler;
    private HistoryDisplayAdapter displayAdapter;
    private ArrayList<History> histories;

    public HistoryFragment() {
        histories = new HistoryDao(getContext()).getAllHistory();
        /*History history = new History();
        history.setId(1);
        history.setMedName("Crocin");
        history.setMedType(1);
        history.setQuantityTaken("1 unit");
        history.setDueTime("12:40 am");
        history.setDueDate("5th May,2020");
        history.setDueDay("Tuesday");
        history.setTakeTime("12.45 am");
        history.setTakeDate("5th May,2020");
        history.setTakeDay("Tuesday");
        histories.add(history);*/
    }


    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_history, container, false);

        historyDisplayRecyler = view.findViewById(R.id.historyDisplayRecycler);
        if (histories.isEmpty()) {
            historyDisplayRecyler.setVisibility(View.GONE);
            TextView historyDisplayNoHistoryMessage = view.findViewById(R.id.historyDisplayNoHistoryMessage);
            historyDisplayNoHistoryMessage.setVisibility(View.VISIBLE);

        } else {
            displayAdapter = new HistoryDisplayAdapter(getContext(), histories);
            historyDisplayRecyler.setAdapter(displayAdapter);
            historyDisplayRecyler.setLayoutManager(new LinearLayoutManager(getContext()));
            historyDisplayRecyler.setVisibility(View.VISIBLE);
            TextView historyDisplayNoHistoryMessage = view.findViewById(R.id.historyDisplayNoHistoryMessage);
            historyDisplayNoHistoryMessage.setVisibility(View.GONE);
        }

        return view;
    }
}
