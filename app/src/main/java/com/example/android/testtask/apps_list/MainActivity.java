package com.example.android.testtask.apps_list;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.android.testtask.R;
import com.example.android.testtask.detail.DetailActivity;
import com.example.android.testtask.model.AppInfo;
import com.example.android.testtask.pin.CreatePinActivity;
import com.example.android.testtask.pin.PinActivity;
import com.example.android.testtask.util.UiUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        AppAdapter.AppAdapterOnItemClickHandler {
    private UsageStatsManager usageStatsManager;
    private RecyclerView recyclerView;
    private AppAdapter appAdapter;
    private List<AppInfo> appUsageStatsList;
    private DateFormat dateFormat = new SimpleDateFormat();
    private static final long DEFAULT_NUM_OF_MILLIS = 10800000;

    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getSharedPreferences("PREFS", 0);
        String pin = sharedPreferences.getString("pin", "");

        if ((UiUtil.nameOfLastStoppedActivity.equals(this.getClass().getName())
                && !UiUtil.nameOfLastStoppedActivity.equals(PinActivity.class.getName()))
                && !UiUtil.nameOfLastStoppedActivity.equals(CreatePinActivity.class.getName())) {
            if (!UiUtil.isPINCodeActive)
                UiUtil.promptPINCode(this, pin);
        }
    }

    protected void onPause() {
        super.onPause();

        UiUtil.nameOfLastStoppedActivity = this.getClass().getName();
    }

    public void onStop () {
        super.onStop();

        UiUtil.nameOfLastStoppedActivity = this.getClass().getName();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.apps_list_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);

        getAppsInfo();

        appAdapter = new AppAdapter(this, this, appUsageStatsList);
        recyclerView.addItemDecoration(new GridItemDecoration(2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(appAdapter);
    }

    @Override
    public void onItemClick(AppInfo appInfo) {
        Intent appDetailIntent = new Intent(MainActivity.this, DetailActivity.class);
        appDetailIntent.putExtra(AppInfo.class.getCanonicalName(), appInfo);
        startActivity(appDetailIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void getAppsInfo() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);

        usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> queryUsageStats = usageStatsManager
                .queryUsageStats(UsageStatsManager.INTERVAL_DAILY, calendar.getTimeInMillis(),
                        System.currentTimeMillis());

        if (queryUsageStats.size() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.error_dialog_message)
                    .setTitle(R.string.error_dialog_title)
                    .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else {
            Collections.sort(queryUsageStats, new LastTimeUsedComparatorDesc());
            appUsageStatsList = new ArrayList<>();
            for (int i = 0; i < queryUsageStats.size(); i++) {
                if(!(isPackageNameExist(appUsageStatsList, queryUsageStats.get(i).getPackageName()))) {
                    AppInfo appInfo = new AppInfo();
                    appInfo.setPackageName(queryUsageStats.get(i).getPackageName());
                    String appName = getAppNameFromPackage(appInfo.getPackageName(), MainActivity.this);
                    if (appName != null) {
                        appInfo.setAppName(appName);
                    } else {
                        appInfo.setAppName(appInfo.getPackageName());
                        continue;
                    }
                    queryUsageStats.get(i).getLastTimeUsed();
                    long timeInForeground = queryUsageStats.get(i).getTotalTimeInForeground();
                    String timeOfUse = getTimeOfUseText(timeInForeground);
                    appInfo.setTimeOfUse(timeOfUse);
                    long lastTimeOfUse = queryUsageStats.get(i).getLastTimeUsed();
                    if(lastTimeOfUse <= DEFAULT_NUM_OF_MILLIS) appInfo.setLastTimeUsed("-");
                    else appInfo.setLastTimeUsed(dateFormat.format(new Date(lastTimeOfUse)));
                    try {
                        Drawable appIcon = MainActivity.this.getPackageManager()
                                .getApplicationIcon(appInfo.getPackageName());
                        appInfo.setAppIcon(appIcon);
                    } catch (PackageManager.NameNotFoundException e) {
                    }
                    appUsageStatsList.add(appInfo);
                }
            }
        }
    }

    private boolean isPackageNameExist(List<AppInfo> appInfoList, String packageName) {
        for(AppInfo app : appInfoList) {
            if(app.getPackageName().equals(packageName))
                return true;
        }
        return false;
    }

    public static class LastTimeUsedComparatorDesc implements Comparator<UsageStats> {
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public int compare(UsageStats left, UsageStats right) {
            return Long.compare(right.getLastTimeUsed(), left.getLastTimeUsed());
        }
    }

    static public String getAppNameFromPackage(String packageName, Context context) {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> pkgAppsList = context.getPackageManager()
                .queryIntentActivities(mainIntent, 0);

        for (ResolveInfo app : pkgAppsList) {
            if (app.activityInfo.packageName.equals(packageName)) {
                return app.activityInfo.loadLabel(context.getPackageManager()).toString();
            }
        }
        return null;
    }

    static public String getTimeOfUseText(long timeInForeground) {
        String result = "";
        int hours = (int) ((timeInForeground/(1000*60*60))%24);
        int minutes = (int) ((timeInForeground/(1000*60))%60);
        int seconds = (int) (timeInForeground/1000)%60;
        if(hours != 0)
            result += hours + "h";
        if(minutes != 0)
            result += minutes + "m";
        if(seconds != 0)
            result += seconds + "s";
        if(hours == 0 && minutes == 0 && seconds == 0)
            result = "-";
        return result;
    }

    public class GridItemDecoration extends RecyclerView.ItemDecoration {
        private int spanCount;

        public GridItemDecoration(int spanCount) {
            this.spanCount = spanCount;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int column = position % spanCount;

            outRect.left = 0;
            outRect.right = - column - 1;
            if (position >= spanCount) {
                outRect.top = 0;
            }
        }
    }
}
