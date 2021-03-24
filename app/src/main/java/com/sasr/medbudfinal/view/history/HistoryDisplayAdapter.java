package com.sasr.medbudfinal.view.history;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sasr.medbudfinal.R;
import com.sasr.medbudfinal.model.History;

import java.util.ArrayList;


public class HistoryDisplayAdapter extends RecyclerView.Adapter<HistoryDisplayAdapter.HistoryDisplayViewHolder> {
    private Context context;
    private ArrayList<History> histories;

    public HistoryDisplayAdapter(Context context, ArrayList<History> histories) {
        this.context = context;
        this.histories = histories;
    }

    @NonNull
    @Override
    public HistoryDisplayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HistoryDisplayViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.history_display_adapter,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryDisplayViewHolder holder, int position) {
        History history = histories.get(position);
        TypedArray pillTypeIcon = context.getResources().obtainTypedArray(R.array.arrMedTypesImages);
        holder.historyDisplayType.setImageResource(pillTypeIcon.getResourceId(history.getMedType(),-1));
        pillTypeIcon.recycle();
        holder.historyDisplayMedName.setText(history.getMedName());
        StringBuilder builder = new StringBuilder();
        builder.append("Due on : ")
                .append(history.getDueDate())
                .append(", ")
                .append(history.getDueDay())
                .append(" at ")
                .append(history.getDueTime());
        holder.historyDisplayDue.setText(builder.toString());
        builder.setLength(0);
        if (history.getTakeDate().equalsIgnoreCase("Medicine Skipped")) {
            builder.append(history.getTakeDate());
        }else {
            builder.append("Taken on : ")
                    .append(history.getTakeDate())
                    .append(", ")
                    .append(history.getTakeDay())
                    .append(" at ")
                    .append(history.getTakeTime());
        }
        holder.historyDisplayTaken.setText(builder.toString());
        holder.historyDisplayQuantity.setText(history.getQuantityTaken());
    }

    @Override
    public int getItemCount() {
        return histories.size();
    }

    class HistoryDisplayViewHolder extends RecyclerView.ViewHolder {
        private ImageView historyDisplayType;
        private TextView historyDisplayMedName;
        private TextView historyDisplayDue;
        private TextView historyDisplayTaken;
        private TextView historyDisplayQuantity;

        public HistoryDisplayViewHolder(@NonNull View itemView) {
            super(itemView);
            historyDisplayType = itemView.findViewById(R.id.historyDisplayType);
            historyDisplayMedName = itemView.findViewById(R.id.historyDisplayMedName);
            historyDisplayDue = itemView.findViewById(R.id.historyDisplayDue);
            historyDisplayTaken = itemView.findViewById(R.id.historyDisplayTaken);
            historyDisplayQuantity = itemView.findViewById(R.id.historyDisplayQuantity);
        }
    }
}
