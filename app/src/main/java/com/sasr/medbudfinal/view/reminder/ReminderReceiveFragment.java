package com.sasr.medbudfinal.view.reminder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sasr.medbudfinal.R;
import com.sasr.medbudfinal.data.history.HistoryDao;
import com.sasr.medbudfinal.data.inv.InventoryDao;
import com.sasr.medbudfinal.data.reminder.ReminderDao;
import com.sasr.medbudfinal.model.History;
import com.sasr.medbudfinal.model.Inventory;
import com.sasr.medbudfinal.model.Reminder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ReminderReceiveFragment extends Fragment {

    private TextView remReceiveTvMedTIme;
    private ImageView remReceiveImgMedType;
    private TextView remReceiveTvMedName;
    private TextView remReceiveTvDosage;
    private ImageView remReceiveImgIgnoreMed;
    private ImageView remReceiveImgTakeMed;
    private Reminder reminder;
    private int remId;

    public ReminderReceiveFragment() {

    }


    public static ReminderReceiveFragment newInstance(int id) {
        ReminderReceiveFragment fragment = new ReminderReceiveFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ReminderAddFragment.KEY_ID,id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_reminder_receive, container, false);
        remReceiveTvMedTIme = view.findViewById(R.id.remReceiveTvMedTIme);
        remReceiveImgMedType = view.findViewById(R.id.remReceiveImgMedType);
        remReceiveTvMedName = view.findViewById(R.id.remReceiveTvMedName);
        remReceiveTvDosage = view.findViewById(R.id.remReceiveTvDosage);
        remReceiveImgIgnoreMed = view.findViewById(R.id.remReceiveImgIgnoreMed);
        remReceiveImgTakeMed = view.findViewById(R.id.remReceiveImgTakeMed);

        assert getArguments() != null;
        remId = getArguments().getInt(ReminderAddFragment.KEY_ID);
        reminder = new ReminderDao(getContext()).getReminderEntry(remId);
        remReceiveTvMedTIme.setText(reminder.getTime());

        TypedArray pillTypeIcon = requireContext().getResources().obtainTypedArray(R.array.arrMedTypesImages);
        remReceiveImgMedType.setImageResource(pillTypeIcon.getResourceId(reminder.getInventory().getPill().getType(),-1));
        pillTypeIcon.recycle();

        remReceiveTvMedName.setText(reminder.getInventory().getPill().getName());
        String dosage = reminder.getDosage()+" "
                +requireContext().getResources().getStringArray(R.array.arrMedQuantityType)[reminder.getInventory().getQuantityUnit()]
                +", "+requireContext().getResources().getStringArray(R.array.arrMedRemTimeDesc)[reminder.getInstruction()];
        remReceiveTvDosage.setText(dosage);

        remReceiveImgIgnoreMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ignoreMed();
            }
        });

        remReceiveImgTakeMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeMed();
            }
        });
        return view;
    }

    private void takeMed() {
        final Inventory inventory = reminder.getInventory();
        final int quantity = Math.max((inventory.getQuantity() - reminder.getDosage()), 0);
        if (quantity <= inventory.getWarningQuantity()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Warning!!")
                    .setMessage("You have " + quantity
                            + " "+getActivity().getResources()
                            .getStringArray(R.array.arrMedQuantityType)[inventory.getQuantityUnit()]
                            +" of '"+inventory.getPill().getName()+"' left. Please refill your inventory!")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (quantity <= 0 && inventory.getQuantity() > 0) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Warning!!")
                                        .setMessage("Due to having low amount of '"
                                                +inventory.getPill().getName()+"' left in your inventory, You can not take the full amount of dosage right now.")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                reminder.setDosage(inventory.getQuantity());
                                                updateHistory(inventory,quantity);
                                                requireActivity().finish();
                                            }
                                        })
                                        .create()
                                        .show();

                            }else if (quantity <= 0 && inventory.getQuantity() <= 0) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Warning!!")
                                        .setMessage("You do not have any amount of '"
                                                +inventory.getPill().getName()+"' left in your inventory, you would be skipping it for now.")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                ignoreMed2();
                                                requireActivity().finish();
                                            }
                                        })
                                        .create()
                                        .show();
                            }else {
                                updateHistory(inventory,quantity);
                                requireActivity().finish();
                            }
                        }
                    })
                    .create()
                    .show();
        }else {
            updateHistory(inventory, quantity);
            requireActivity().finish();
        }

    }

    private void updateHistory (Inventory inventory,int quantity) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        String takeDate = formatter.format(calendar.getTime());
        formatter = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String takeTime = formatter.format(calendar.getTime());
        String takeDay = getContext().getResources().getStringArray(R.array.arrDayName)[calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH)];
        History history = new History();
        history.setMedName(reminder.getInventory().getPill().getName());
        history.setMedType(reminder.getInventory().getPill().getType());
        history.setQuantityTaken(reminder.getDosage() + " "
                + getContext().getResources().getStringArray(R.array.arrMedQuantityType)[reminder.getInventory().getQuantityUnit()]);
        history.setDueDate(getActivity().getIntent().getStringExtra(AlarmReceiver.KEY_DUE_DATE));
        history.setDueTime(getActivity().getIntent().getStringExtra(AlarmReceiver.KEY_DUE_TIME));
        history.setDueDay(getActivity().getIntent().getStringExtra(AlarmReceiver.KEY_DUE_DAY));
        history.setTakeDate(takeDate);
        history.setTakeTime(takeTime);
        history.setTakeDay(takeDay);

        new HistoryDao(getContext()).inserHistoryEntry(history);

        inventory.setQuantity(quantity);
        new InventoryDao(getContext()).updateInventoryEntry(inventory);
    }

    private void ignoreMed() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("Warning!");
        dialog.setMessage("Are you sure you want to skip this medicine?");
        dialog.setPositiveButton("Skip", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                History history = new History();
                history.setMedName(reminder.getInventory().getPill().getName());
                history.setMedType(reminder.getInventory().getPill().getType());
                history.setQuantityTaken("");
                history.setDueDate(getActivity().getIntent().getStringExtra(AlarmReceiver.KEY_DUE_DATE));
                history.setDueTime(getActivity().getIntent().getStringExtra(AlarmReceiver.KEY_DUE_TIME));
                history.setDueDay(getActivity().getIntent().getStringExtra(AlarmReceiver.KEY_DUE_DAY));
                history.setTakeDate("Medicine Skipped");
                history.setTakeTime("");
                history.setTakeDay("");
                new HistoryDao(getContext()).inserHistoryEntry(history);
                requireActivity().finish();
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void ignoreMed2() {
        History history = new History();
        history.setMedName(reminder.getInventory().getPill().getName());
        history.setMedType(reminder.getInventory().getPill().getType());
        history.setQuantityTaken("");
        history.setDueDate(getActivity().getIntent().getStringExtra(AlarmReceiver.KEY_DUE_DATE));
        history.setDueTime(getActivity().getIntent().getStringExtra(AlarmReceiver.KEY_DUE_TIME));
        history.setDueDay(getActivity().getIntent().getStringExtra(AlarmReceiver.KEY_DUE_DAY));
        history.setTakeDate("Medicine Skipped");
        history.setTakeTime("");
        history.setTakeDay("");
        new HistoryDao(getContext()).inserHistoryEntry(history);
        requireActivity().finish();

    }
}
