package com.example.android.testtask.model;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public class AppInfo implements Parcelable {
    private String packageName;
    private String appName;
    private String timeOfUse;
    private String lastTimeUsed;
    private Drawable appIcon;

    public AppInfo(){ }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getTimeOfUse() {
        return timeOfUse;
    }

    public void setTimeOfUse(String timeOfUse) {
        this.timeOfUse = timeOfUse;
    }

    public String getLastTimeUsed() {
        return lastTimeUsed;
    }

    public void setLastTimeUsed(String lastTimeUsed) {
        this.lastTimeUsed = lastTimeUsed;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(packageName);
        parcel.writeString(appName);
        parcel.writeString(timeOfUse);
        parcel.writeString(lastTimeUsed);
    }

    public static final Parcelable.Creator<AppInfo> CREATOR = new Parcelable.Creator<AppInfo>() {
        public AppInfo createFromParcel(Parcel in) {
            return new AppInfo(in);
        }

        public AppInfo[] newArray(int size) {
            return new AppInfo[size];
        }
    };

    private AppInfo(Parcel parcel) {
        packageName = parcel.readString();
        appName = parcel.readString();
        timeOfUse = parcel.readString();
        lastTimeUsed = parcel.readString();
    }
}
