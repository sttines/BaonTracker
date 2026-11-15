package com.baontracker.app.data;

import com.baontracker.app.util.DateUtil;

/** Numbers for the current allowance week, shared by Home and Summary. */
public class WeekStats {
    public long weekStart;
    public double allowance;
    public double spent;
    public double remaining;
    public double spentToday;
    public int daysLeft;
    public double dailyLimit;

    public static WeekStats compute(AppDatabase db, User user) {
        WeekStats s = new WeekStats();
        s.weekStart = DateUtil.weekStart(user.weekStartDay);
        s.allowance = db.allowanceDao().sumSince(s.weekStart);
        s.spent = db.expenseDao().sumSince(s.weekStart);
        s.remaining = s.allowance - s.spent;
        s.spentToday = db.expenseDao().sumSince(DateUtil.startOfToday());
        s.daysLeft = DateUtil.daysLeftInWeek(user.weekStartDay);
        s.dailyLimit = s.remaining > 0 ? s.remaining / s.daysLeft : 0;
        return s;
    }

    /** True when spending has reached 80% of the allowance. */
    public boolean isNearLimit() {
        return allowance > 0 && spent >= allowance * 0.8;
    }
}
