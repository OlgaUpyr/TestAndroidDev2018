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

public class CreatePinActivity extends AppCompatActivity {
    Button createPin;
    Pinview enterPin, confirmPin;

    protected void onPause() {
        super.onPause();

        UiUtil.nameOfLastStoppedActivity = this.getClass().getName();
    }

    public void onStop () {
        super.onStop();

        UiUtil.nameOfLastStoppedActivity = this.getClass().getName();
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pin);

        enterPin = (Pinview) findViewById(R.id.enterPinCodeView);
        confirmPin = (Pinview) findViewById(R.id.confirmPinCodeView);


        createPin = (Button) findViewById(R.id.loginButton);
        createPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pin1 = enterPin.getValue().toString();
                String pin2 = confirmPin.getValue().toString();

                if(pin1.equals("") || pin2.equals("")){
                    Toast.makeText(CreatePinActivity.this, "No password entered!", Toast.LENGTH_LONG).show();
                }
                else {
                    if(pin1.equals(pin2)){
                        SharedPreferences sharedPreferences = getSharedPreferences("PREFS", 0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("pin", pin1);
                        editor.apply();
                        if(!isAccessGranted()){
                            startActivity(new Intent(CreatePinActivity.this, UsagePermissionActivity.class));
                        }
                        else {
                            startActivity(new Intent(CreatePinActivity.this, MainActivity.class));
                        }
                    }
                    else {
                        Toast.makeText(CreatePinActivity.this, "Pin codes don\'t match!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}
