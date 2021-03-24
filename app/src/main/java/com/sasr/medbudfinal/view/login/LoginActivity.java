package com.sasr.medbudfinal.view.login;

import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.sasr.medbudfinal.R;
import com.sasr.medbudfinal.data.userinfo.UserInfoDao;
import com.sasr.medbudfinal.model.UserInfo;
import com.sasr.medbudfinal.view.HomeActivity;


public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int SIGN_IN = 9001;
    private GoogleApiClient googleApiClient;
    Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserInfo userInfo = new UserInfoDao(LoginActivity.this).getUserData();
        if (userInfo != null) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bitmap recentIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_med_bud_round); // Initialize this to whatever you want
            String title = getResources().getString(R.string.app_name);  // You can either set the title to whatever you want or just use null and it will default to your app/activity name
            int color = R.color.colorPrimary; // Set the color you want to set the title to, it's a good idea to use the colorPrimary attribute

            ActivityManager.TaskDescription description = new ActivityManager.TaskDescription(title, recentIcon);
            this.setTaskDescription(description);
        }

        setContentView(R.layout.activity_login);

        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).
                            addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();
        signInButton = findViewById(R.id.signIn_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent,SIGN_IN);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result != null && result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                assert account != null;
                UserInfo userInfo = new UserInfo(
                        account.getId(),
                        account.getEmail(),
                        account.getDisplayName(),
                        0,
                        0,
                        null,
                        null
                );
                new UserInfoDao(LoginActivity.this).inserUserData(userInfo);
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                finish();
            } else {
                Toast.makeText(LoginActivity.this,"Login failed!",Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

