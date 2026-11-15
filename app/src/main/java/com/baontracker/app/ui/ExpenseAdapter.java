package com.baontracker.app.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.baontracker.app.R;
import com.baontracker.app.data.ExpenseItem;
import com.baontracker.app.util.DateUtil;
import com.baontracker.app.util.Money;

import java.util.ArrayList;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.Holder> {

    public interface OnExpenseClick {
        void onClick(ExpenseItem item);
    }

    private final List<ExpenseItem> items = new ArrayList<>();
    private final boolean showDayHeaders;
    private final OnExpenseClick listener;

    /** showDayHeaders: true on Expenses (Today, Yesterday...), false on Home. */
    public ExpenseAdapter(boolean showDayHeaders, OnExpenseClick listener) {
        this.showDayHeaders = showDayHeaders;
        this.listener = listener;
    }

    public void setItems(List<ExpenseItem> list) {
        items.clear();
        items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int position) {
        ExpenseItem item = items.get(position);

        boolean firstOfDay = position == 0
                || DateUtil.startOfDay(items.get(position - 1).expenseDate)
                != DateUtil.startOfDay(item.expenseDate);
        h.header.setVisibility(showDayHeaders && firstOfDay ? View.VISIBLE : View.GONE);
        h.header.setText(DateUtil.dayLabel(item.expenseDate));

        boolean hasNote = item.note != null && !item.note.trim().isEmpty();
        h.title.setText(hasNote ? item.note : item.categoryName);

        String when = showDayHeaders
                ? DateUtil.time(item.expenseDate)
                : DateUtil.dayLabel(item.expenseDate) + ", " + DateUtil.time(item.expenseDate);
        h.subtitle.setText(item.categoryName + ", " + when);
        h.amount.setText(Money.format(item.amount));

        h.card.setOnClickListener(v -> listener.onClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        final TextView header, title, subtitle, amount;
        final View card;

        Holder(View v) {
            super(v);
            header = v.findViewById(R.id.tvHeader);
            title = v.findViewById(R.id.tvTitle);
            subtitle = v.findViewById(R.id.tvSubtitle);
            amount = v.findViewById(R.id.tvAmount);
            card = v.findViewById(R.id.card);
        }
    }
}
