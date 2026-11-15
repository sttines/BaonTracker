package com.baontracker.app.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {User.class, Allowance.class, Category.class, Expense.class},
        version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase instance;

    public abstract UserDao userDao();
    public abstract AllowanceDao allowanceDao();
    public abstract CategoryDao categoryDao();
    public abstract ExpenseDao expenseDao();

    public static AppDatabase get(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "baon_tracker.db")
                            // The data is small and local, so main thread queries keep the code simple.
                            .allowMainThreadQueries()
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    String[] names = {"Food", "Fare", "Load", "School Supplies", "Others"};
                                    for (String name : names) {
                                        db.execSQL("INSERT INTO categories (name, is_default) VALUES (?, 1)",
                                                new Object[]{name});
                                    }
                                }
                            })
                            .build();
                }
            }
        }
        return instance;
    }
}
