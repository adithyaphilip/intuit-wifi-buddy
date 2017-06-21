package com.aaphilip.tools.autowifilogin.util;

@SuppressWarnings("WeakerAccess") // since we may use constants not yet used outside
public class Constants {
    public static class Prefs {
        public static final String KEY_LOCAL_AUTH = "LOCAL_AUTH";

        private static final String USERNAME_PREFIX = "USR_";
        private static final String PASSWORD_PREFIX = "PWD_";
        public static final String KEY_DOMAIN_SET = "DOMAIN_SET";
        public static final String PREF_FILE = "PREFERENCES";

        public static String getUsernameKey(String domain) {
            return USERNAME_PREFIX + domain;
        }

        public static String getPasswordKey(String domain) {
            return PASSWORD_PREFIX + domain;
        }
    }

    public static final String WIFI_NAME = "Guest";
    public static final String LOGIN_URL = "https://guestportal.intuit.com/login.html";
    public static final String INTUIT_USERNAME = "Guest";
    public static final String INTUIT_PASSWORD = "intuit";


    public static int CONNECTIVITY_WAIT_MS = 3000;

    public static final boolean LOG_ENABLED = true;
    public static final String APP_DIR = "IntuitWifiBuddy";
    public static final String LOG_FILE = "log.csv";
    public static final int MAX_LOG_SIZE_KB = 10 * 1024;

}
