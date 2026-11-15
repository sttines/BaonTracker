package com.baontracker.app.data;

import androidx.room.ColumnInfo;

/** One expense joined with its category name, used for lists. */
public class ExpenseItem {
    @ColumnInfo(name = "expense_id")
    public int expenseId;

    @ColumnInfo(name = "amount")
    public double amount;

    @ColumnInfo(name = "expense_date")
    public long expenseDate;

    @ColumnInfo(name = "note")
    public String note;

    @ColumnInfo(name = "category_name")
    public String categoryName;
}
