package com.sasr.medbudfinal.view.info;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.sasr.medbudfinal.R;
import com.sasr.medbudfinal.data.userinfo.UserInfoDao;
import com.sasr.medbudfinal.model.UserInfo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class MyInfoFragment extends Fragment {
    private static final String TAG = "MyInfoFragment";

    private EditText myInfoTxtName;
    private EditText myInfoTxtAge;
    private Spinner myInfoSpnGender;
    private EditText myInfoTxtDtName;
    private EditText myInfoTxtMedHistory;
    private ImageView myInfoImgPrescription;
    private Button myInfoBtnSave;

    private ImageView myInfoPopupImgPrescription;
    private ImageButton myInfoPopupBtnSave;

    private UserInfo userInfo;
    private UserInfoDao userInfoDao;

    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;
    private File photoFile = null;
    private String mCurrentPhotoPath;

    public MyInfoFragment() {

    }

    public static MyInfoFragment newInstance(String param1, String param2) {
        return new MyInfoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userInfoDao = new UserInfoDao(getContext());
        userInfo = userInfoDao.getUserData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_info, container, false);
        myInfoTxtName = view.findViewById(R.id.myInfoTxtName);
        myInfoTxtAge = view.findViewById(R.id.myInfoTxtAge);
        myInfoSpnGender = view.findViewById(R.id.myInfoSpnGender);
        myInfoTxtDtName = view.findViewById(R.id.myInfoTxtDtName);
        myInfoTxtMedHistory = view.findViewById(R.id.myInfoTxtMedHistory);
        myInfoImgPrescription = view.findViewById(R.id.myInfoImgPrescription);
        myInfoBtnSave = view.findViewById(R.id.myInfoBtnSave);

        setUpUserData ();
        myInfoBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInfo ();
            }
        });
        myInfoImgPrescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPrescriptionImage ();
            }
        });
        return view;
    }

    private void setUpUserData () {
        if (userInfo != null) {
            myInfoTxtName.setText(userInfo.getName());
            if (userInfo.getAge() != 0) {
                myInfoTxtAge.setText(String.valueOf(userInfo.getAge()));
            }
            String doctorName = userInfo.getDoctorName();
            if (doctorName != null && !(doctorName.isEmpty())) {
                myInfoTxtDtName.setText(doctorName);
            }
            String medHistory = userInfo.getMedicalHistory();
            if (medHistory != null && !(medHistory.isEmpty())) {
                myInfoTxtMedHistory.setText(medHistory);
            }
            myInfoSpnGender.setSelection(userInfo.getGender());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                photoFile = createImageFile();
            } else {
                photoFile = createImageFileOlder();
            }
            byte[] outImage=userInfo.getPrescriptionImage();
            if (null != outImage && outImage.length > 0) {
                try {
                    FileOutputStream fos = new FileOutputStream(photoFile);
                    fos.write(outImage);
                    fos.flush();
                    fos.close();
                } catch (IOException ioe) {
                    Log.d(TAG, "setUpUserData: " + ioe.getLocalizedMessage());
                }
                myInfoImgPrescription.setImageBitmap(BitmapFactory.decodeFile(photoFile.getAbsolutePath()));
            }
        }
    }

    private void saveUserInfo () {
        String name = myInfoTxtName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(getContext(), "Please provide your name", Toast.LENGTH_SHORT).show();
            return;
        }
        int age = 0;
        if (! myInfoTxtAge.getText().toString().trim().isEmpty()) {
            age = Integer.parseInt(myInfoTxtAge.getText().toString());
            if (age <= 0) {
                Toast.makeText(getContext(), "Age can not be 0 or negative", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        int gender = myInfoSpnGender.getSelectedItemPosition();
        String doctorName = myInfoTxtDtName.getText().toString().trim();
        String medicalHistory = myInfoTxtMedHistory.getText().toString().trim();

        userInfo.setName(name);
        userInfo.setAge(age);
        userInfo.setGender(gender);
        userInfo.setDoctorName(doctorName);
        userInfo.setMedicalHistory(medicalHistory);
        userInfoDao.updateUserData(userInfo);
        requireActivity().onBackPressed();
    }

    private void setPrescriptionImage () {

        View getPrescriptionView = LayoutInflater.from(getContext()).inflate(R.layout.my_info_prescription_popup,null);
        myInfoPopupImgPrescription = getPrescriptionView.findViewById(R.id.myInfoPopupImgPrescription);
        ImageButton myInfoPopupImgCamera = getPrescriptionView.findViewById(R.id.myInfoPopupImgCamera);
        ImageButton myInfoPopupImgBrowse = getPrescriptionView.findViewById(R.id.myInfoPopupImgBrowse);
        myInfoPopupBtnSave = getPrescriptionView.findViewById(R.id.myInfoPopupBtnSave);

        byte[] outImage=userInfo.getPrescriptionImage();
        if (null != outImage && outImage.length > 0) {
            myInfoPopupImgPrescription.setImageBitmap(BitmapFactory.decodeFile(photoFile.getAbsolutePath()));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(getPrescriptionView);
        final AlertDialog dialog = builder.create();


        myInfoPopupImgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePic();
            }
        });
        myInfoPopupImgBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browseImages();
            }
        });

        myInfoPopupBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImageFile();
                dialog.dismiss();
                myInfoImgPrescription.setImageBitmap(BitmapFactory.decodeFile(photoFile.getAbsolutePath()));
            }
        });

        dialog.show();

    }

    private void takePic() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            captureImage();
        }
        else
        {
            captureImageOlder();
        }
    }

    /* Capture Image function for 4.4.4 and lower. Not tested for Android Version 3 and 2 */
    private void captureImageOlder() {

        try {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            //photoFile = createImageFileOlder();
            if(photoFile!=null)
            {
                Log.i(TAG,photoFile.getAbsolutePath());
                Uri photoURI  = Uri.fromFile(photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, REQUEST_CAMERA);
            }
        }
        catch (Exception e)
        {
            Toast.makeText(getContext(), "Unable to open camera", Toast.LENGTH_SHORT).show();
        }
    }

    private void captureImage()
    {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[] { Manifest.permission.CAMERA }, 0);
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                // Create the File where the photo should go
                try {

                    //photoFile = createImageFile();
                    //Log.i(TAG,photoFile.getAbsolutePath());
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(requireContext(),
                                "com.sasr.medbudfinal.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_CAMERA);
                    }
                } catch (Exception ex) {
                    // Error occurred while creating the File
                    Log.d(TAG, "captureImage: "+ex.getLocalizedMessage());
                }


            }else {
                Log.d(TAG, "captureImage: photo null");
            }
        }
    }

    private File createImageFileOlder()
    {
        // External sdcard location
        File mediaStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Toast.makeText(getContext(), "Unable to create directory", Toast.LENGTH_SHORT).show();
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
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void browseImages() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
            Log.d(TAG, "browseImages: not granted");
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/");
            startActivityForResult(Intent.createChooser(intent, "Select Prescription Image"), REQUEST_GALLERY);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Bundle extras = data.getExtras();
        //Bitmap imageBitmap = (Bitmap) extras.get("data");
        //imageView.setImageBitmap(imageBitmap);
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            Bitmap myBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            Bitmap bitmap = Bitmap.createScaledBitmap(myBitmap, 800, 800, true);
            myInfoPopupImgPrescription.setImageBitmap(bitmap);
        } else if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            assert selectedImage != null;
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = requireActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            if (null != cursor && cursor.moveToNext()) {
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                photoFile = new File(picturePath);
                Bitmap myBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                Bitmap bitmap = Bitmap.createScaledBitmap(myBitmap, 800, 800, true);
                myInfoPopupImgPrescription.setImageBitmap(bitmap);
            }
        }
        else
        {
            Toast.makeText(getContext(), "Something went wrong, please try again!!!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageFile () {
        if (photoFile != null) {
            Bitmap myBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            Bitmap bitmap = Bitmap.createScaledBitmap(myBitmap, 800, 800, true);
            userInfo.setPrescriptionImage(getBitmapAsByteArray(bitmap));
        }
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        return outputStream.toByteArray();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(),
                            new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1);
                } else {
                    captureImage();
                }
            }
        } else if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                captureImage();
            }
        } else if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult: granted");
                browseImages();
            }
        }

    }
}
