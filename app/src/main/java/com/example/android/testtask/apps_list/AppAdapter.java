package com.example.android.testtask.apps_list;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.testtask.R;
import com.example.android.testtask.model.AppInfo;

import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AppAdapterViewHolder>{
    private final Context context;
    private final AppAdapterOnItemClickHandler clickHandler;
    private List<AppInfo> appInfoList;

    AppAdapter(@NonNull Context context, AppAdapterOnItemClickHandler clickHandler, List<AppInfo> appInfoList) {
        this.context = context;
        this.clickHandler = clickHandler;
        this.appInfoList = appInfoList;
    }

    @Override
    public AppAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.app_card, viewGroup, false);
        itemView.setFocusable(true);
        return new AppAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AppAdapter.AppAdapterViewHolder holder, int position) {
        AppInfo appInfo = appInfoList.get(position);
        holder.appNameText.setText(appInfo.getAppName());
        holder.timeText.setText("TIME OF USE: " + appInfo.getTimeOfUse());
        holder.percentageText.setText("LAST TIME: " + appInfo.getLastTimeUsed());
        holder.appIconImage.setImageDrawable(appInfo.getAppIcon());
    }

    @Override
    public int getItemCount() {
        if (appInfoList == null) return 0;
        return appInfoList.size();
    }

    public interface AppAdapterOnItemClickHandler {
        void onItemClick(AppInfo appInfo);
    }

    class AppAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView appIconImage;
        final TextView appNameText, timeText, percentageText;

        AppAdapterViewHolder(View view) {
            super(view);

            appIconImage = view.findViewById(R.id.app_icon);
            appNameText = view.findViewById(R.id.app_name);
            timeText = view.findViewById(R.id.time_of_use);
            percentageText = view.findViewById(R.id.percentage_of_use);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            AppInfo appInfo = appInfoList.get(adapterPosition);
            clickHandler.onItemClick(appInfo);
        }
    }
}
