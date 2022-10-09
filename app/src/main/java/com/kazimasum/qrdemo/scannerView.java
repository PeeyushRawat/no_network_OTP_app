package com.kazimasum.qrdemo;

import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;

import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.Arrays;
import java.util.Objects;

import me.dm7.barcodescanner.zxing.ZXingScannerView;



public class scannerView extends AppCompatActivity implements ZXingScannerView.ResultHandler
{
    ZXingScannerView scannerView;

    private static final int PERMISSION_REQUEST_CODE = 1;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView=new ZXingScannerView(this);
        setContentView(scannerView);

        Dexter.withContext(getApplicationContext())
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        scannerView.startCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                     permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void handleResult(Result rawResult) {
        String str = rawResult.getText();
        String[] lines = str.split("\n");
        String phoneNumber = Objects.requireNonNull(getIntent().getStringExtra("extra")).trim();
        if(lines.length!=3 || !lines[2].equals("sinisterrules@1101")) {
            if(str.startsWith("http://")){
                MainActivity.scantext.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                MainActivity.scantext.setTextColor(Color.parseColor("#FF0000"));
                MainActivity.scantext.setText("Malicious Link Blocked");
            }
            else {
                MainActivity.scantext.setTextColor(Color.parseColor("#000000"));
                MainActivity.scantext.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                MainActivity.scantext.setText(str);
            }
        }

        else if(lines.length==3 && lines[2].equals("sinisterrules@1101")){
            String otpFromQR = lines[1];
//            MainActivity.scantext.setText(phoneNumber);
            if(lines[0].equals(phoneNumber)) {
                String a = Character.toString(phoneNumber.charAt(otpFromQR.charAt(0)-'0'));
                String b = Character.toString(phoneNumber.charAt(otpFromQR.charAt(1)-'0'));
                String c = Character.toString(phoneNumber.charAt(otpFromQR.charAt(2)-'0'));
                String d = Character.toString(phoneNumber.charAt(otpFromQR.charAt(3)-'0'));
                MainActivity.scantext.setTextColor(Color.parseColor("#48bf53"));
                MainActivity.scantext.setText("Your OTP is:\n"+a+b+c+d);
            }
//
            else{
                MainActivity.scantext.setTextColor(Color.parseColor("#FF0000"));
                MainActivity.scantext.setText(lines[0]+" is not present in phone!!");
            }
        }
       onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }
}