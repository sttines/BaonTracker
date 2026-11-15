package com.baontracker.app.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "allowances",
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "user_id",
                childColumns = "user_id",
                onDelete = ForeignKey.CASCADE),
        indices = @Index("user_id"))
public class Allowance {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "allowance_id")
    public int allowanceId;

    @ColumnInfo(name = "user_id")
    public int userId;

    @ColumnInfo(name = "amount")
    public double amount;

    @ColumnInfo(name = "date_received")
    public long dateReceived;

    @ColumnInfo(name = "source")
    public String source;
}
