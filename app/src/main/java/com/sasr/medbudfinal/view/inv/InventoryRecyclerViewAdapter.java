package com.sasr.medbudfinal.view.inv;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sasr.medbudfinal.R;
import com.sasr.medbudfinal.data.inv.InventoryDao;
import com.sasr.medbudfinal.data.reminder.ReminderDao;
import com.sasr.medbudfinal.interfaces.InvEditCallback;
import com.sasr.medbudfinal.model.Inventory;
import com.tooltip.Tooltip;

import java.util.ArrayList;

public class InventoryRecyclerViewAdapter extends RecyclerView.Adapter <InventoryRecyclerViewAdapter.InventoryViewHolder> {
    private static final String TAG = "InventoryRecyclerViewAd";

    private Context mContext;
    private ArrayList<Inventory> pillInventoryList;

    public InventoryRecyclerViewAdapter(Context mContext, ArrayList<Inventory> pillInventoryList) {
        this.mContext = mContext;
        this.pillInventoryList = pillInventoryList;
        Log.d(TAG, "InventoryRecyclerViewAdapter: here");
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InventoryViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.inv_recycle_display_adapter,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final InventoryViewHolder holder, final int position) {
        Inventory inventory = pillInventoryList.get(position);
        TypedArray pillTypeIcon = mContext.getResources().obtainTypedArray(R.array.arrMedTypesImages);
        holder.invDisplayImgMedType.setImageResource(pillTypeIcon.getResourceId(inventory.getPill().getType(),-1));
        pillTypeIcon.recycle();
        holder.invDisplayTvMedName.setText(inventory.getPill().getName());
        holder.invDisplayTvMedQuantity.setText("Remaining: "+inventory.getQuantity());
        if (inventory.getQuantity() <= inventory.getWarningQuantity()) {
            holder.cardView.setCardBackgroundColor(Color.RED);
            //holder.invDisplayTvMedQuantity.setBackgroundColor(Color.YELLOW);
            holder.invDisplayImgvWarning.setVisibility(View.VISIBLE);
            holder.invDisplayImgvWarning.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Tooltip tooltip = new Tooltip.Builder(holder.invDisplayImgvWarning).setText("Inventory is low!")
                            .setBackgroundColor(Color.YELLOW).setTextAppearance(R.style.TooltipTextAppearance).setGravity(Gravity.START).show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tooltip.dismiss();
                        }
                    }, 1700);
                }
            });
        } else {
            holder.invDisplayTvMedQuantity.setBackgroundColor(Color.WHITE);
            holder.invDisplayImgvWarning.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return pillInventoryList.size();
    }

    class InventoryViewHolder extends RecyclerView.ViewHolder {
        TextView invDisplayTvMedName;
        ImageView invDisplayImgvWarning;
        TextView invDisplayTvMedQuantity;
        ImageView invDisplayImgvEdit;
        ImageView invDisplayImgvDelete;
        ImageView invDisplayImgMedType;
        CardView cardView;

        public InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            invDisplayTvMedName = itemView.findViewById(R.id.invDisplayTvMedName);
            invDisplayImgvWarning = itemView.findViewById(R.id.invDisplayImgvWarning);
            invDisplayTvMedQuantity = itemView.findViewById(R.id.invDisplayTvMedQuantity);
            invDisplayImgvEdit = itemView.findViewById(R.id.invDisplayImgvEdit);
            invDisplayImgvDelete = itemView.findViewById(R.id.invDisplayImgvDelete);
            invDisplayImgMedType = itemView.findViewById(R.id.invDisplayImgMedType);
            cardView = itemView.findViewById(R.id.inventoryCardView);
            invDisplayImgvEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editMed(getAdapterPosition());
                    //InventoryRecyclerViewAdapter.this.notifyItemChanged(position);
                }
            });

            invDisplayImgvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteMed(getAdapterPosition());
                    //InventoryRecyclerViewAdapter.this.notifyItemRemoved(position);
                }
            });
        }

        private void deleteMed(final int position) {
            final Inventory inventory = pillInventoryList.get(position);
            long remCount = new ReminderDao(mContext).getReminderCountOfInventory(inventory.getId());
            if (remCount > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Warning!!")
                        .setMessage("You have associated reminders for '" + inventory.getPill().getName()
                                + "'. If you want to delete this medicine from your inventory, please first make sure "+
                                "to delete all of its associated reminders.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
            }else {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Warning!!")
                        .setMessage("Are you sure you want to delete " + inventory.getPill().getName()
                                + " from Inventory?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                InventoryDao inventoryDao = new InventoryDao(mContext);
                                if (inventoryDao.deleteInventoryEntry(inventory.getId()) > 0) {
                                    pillInventoryList.remove(position);
                                    dialog.dismiss();
                                    notifyItemRemoved(getAdapterPosition());
                                    Toast.makeText(mContext, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
            }
        }

        private void editMed(int position) {
            Inventory inventory = pillInventoryList.get(position);
            AddEditInventoryHandler addEditInventoryHandler = new AddEditInventoryHandler(mContext,pillInventoryList);
            Log.d(TAG, "editMed before: "+pillInventoryList);
            pillInventoryList = addEditInventoryHandler.addEditInventoryItem(inventory,position,new InvEditCallback(){
                public void editCallBack() {
                    notifyItemChanged(getAdapterPosition());
                    Log.d(TAG, "editMed after: "+pillInventoryList);
                    Toast.makeText(mContext, "Updated Successfully", Toast.LENGTH_SHORT).show();
                }
            });

            /*Inventory newInventory = pillInventoryList.get(position);
            notifyItemChanged(getAdapterPosition(), newInventory);*/
        }
    }
}
