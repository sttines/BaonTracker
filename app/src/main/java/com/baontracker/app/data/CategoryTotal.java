package com.baontracker.app.data;

import androidx.room.ColumnInfo;

/** Total spent for one category, used on the Summary screen. */
public class CategoryTotal {
    @ColumnInfo(name = "category_name")
    public String categoryName;

    @ColumnInfo(name = "total")
    public double total;
}
