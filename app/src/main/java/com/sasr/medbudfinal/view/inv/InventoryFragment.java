package com.sasr.medbudfinal.view.inv;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sasr.medbudfinal.R;
import com.sasr.medbudfinal.data.inv.InventoryDao;
import com.sasr.medbudfinal.data.userinfo.UserInfoDao;
import com.sasr.medbudfinal.interfaces.InvEditCallback;
import com.sasr.medbudfinal.model.Inventory;
import com.sasr.medbudfinal.model.MedDisplay;
import com.sasr.medbudfinal.model.UserInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class InventoryFragment extends Fragment {
    private static final String TAG = "InventoryFragment";

    private RecyclerView invRecycleDisplay;
    InventoryRecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<Inventory> pillInventoryList = new ArrayList<>();
    private ArrayList <MedDisplay> pillTypeList = new ArrayList<>();
    private InventoryDao inventoryDao;
    private File photoFile = null;
    private Uri photoURI = null;
    private String inventorySummary = "";

    public InventoryFragment() {

    }


    public static InventoryFragment newInstance() {
        InventoryFragment fragment = new InventoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inventoryDao = new InventoryDao(getContext());
        pillInventoryList = inventoryDao.getAllInventoryEntries();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: here");

        View view = inflater.inflate(R.layout.fragment_inventory, container, false);
        invRecycleDisplay = view.findViewById(R.id.invRecycleDisplay);
        recyclerViewAdapter = new InventoryRecyclerViewAdapter(getContext(), pillInventoryList);
        invRecycleDisplay.setAdapter(recyclerViewAdapter);
        invRecycleDisplay.setLayoutManager(new LinearLayoutManager(getContext()));
        if (pillInventoryList.isEmpty()) {
            invRecycleDisplay.setVisibility(View.GONE);
            TextView invDisplayNoInvMessage = view.findViewById(R.id.invDisplayNoInvMessage);
            invDisplayNoInvMessage.setVisibility(View.VISIBLE);
        }else {
            invRecycleDisplay.setVisibility(View.VISIBLE);
            TextView invDisplayNoInvMessage = view.findViewById(R.id.invDisplayNoInvMessage);
            invDisplayNoInvMessage.setVisibility(View.GONE);
        }
        return view;
    }

    public void addMedicine () {
        AddEditInventoryHandler addEditInventoryHandler = new AddEditInventoryHandler(getContext(),pillInventoryList);
        pillInventoryList = addEditInventoryHandler.addEditInventoryItem(null,0,new InvEditCallback(){
            public void editCallBack() {
                if (invRecycleDisplay.getVisibility() == View.GONE)
                    invRecycleDisplay.setVisibility(View.VISIBLE);
                recyclerViewAdapter.notifyDataSetChanged();
            }
        });
    }

    private void generateInvSummary() {
        StringBuilder builder = new StringBuilder();
        builder.append("Current Inventory Status");
        builder.append("\n==================================\n");
        for (Inventory inventory : pillInventoryList) {
            builder.append(inventory.getPill().getName());
            builder.append("  ");
            builder.append(inventory.getQuantity());
            builder.append(",\n");
        }
        inventorySummary = builder.toString();
    }

    void shareInventory() {
        generateInvSummary();
        UserInfo userInfo = new UserInfoDao(requireContext()).getUserData();
        byte[] outImage=userInfo.getPrescriptionImage();
        if (null != outImage && outImage.length > 0) {
            getImagePath();
            try {
                FileOutputStream fos = new FileOutputStream(photoFile);
                fos.write(outImage);
                fos.flush();
                fos.close();
            } catch (IOException ioe) {
                Log.d("TAG", "setUpUserData: " + ioe.getLocalizedMessage());
            }
        }
        Intent share = new Intent(Intent.ACTION_SEND);
        if (photoFile != null && photoURI != null) {
            share.setType("image/jpeg");
            share.putExtra(Intent.EXTRA_STREAM, photoURI);
            share.putExtra(Intent.EXTRA_SUBJECT,"Inventory Summary");
            share.putExtra(Intent.EXTRA_TEXT,inventorySummary);
            startActivity(Intent.createChooser(share, "Share Inventory"));
        }else {
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_SUBJECT,"Inventory Summary");
            share.putExtra(Intent.EXTRA_TEXT,inventorySummary);
            startActivity(Intent.createChooser(share, "Share Inventory"));
        }
    }

    private void getImagePath() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            photoFile = createImageFile();
            photoURI = FileProvider.getUriForFile(requireContext(),
                    "com.sasr.medbudfinal.fileprovider",
                    photoFile);
        } else {
            photoFile = createImageFileOlder();
            photoURI  = Uri.fromFile(photoFile);
        }

    }

    private File createImageFileOlder()
    {
        // External sdcard location
        File mediaStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Toast.makeText(requireContext(), "Unable to create directory", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        return new File(mediaStorageDir.getPath() + File.separator
                + "Prescription.jpg");

    }

    private File createImageFile() {
        String imageFileName = "Prescription.jpg";
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir+File.separator+imageFileName);
        return image;
    }

}
