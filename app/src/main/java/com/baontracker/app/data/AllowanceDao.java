package com.baontracker.app.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface AllowanceDao {
    @Insert
    long insert(Allowance allowance);

    @Query("SELECT COALESCE(SUM(amount), 0) FROM allowances WHERE date_received >= :from")
    double sumSince(long from);
}
