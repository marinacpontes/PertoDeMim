package com.app.pertodemim.network;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "UserSession", KEY_TOKEN = "auth_token", KEY_USER_ID = "user_id", KEY_USER_TYPE = "user_type";
    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void saveAuthToken(String token) { editor.putString(KEY_TOKEN, token).apply(); }
    public void saveUserId(int id) { editor.putInt(KEY_USER_ID, id).apply(); }
    public int fetchUserId() { return pref.getInt(KEY_USER_ID, -1); }
    public String fetchAuthToken() { return pref.getString(KEY_TOKEN, null); }
    public void saveUserType(String type) { editor.putString(KEY_USER_TYPE, type).apply(); }
    public String fetchUserType() { return pref.getString(KEY_USER_TYPE, null); }
    public void clearSession() { editor.clear().apply(); }
}
