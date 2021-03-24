package com.sasr.medbudfinal.view.inv;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sasr.medbudfinal.R;
import com.sasr.medbudfinal.data.MedNameSpinnerAdapter;
import com.sasr.medbudfinal.data.inv.InventoryDao;
import com.sasr.medbudfinal.interfaces.InvEditCallback;
import com.sasr.medbudfinal.model.Inventory;
import com.sasr.medbudfinal.model.MedDisplay;
import com.sasr.medbudfinal.model.Pill;

import java.util.ArrayList;

public class AddEditInventoryHandler {
    private static final String TAG = "AddEditInventoryHandler";
    private Context context;
    private ArrayList<Inventory> pillInventoryList ;
    private ArrayList <MedDisplay> pillTypeList = new ArrayList<>();

    private EditText invPopupTxtMedName;
    private Spinner invPopupSpnMedType;
    private EditText invPopupTxtMedQuantity;
    private Spinner invPopupSpnQuantityUnit;
    private EditText invPopupTxtMedWarningQuantity;
    private Button InvPopupBtnCancel;
    private Button InvPopupBtnAdd;

    private InventoryDao inventoryDao;

    public AddEditInventoryHandler (Context context,ArrayList<Inventory> pillInventoryList) {
        this.context = context;
        inventoryDao = new InventoryDao(context);
        this.pillInventoryList = pillInventoryList;
    }

    ArrayList<Inventory> addEditInventoryItem (final Inventory inventory, final int position, final InvEditCallback callback) {
        initPillTypeList ();
        View alertView = LayoutInflater.from(context).inflate(R.layout.inv_add_medicine_popup,null);
        TextView invPopupTvTitle = alertView.findViewById(R.id.invPopupTvTitle);
        invPopupTxtMedName = alertView.findViewById(R.id.invPopupTxtMedName);
        invPopupSpnMedType = alertView.findViewById(R.id.invPopupSpnMedType);
        invPopupTxtMedQuantity = alertView.findViewById(R.id.invPopupTxtMedQuantity);
        invPopupSpnQuantityUnit = alertView.findViewById(R.id.invPopupSpnQuantityUnit);
        invPopupTxtMedWarningQuantity = alertView.findViewById(R.id.invPopupTxtMedWarningQuantity);
        InvPopupBtnCancel = alertView.findViewById(R.id.InvPopupBtnCancel);
        InvPopupBtnAdd = alertView.findViewById(R.id.InvPopupBtnAdd);

        MedNameSpinnerAdapter medNameSpinnerAdapter = new MedNameSpinnerAdapter(context,pillTypeList);
        invPopupSpnMedType.setAdapter(medNameSpinnerAdapter);

        if (inventory != null) {
            invPopupTvTitle.setText(context.getResources().getString(R.string.strInvPopupHeaderEdit));
            InvPopupBtnAdd.setText(context.getResources().getString(R.string.strEdit));
            invPopupTxtMedName.setText(inventory.getPill().getName());
            invPopupSpnMedType.setSelection(inventory.getPill().getType());
            invPopupTxtMedQuantity.setText(String.valueOf(inventory.getQuantity()));
            invPopupSpnQuantityUnit.setSelection(inventory.getQuantityUnit());
            invPopupTxtMedWarningQuantity.setText(String.valueOf(inventory.getWarningQuantity()));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(alertView);
        final AlertDialog dialog = builder.create();
        dialog.show();

        InvPopupBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inventory != null) {
                    if (verifyAndSaveMedicineData(true, position,inventory)) {
                        dialog.dismiss();
                        if (callback != null)
                            callback.editCallBack();
                    }
                } else {
                    if (verifyAndSaveMedicineData(false, position,null)) {
                        dialog.dismiss();
                        if (callback != null)
                            callback.editCallBack();
                    }
                }
            }
        });

        InvPopupBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        return pillInventoryList;
    }

    private boolean verifyAndSaveMedicineData (boolean edit,int position,Inventory editInventory) {
        String medName = invPopupTxtMedName.getText().toString().trim();
        if (medName.isEmpty()) {
            Toast.makeText(context, "Please enter Medicine Name!", Toast.LENGTH_SHORT).show();
            invPopupTxtMedName.requestFocus();
            return false;
        }
        if (!edit) {
            for (Inventory inventory : pillInventoryList) {
                if (inventory.getPill().getName().equalsIgnoreCase(medName)) {
                    Toast.makeText(context, "Medicine already added in Inventory!", Toast.LENGTH_SHORT).show();
                    invPopupTxtMedName.requestFocus();
                    return false;
                }
            }
        }
        String medQuantity = invPopupTxtMedQuantity.getText().toString().trim();
        if (medQuantity.isEmpty()) {
            Toast.makeText(context, "Quantity can not be empty!", Toast.LENGTH_SHORT).show();
            invPopupTxtMedQuantity.requestFocus();
            return false;
        } else {
            int quantity = Integer.parseInt(medQuantity);
            if (quantity <= 0) {
                Toast.makeText(context, "Quantity can not be 0 or less!", Toast.LENGTH_SHORT).show();
                invPopupTxtMedQuantity.requestFocus();
                return false;
            }
        }
        String medQuantityWarning = invPopupTxtMedWarningQuantity.getText().toString().trim();
        if (medQuantityWarning.isEmpty() || Integer.parseInt(medQuantityWarning) <= 0) {
            medQuantityWarning = "5";
        }
        Pill pill;
        Inventory inventory;
        if (edit) {
            inventory = editInventory;
            pill = editInventory.getPill();
        } else {
            pill = new Pill();
            inventory = new Inventory();
        }
        pill.setName(medName);
        pill.setType(invPopupSpnMedType.getSelectedItemPosition());
        inventory.setPill(pill);
        inventory.setQuantity(Integer.parseInt(medQuantity));
        inventory.setQuantityUnit(invPopupSpnQuantityUnit.getSelectedItemPosition());
        inventory.setWarningQuantity(Integer.parseInt(medQuantityWarning));
        if (edit) {
            int updateFlag = inventoryDao.updateInventoryEntry(inventory);
            if ( updateFlag <= 0) {
                Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "verifyAndSaveMedicineData: "+updateFlag);
                pillInventoryList.set(position, inventory);
            }
        }
        else {
            inventoryDao.addInventoryEntry(inventory);
            inventory = inventoryDao.getSingleInventoryEntryByName(inventory);
            pillInventoryList.add(inventory);
        }
        //recyclerViewAdapter.notifyDataSetChanged();
        return true;

    }

    private void initPillTypeList () {
        String[] pillType = context.getResources().getStringArray(R.array.arrMedTypes);
        //TypedArray pillTypeIcon = context.getResources().obtainTypedArray(R.array.arrMedTypesImages);

        for (int i =0; i < pillType.length; i++)
            pillTypeList.add(new MedDisplay(pillType[i],i));
        //pillTypeIcon.recycle();
    }
}
