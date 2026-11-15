package com.baontracker.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baontracker.app.R;
import com.baontracker.app.data.AppDatabase;
import com.baontracker.app.data.Category;
import com.baontracker.app.data.ExpenseItem;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class ExpensesFragment extends Fragment {

    private static final int ALL = -1;

    private ChipGroup chipGroup;
    private TextView tvEmpty;
    private ExpenseAdapter adapter;
    private int selectedCategoryId = ALL;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_expenses, container, false);
        chipGroup = v.findViewById(R.id.chipGroup);
        tvEmpty = v.findViewById(R.id.tvEmpty);

        RecyclerView rv = v.findViewById(R.id.rvExpenses);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ExpenseAdapter(true, item -> startActivity(
                new Intent(requireContext(), AddExpenseActivity.class)
                        .putExtra(AddExpenseActivity.EXTRA_EXPENSE_ID, item.expenseId)));
        rv.setAdapter(adapter);

        addChip(inflater, "All", ALL);
        for (Category c : AppDatabase.get(requireContext()).categoryDao().getAll()) {
            addChip(inflater, c.name, c.categoryId);
        }
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            Chip chip = group.findViewById(checkedIds.get(0));
            selectedCategoryId = (int) chip.getTag();
            load();
        });

        v.findViewById(R.id.btnAdd).setOnClickListener(x ->
                startActivity(new Intent(requireContext(), AddExpenseActivity.class)));
        return v;
    }

    private void addChip(LayoutInflater inflater, String label, int categoryId) {
        Chip chip = (Chip) inflater.inflate(R.layout.item_chip, chipGroup, false);
        chip.setId(View.generateViewId());
        chip.setText(label);
        chip.setTag(categoryId);
        chipGroup.addView(chip);
        if (categoryId == selectedCategoryId) chipGroup.check(chip.getId());
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    private void load() {
        AppDatabase db = AppDatabase.get(requireContext());
        List<ExpenseItem> list = selectedCategoryId == ALL
                ? db.expenseDao().getAll()
                : db.expenseDao().getByCategory(selectedCategoryId);
        adapter.setItems(list);

        if (list.isEmpty()) {
            tvEmpty.setText(selectedCategoryId == ALL
                    ? "No expenses yet. Tap + Add Expense to start."
                    : "No expenses in this category yet.");
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }
    }
}
