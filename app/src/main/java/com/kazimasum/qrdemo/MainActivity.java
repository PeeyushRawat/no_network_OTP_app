package com.kazimasum.qrdemo;

import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;

import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class MainActivity extends AppCompatActivity {

    public static Button scanbtn;
    public static Button scanbtn2;
    public static EditText scantext;
    public static EditText scantext2;
    public static TextView phoneNumberText;
    public static String phoneNumber;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scantext=(EditText) findViewById(R.id.scantext);
        scantext2=(EditText) findViewById(R.id.scantext2);
        scanbtn=(Button) findViewById(R.id.scanbtn);
        scanbtn2=(Button) findViewById(R.id.scanbtn2);
        phoneNumberText = findViewById(R.id.textView2);

        if (ActivityCompat.checkSelfPermission(this, READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, READ_PHONE_NUMBERS) ==
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            phoneNumber = telephonyManager.getLine1Number();

            return;
        } else {
            requestPermission();
        }
        scanbtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), scannerView.class);
                String mainAct;
                mainAct = MainActivity.phoneNumberText.getText().toString();
                myIntent.putExtra("extra", mainAct.substring(mainAct.length()-10));
                startActivityForResult(myIntent, 0);
            }
        });
        scanbtn2.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                String otpFromQR = scantext2.getText().toString();
                String number = phoneNumberText.getText().toString();
                number = number.substring(number.length()-10);
                String b = Character.toString(number.charAt(otpFromQR.charAt(1)-'0'));
                String a = Character.toString(number.charAt(otpFromQR.charAt(0)-'0'));
                String c = Character.toString(number.charAt(otpFromQR.charAt(2)-'0'));
                String d = Character.toString(number.charAt(otpFromQR.charAt(3)-'0'));
                MainActivity.scantext.setTextColor(Color.parseColor("#48bf53"));
                MainActivity.scantext.setText("Your OTP is:\n"+a+b+c+d);
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{READ_SMS, READ_PHONE_NUMBERS, READ_PHONE_STATE}, 100);
        }
    }

    @SuppressLint("SetTextI18n")
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, READ_SMS) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            @SuppressLint("HardwareIds") String phoneNumber = telephonyManager.getLine1Number();
            MainActivity.phoneNumberText.setText("+91 "+phoneNumber.substring(phoneNumber.length()-10));
            MainActivity.scantext.setText("Your QR Result will show here");
        } else {
            throw new IllegalStateException("Unexpected value: " + requestCode);
        }
    }
}