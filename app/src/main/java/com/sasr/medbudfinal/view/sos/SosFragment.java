package com.sasr.medbudfinal.view.sos;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.sasr.medbudfinal.R;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class SosFragment extends Fragment {
    private static final String TAG = "SosFragment";

    EditText numberEditText;
    EditText messageEditText;
    Button sendButton;
    ImageView contactImageView;
    FusedLocationProviderClient fusedLocationProviderClient;
    String[] latitudeText = new String[1];
    String[] longitudeText = new String[1];
    String[] addressText = new String[1];
    int PERMISSION_ID = 44;
    private final int REQUEST_CONTACT = 1;
    private String phoneNo;
    private String sosText;
    public static final String KEY_NUMBER = "phoneNumber";
    public static final String KEY_MESSAGE = "sosMessage";

    public SosFragment() {
        // Required empty public constructor
    }

    public static SosFragment newInstance() {
        return new SosFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_sos, container, false);
        numberEditText = view.findViewById(R.id.numberEditText);
        messageEditText = view.findViewById(R.id.messageEditText);
        sendButton = view.findViewById(R.id.sendButton);
        contactImageView = view.findViewById(R.id.contact);
        contactImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContactDetails();
            }
        });
        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.SEND_SMS,
                Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNo = numberEditText.getText().toString().trim();
                sosText = messageEditText.getText().toString().trim();
                if (phoneNo.isEmpty()) {
                    Toast.makeText(getContext(), "Please provide a Phone Number!", Toast.LENGTH_SHORT).show();
                    numberEditText.requestFocus();
                }
                else if (sosText.isEmpty()) {
                    Toast.makeText(getContext(), "Please provide a Message!", Toast.LENGTH_SHORT).show();
                    messageEditText.requestFocus();
                }
                else {
                    saveSOSdata();
                    initiateSMS();
                    sendSms();
                }

            }
        });
        restoreSOSdata();
        return view;
    }

    private void restoreSOSdata () {
        SharedPreferences sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE);
        phoneNo = sharedPref.getString(KEY_NUMBER,"");
        sosText = sharedPref.getString(KEY_MESSAGE,"");
        if (!phoneNo.isEmpty()) {
            numberEditText.setText(phoneNo);
        }
        if (!sosText.isEmpty()) {
            messageEditText.setText(sosText);
        }
    }

    private void saveSOSdata () {
        /*SharedPreferences sharedPref = requireActivity().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);*/
        SharedPreferences sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_NUMBER, phoneNo);
        editor.putString(KEY_MESSAGE, sosText);
        editor.apply();
    }

    private void initiateSMS() {
        requestNewLocationData();
    }

    private void getContactDetails() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        startActivityForResult(intent, REQUEST_CONTACT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Number", "inside!"+requestCode);

        if(requestCode == REQUEST_CONTACT){
            Log.d("Number", "first if!");
            if(resultCode == RESULT_OK){
                Log.d("Number", "second if!");
                Uri contactData = data.getData();
                Cursor cursor = requireActivity().managedQuery(contactData, null, null, null, null);
                cursor.moveToFirst();

                String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                Log.d("Number", "number: "+number);
                //contactName.setText(name);
                numberEditText.setText(number);
                //contactEmail.setText(email);
            }
        }
    }

    private void sendSms() {
        if (latitudeText[0] != null || longitudeText[0] != null) {
            String number = numberEditText.getText().toString();
            String messageText = messageEditText.getText().toString();
            SmsManager sms = SmsManager.getDefault();
            ArrayList<String> parts = sms.divideMessage(generateMessage(messageText));
            sms.sendMultipartTextMessage(number, null, parts, null, null);

            /*SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, generateMessage(messageText), null, null);*/

            Toast.makeText(getContext(), "SOS sent!", Toast.LENGTH_SHORT).show();
            fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
            requireActivity().onBackPressed();
        }else {
            getLastLocation();
        }
    }

    //LocationRequest locationRequest;
    private String generateMessage(String messageText) {
        if (latitudeText[0] == null || longitudeText[0] == null) {
            getLastLocation();
        } else {
            return generateFinalMessage(latitudeText, longitudeText, addressText, messageText);
        }
        return null;
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        //fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (checkPermissions()) {
            new InternetCheck(new InternetCheck.Consumer() {
                @Override
                public void accept(Boolean internet) {
                    if (internet) {
                        if (isLocationEnabled()) {
                            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(
                                    new OnCompleteListener<Location>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Location> task) {
                                            Location location = task.getResult();
                                            if (location == null) {
                                                requestNewLocationData();
                                            } else {
                                                assignLocationInText(location);
                                                latitudeText[0] = location.getLatitude() + "";
                                                longitudeText[0] = location.getLongitude() + "";
                                            }
                                        }
                                    }
                            );
                        } else {
                            Toast.makeText(getContext(), "Please turn on Location and Internet", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(getContext(), "Please turn on Internet", Toast.LENGTH_LONG).show();
                    }
                }
            });

        } else {
            requestPermissions();
        }
    }




    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        fusedLocationProviderClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            assignLocationInText(mLastLocation);
            Log.d(TAG, "onLocationResult: "+mLastLocation);
            sendSms();
        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED
        ) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                requireActivity(),
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        final LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    static class InternetCheck extends AsyncTask<Void,Void,Boolean> {

        private Consumer mConsumer;
        public  interface Consumer { void accept(Boolean internet); }

        public  InternetCheck(Consumer consumer) { mConsumer = consumer; execute(); }

        @Override protected Boolean doInBackground(Void... voids) { try {
            Socket sock = new Socket();
            sock.connect(new InetSocketAddress("8.8.8.8", 53), 1500);
            sock.close();
            return true;
        } catch (IOException e) { return false; } }

        @Override protected void onPostExecute(Boolean internet) { mConsumer.accept(internet); }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    private void assignLocationInText(Location location) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),
                    location.getLongitude(),1);
            latitudeText[0] = addresses.get(0).getLatitude()+"";
            longitudeText[0] = addresses.get(0).getLongitude()+"";
            addressText[0] = addresses.get(0).getAddressLine(0)+"";
        } catch (IOException e) {
            Log.d("LoacationTag", "onCompleteError: "+e.getMessage());
        }
    }

    private String generateFinalMessage(String[] latitudeText, String[] longitudeText, String[] addressText,String messageText) {
        String finalMessage;
       /* finalMessage = messageText + "\nLatitude: " + latitudeText[0]
                + ",\nLongitude: " + longitudeText[0] +",\nAddress: "+addressText[0]
                +",\nMap Link: https://www.google.com/maps/search/?api=1&query="+latitudeText[0]+","+longitudeText[0];*/
        finalMessage = messageText + "\nAddress: "+addressText[0]
                +"\nMap Link: https://www.google.com/maps/search/?api=1&query="+latitudeText[0]+","+longitudeText[0];
        Log.d("LoacationTag", "finalMessage: \n" + finalMessage);
        return finalMessage;
    }
}