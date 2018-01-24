package com.example.android.testtask.detail;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.testtask.R;
import com.example.android.testtask.apps_list.MainActivity;
import com.example.android.testtask.model.AppInfo;
import com.example.android.testtask.model.DetailInfo;

import java.util.Calendar;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private ImageView appIconImage;
    private TextView timeOfUseText, lastTimeUsedText, appNameText;

    private UsageStatsManager usageStatsManager;
    private DetailInfo detailInfo;
    private AppInfo appInfo;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        appIconImage = (ImageView) findViewById(R.id.detail_appIcon);
        timeOfUseText = (TextView) findViewById(R.id.detail_timeOfUse);
        lastTimeUsedText = (TextView) findViewById(R.id.detail_lastTimeUsed);
        appNameText = (TextView) findViewById(R.id.detail_appName);

        appInfo = (AppInfo) getIntent().getParcelableExtra(
                AppInfo.class.getCanonicalName());
        getAppDetailInfo();
        bindAppDetailInfoToUI();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void getAppDetailInfo(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);

        usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> queryUsageStats = usageStatsManager
                .queryUsageStats(UsageStatsManager.INTERVAL_DAILY, calendar.getTimeInMillis(),
                        System.currentTimeMillis());

        for (int i = 0; i < queryUsageStats.size(); i++) {
            if(queryUsageStats.get(i).getPackageName().equals(appInfo.getPackageName())) {
                try {
                    Drawable appIcon = DetailActivity.this.getPackageManager()
                            .getApplicationIcon(appInfo.getPackageName());
                    appInfo.setAppIcon(appIcon);
                } catch (PackageManager.NameNotFoundException e) { }
                break;
            }
        }

        detailInfo = new DetailInfo();
        String appName = MainActivity.getAppNameFromPackage(appInfo.getPackageName(), DetailActivity.this);
    }

    private String getLastTimeUsed(long lastTimeUsed){
        String result = "";
        int hours = (int) ((lastTimeUsed/(1000*60*60))%24);
        int minutes = (int) ((lastTimeUsed/(1000*60))%60);
        int seconds = (int) (lastTimeUsed/1000)%60;
        return result;
    }

    private void bindAppDetailInfoToUI(){
        appIconImage.setImageDrawable(appInfo.getAppIcon());
        timeOfUseText.setText(appInfo.getTimeOfUse());
        lastTimeUsedText.setText(appInfo.getLastTimeUsed());
        appNameText.setText(appInfo.getAppName());
    }
}
