package cherish.expensetracker.userinterface.history;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import cherish.expensetracker.R;
import cherish.expensetracker.expenseandcategoriesadapter.ExpensesAdapter;
import cherish.expensetracker.custom.BaseViewHolder;
import cherish.expensetracker.custom.RecyclerViewItemDecorator;
import cherish.expensetracker.custom.DateSelectionFragment;
import cherish.expensetracker.custom.SparseParcelable;
import cherish.expensetracker.custom.ContentWrapRecyclerView;
import cherish.expensetracker.entities.Expense;
import cherish.expensetracker.interface_helpers.IConstants;
import cherish.expensetracker.interface_helpers.IExpensesType;
import cherish.expensetracker.interface_helpers.ISelectDateFragment;
import cherish.expensetracker.userinterface.MainActivity;
import cherish.expensetracker.userinterface.MainFragment;
import cherish.expensetracker.userinterface.expenses.DetailExpenseActivity;
import cherish.expensetracker.userinterface.expenses.DetailExpenseFragment;
import cherish.expensetracker.utils.DateManager;
import cherish.expensetracker.utils.DialogManager;
import cherish.expensetracker.utils.ExpensesManager;
import cherish.expensetracker.utils.Util;


public class HistoryFragment extends MainFragment implements BaseViewHolder.RecyclerClickListener, ISelectDateFragment {

    private RecyclerView rvHistory;

    private ExpensesAdapter mExpensesAdapter;
    private DateSelectionFragment dateSelectionFragment;

    public static final int REQUEST_WRITE_EXTERNAL_STORE = 101;

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    public HistoryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        rvHistory = (RecyclerView)rootView.findViewById(R.id.rv_history);
        dateSelectionFragment = (DateSelectionFragment)getChildFragmentManager().findFragmentById(R.id.select_date_fragment);
        dateSelectionFragment.setSelectDateFragment(this);
        setHasOptionsMenu(true);
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMainActivityListener.setMode(MainActivity.NAVIGATION_MODE_STANDARD);
        mMainActivityListener.setTitle(getString(R.string.history));

        rvHistory.setLayoutManager(new ContentWrapRecyclerView(getActivity()));
        rvHistory.setHasFixedSize(true);
        rvHistory.addItemDecoration(new RecyclerViewItemDecorator(getResources().getDimension(R.dimen.dimen_10dp)));
        rvHistory.setNestedScrollingEnabled(false);
    }


    @Override
    public void updateData() {
        float total = Expense.getCategoryTotalByDate(DateManager.getInstance().getDateFrom(), DateManager.getInstance().getDateTo(), null);
        ExpensesManager.getInstance().setExpensesList(DateManager.getInstance().getDateFrom(), DateManager.getInstance().getDateTo(), IExpensesType.MODE_EXPENSES, null);
        if ( mExpensesAdapter == null ) {
            mExpensesAdapter = new ExpensesAdapter(getActivity(), this);
            rvHistory.setAdapter(mExpensesAdapter);
        } else {
            mExpensesAdapter.updateExpenses(ExpensesManager.getInstance().getExpensesList());
        }
        dateSelectionFragment.getTextViewTotal().setText(Util.getFormattedCurrency(total));
    }

    // Action mode for cat.
    private android.view.ActionMode mActionMode;

    private android.view.ActionMode.Callback mActionModeCallback = new android.view.ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.expenses_context_menu, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete:
                    eraseExpenses();
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(android.view.ActionMode mode) {
            mExpensesAdapter.clearSelection();
            mActionMode = null;
        }
    };

    @Override
    public void onClick(RecyclerView.ViewHolder vh, int position) {
        if (mActionMode == null) {
            Expense expenseSelected = (Expense) vh.itemView.getTag();
            Intent expenseDetail = new Intent(getActivity(), DetailExpenseActivity.class);
            expenseDetail.putExtra(DetailExpenseFragment.EXPENSE_ID_KEY, expenseSelected.getId());
            startActivity(expenseDetail);
        } else {
            toggleSelection(position);
        }
    }

    @Override
    public void onLongClick(RecyclerView.ViewHolder vh, int position) {
        if (mActionMode == null) {
            mActionMode = mMainActivityListener.setActionMode(mActionModeCallback);
        }
        toggleSelection(position);
    }

    public void toggleSelection(int position) {
        mExpensesAdapter.toggleSelection(position);
        int count = mExpensesAdapter.getSelectedItemCount();
        if (count == 0) {
            mActionMode.finish();
        } else {
            mActionMode.setTitle(String.valueOf(count));
            mActionMode.invalidate();
        }
    }

    public void cancelActionMode() {
        if (mActionMode != null) {
            mActionMode.finish();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(IConstants.IS_ACTION_MODE_ACTIVATED, mActionMode != null);
        outState.putParcelable(IConstants.TAG_SELECTED_ITEMS, new SparseParcelable(mExpensesAdapter.getSelectedBooleanArray()));
        super.onSaveInstanceState(outState);
    }

    private void eraseExpenses() {
        DialogManager.getInstance().createCustomAcceptDialog(getActivity(), getString(R.string.del), getString(R.string.conf_del_items), getString(R.string.confirm), getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    ExpensesManager.getInstance().eraseSelectedExpenses();
                }
                updateData();
                mActionMode.finish();
                mActionMode = null;
            }
        });
    }

    
}
