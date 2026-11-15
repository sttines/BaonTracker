package com.baontracker.app.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ExpenseDao {
    String ITEMS = "SELECT e.expense_id, e.amount, e.expense_date, e.note, c.name AS category_name "
            + "FROM expenses e INNER JOIN categories c ON e.category_id = c.category_id";

    @Insert
    long insert(Expense expense);

    @Update
    void update(Expense expense);

    @Delete
    void delete(Expense expense);

    @Query("SELECT * FROM expenses WHERE expense_id = :id")
    Expense getById(int id);

    @Query("SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE expense_date >= :from")
    double sumSince(long from);

    @Query(ITEMS + " ORDER BY e.expense_date DESC")
    List<ExpenseItem> getAll();

    @Query(ITEMS + " WHERE e.category_id = :categoryId ORDER BY e.expense_date DESC")
    List<ExpenseItem> getByCategory(int categoryId);

    @Query(ITEMS + " ORDER BY e.expense_date DESC LIMIT :limit")
    List<ExpenseItem> getRecent(int limit);

    @Query("SELECT c.name AS category_name, SUM(e.amount) AS total "
            + "FROM expenses e INNER JOIN categories c ON e.category_id = c.category_id "
            + "WHERE e.expense_date >= :from GROUP BY c.category_id ORDER BY total DESC")
    List<CategoryTotal> totalsSince(long from);
}
