package com.sasr.medbudfinal.view.reminder;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.internal.ICancelToken;
import com.sasr.medbudfinal.R;
import com.sasr.medbudfinal.data.reminder.ReminderPresenterDao;
import com.sasr.medbudfinal.interfaces.RemEditCallBack;
import com.sasr.medbudfinal.model.Reminder;
import com.sasr.medbudfinal.model.ReminderPresenter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ReminderDisplayAdapter extends RecyclerView.Adapter<ReminderDisplayAdapter.ReminderDisplayViewHolder> {
    private static final String TAG = "ReminderDisplayAdapter";

    private Context context;
    private ArrayList<ReminderPresenter> reminderArrayList;

    public ReminderDisplayAdapter (Context context,ArrayList<ReminderPresenter> reminderArrayList) {
        this.context = context;
        this.reminderArrayList = reminderArrayList;
    }

    @NonNull
    @Override
    public ReminderDisplayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReminderDisplayViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reminder_display_adapter,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderDisplayViewHolder holder, int position) {
        ReminderPresenter reminderPresenter = reminderArrayList.get(position);
        TypedArray pillTypeIcon = context.getResources().obtainTypedArray(R.array.arrMedTypesImages);
        holder.remDisplayAdapterImgType.setImageResource(pillTypeIcon.getResourceId(
                reminderPresenter.getLastReminder().getInventory().getPill().getType(),-1)
        );
        pillTypeIcon.recycle();
        holder.remDisplayAdapterTvMedName.setText(reminderPresenter.getMedName());
        holder.remDisplayAdapterTvDuration.setText("From "+reminderPresenter.getFromDate()+" to "+reminderPresenter.getToDate());
        holder.remDisplayAdapterTvDays.setText(getDaysString(reminderPresenter.getOnDays()));

        ArrayList<Reminder> reminders = reminderPresenter.getReminders();
        for (final Reminder reminder : reminders) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            TextView desc = new TextView(context);
            desc.setLayoutParams(params);
            String strDesc = context.getResources().getStringArray(R.array.arrMedRemTimeDesc)[reminder.getInstruction()]+", ";
            desc.setText(strDesc);
            TextView time = new TextView(context);
            time.setLayoutParams(params);
            time.setText(reminder.getTime());
            final Switch onOff = new Switch(context);
            onOff.setLayoutParams(params);
            onOff.setChecked(true);
            //onOff.setTag(String.valueOf(reminder.getId()));
            onOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (onOff.isChecked()) {
                        setAlarm(reminder.getId(),reminder.getTime(),reminder.getDayMarker());
                    } else {
                        cancelAlarm(reminder.getId(),reminder.getTime(),reminder.getDayMarker());
                    }
                }
            });
            LinearLayout.LayoutParams paramsContainer = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            //paramsContainer.setMargins(10,10,10,10);
            LinearLayout container = new LinearLayout(context);
            container.setLayoutParams(paramsContainer);
            container.setOrientation(LinearLayout.HORIZONTAL);
            container.addView(desc);
            container.addView(time);
            container.addView(onOff);

            holder.remDisplayAdapterRemTimeContainer.addView(container);
        }
    }

    private String getDaysString (String inpDayString) {
        String[] dayName = context.getResources().getStringArray(R.array.arrDayName);
        String outDayString = "";
        for (int i = 0; i < dayName.length; i++) {
            if (inpDayString.charAt(i) == '1') {
                outDayString+=dayName[i]+", ";
            }
        }
        outDayString = outDayString.substring(0,outDayString.length()-1);
        return outDayString;
    }

    private boolean setAlarm (int id,String time,String daysString) {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        Calendar date = Calendar.getInstance();
        try {
            date.setTime(format.parse(time));
            //Log.d(TAG, "setAlarm: "+date);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        for (int i = 0; i < 7; i++) {
            int dayOfWeek = i+1;
            int intent_id = (10*id)+dayOfWeek;
            if (daysString.charAt(i) == '1') {
                Intent intent = new Intent(context, AlarmReceiver.class);
                intent.putExtra(ReminderAddFragment.KEY_ID,id);
                //Log.d(TAG, "setAlarm: "+intent_id);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, intent_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, date.get(Calendar.HOUR_OF_DAY));
                calendar.set(Calendar.MINUTE, date.get(Calendar.MINUTE));
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
                //Log.d(TAG, "setAlarm: "+calendar);
                /** Converting the date and time in to milliseconds elapsed since epoch */
                long alarm_time = calendar.getTimeInMillis();
                //Log.d(TAG, "setAlarm: "+alarm_time);
                if (calendar.before(Calendar.getInstance()))
                    alarm_time += AlarmManager.INTERVAL_DAY * 7;

                assert alarmManager != null;
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarm_time,
                        AlarmManager.INTERVAL_DAY * 7, pendingIntent);
            }
        }
        /*boolean alarmUp = (PendingIntent.getBroadcast(getContext(), id,
                new Intent(getContext(), AlarmReceiver.class),
                PendingIntent.FLAG_NO_CREATE) != null);

        if (alarmUp)
        {
            Log.d("myTag", "Alarm is already active");
        }*/
        return true;
    }

    private boolean cancelAlarm (int id,String time,String daysString) {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        Calendar date = Calendar.getInstance();
        try {
            date.setTime(format.parse(time));
            //Log.d(TAG, "setAlarm: "+date);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        for (int i = 0; i < 7; i++) {
            int dayOfWeek = i + 1;
            int intent_id = (10 * id) + dayOfWeek;
            if (daysString.charAt(i) == '1') {
                Intent intent = new Intent(context, AlarmReceiver.class);
                intent.putExtra(ReminderAddFragment.KEY_ID, id);
                //Log.d(TAG, "setAlarm: "+intent_id);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, intent_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, date.get(Calendar.HOUR_OF_DAY));
                calendar.set(Calendar.MINUTE, date.get(Calendar.MINUTE));
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
                assert alarmManager != null;
                alarmManager.cancel(pendingIntent);
                pendingIntent.cancel();
            }
        }
        return true;
    }

    @Override
    public int getItemCount() {
        return reminderArrayList.size();
    }

    class ReminderDisplayViewHolder extends RecyclerView.ViewHolder {
        TextView remDisplayAdapterTvMedName;
        ImageView remDisplayAdapterImgType;
        TextView remDisplayAdapterTvDuration;
        TextView remDisplayAdapterTvDays;
        LinearLayout remDisplayAdapterRemTimeContainer;
        ImageView remDisplayAdapterImgEdit;
        ImageView remDisplayAdapterImgDelete;

        public ReminderDisplayViewHolder(@NonNull View itemView) {
            super(itemView);
            remDisplayAdapterTvMedName = itemView.findViewById(R.id.remDisplayAdapterTvMedName);
            remDisplayAdapterTvDuration = itemView.findViewById(R.id.remDisplayAdapterTvDuration);
            remDisplayAdapterTvDays = itemView.findViewById(R.id.remDisplayAdapterTvDays);
            remDisplayAdapterRemTimeContainer = itemView.findViewById(R.id.remDisplayAdapterRemTimeContainer);
            remDisplayAdapterImgEdit = itemView.findViewById(R.id.remDisplayAdapterImgEdit);
            remDisplayAdapterImgDelete = itemView.findViewById(R.id.remDisplayAdapterImgDelete);
            remDisplayAdapterImgType = itemView.findViewById(R.id.remDisplayAdapterImgType);

            remDisplayAdapterImgEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editReminderData(reminderArrayList.get(getAdapterPosition()));
                }
            });

            remDisplayAdapterImgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteReminderData();
                }
            });
        }

        private void editReminderData (ReminderPresenter reminderPresenter) {
            RemEditCallBack remEditCallBack = (RemEditCallBack) context;
            remEditCallBack.editReminder(reminderPresenter);
        }

        private void deleteReminderData () {
            final ReminderPresenter reminderPresenter = reminderArrayList.get(getAdapterPosition());
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Are you sure you want to delete this reminder entirely?");
            builder.setTitle("Warning!");
            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    for (Reminder reminder : reminderPresenter.getReminders()) {
                        cancelAlarm(reminder.getId(),reminder.getTime(),reminder.getDayMarker());
                    }
                    new ReminderPresenterDao(context).deleteAllReminders(reminderPresenter);
                    reminderArrayList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create();
            builder.show();
        }
    }
}
