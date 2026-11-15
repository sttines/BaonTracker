package com.baontracker.app.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.baontracker.app.R;
import com.baontracker.app.data.Allowance;
import com.baontracker.app.data.AppDatabase;
import com.baontracker.app.data.User;
import com.baontracker.app.util.DateUtil;
import com.baontracker.app.util.Money;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

public class AddAllowanceActivity extends AppCompatActivity {

    private TextInputLayout tilAmount;
    private TextInputEditText etAmount, etDate, etSource;
    private final Calendar chosen = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_allowance);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        tilAmount = findViewById(R.id.tilAmount);
        etAmount = findViewById(R.id.etAmount);
        etDate = findViewById(R.id.etDate);
        etSource = findViewById(R.id.etSource);

        // Fill in the usual weekly amount to save typing.
        User user = AppDatabase.get(this).userDao().getUser();
        etAmount.setText(Money.plain(user.weeklyAllowance));
        etSource.setText("Parents");
        etDate.setText(DateUtil.fieldDate(chosen.getTimeInMillis()));
        etDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(this, (view, y, m, d) -> {
                chosen.set(y, m, d);
                etDate.setText(DateUtil.fieldDate(chosen.getTimeInMillis()));
            }, chosen.get(Calendar.YEAR), chosen.get(Calendar.MONTH), chosen.get(Calendar.DAY_OF_MONTH));
            dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            dialog.show();
        });

        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());
        findViewById(R.id.btnSave).setOnClickListener(v -> save(user));
    }

    private void save(User user) {
        String amountText = etAmount.getText() == null ? "" : etAmount.getText().toString();
        String error = Money.validate(amountText);
        tilAmount.setError(error);
        if (error != null) return;

        String source = etSource.getText() == null ? "" : etSource.getText().toString().trim();
        Allowance a = new Allowance();
        a.userId = user.userId;
        a.amount = Money.parse(amountText);
        a.dateReceived = chosen.getTimeInMillis();
        a.source = source.isEmpty() ? "Others" : source;
        AppDatabase.get(this).allowanceDao().insert(a);

        Toast.makeText(this, "Allowance added", Toast.LENGTH_SHORT).show();
        finish();
    }
}
