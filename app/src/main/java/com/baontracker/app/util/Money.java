package com.baontracker.app.util;

import java.util.Locale;

public final class Money {

    private Money() {
    }

    /** Formats an amount like "₱ 1,250.00". */
    public static String format(double amount) {
        String text = String.format(Locale.US, "₱ %,.2f", Math.abs(amount));
        return amount < 0 ? "- " + text : text;
    }

    /** Plain number for input fields, like "85.00". */
    public static String plain(double amount) {
        return String.format(Locale.US, "%.2f", amount);
    }

    /** Returns an error message, or null when the amount is valid. */
    public static String validate(String text) {
        if (text == null || text.trim().isEmpty()) return "Amount is required";
        String value = text.trim();
        double amount;
        try {
            amount = Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return "Enter a valid amount";
        }
        if (amount <= 0) return "Amount must be more than 0";
        int dot = value.indexOf('.');
        if (dot >= 0 && value.length() - dot - 1 > 2) return "Use up to 2 decimal places";
        if (amount > 1_000_000) return "Amount is too large";
        return null;
    }

    public static double parse(String text) {
        return Double.parseDouble(text.trim());
    }
}
