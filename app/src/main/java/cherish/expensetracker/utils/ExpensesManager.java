package cherish.expensetracker.utils;

import android.content.Intent;
import android.util.SparseBooleanArray;

import cherish.expensetracker.CherishExpenseTracker;
import cherish.expensetracker.entities.Category;
import cherish.expensetracker.entities.Expense;
import cherish.expensetracker.interface_helpers.IDateMode;
import cherish.expensetracker.interface_helpers.IExpensesType;
import cherish.expensetracker.widget.ExpensesWidgetProvider;
import cherish.expensetracker.widget.ExpensesWidgetService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ExpensesManager {

    private List<Expense> mExpensesDataListReords = new ArrayList<>();
    private SparseBooleanArray mSelectedExpensesItemsDatalistRecords = new SparseBooleanArray();

    private static ExpensesManager ourInstance = new ExpensesManager();

    public static ExpensesManager getInstance() {
        return ourInstance;
    }

    private ExpensesManager() {
    }

    public void setExpensesList(Date dateFrom, Date dateTo, @IExpensesType int type,  Category category) {
        mExpensesDataListReords = Expense.getExpensesList(dateFrom, dateTo, type, category);
        resetSelectedItems();
    }

    public void setExpensesDataRecordsListByDateMode(@IDateMode int mCurrentDateMode) {
        switch (mCurrentDateMode) {
            case IDateMode.MODE_TODAY:
                mExpensesDataListReords = Expense.getTodayExpenses();
                break;
            case IDateMode.MODE_WEEK:
                mExpensesDataListReords = Expense.getWeekExpenses();
                break;
            case IDateMode.MODE_MONTH:
                mExpensesDataListReords = Expense.getMonthExpenses();
                break;
        }
    }

    public List<Expense> getExpensesList() {
        return mExpensesDataListReords;
    }

    public SparseBooleanArray getSelectedExpensesItems() {
        return mSelectedExpensesItemsDatalistRecords;
    }

    public void resetSelectedItems() {
        mSelectedExpensesItemsDatalistRecords.clear();
    }

    public List<Integer> getSelectedExpensesDataRecordIndex() {
        List<Integer> items = new ArrayList<>(mSelectedExpensesItemsDatalistRecords.size());
        for (int i = 0; i < mSelectedExpensesItemsDatalistRecords.size(); ++i) {
            items.add(mSelectedExpensesItemsDatalistRecords.keyAt(i));
        }
        return items;
    }

    public void eraseSelectedExpensesDataRecords() {
        boolean isToday = false;
        List<Expense> expensesToDelete = new ArrayList<>();
        for (int position : getSelectedExpensesDataRecordIndex()) {
            Expense expense = mExpensesDataListReords.get(position);
            expensesToDelete.add(expense);
            Date expenseDate = expense.getDate();
            // update widget if the expense is created today
            if (DateUtils.isToday(expenseDate)) {
                isToday = true;
            }
        }
        if (isToday) {
            Intent i = new Intent(CherishExpenseTracker.getContext(), ExpensesWidgetProvider.class);
            i.setAction(ExpensesWidgetService.UPDATE_WIDGET);
            CherishExpenseTracker.getContext().sendBroadcast(i);
        }
        RealmManager.getInstance().delete(expensesToDelete);
    }

    public void setSelectedItems(SparseBooleanArray selectedItems) {
        this.mSelectedExpensesItemsDatalistRecords = selectedItems;
    }

}
