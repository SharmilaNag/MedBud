package com.sasr.medbudfinal.view.reminder;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.sasr.medbudfinal.R;
import com.sasr.medbudfinal.controller.AppController;
import com.sasr.medbudfinal.data.reminder.ReminderDao;
import com.sasr.medbudfinal.data.reminder.ReminderPresenterDao;
import com.sasr.medbudfinal.model.Reminder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";

    private NotificationManagerCompat notificationManager;
    private final String NOTI_TITLE = "Time to take your medicine";
    private Reminder reminder;
    public static final String KEY_DUE_DATE = "due_date";
    public static final String KEY_DUE_TIME = "due_time";
    public static final String KEY_DUE_DAY = "due_day";
    Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Log.d(TAG, "onReceive: here");
        if (intent != null) {
            reminder = new ReminderDao(context).getReminderEntry(intent.getIntExtra(ReminderAddFragment.KEY_ID,-1));
            if (reminder != null) {
                Log.d(TAG, "onReceive: "+reminder);
                SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
                Calendar calendar = Calendar.getInstance();
                Calendar remCalendarTo = Calendar.getInstance();
                Calendar remCalendarFrom = Calendar.getInstance();
                //remCalendar.clear();
                try {
                    remCalendarFrom.setTime(formatter.parse(reminder.getFromDate()));
                    remCalendarTo.setTime(formatter.parse(reminder.getToDate()));
                    remCalendarTo.add(Calendar.DAY_OF_YEAR,1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (calendar.before(remCalendarFrom))
                    return;
                if (calendar.after(remCalendarTo)) {
                    cancelAlarm(reminder.getId(),reminder.getTime(),reminder.getDayMarker());
                    new ReminderDao(context).deleteReminderEntry(reminder.getId());
                    return;
                }
                String dueDate = formatter.format(calendar.getTime());
                formatter = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                //String dueTime = formatter.format(calendar.getTime());
                String dueTime = reminder.getTime();
                String dueDay = context.getResources().getStringArray(R.array.arrDayName)[calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH)];
                String contextText = "Take "+reminder.getInventory().getPill().getName()+", "
                        +reminder.getDosage()+" "
                        +context.getResources().getStringArray(R.array.arrMedQuantityType)[reminder.getInventory().getQuantityUnit()]
                        +", "+context.getResources().getStringArray(R.array.arrMedRemTimeDesc)[reminder.getInstruction()];
                Intent activityIntent = new Intent(context,ReminderRecieveActivity.class);
                activityIntent.putExtra(ReminderAddFragment.KEY_ID,reminder.getId());
                activityIntent.putExtra(KEY_DUE_DATE,dueDate);
                activityIntent.putExtra(KEY_DUE_TIME,dueTime);
                activityIntent.putExtra(KEY_DUE_DAY,dueDay);
                activityIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                PendingIntent contentIntent = PendingIntent.getActivity(context,reminder.getId(),activityIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                //Log.d(TAG, "onReceive: "+intent.getExtras());
                Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                notificationManager = NotificationManagerCompat.from(context);
                Notification notification = new NotificationCompat.Builder(context, AppController.CHANNEL_1_ID)
                        .setSmallIcon(R.mipmap.ic_med_bud_round)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_med_bud_round))
                        .setContentTitle(NOTI_TITLE)
                        .setSound(soundUri, AudioManager.STREAM_NOTIFICATION)
                        .setContentText(contextText)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setContentIntent(contentIntent)
                        .build();
                notification.flags |= Notification.FLAG_AUTO_CANCEL;
                notificationManager.notify(reminder.getId(), notification);
            }
        }
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
}
