package com.sasr.medbudfinal.view;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sasr.medbudfinal.R;
import com.sasr.medbudfinal.view.history.HistoryManageActivity;
import com.sasr.medbudfinal.view.info.MyInfoActivity;
import com.sasr.medbudfinal.view.inv.InvManageActivity;
import com.sasr.medbudfinal.view.reminder.ReminderManageActivity;

public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewAdapter.ViewHolder> {

    int[] imageArr;
    String[] nameArr;
    Context context;

    public HomeRecyclerViewAdapter(Context context,int[] imageArr, String[] nameArr) {
        this.context = context;
        this.imageArr = imageArr;
        this.nameArr = nameArr;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_view_home,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imageView.setImageResource(imageArr[position]);
        holder.textView.setText(nameArr[position]);
    }

    @Override
    public int getItemCount() {
        return imageArr.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openActivity(nameArr[getAdapterPosition()]);
                }
            });
        }
    }

    private void openActivity(String buttonSelected) {
        if(buttonSelected.equalsIgnoreCase(context.getResources().getString(R.string.strUserInfo))){
            context.startActivity(new Intent(context, MyInfoActivity.class));
        }
        else if(buttonSelected.equalsIgnoreCase(context.getResources().getString(R.string.strReminders))){
            context.startActivity(new Intent(context, ReminderManageActivity.class));
        }
        else if(buttonSelected.equalsIgnoreCase(context.getResources().getString(R.string.strInventory))){
            context.startActivity(new Intent(context, InvManageActivity.class));
        }
        else if(buttonSelected.equalsIgnoreCase(context.getResources().getString(R.string.strHistory))){
            context.startActivity(new Intent(context, HistoryManageActivity.class));
        }
    }
}
