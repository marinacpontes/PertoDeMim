package com.app.pertodemim.network;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_TYPE = "user_type";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void saveAuthToken(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public String fetchAuthToken() {
        return pref.getString(KEY_TOKEN, null);
    }

    public void saveUserType(String type) {
        editor.putString(KEY_USER_TYPE, type);
        editor.apply();
    }

    public String fetchUserType() {
        return pref.getString(KEY_USER_TYPE, null);
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }
}
