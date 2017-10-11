package com.chandigarhadmin.utils;


import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.text.TextUtils;
import android.widget.Toast;

public class Constant {
    public static final String USERNAME = "diamantedesk";
  //  public static final String USERNAME = "admin";
    public static final String WHITE_SPACE = " ";
    public static final String INPUT_FIRST_NAME = "first_name";
    public static final String INPUT_LAST_NAME = "last_name";
    public static final String INPUT_EMAIL = "email";
    public static final String INPUT_TICKET_DATA = "ticket_data";
    public static final String INPUT_CTICKET_DATA = "cticket_data";
    public static final String SELECTED_LOCALE = "selected_locale";
    public static final String SELECTED_LOCALE_LANGUAGE = "selected_locale_language";
  //  public static final String BASE = "http://35.202.56.111/api/rest/latest/desk/";
    public static final String BASE = "http://104.154.217.246/api/rest/latest/desk/";
  //  public static final String PASSWORD = "ee47ba573ef6205fa11b0bcb5e0d959db02f36a5";
   // public static final String PASSWORDH = "545a02153193941c486d9a083d0e0f260711a5aa";
    public static final String PASSWORDH = "7c19496a1a40bcee51ebeefbaaed9d115d304d81";
    public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static final String AI_CONFIGURATION_TOKEN="04fcf37688c1491bbc1aa39128923365";
    public static ProgressDialog createDialog(Context context, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        if (null != message) {
            progressDialog.setMessage(message);
        } else {
            progressDialog.setMessage("Please wait");
        }
        progressDialog.setCancelable(true);
        return progressDialog;
    }

    //checking for internet connectivity status
    public static boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public static void showToastMessage(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    //checking whether is null or valid
    public static boolean checkString(String st) {
        if (st == null || st.equals(null)
                || st.equalsIgnoreCase("null") || TextUtils.isEmpty(st)) {
            return false;
        } else {
            return true;
        }
    }
}

