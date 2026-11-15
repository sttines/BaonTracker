package com.baontracker.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.baontracker.app.R;
import com.baontracker.app.data.AppDatabase;
import com.baontracker.app.data.CategoryTotal;
import com.baontracker.app.data.User;
import com.baontracker.app.data.WeekStats;
import com.baontracker.app.util.DateUtil;
import com.baontracker.app.util.Money;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.List;

public class SummaryFragment extends Fragment {

    private TextView tvWeekRange, tvTotal, tvOfAllowance, tvEmpty;
    private LinearProgressIndicator progressTotal;
    private LinearLayout categoryContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_summary, container, false);
        tvWeekRange = v.findViewById(R.id.tvWeekRange);
        tvTotal = v.findViewById(R.id.tvTotal);
        tvOfAllowance = v.findViewById(R.id.tvOfAllowance);
        tvEmpty = v.findViewById(R.id.tvEmpty);
        progressTotal = v.findViewById(R.id.progressTotal);
        categoryContainer = v.findViewById(R.id.categoryContainer);
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
        tvWeekRange.setText("This week: " + DateUtil.shortDate(s.weekStart)
                + " to " + DateUtil.shortDate(DateUtil.addDays(s.weekStart, 6)));
        tvTotal.setText(Money.format(s.spent));
        tvOfAllowance.setText("of " + Money.format(s.allowance) + " allowance");
        int usedPercent = s.allowance > 0 ? (int) Math.min(100, Math.round(s.spent * 100 / s.allowance)) : 0;
        progressTotal.setProgress(usedPercent);

        categoryContainer.removeAllViews();
        List<CategoryTotal> totals = db.expenseDao().totalsSince(s.weekStart);
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        for (CategoryTotal t : totals) {
            View row = inflater.inflate(R.layout.item_category_total, categoryContainer, false);
            double percent = s.spent > 0 ? t.total * 100 / s.spent : 0;
            ((TextView) row.findViewById(R.id.tvName)).setText(t.categoryName);
            ((TextView) row.findViewById(R.id.tvAmount)).setText(Money.format(t.total));
            ((LinearProgressIndicator) row.findViewById(R.id.progress)).setProgress((int) Math.round(percent));
            ((TextView) row.findViewById(R.id.tvPercent)).setText(Math.round(percent) + "% of this week's spending");
            categoryContainer.addView(row);
        }
        tvEmpty.setVisibility(totals.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
