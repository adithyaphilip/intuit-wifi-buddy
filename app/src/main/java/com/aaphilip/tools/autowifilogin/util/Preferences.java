package com.aaphilip.tools.autowifilogin.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashSet;
import java.util.Set;

public class Preferences {
    private static Preferences sPreferences;
    private SharedPreferences mSharedPreferences;

    private Preferences(Context context) {
        mSharedPreferences = context.getSharedPreferences(Constants.Prefs.PREF_FILE, Context.MODE_PRIVATE);
    }

    /**
     * Implements a singleton pattern, by keeping only one Preferences object active at a time
     * @return a Preferences object, either newly created and cached, or already cached
     */
    public static Preferences getPreferences(@NonNull Context context) {
        if (sPreferences == null) {
            sPreferences = new Preferences(context);
        }
        return sPreferences;
    }

    public void setUsernamePassword(String domain, String username, String password) {
        String usernameKey = Constants.Prefs.getUsernameKey(domain);
        String passwordKey = Constants.Prefs.getPasswordKey(domain);

        Set<String> domainSet = getDomainSet();
        domainSet.add(domain);

        mSharedPreferences.edit().putString(usernameKey, username)
                .putString(passwordKey, password)
                .putStringSet(Constants.Prefs.KEY_DOMAIN_SET, domainSet)
                .apply();
    }

    public void setLocallyAuthenticated() {
        mSharedPreferences.edit().putBoolean(Constants.Prefs.KEY_LOCAL_AUTH, true).apply();
    }

    public boolean isLocallyAuthenticated() {
        return mSharedPreferences.getBoolean(Constants.Prefs.KEY_LOCAL_AUTH, false);
    }

    @NonNull
    public Set<String> getDomainSet() {
       return mSharedPreferences.getStringSet(Constants.Prefs.KEY_DOMAIN_SET, new HashSet<String>());
    }

    @Nullable
    public String getUsername(String domain) {
        String usernameKey = Constants.Prefs.getUsernameKey(domain);
        return mSharedPreferences.getString(usernameKey, null);
    }

    @Nullable
    public String getPassword(String domain) {
        String passwordKey = Constants.Prefs.getPasswordKey(domain);
        return mSharedPreferences.getString(passwordKey, null);
    }
}
