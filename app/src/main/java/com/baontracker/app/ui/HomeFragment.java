package com.baontracker.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baontracker.app.R;
import com.baontracker.app.data.AppDatabase;
import com.baontracker.app.data.ExpenseItem;
import com.baontracker.app.data.User;
import com.baontracker.app.data.WeekStats;
import com.baontracker.app.util.Money;
import com.baontracker.app.util.Prefs;

import java.util.List;

public class HomeFragment extends Fragment {

    private TextView tvRemaining, tvAllowanceInfo, tvSpentToday, tvDailyLimit, tvWarning, tvEmpty;
    private ExpenseAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        tvRemaining = v.findViewById(R.id.tvRemaining);
        tvAllowanceInfo = v.findViewById(R.id.tvAllowanceInfo);
        tvSpentToday = v.findViewById(R.id.tvSpentToday);
        tvDailyLimit = v.findViewById(R.id.tvDailyLimit);
        tvWarning = v.findViewById(R.id.tvWarning);
        tvEmpty = v.findViewById(R.id.tvEmpty);

        RecyclerView rv = v.findViewById(R.id.rvRecent);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ExpenseAdapter(false, item -> startActivity(
                new Intent(requireContext(), AddExpenseActivity.class)
                        .putExtra(AddExpenseActivity.EXTRA_EXPENSE_ID, item.expenseId)));
        rv.setAdapter(adapter);

        v.findViewById(R.id.btnAdd).setOnClickListener(x ->
                startActivity(new Intent(requireContext(), AddExpenseActivity.class)));
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    private void load() {
        AppDatabase db = AppDatabase.get(requireContext());
        User user = db.userDao().getUser();
        if (user == null) return;

        WeekStats s = WeekStats.compute(db, user);
        tvRemaining.setText(Money.format(s.remaining));
        tvRemaining.setTextColor(ContextCompat.getColor(requireContext(),
                s.remaining < 0 ? R.color.error : R.color.text_primary));
        tvAllowanceInfo.setText("of " + Money.format(s.allowance) + " this week");
        tvSpentToday.setText(Money.format(s.spentToday));
        tvDailyLimit.setText(Money.format(s.dailyLimit));

        if (Prefs.isWarningOn(requireContext()) && s.isNearLimit()) {
            if (s.remaining < 0) {
                tvWarning.setText("You are over this week's allowance by " + Money.format(-s.remaining) + ".");
            } else {
                int percent = (int) Math.round(s.spent * 100 / s.allowance);
                tvWarning.setText("Heads up! You have used " + percent + "% of this week's allowance.");
            }
            tvWarning.setVisibility(View.VISIBLE);
        } else {
            tvWarning.setVisibility(View.GONE);
        }

        List<ExpenseItem> recent = db.expenseDao().getRecent(3);
        adapter.setItems(recent);
        tvEmpty.setVisibility(recent.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
