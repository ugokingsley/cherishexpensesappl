package cherish.expensetracker.userinterface.expenses;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import cherish.expensetracker.R;
import cherish.expensetracker.entities.Expense;
import cherish.expensetracker.interface_helpers.IExpensesType;
import cherish.expensetracker.interface_helpers.IUserActionsMode;
import cherish.expensetracker.userinterface.BaseFragment;
import cherish.expensetracker.utils.RealmManager;
import cherish.expensetracker.utils.Util;


public class ExpenseDetailFragment extends BaseFragment implements View.OnClickListener {

    public static final String EXPENSE_ID_KEY = "_expense_id";
    public static final int RQ_EDIT_EXPENSE = 1001;


    private Expense expense;

    static ExpenseDetailFragment newInstance(String id) {
        ExpenseDetailFragment expenseDetailFragment = new ExpenseDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXPENSE_ID_KEY, id);
        expenseDetailFragment.setArguments(bundle);
        return expenseDetailFragment;
    }

    public ExpenseDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = onCreateFragmentView(R.layout.fragment_expense_detail, inflater, container, true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(EXPENSE_ID_KEY)) {
            setTitle(getString(R.string.exp_detail));
            String id = getArguments().getString(EXPENSE_ID_KEY);
            if( id != null) {
                expense = (Expense) RealmManager.getInstance().findById(Expense.class, id);
                loadData();

            }
        }
    }

    private void loadData() {
        TextView tvExpenseTotal = ((TextView)getView().findViewById(R.id.tv_total));
        tvExpenseTotal.setText(Util.getFormattedCurrency(expense.getTotal()));
        tvExpenseTotal.setTextColor(getResources().getColor(expense.getType() == IExpensesType.MODE_EXPENSES ? R.color.colorAccentRed : R.color.colorAccentGreen));
        ((TextView)getView().findViewById(R.id.tv_category)).setText(String.valueOf(expense.getCategory().getName()));
        ((TextView)getView().findViewById(R.id.tv_description)).setText(String.valueOf(expense.getDescription()));
        ((TextView)getView().findViewById(R.id.tv_date)).setText(Util.formatDateToString(expense.getDate(), Util.getCurrentDateFormat()));
        (getView().findViewById(R.id.fab_edit)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab_edit) {
            onEditExpense();
        }
    }

    private void onEditExpense() {
        NewExpenseFragment newExpenseFragment = NewExpenseFragment.newInstance(IUserActionsMode.MODE_UPDATE, expense.getId());
        newExpenseFragment.setTargetFragment(this, RQ_EDIT_EXPENSE);
        newExpenseFragment.show(getFragmentManager(), "EDIT_EXPENSE");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RQ_EDIT_EXPENSE && resultCode == Activity.RESULT_OK) {
            loadData();

        }
    }

}