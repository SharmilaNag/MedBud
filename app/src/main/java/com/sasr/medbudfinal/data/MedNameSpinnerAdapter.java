package com.sasr.medbudfinal.data;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sasr.medbudfinal.R;
import com.sasr.medbudfinal.model.MedDisplay;

import java.util.ArrayList;

public class MedNameSpinnerAdapter extends ArrayAdapter<MedDisplay> {
    private Context mContext;
//    private ArrayList<MedDisplay> pillNameList
    public MedNameSpinnerAdapter (Context mContext, ArrayList<MedDisplay> pillNameList) {
        super (mContext,0,pillNameList);
        this.mContext = mContext;
//        this.pillNameList = pillNameList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position,convertView,parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position,convertView,parent);
    }

    private View initView (int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.adapter_medtype_spinner,parent,false);
        }
        ImageView medIcon = convertView.findViewById(R.id.imgSpnMedType);
        TextView medText = convertView.findViewById(R.id.tvSpnMedType);
        MedDisplay medDisplay = getItem(position);
        if (medDisplay != null) {
            TypedArray pillTypeIcon = mContext.getResources().obtainTypedArray(R.array.arrMedTypesImages);
            medIcon.setImageResource(pillTypeIcon.getResourceId(medDisplay.getMedIcon(),-1));
            medIcon.setTag(String.valueOf(medDisplay.getMedIcon()));
            medText.setText(medDisplay.getMedName());
            pillTypeIcon.recycle();
        }

        return convertView;
    }
}
