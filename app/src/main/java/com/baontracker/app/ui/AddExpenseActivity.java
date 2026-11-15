package com.baontracker.app.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.baontracker.app.R;
import com.baontracker.app.data.AppDatabase;
import com.baontracker.app.data.Category;
import com.baontracker.app.data.Expense;
import com.baontracker.app.data.User;
import com.baontracker.app.data.WeekStats;
import com.baontracker.app.util.DateUtil;
import com.baontracker.app.util.Money;
import com.baontracker.app.util.Prefs;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/** Adds a new expense, or edits and deletes one when EXTRA_EXPENSE_ID is given. */
public class AddExpenseActivity extends AppCompatActivity {

    public static final String EXTRA_EXPENSE_ID = "expense_id";

    private AppDatabase db;
    private TextInputLayout tilAmount, tilCategory;
    private TextInputEditText etAmount, etDate, etNote;
    private MaterialAutoCompleteTextView actvCategory;
    private List<Category> categories;
    private int selectedCategoryId = -1;
    private final Calendar chosen = Calendar.getInstance();
    private Expense editing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        db = AppDatabase.get(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        tilAmount = findViewById(R.id.tilAmount);
        tilCategory = findViewById(R.id.tilCategory);
        etAmount = findViewById(R.id.etAmount);
        etDate = findViewById(R.id.etDate);
        etNote = findViewById(R.id.etNote);
        actvCategory = findViewById(R.id.actvCategory);

        categories = db.categoryDao().getAll();
        List<String> names = new ArrayList<>();
        for (Category c : categories) names.add(c.name);
        actvCategory.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names));
        actvCategory.setOnItemClickListener((parent, view, position, id) -> {
            selectedCategoryId = categories.get(position).categoryId;
            tilCategory.setError(null);
        });

        etDate.setOnClickListener(v -> pickDate());

        int expenseId = getIntent().getIntExtra(EXTRA_EXPENSE_ID, -1);
        if (expenseId != -1) {
            editing = db.expenseDao().getById(expenseId);
        }
        if (editing != null) {
            toolbar.setTitle("Edit Expense");
            etAmount.setText(Money.plain(editing.amount));
            selectedCategoryId = editing.categoryId;
            for (Category c : categories) {
                if (c.categoryId == editing.categoryId) actvCategory.setText(c.name, false);
            }
            chosen.setTimeInMillis(editing.expenseDate);
            etNote.setText(editing.note);
            View btnDelete = findViewById(R.id.btnDelete);
            btnDelete.setVisibility(View.VISIBLE);
            btnDelete.setOnClickListener(v -> confirmDelete());
        }
        updateDateText();

        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());
        findViewById(R.id.btnSave).setOnClickListener(v -> save());
    }

    private void pickDate() {
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, day) -> {
            chosen.set(year, month, day);
            updateDateText();
        }, chosen.get(Calendar.YEAR), chosen.get(Calendar.MONTH), chosen.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

    private void updateDateText() {
        etDate.setText(DateUtil.fieldDate(chosen.getTimeInMillis()));
    }

    private void save() {
        String amountText = etAmount.getText() == null ? "" : etAmount.getText().toString();
        String note = etNote.getText() == null ? "" : etNote.getText().toString().trim();

        boolean ok = true;
        String amountError = Money.validate(amountText);
        tilAmount.setError(amountError);
        if (amountError != null) ok = false;
        if (selectedCategoryId == -1) {
            tilCategory.setError("Choose a category");
            ok = false;
        }
        if (!ok) return;

        User user = db.userDao().getUser();
        WeekStats before = WeekStats.compute(db, user);

        Expense e = editing != null ? editing : new Expense();
        e.userId = user.userId;
        e.categoryId = selectedCategoryId;
        e.amount = Money.parse(amountText);
        e.expenseDate = chosen.getTimeInMillis();
        e.note = note.isEmpty() ? null : note;

        if (editing == null) {
            db.expenseDao().insert(e);
            Toast.makeText(this, "Expense saved", Toast.LENGTH_SHORT).show();
        } else {
            db.expenseDao().update(e);
            Toast.makeText(this, "Expense updated", Toast.LENGTH_SHORT).show();
        }

        // Warn once, right when spending crosses 80% of the allowance.
        WeekStats after = WeekStats.compute(db, user);
        if (Prefs.isWarningOn(this) && !before.isNearLimit() && after.isNearLimit()) {
            Toast.makeText(this, "Heads up! You have used 80% of this week's allowance.",
                    Toast.LENGTH_LONG).show();
        }
        finish();
    }

    private void confirmDelete() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete this expense?")
                .setMessage("This cannot be undone.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (d, w) -> {
                    db.expenseDao().delete(editing);
                    Toast.makeText(this, "Expense deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .show();
    }
}
