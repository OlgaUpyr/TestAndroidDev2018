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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.testtask.R;
import com.example.android.testtask.apps_list.MainActivity;
import com.example.android.testtask.model.AppInfo;
import com.example.android.testtask.model.DetailInfo;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DetailActivity extends AppCompatActivity
    implements View.OnClickListener {
    private ImageView appIconImage;
    private TextView timeOfUseText, lastTimeUsedText, appNameText;
    private Button intervalDailyButton, intervalWeeklyButton, intervalMonthlyButton, intervalYearlyButton;

    private static final long DEFAULT_NUM_OF_MILLIS = 10800000;

    private UsageStatsManager usageStatsManager;
    private AppInfo appInfo;
    private List<UsageStats> queryUsageStats;

    private List<DetailInfo> dailyStatistic;
    private List<DetailInfo> weeklyStatistic;
    private List<DetailInfo> monthlyStatistic;
    private List<DetailInfo> yearlyStatistic;

    private BarChart barChart;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        appIconImage = (ImageView) findViewById(R.id.detail_appIcon);
        timeOfUseText = (TextView) findViewById(R.id.detail_timeOfUse);
        lastTimeUsedText = (TextView) findViewById(R.id.detail_lastTimeUsed);
        appNameText = (TextView) findViewById(R.id.detail_appName);

        barChart = (BarChart) findViewById(R.id.usageHistoryChart);
        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);

        intervalDailyButton = (Button) findViewById(R.id.intervalDailyButton);
        intervalWeeklyButton = (Button) findViewById(R.id.intervalWeeklyButton);
        intervalMonthlyButton = (Button) findViewById(R.id.intervalMonthlyButton);
        intervalYearlyButton = (Button) findViewById(R.id.intervalYearlyButton);

        intervalDailyButton.setOnClickListener(this);
        intervalWeeklyButton.setOnClickListener(this);
        intervalMonthlyButton.setOnClickListener(this);
        intervalYearlyButton.setOnClickListener(this);

        getAppDetailInfo();
        bindAppDetailInfoToUI();
        setStatistic(dailyStatistic);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void getAppDetailInfo(){
        dailyStatistic = new ArrayList<>();
        weeklyStatistic = new ArrayList<>();
        monthlyStatistic = new ArrayList<>();
        yearlyStatistic = new ArrayList<>();

        appInfo = (AppInfo) getIntent().getParcelableExtra(
                AppInfo.class.getCanonicalName());

        try {
            Drawable appIcon = DetailActivity.this.getPackageManager()
                    .getApplicationIcon(appInfo.getPackageName());
            appInfo.setAppIcon(appIcon);
        } catch (PackageManager.NameNotFoundException e) { }

        getUsageStatsByInterval(UsageStatsManager.INTERVAL_DAILY);
        getStatistic(dailyStatistic);

        getUsageStatsByInterval(UsageStatsManager.INTERVAL_WEEKLY);
        getStatistic(weeklyStatistic);

        getUsageStatsByInterval(UsageStatsManager.INTERVAL_MONTHLY);
        getStatistic(monthlyStatistic);

        getUsageStatsByInterval(UsageStatsManager.INTERVAL_YEARLY);
        getStatistic(yearlyStatistic);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void getUsageStatsByInterval(int typeInterval){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);

        usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        queryUsageStats = usageStatsManager
                .queryUsageStats(typeInterval, calendar.getTimeInMillis(),
                        System.currentTimeMillis());

        for (int i = 0; i < queryUsageStats.size(); i++) {
            if (!(queryUsageStats.get(i).getPackageName().equals(appInfo.getPackageName()))) {
                queryUsageStats.remove(queryUsageStats.get(i));
            }
        }

        Collections.sort(queryUsageStats, new MainActivity.LastTimeUsedComparatorDesc());

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void getStatistic(List<DetailInfo> statistic) {
        statistic.clear();
        long currentDayOfUse = queryUsageStats.get(0).getLastTimeUsed();
        long timeOfUseByDay = 0;
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(new Date(currentDayOfUse).getTime());
        String currentDayStr = c.get(Calendar.DATE) + "/" + c.get(Calendar.MONTH)+ "/" + c.get(Calendar.YEAR);
        String tempCurrentDayStr;

        for (int i = 0; i < queryUsageStats.size(); i++) {
            c.clear();
            c.setTimeInMillis(new Date(queryUsageStats.get(i).getLastTimeUsed()).getTime());
            tempCurrentDayStr = c.get(Calendar.DATE) + "/" + c.get(Calendar.MONTH)+ "/" + c.get(Calendar.YEAR);
            if (tempCurrentDayStr.equals(currentDayStr)) {
                if(!(queryUsageStats.get(i).getLastTimeUsed() <= DEFAULT_NUM_OF_MILLIS))
                    timeOfUseByDay += queryUsageStats.get(i).getTotalTimeInForeground();
            }
            else {
                statistic.add(new DetailInfo(currentDayStr, timeOfUseByDay));
                currentDayStr = tempCurrentDayStr;
                timeOfUseByDay = 0;
                i--;
            }
        }
    }

    private void setStatistic(List<DetailInfo> statistic){
        ArrayList<BarEntry> yVals = new ArrayList<>();

        for (int i = 0; i < statistic.size() - 1; i++) {
            yVals.add(new BarEntry(i, statistic.get(i).getTimeOfUse()/(1000*60)));
        }

        BarDataSet barDataSet = new BarDataSet(yVals, "");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setDrawValues(true);

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barChart.invalidate();
    }

    private void bindAppDetailInfoToUI(){
        appIconImage.setImageDrawable(appInfo.getAppIcon());
        timeOfUseText.setText(appInfo.getTimeOfUse());
        lastTimeUsedText.setText(appInfo.getLastTimeUsed());
        appNameText.setText(appInfo.getAppName());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.intervalDailyButton:
                setStatistic(dailyStatistic);
                break;
            case R.id.intervalWeeklyButton:
                setStatistic(weeklyStatistic);
                break;
            case R.id.intervalMonthlyButton:
                setStatistic(monthlyStatistic);
                break;
            case R.id.intervalYearlyButton:
                setStatistic(yearlyStatistic);
                break;
        }
    }
}
