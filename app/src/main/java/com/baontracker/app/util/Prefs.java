package com.baontracker.app.util;

import android.content.Context;
import android.content.SharedPreferences;

public final class Prefs {

    private static final String NAME = "baon_prefs";
    private static final String WARNING_ON = "warning_on";

    private Prefs() {
    }

    private static SharedPreferences get(Context context) {
        return context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public static boolean isWarningOn(Context context) {
        return get(context).getBoolean(WARNING_ON, true);
    }

    public static void setWarningOn(Context context, boolean on) {
        get(context).edit().putBoolean(WARNING_ON, on).apply();
    }
}
