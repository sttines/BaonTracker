package com.baontracker.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.baontracker.app.R;
import com.baontracker.app.data.AppDatabase;
import com.baontracker.app.data.User;
import com.baontracker.app.util.PinUtil;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/** First screen. Sends new users to Set Up, and asks returning users for their PIN. */
public class PinActivity extends AppCompatActivity {

    private User user;
    private TextInputLayout tilPin;
    private TextInputEditText etPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = AppDatabase.get(this).userDao().getUser();
        if (user == null) {
            startActivity(new Intent(this, SetupActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_pin);
        tilPin = findViewById(R.id.tilPin);
        etPin = findViewById(R.id.etPin);
        TextView tvGreeting = findViewById(R.id.tvGreeting);

        String firstName = user.fullName.trim().split("\\s+")[0];
        tvGreeting.setText("Hi, " + firstName + "! Enter your PIN.");

        findViewById(R.id.btnUnlock).setOnClickListener(v -> unlock());
        etPin.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                unlock();
                return true;
            }
            return false;
        });
    }

    private void unlock() {
        String pin = etPin.getText() == null ? "" : etPin.getText().toString();
        if (!PinUtil.isValid(pin)) {
            tilPin.setError("Enter your 4 digit PIN");
            return;
        }
        if (!PinUtil.hash(pin).equals(user.pinHash)) {
            tilPin.setError("Wrong PIN, try again");
            etPin.setText("");
            return;
        }
        tilPin.setError(null);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
