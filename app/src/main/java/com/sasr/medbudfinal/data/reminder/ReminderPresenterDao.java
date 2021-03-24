package com.sasr.medbudfinal.data.reminder;

import android.content.Context;
import android.util.Log;

import com.sasr.medbudfinal.model.Reminder;
import com.sasr.medbudfinal.model.ReminderPresenter;

import java.util.ArrayList;

public class ReminderPresenterDao {
    private static final String TAG = "ReminderPresenterDao";
    private Context context;

    public ReminderPresenterDao (Context context) {
        this.context = context;
    }

    public ArrayList<ReminderPresenter> getOrganizedReminders () {
        ArrayList<ReminderPresenter> reminderPresenterArrayList = new ArrayList<>();
        ArrayList <Reminder> reminders = new ReminderDao(context).getAllReminderEntries();
        //Log.d(TAG, "getOrganizedReminders: "+reminders.size());
        if (!reminders.isEmpty()) {
            ReminderPresenter reminderPresenter =  null;
            for (Reminder reminder : reminders) {
                if (reminderPresenter == null) {
                    reminderPresenter =  new ReminderPresenter();
                    reminderPresenter.setMedName(reminder.getInventory().getPill().getName());
                    reminderPresenter.setFromDate(reminder.getFromDate());
                    reminderPresenter.setToDate(reminder.getToDate());
                    reminderPresenter.setOnDays(reminder.getDayMarker());
                    reminderPresenter.setLastReminder(reminder);
                } else {
                    if (
                            reminderPresenter.getLastReminder().getInventory().getId() == reminder.getInventory().getId()
                                    &&
                                    reminderPresenter.getLastReminder().getDayMarker().equalsIgnoreCase(reminder.getDayMarker())
                            && reminderPresenter.getLastReminder().getFromDate().equalsIgnoreCase(reminder.getFromDate())
                            && reminderPresenter.getLastReminder().getToDate().equalsIgnoreCase(reminder.getToDate())
                    ) {
                        reminderPresenter.setLastReminder(reminder);

                    } else {
                        reminderPresenterArrayList.add(reminderPresenter);
                        reminderPresenter =  new ReminderPresenter();
                        reminderPresenter.setMedName(reminder.getInventory().getPill().getName());
                        reminderPresenter.setFromDate(reminder.getFromDate());
                        reminderPresenter.setToDate(reminder.getToDate());
                        reminderPresenter.setOnDays(reminder.getDayMarker());
                        reminderPresenter.setLastReminder(reminder);
                    }
                }
            }
            reminderPresenterArrayList.add(reminderPresenter);
        }
        //Log.d(TAG, "getOrganizedReminders: "+reminderPresenterArrayList.size());
        return reminderPresenterArrayList;
    }

    public void deleteAllReminders (ReminderPresenter reminderPresenter) {
        ArrayList<Reminder> reminders = reminderPresenter.getReminders();
        ReminderDao reminderDao = new ReminderDao(context);
        for (Reminder reminder : reminders) {
            reminderDao.deleteReminderEntry(reminder.getId());
        }
    }
}
