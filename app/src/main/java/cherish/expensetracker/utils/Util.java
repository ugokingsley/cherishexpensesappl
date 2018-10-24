package cherish.expensetracker.utils;

import android.preference.PreferenceManager;
import android.widget.EditText;

import cherish.expensetracker.CherishExpenseTracker;
import cherish.expensetracker.R;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Util {

    public static String formatDateToString(Date date, String pattern) {
        DateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(date);
    }

    public static boolean isEmptyField(EditText et) {
        return (et.getText() == null || et.getText().toString().isEmpty());
    }

    public static String getFormattedCurrency(float number) {
        String countryCode = PreferenceManager.getDefaultSharedPreferences(CherishExpenseTracker.getContext()).getString(CherishExpenseTracker.getContext().getString(R.string.pref_country_key), CherishExpenseTracker.getContext().getString(R.string.def_country));
        Locale locale = new Locale("EN", countryCode);
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
        String formattedNumber = numberFormat.format(number);
        String symbol = numberFormat.getCurrency().getSymbol(locale);
        return formattedNumber.replace(symbol, symbol+" ");
    }

    public static String getCurrentDateFormat() {
        return PreferenceManager.getDefaultSharedPreferences(CherishExpenseTracker.getContext()).getString(CherishExpenseTracker.getContext().getString(R.string.date_format_key), CherishExpenseTracker.getContext().getString(R.string.default_date_format));
    }

}
