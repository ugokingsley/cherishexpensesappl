package cherish.expensetracker.userinterface.expenses;

import android.os.Bundle;

import cherish.expensetracker.R;
import cherish.expensetracker.userinterface.BaseActivity;

public class DetailExpenseActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        String expenseId = getIntent().getStringExtra(DetailExpenseFragment.EXPENSE_ID_KEY);
        replaceFragment(DetailExpenseFragment.newInstance(expenseId), false);
    }

}
