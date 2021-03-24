package com.sasr.medbudfinal.view.reminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.transition.FragmentTransitionSupport;

import android.app.ActivityManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.sasr.medbudfinal.R;
import com.sasr.medbudfinal.data.inv.InventoryDao;
import com.sasr.medbudfinal.interfaces.IOBackPressedCallBack;
import com.sasr.medbudfinal.interfaces.RemEditCallBack;
import com.sasr.medbudfinal.model.ReminderPresenter;

public class ReminderManageActivity extends AppCompatActivity implements RemEditCallBack {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_manage);
        Toolbar toolbar = findViewById(R.id.homeToolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bitmap recentIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_med_bud_round); // Initialize this to whatever you want
            String title = getResources().getString(R.string.app_name);  // You can either set the title to whatever you want or just use null and it will default to your app/activity name
            int color = R.color.colorPrimary; // Set the color you want to set the title to, it's a good idea to use the colorPrimary attribute

            ActivityManager.TaskDescription description = new ActivityManager.TaskDescription(title, recentIcon);
            this.setTaskDescription(description);
        }

        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fab.getTag().toString().equalsIgnoreCase(getString(R.string.strAdd))) {
                    transitionFragment(new ReminderAddFragment(null),"ReminderAdd",true);
                    fab.setImageResource(R.drawable.alarm_on_white_48);
                    fab.setTag(getString(R.string.strOk));
                } else if (fab.getTag().toString().equalsIgnoreCase(getString(R.string.strOk))) {
                    ReminderAddFragment reminderAddFragment = (ReminderAddFragment) getSupportFragmentManager().findFragmentByTag("ReminderAdd");
                    assert reminderAddFragment != null;
                    if (reminderAddFragment.verifyQuantity()) {
                        if (reminderAddFragment.setReminder()) {
                            fab.setImageResource(R.drawable.alarm_add_white_48);
                            fab.setTag(getString(R.string.strAdd));
                        }
                    }
                }
            }
        });
        int invCount = new InventoryDao(ReminderManageActivity.this).getInventorySize();
        if (invCount <= 0) {
            fab.setVisibility(View.GONE);
            TextView remManageActTvWarningNoInventory = findViewById(R.id.remManageActTvWarningNoInventory);
            Button remManageActBtnWarningGoBack = findViewById(R.id.remManageActBtnWarningGoBack);
            remManageActTvWarningNoInventory.setVisibility(View.VISIBLE);
            remManageActBtnWarningGoBack.setVisibility(View.VISIBLE);
            remManageActBtnWarningGoBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }else {
            fab.setVisibility(View.VISIBLE);
            TextView remManageActTvWarningNoInventory = findViewById(R.id.remManageActTvWarningNoInventory);
            Button remManageActBtnWarningGoBack = findViewById(R.id.remManageActBtnWarningGoBack);
            remManageActTvWarningNoInventory.setVisibility(View.GONE);
            remManageActBtnWarningGoBack.setVisibility(View.GONE);
            transitionFragment(new ReminderDisplayFragment(), "ReminderDisplay", false);
        }
    }

    private void transitionFragment(Fragment fragment,String tag,boolean addToBackStack) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.remActivityFragmentContainer,fragment,tag);
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(tag);
        }
        fragmentTransaction.commit();
    }

    @Override
    public void editReminder(ReminderPresenter reminderPresenter) {
        transitionFragment(new ReminderAddFragment(reminderPresenter),"ReminderAdd",true);
        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setImageResource(R.drawable.alarm_on_white_48);
        fab.setTag(getString(R.string.strOk));
    }

    @Override public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("ReminderAdd");
        if (!(fragment instanceof IOBackPressedCallBack) || !((IOBackPressedCallBack) fragment).onBackPressed()) {
            super.onBackPressed();
        }
    }
}
