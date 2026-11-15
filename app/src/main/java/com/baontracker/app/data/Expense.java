package com.baontracker.app.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "expenses",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "user_id",
                        childColumns = "user_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Category.class,
                        parentColumns = "category_id",
                        childColumns = "category_id")
        },
        indices = {@Index("user_id"), @Index("category_id")})
public class Expense {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "expense_id")
    public int expenseId;

    @ColumnInfo(name = "user_id")
    public int userId;

    @ColumnInfo(name = "category_id")
    public int categoryId;

    @ColumnInfo(name = "amount")
    public double amount;

    @ColumnInfo(name = "expense_date")
    public long expenseDate;

    @ColumnInfo(name = "note")
    public String note;
}
