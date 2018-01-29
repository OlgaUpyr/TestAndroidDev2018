package com.example.android.testtask.pin;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.android.testtask.R;
import com.example.android.testtask.UsagePermissionActivity;
import com.example.android.testtask.apps_list.MainActivity;
import com.example.android.testtask.util.UiUtil;
import com.goodiebag.pinview.Pinview;

public class PinActivity extends AppCompatActivity implements View.OnClickListener{
    Pinview pinview;
    Button loginButton, createButton;

    String pin;

    protected void onPause() {
        super.onPause();

        UiUtil.nameOfLastStoppedActivity = this.getClass().getName();
    }

    public void onStop () {
        super.onStop();

        UiUtil.nameOfLastStoppedActivity = this.getClass().getName();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        SharedPreferences sharedPreferences = getSharedPreferences("PREFS", 0);
        pin = sharedPreferences.getString("pin", "");

        pinview = (Pinview) findViewById(R.id.pinCodeView);

        loginButton = (Button) findViewById(R.id.loginButton);
        createButton = (Button) findViewById(R.id.createButton);

        loginButton.setOnClickListener(this);
        createButton.setOnClickListener(this);
    }

    private boolean isAccessGranted() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.createButton:
                if(!pin.equals("")){
                    Toast.makeText(PinActivity.this, "There is a pin!", Toast.LENGTH_LONG).show();
                }
                else {
                    startActivity(new Intent(PinActivity.this, CreatePinActivity.class));
                }
                break;
            case R.id.loginButton:
                if(pin.equals("")){
                    Toast.makeText(PinActivity.this, "There is no pin!", Toast.LENGTH_LONG).show();
                }
                else {
                    String pinCode = pinview.getValue();
                    if(pinCode.equals(pin)){
                        if(!isAccessGranted()){
                            startActivity(new Intent(PinActivity.this, UsagePermissionActivity.class));
                        }
                        else {
                            startActivity(new Intent(PinActivity.this, MainActivity.class));
                        }
                    }
                    else {
                        Toast.makeText(PinActivity.this, "Wrong pin code!", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
}
