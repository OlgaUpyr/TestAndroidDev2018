package com.example.android.testtask.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.android.testtask.R;
import com.goodiebag.pinview.Pinview;

public class UiUtil {
    public static String nameOfLastStoppedActivity = "";
    public static boolean isPINCodeActive = false;

    public static void promptPINCode(final Context context, final String pin){
        isPINCodeActive = true;

        LayoutInflater li = LayoutInflater.from(context);
        View pinView = li.inflate(R.layout.dialog_pin, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        alertDialogBuilder.setView(pinView);

        final Pinview pinCode = (Pinview) pinView.findViewById(R.id.pinCodeView);

        alertDialogBuilder
                .setTitle("Enter pin code")
                .setCancelable(false)
                .setPositiveButton("OK", null);
        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button okButton = ((AlertDialog) alertDialog).getButton(AlertDialog.BUTTON_POSITIVE);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String enterPin = pinCode.getValue();
                        if(!enterPin.equals(pin)){
                            Toast.makeText(context, "Wrong pin code!", Toast.LENGTH_LONG).show();
                        }
                        else{
                            isPINCodeActive = false;
                            alertDialog.dismiss();
                        }
                    }
                });
            }
        });

        alertDialog.show();
    }
}
