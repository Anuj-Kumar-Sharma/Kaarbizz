package com.anuj55149.kaarbizz.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.anuj55149.kaarbizz.R;
import com.anuj55149.kaarbizz.dao.ServerStateDao;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DialogBoxes {

    private static ProgressDialog progressDialog;

    public static void showChangeIPDialog(View view, final Context context, final ServerStateDao serverStateDao) {
        final SharedPreference pref = new SharedPreference(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        final EditText etIpAddress = view.findViewById(R.id.etIPAddress);
        TextView tvIpOK = view.findViewById(R.id.tvIPOK);

        etIpAddress.setText(pref.getServerIPAddress());
        etIpAddress.setSelection(etIpAddress.getText().length());

        tvIpOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = etIpAddress.getText().toString();
                Pattern IP_ADDRESS
                        = Pattern.compile(
                        "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
                                + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
                                + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                                + "|[1-9][0-9]|[0-9]))");
                Matcher matcher = IP_ADDRESS.matcher(ip);
                if (matcher.matches()) {
                    dialog.dismiss();
                    pref.setServerIPAddress(ip);
                    showProgressDialog(context, "Connecting...");
                    Utilities.startServerCheck(serverStateDao);
                } else {
                    etIpAddress.setError("Incorrect IP Address");
                }
            }
        });

    }

    public static void showProgressDialog(Context context, String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.setMessage(message);
        if (!((Activity) context).isFinishing()) {
            progressDialog.show();
        }
    }

    public static void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
