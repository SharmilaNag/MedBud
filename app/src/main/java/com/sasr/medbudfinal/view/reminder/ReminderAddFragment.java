package com.sasr.medbudfinal.view.reminder;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sasr.medbudfinal.R;
import com.sasr.medbudfinal.data.MedNameSpinnerAdapter;
import com.sasr.medbudfinal.data.inv.InventoryDao;
import com.sasr.medbudfinal.data.reminder.ReminderDao;
import com.sasr.medbudfinal.interfaces.IOBackPressedCallBack;
import com.sasr.medbudfinal.model.Inventory;
import com.sasr.medbudfinal.model.MedDisplay;
import com.sasr.medbudfinal.model.Reminder;
import com.sasr.medbudfinal.model.ReminderPresenter;
import com.sasr.medbudfinal.view.DayViewCheckBox;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class ReminderAddFragment extends Fragment implements IOBackPressedCallBack {
    private static final String TAG = "ReminderAddFragment";
    public static final String KEY_ID = "REM_ID";

    private Spinner remAddSpnMedName;
    private CardView remAddCardShedule;
    private RadioGroup remAddRadioGShedule;
    private RadioButton remAddRadioBtnDaily;
    private RadioButton remAddRadioBtnWeekly;
    private LinearLayout remAddLLDaily;
    private CheckBox remAddChkDailyEveryday;
    private LinearLayout remAddLLDailyDayList;
    private CardView remAddCardDuration;
    private TextView remAddTvFrom;
    private TextView remAddTvTo;
    private EditText remAddTxtNoOfDays;
    private CardView remAddCardRoutine;
    private TableLayout remAddTableRoutine;
    private TextView remAddTvAddNewRoutine;

    private Date dateFrom;
    private Date dateTo;
    private long duration;
    private ArrayList<Inventory> inventories;
    private ArrayList<MedDisplay> pillTypeList = new ArrayList<>();
    private ArrayList<Reminder> reminderArrayList;
    private ReminderPresenter reminderPresenter;

    public ReminderAddFragment(ReminderPresenter reminderPresenter) {
        this.reminderPresenter = reminderPresenter;
    }

    public static ReminderAddFragment newInstance() {
        ReminderAddFragment fragment = new ReminderAddFragment(null);
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
        View view = inflater.inflate(R.layout.fragment_reminder_add, container, false);

        remAddSpnMedName = view.findViewById(R.id.remAddSpnMedName);
        remAddCardShedule = view.findViewById(R.id.remAddCardShedule);
        remAddRadioGShedule = view.findViewById(R.id.remAddRadioGShedule);
        remAddRadioBtnDaily = view.findViewById(R.id.remAddRadioBtnDaily);
        remAddRadioBtnWeekly = view.findViewById(R.id.remAddRadioBtnWeekly);
        remAddLLDaily = view.findViewById(R.id.remAddLLDaily);
        remAddChkDailyEveryday = view.findViewById(R.id.remAddChkDailyEveryday);
        remAddLLDailyDayList = view.findViewById(R.id.remAddLLDailyDayList);
        remAddCardDuration = view.findViewById(R.id.remAddCardDuration);
        remAddTvFrom = view.findViewById(R.id.remAddTvFrom);
        remAddTvTo = view.findViewById(R.id.remAddTvTo);
        remAddTxtNoOfDays = view.findViewById(R.id.remAddTxtNoOfDays);
        remAddCardRoutine = view.findViewById(R.id.remAddCardRoutine);
        remAddTableRoutine = view.findViewById(R.id.remAddTableRoutine);
        remAddTvAddNewRoutine = view.findViewById(R.id.remAddTvAddNewRoutine);

        getInventoryList();
        MedNameSpinnerAdapter medNameSpinnerAdapter = new MedNameSpinnerAdapter(getContext(), pillTypeList);
        remAddSpnMedName.setAdapter(medNameSpinnerAdapter);

        remAddRadioBtnDaily.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    remAddLLDaily.setVisibility(View.GONE);
                } else {
                    remAddLLDaily.setVisibility(View.VISIBLE);
                }
            }
        });

        remAddChkDailyEveryday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkAllDays();
                } else {
                    uncheckAllDays();
                }
            }
        });

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        dateFrom = calendar.getTime();
        remAddTvFrom.setText(formatter.format(dateFrom));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        dateTo = calendar.getTime();
        remAddTvTo.setText(formatter.format(dateTo));
        long diff = dateTo.getTime() - dateFrom.getTime();
        duration =  TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        remAddTxtNoOfDays.setText(String.valueOf(duration));

        remAddTvFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFromToDate('f');
            }
        });
        remAddTvTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFromToDate('t');
            }
        });

        remAddTvAddNewRoutine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTimeRow();
            }
        });

        if (reminderPresenter != null) {
            editReminder(reminderPresenter);
        } else {
            addTimeRow();
        }
        return view;
    }

    private void editReminder(ReminderPresenter reminderPresenter) {
        int selection = 0;
        for (int i = 0; i < inventories.size(); i++) {
            if (inventories.get(i).getId() == reminderPresenter.getLastReminder().getInventory().getId()) {
                selection = i;
                break;
            }
        }
        remAddSpnMedName.setSelection(selection);
        String dayMarker = reminderPresenter.getLastReminder().getDayMarker();
        if (dayMarker.equalsIgnoreCase("1111111")) {
            remAddRadioBtnDaily.setChecked(true);
        }else {
            remAddRadioBtnWeekly.setChecked(true);
            checkSelectedDays(dayMarker);
        }
        remAddTvFrom.setText(reminderPresenter.getFromDate());
        remAddTvTo.setText(reminderPresenter.getToDate());
        remAddTxtNoOfDays.setText(String.valueOf(reminderPresenter.getLastReminder().getDuration()));
        reminderArrayList = reminderPresenter.getReminders();
        for(int i = 0; i < reminderArrayList.size(); i++) {
            addTimeRow();
        }
        for (int i = 1; i < remAddTableRoutine.getChildCount(); i++) {
            TableRow row = (TableRow) remAddTableRoutine.getChildAt(i);
            Spinner desc = (Spinner) row.getChildAt(0);
            TextView time = (TextView) row.getChildAt(1);
            EditText quantity = (EditText) row.getChildAt(2);

            desc.setSelection(reminderArrayList.get(i-1).getInstruction());
            time.setText(reminderArrayList.get(i-1).getTime());
            quantity.setText(String.valueOf(reminderArrayList.get(i-1).getDosage()));
        }

    }

    private void setFromToDate(final char which) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.clear();
                        calendar.set(year, month, dayOfMonth);
                        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
                        switch (which) {
                            case 'f':
                                dateFrom = calendar.getTime();
                                remAddTvFrom.setText(formatter.format(dateFrom));
                                break;
                            case 't':
                                dateTo = calendar.getTime();
                                remAddTvTo.setText(formatter.format(dateTo));
                                break;
                        }
                        long diff = dateTo.getTime() - dateFrom.getTime();
                        duration =  TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                        //duration = Math.round((dateTo.getTime() - dateFrom.getTime()) / (double) 86400000);
                        duration = duration < 0 ? 0 : duration;
                        remAddTxtNoOfDays.setText(String.valueOf(duration));
                    }
                },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void getInventoryList() {
        inventories = new InventoryDao(getContext()).getAllInventoryEntries();
        for (Inventory inventory : inventories) {
            pillTypeList.add(new MedDisplay(
                    inventory.getPill().getName(),
                    inventory.getPill().getType()
            ));
        }
    }

    private void checkAllDays() {
        for (int i = 0; i < remAddLLDailyDayList.getChildCount(); i++) {
            DayViewCheckBox dayViewCheckBox = (DayViewCheckBox) remAddLLDailyDayList.getChildAt(i);
            dayViewCheckBox.setChecked(true);
        }
    }

    private void checkSelectedDays(String dayString) {
        for (int i = 0; i < remAddLLDailyDayList.getChildCount(); i++) {
            DayViewCheckBox dayViewCheckBox = (DayViewCheckBox) remAddLLDailyDayList.getChildAt(i);
            if (dayString.charAt(i) == '1')
                dayViewCheckBox.setChecked(true);
            else
                dayViewCheckBox.setChecked(false);
        }
    }

    private void uncheckAllDays() {
        for (int i = 0; i < remAddLLDailyDayList.getChildCount(); i++) {
            DayViewCheckBox dayViewCheckBox = (DayViewCheckBox) remAddLLDailyDayList.getChildAt(i);
            dayViewCheckBox.setChecked(false);
        }
    }

    private void addTimeRow() {
        final TableRow newTableRow = new TableRow(getContext());
        TableLayout.LayoutParams paramsRow = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT, 1);
        newTableRow.setLayoutParams(paramsRow);
        newTableRow.setWeightSum(4);
        newTableRow.setPadding(5, 5, 5, 5);

        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT, 1);

        Spinner spnDescription = new Spinner(getContext());
        spnDescription.setLayoutParams(params);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>
                (requireContext(), android.R.layout.simple_spinner_item,
                        requireContext().getResources().getStringArray(R.array.arrMedRemTimeDesc)); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spnDescription.setAdapter(spinnerArrayAdapter);

        final TextView tvTime = new TextView(getContext());
        tvTime.setLayoutParams(params);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        tvTime.setText(format.format(calendar.getTime()));
        tvTime.setTextColor(requireContext().getResources().getColor(R.color.colorBlue));
        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.clear();
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                calendar.set(Calendar.SECOND, 0);
                                SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                                tvTime.setText(format.format(calendar.getTime()));
                            }
                        },
                        Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                        Calendar.getInstance().get(Calendar.MINUTE),
                        false
                );
                timePickerDialog.show();
            }
        });

        EditText txtQuantity = new EditText(getContext());
        txtQuantity.setLayoutParams(params);
        txtQuantity.setInputType(InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_VARIATION_NORMAL);
        txtQuantity.setText(String.valueOf(1));

        ImageView imgCancel = new ImageView(getContext());
        TableRow.LayoutParams imgParam = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
        );
        imgParam.gravity = Gravity.CENTER;
        imgCancel.setLayoutParams(imgParam);
        imgCancel.setImageResource(R.drawable.round_remove_circle_outline_white_18);
        imgCancel.setBackground(requireContext().getResources().getDrawable(R.drawable.background_oval));
        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TableLayout parent = (TableLayout) newTableRow.getParent();
                parent.removeView(newTableRow);
            }
        });

        newTableRow.addView(spnDescription);
        newTableRow.addView(tvTime);
        newTableRow.addView(txtQuantity);
        newTableRow.addView(imgCancel);
        remAddTableRoutine.addView(newTableRow);
    }

    boolean verifyQuantity() {
        if (!getDaysString().contains("1")) {
            Toast.makeText(getContext(), "Please set a Schedule", Toast.LENGTH_SHORT).show();
            return false;
        }
        for (int i = 1; i < remAddTableRoutine.getChildCount(); i++) {
            TableRow row = (TableRow) remAddTableRoutine.getChildAt(i);
            EditText quantity = (EditText) row.getChildAt(2);
            if (quantity.getText().toString().trim().isEmpty() ||
                    quantity.getText().toString().trim().equalsIgnoreCase("0")) {
                Toast.makeText(getContext(), "Quantity can not be Nil or 0", Toast.LENGTH_SHORT).show();
                quantity.requestFocus();
                return false;
            }
        }
        return true;
    }

    boolean setReminder() {
        if (reminderPresenter != null) {
            cancelReminders();
        }
        ReminderDao reminderDao = new ReminderDao(getContext());
        int lastEntryId = reminderDao.getLastAddedReminderId();

        Inventory inventory = inventories.get(remAddSpnMedName.getSelectedItemPosition());
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        String fromDate = formatter.format(dateFrom);
        String toDate = formatter.format(dateTo);
        String daysString = getDaysString();
        for (int i = 1; i < remAddTableRoutine.getChildCount(); i++) {
            Reminder reminder = new Reminder();
            reminder.setInventory(inventory);
            reminder.setFromDate(fromDate);
            reminder.setToDate(toDate);
            reminder.setDayMarker(daysString);
            reminder.setDuration((int) duration);

            TableRow row = (TableRow) remAddTableRoutine.getChildAt(i);
            Spinner desc = (Spinner) row.getChildAt(0);
            reminder.setInstruction(desc.getSelectedItemPosition());
            TextView time = (TextView) row.getChildAt(1);
            reminder.setTime((String) time.getText());
            EditText quantity = (EditText) row.getChildAt(2);
            reminder.setDosage(Integer.parseInt(quantity.getText().toString().trim()));


            if (!(reminderDao.addReminderEntry(reminder) >0) ) {
                Toast.makeText(getContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                if(setAlarm (++lastEntryId,time.getText().toString(),daysString)) {
                    Log.d(TAG, "setReminder: successfully set for "+lastEntryId);
                } else {
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    reminderDao.deleteReminderEntry(lastEntryId);
                    return false;
                }
            }
        }
        getActivity().getSupportFragmentManager().popBackStack();
        return true;
    }

    private String getDaysString() {
        String daysString = "";
        if (remAddRadioBtnDaily.isChecked() || remAddChkDailyEveryday.isChecked()) {
            daysString="1111111";
            return daysString;
        }
        for (int i = 0; i < remAddLLDailyDayList.getChildCount(); i++) {
            DayViewCheckBox dayViewCheckBox = (DayViewCheckBox)remAddLLDailyDayList.getChildAt(i);
            if (dayViewCheckBox.isChecked()) {
                daysString+="1";
            }else {
                daysString+="0";
            }
        }
        return daysString;
    }

    private boolean setAlarm (int id,String time,String daysString) {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        Calendar date = Calendar.getInstance();
        //SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Calendar remFromDate = Calendar.getInstance();
        try {
            date.setTime(format.parse(time));
            remFromDate.setTime(dateFrom);
            //Log.d(TAG, "setAlarm: "+date);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);

        for (int i = 0; i < 7; i++) {
            int dayOfWeek = i+1;
            int intent_id = (10*id)+dayOfWeek;
            if (daysString.charAt(i) == '1') {
                Intent intent = new Intent(getContext(), AlarmReceiver.class);
                intent.putExtra(KEY_ID,id);
                //Log.d(TAG, "setAlarm: "+intent_id);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), intent_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                Calendar calendar = Calendar.getInstance();
                /*calendar.clear();
                calendar.set(Calendar.YEAR,remFromDate.get(Calendar.YEAR));
                calendar.set(Calendar.MONTH,remFromDate.get(Calendar.MONTH));
                calendar.set(Calendar.DAY_OF_MONTH,remFromDate.get(Calendar.DAY_OF_MONTH));
                calendar.set(Calendar.DATE,remFromDate.get(Calendar.DATE));*/
                calendar.set(Calendar.HOUR_OF_DAY, date.get(Calendar.HOUR_OF_DAY));
                calendar.set(Calendar.MINUTE, date.get(Calendar.MINUTE));
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
                Log.d(TAG, "setAlarm: "+calendar);
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

    private void cancelReminders() {
        if (reminderArrayList != null) {
            for (Reminder reminder : reminderArrayList) {
                if (cancelAlarm(reminder.getId(),reminder.getTime(),reminder.getDayMarker())) {
                    int i = new ReminderDao(getContext()).deleteReminderEntry(reminder.getId());
//                    Log.d(TAG, "cancelReminders: "+i+" "+reminder.getId());
                } else {
                    Toast.makeText(getContext(), "Unable to cancel Reminders", Toast.LENGTH_SHORT).show();
                }
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
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);

        for (int i = 0; i < 7; i++) {
            int dayOfWeek = i + 1;
            int intent_id = (10 * id) + dayOfWeek;
            if (daysString.charAt(i) == '1') {
                Intent intent = new Intent(getContext(), AlarmReceiver.class);
                intent.putExtra(KEY_ID, id);
                //Log.d(TAG, "setAlarm: "+intent_id);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), intent_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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
    public boolean onBackPressed() {
        ReminderManageActivity reminderManageActivity = (ReminderManageActivity)requireActivity();
        FloatingActionButton fab = reminderManageActivity.findViewById(R.id.fab);
        fab.setImageResource(R.drawable.alarm_add_white_48);
        fab.setTag(getString(R.string.strAdd));
        return false;
    }
}
