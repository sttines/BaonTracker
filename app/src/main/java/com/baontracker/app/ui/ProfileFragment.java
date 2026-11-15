package com.baontracker.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.baontracker.app.R;
import com.baontracker.app.data.AppDatabase;
import com.baontracker.app.data.User;
import com.baontracker.app.util.Money;
import com.baontracker.app.util.PinUtil;
import com.baontracker.app.util.Prefs;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ProfileFragment extends Fragment {

    private TextView tvInitials, tvName, tvAllowance;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        tvInitials = v.findViewById(R.id.tvInitials);
        tvName = v.findViewById(R.id.tvName);
        tvAllowance = v.findViewById(R.id.tvAllowance);

        v.findViewById(R.id.rowAddAllowance).setOnClickListener(x ->
                startActivity(new Intent(requireContext(), AddAllowanceActivity.class)));
        v.findViewById(R.id.rowChangePin).setOnClickListener(x -> showChangePinDialog());

        MaterialSwitch switchWarning = v.findViewById(R.id.switchWarning);
        switchWarning.setChecked(Prefs.isWarningOn(requireContext()));
        switchWarning.setOnCheckedChangeListener((button, checked) ->
                Prefs.setWarningOn(requireContext(), checked));

        v.findViewById(R.id.btnLock).setOnClickListener(x -> {
            Intent intent = new Intent(requireContext(), PinActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        User user = AppDatabase.get(requireContext()).userDao().getUser();
        if (user == null) return;
        tvInitials.setText(initials(user.fullName));
        tvName.setText(user.fullName);
        tvAllowance.setText("Weekly allowance: " + Money.format(user.weeklyAllowance)
                + ", every " + user.weekStartDay);
    }

    private static String initials(String name) {
        StringBuilder sb = new StringBuilder();
        for (String part : name.trim().split("\\s+")) {
            if (!part.isEmpty() && sb.length() < 2) sb.append(Character.toUpperCase(part.charAt(0)));
        }
        return sb.toString();
    }

    private static String text(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    private void showChangePinDialog() {
        View dv = getLayoutInflater().inflate(R.layout.dialog_change_pin, null);
        TextInputLayout tilCurrent = dv.findViewById(R.id.tilCurrent);
        TextInputLayout tilNew = dv.findViewById(R.id.tilNew);
        TextInputLayout tilConfirm = dv.findViewById(R.id.tilConfirm);
        TextInputEditText etCurrent = dv.findViewById(R.id.etCurrent);
        TextInputEditText etNew = dv.findViewById(R.id.etNew);
        TextInputEditText etConfirm = dv.findViewById(R.id.etConfirm);

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Change PIN")
                .setView(dv)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", null)
                .create();

        // Set the click here so the dialog stays open when there is an error.
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(b -> {
            AppDatabase db = AppDatabase.get(requireContext());
            User user = db.userDao().getUser();
            tilCurrent.setError(null);
            tilNew.setError(null);
            tilConfirm.setError(null);

            if (!PinUtil.hash(text(etCurrent)).equals(user.pinHash)) {
                tilCurrent.setError("Current PIN is wrong");
                return;
            }
            if (!PinUtil.isValid(text(etNew))) {
                tilNew.setError("PIN must be 4 digits");
                return;
            }
            if (!text(etNew).equals(text(etConfirm))) {
                tilConfirm.setError("PINs do not match");
                return;
            }
            user.pinHash = PinUtil.hash(text(etNew));
            db.userDao().update(user);
            Toast.makeText(requireContext(), "PIN changed", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }));
        dialog.show();
    }
}
