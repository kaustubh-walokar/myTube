package com.cmpe277.lab2.mytube;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthUtil;

import java.util.Date;

public class Sessions {

    public static final String NAME = "name";
    public static final String EMAIL = "email";
    private static final String NAME_PREFRANCES = "MyTubePreferences";
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String DISCONNECTED = "IsDisconnected";
    private static final String TOKEN = "Token";
    private static final String LAST_REFRESH = "Last Refresh" ;
    SharedPreferences sharedPreferences;
    Editor theEditor;
    Context theContext;
    int PRIVATE_MODE = 0;

    public Sessions(Context context) {
        this.theContext = context;
        sharedPreferences = theContext.getSharedPreferences(NAME_PREFRANCES, PRIVATE_MODE);
        theEditor = sharedPreferences.edit();
    }

    public void createLoginSession(String name, String email) {
        theEditor.putBoolean(IS_LOGIN, true);
        theEditor.putString(NAME, name);
        theEditor.putString(EMAIL, email);
        theEditor.commit();
        Intent i = new Intent(theContext, HomeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        theContext.startActivity(i);
    }

    public boolean checkForLogin() {
        if (this.isLoggedIn()) {
            Intent i = new Intent(theContext, HomeActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            theContext.startActivity(i);
            return true;
        }
        return false;
    }

    public String getToken() {
        String token = sharedPreferences.getString(TOKEN, "");
        long lastRef = sharedPreferences.getLong(LAST_REFRESH, 0);
        if (token.equalsIgnoreCase("")) {
            token = requestAccessToken();
            setToken(token);
        }
        return token;
    }

    public void setToken(String token) {
        Log.d(Values.TAG, "Token Set:" + token);
        theEditor.putString(TOKEN, token);
        long date = (new Date(System.currentTimeMillis()).getTime());
        theEditor.putLong(LAST_REFRESH, date);
        theEditor.commit();
    }

    public String getLoggedInMail() {
        return sharedPreferences.getString(EMAIL, "");
    }

    public void logoutUser() {
        clearUserDetails();
        Intent i = new Intent(theContext, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        theContext.startActivity(i);
    }

    public void clearUserDetails() {
        theEditor.remove(IS_LOGIN);
        theEditor.remove(NAME);
        theEditor.remove(EMAIL);
        theEditor.remove(TOKEN);
        theEditor.commit();

    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(IS_LOGIN, false);
    }

    public boolean isDisconnected() {
        return sharedPreferences.getBoolean(DISCONNECTED, true);
    }

    public void setIsDisconnected(boolean value) {
        theEditor.putBoolean(DISCONNECTED, value);
        theEditor.commit();
    }

    public String requestAccessToken() {
        String accessToken = "";
        try {
            accessToken = GoogleAuthUtil.getToken(theContext, getLoggedInMail(), Values.SCOPE_STRING);
        } catch (Exception e) {
            Log.e(Values.TAG, "Exception in Access Token Request:", e);
        }
        return accessToken;
    }
}
