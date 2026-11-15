package com.baontracker.app.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id")
    public int userId;

    @NonNull
    @ColumnInfo(name = "full_name")
    public String fullName = "";

    @NonNull
    @ColumnInfo(name = "pin_hash")
    public String pinHash = "";

    @ColumnInfo(name = "weekly_allowance")
    public double weeklyAllowance;

    @NonNull
    @ColumnInfo(name = "week_start_day")
    public String weekStartDay = "Monday";
}
