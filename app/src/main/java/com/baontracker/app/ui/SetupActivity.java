package com.baontracker.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.baontracker.app.R;
import com.baontracker.app.data.Allowance;
import com.baontracker.app.data.AppDatabase;
import com.baontracker.app.data.User;
import com.baontracker.app.util.DateUtil;
import com.baontracker.app.util.Money;
import com.baontracker.app.util.PinUtil;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/** Shown only on first open to create the account. */
public class SetupActivity extends AppCompatActivity {

    private TextInputLayout tilName, tilAllowance, tilPin, tilConfirm;
    private TextInputEditText etName, etAllowance, etPin, etConfirm;
    private Spinner spDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        tilName = findViewById(R.id.tilName);
        tilAllowance = findViewById(R.id.tilAllowance);
        tilPin = findViewById(R.id.tilPin);
        tilConfirm = findViewById(R.id.tilConfirm);
        etName = findViewById(R.id.etName);
        etAllowance = findViewById(R.id.etAllowance);
        etPin = findViewById(R.id.etPin);
        etConfirm = findViewById(R.id.etConfirm);
        spDay = findViewById(R.id.spDay);

        spDay.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, DateUtil.DAYS));

        findViewById(R.id.btnCreate).setOnClickListener(v -> createAccount());
    }

    private static String text(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    private void createAccount() {
        String name = text(etName);
        String allowanceText = text(etAllowance);
        String pin = text(etPin);
        String confirm = text(etConfirm);

        boolean ok = true;
        tilName.setError(null);
        tilAllowance.setError(null);
        tilPin.setError(null);
        tilConfirm.setError(null);

        if (name.isEmpty()) {
            tilName.setError("Name is required");
            ok = false;
        }
        String amountError = Money.validate(allowanceText);
        if (amountError != null) {
            tilAllowance.setError(amountError);
            ok = false;
        }
        if (!PinUtil.isValid(pin)) {
            tilPin.setError("PIN must be 4 digits");
            ok = false;
        } else if (!pin.equals(confirm)) {
            tilConfirm.setError("PINs do not match");
            ok = false;
        }
        if (!ok) return;

        AppDatabase db = AppDatabase.get(this);
        User user = new User();
        user.fullName = name;
        user.pinHash = PinUtil.hash(pin);
        user.weeklyAllowance = Money.parse(allowanceText);
        user.weekStartDay = (String) spDay.getSelectedItem();
        int userId = (int) db.userDao().insert(user);

        // Count this week's allowance right away so Home shows a balance.
        Allowance first = new Allowance();
        first.userId = userId;
        first.amount = user.weeklyAllowance;
        first.dateReceived = System.currentTimeMillis();
        first.source = "Weekly allowance";
        db.allowanceDao().insert(first);

        Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
