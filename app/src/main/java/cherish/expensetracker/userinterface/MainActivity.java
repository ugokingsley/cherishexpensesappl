package cherish.expensetracker.userinterface;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import cherish.expensetracker.R;
import cherish.expensetracker.entities.Expense;
import cherish.expensetracker.interface_helpers.IDateMode;
import cherish.expensetracker.interface_helpers.IMainActivityListener;
import cherish.expensetracker.userinterface.categories.CategoriesFragment;
import cherish.expensetracker.userinterface.expenses.ContainerExpensesFragment;
import cherish.expensetracker.userinterface.history.HistoryFragment;


import cherish.expensetracker.userinterface.income.IncomeFragment;
import cherish.expensetracker.utils.DateUtils;
import cherish.expensetracker.utils.Util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, IMainActivityListener {

    @IntDef({NAVIGATION_MODE_STANDARD, NAVIGATION_MODE_TABS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface NavigationMode {}

    public static final int NAVIGATION_MODE_STANDARD = 0;
    public static final int NAVIGATION_MODE_TABS = 1;
    public static final String NAVIGATION_POSITION = "navigation_position";

    private int mCurrentMode = NAVIGATION_MODE_STANDARD;
    private int idSelectedNavigationItem;

    private DrawerLayout mDrawerLay;
    private NavigationView mainNavigationView;
    private Toolbar mToolbar;
    private TabLayout mainTabLayout;
    private FloatingActionButton mFabBtn;

    // Expenses Summary related views
    private LinearLayout llExpensesSummary;
    private TextView tvDate;
    private TextView tvDescription;
    private TextView tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        setUpDrawer();
        setUpToolbar();
        if ( savedInstanceState != null) {
            int menuItemId = savedInstanceState.getInt(NAVIGATION_POSITION);
            mainNavigationView.setCheckedItem(menuItemId);
            mainNavigationView.getMenu().performIdentifierAction(menuItemId, 0);
        } else {
            mainNavigationView.getMenu().performIdentifierAction(R.id.nav_expenses, 0);
        }
    }

    @NavigationMode
    public int getNavigationMode() {
        return mCurrentMode;
    }

    private void initUI() {
        mDrawerLay = findViewById(R.id.drawer_lay);
        mainTabLayout = findViewById(R.id.tab);
        mainNavigationView = findViewById(R.id.nav_view);
        mFabBtn = findViewById(R.id.fab);
        llExpensesSummary = findViewById(R.id.ll_expense_container);
        tvDate = findViewById(R.id.tv_date);
        tvDescription = findViewById(R.id.tv_description);
        tvTotal = findViewById(R.id.tv_total);
    }

    private void setUpDrawer() {
        mainNavigationView.setNavigationItemSelectedListener(this);
    }

    private void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLay.openDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(NAVIGATION_POSITION, idSelectedNavigationItem);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        menuItem.setChecked(true);
        mDrawerLay.closeDrawers();
        switchFragment(menuItem.getItemId());
        return false;
    }

    @Override
    public void setTabs(List<String> tabList, final TabLayout.OnTabSelectedListener onTabSelectedListener) {
        mainTabLayout.removeAllTabs();
        mainTabLayout.setVisibility(View.VISIBLE);
        mainTabLayout.setOnTabSelectedListener(onTabSelectedListener);
        for (String tab : tabList) {
            mainTabLayout.addTab(mainTabLayout.newTab().setText(tab).setTag(tab));
        }
    }

    @Override
    public void setMode(@NavigationMode int mode) {
        mFabBtn.setVisibility(View.GONE);
        llExpensesSummary.setVisibility(View.GONE);
        mCurrentMode = mode;
        switch (mode) {
            case NAVIGATION_MODE_STANDARD:
                setNavigationModeStandard();
                break;
            case NAVIGATION_MODE_TABS:
                setNavigationModeTabs();
                break;
        }
    }

    @Override
    public void setExpensesSummary(@IDateMode int dateMode) {
        float total = Expense.getTotalExpensesByDateMode(dateMode);
        tvTotal.setText(Util.getFormattedCurrency(total));
        String date;
        switch (dateMode) {

            case IDateMode.MODE_MONTH:
                date = Util.formatDateToString(DateUtils.getFirstDateOfCurrentMonth(), Util.getCurrentDateFormat()).concat(" - ").concat(Util.formatDateToString(DateUtils.getRealLastDateOfCurrentMonth(), Util.getCurrentDateFormat()));
                break;
            default:
                date = "";
                break;
        }
        tvDate.setText(date);
    }

    @Override
    public void setFAB(@DrawableRes int drawableId, View.OnClickListener onClickListener) {
        mFabBtn.setImageDrawable(getResources().getDrawable(drawableId));
        mFabBtn.setOnClickListener(onClickListener);
        mFabBtn.show();
    }

    @Override
    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void setPager(ViewPager vp, final TabLayout.ViewPagerOnTabSelectedListener viewPagerOnTabSelectedListener) {
        mainTabLayout.setupWithViewPager(vp);
        mainTabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(vp) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                @IDateMode int dateMode;
                switch (tab.getPosition()) {
                    case 0:
                        dateMode = IDateMode.MODE_MONTH;
                        break;

                    default:
                        dateMode = IDateMode.MODE_MONTH;
                }
                setExpensesSummary(dateMode);
                viewPagerOnTabSelectedListener.onTabSelected(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                viewPagerOnTabSelectedListener.onTabUnselected(tab);
            }
        });
        setExpensesSummary(IDateMode.MODE_MONTH);
    }

    public ActionMode setActionMode(final ActionMode.Callback actionModeCallback) {
       return mToolbar.startActionMode(new ActionMode.Callback() {
           @Override
           public boolean onCreateActionMode(ActionMode mode, Menu menu) {
               mDrawerLay.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
               return actionModeCallback.onCreateActionMode(mode,menu);
           }

           @Override
           public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
               return actionModeCallback.onPrepareActionMode(mode, menu);
           }

           @Override
           public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
               return actionModeCallback.onActionItemClicked(mode, item);
           }

           @Override
           public void onDestroyActionMode(ActionMode mode) {
               mDrawerLay.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
               actionModeCallback.onDestroyActionMode(mode);
           }
       });
    }

    private void setNavigationModeTabs() {
        //mainTabLayout.setVisibility(View.VISIBLE);
        llExpensesSummary.setVisibility(View.VISIBLE);
    }

    private void setNavigationModeStandard() {
        CoordinatorLayout coordinator = (CoordinatorLayout) findViewById(R.id.main_coordinator);
        AppBarLayout appbar = (AppBarLayout) findViewById(R.id.app_bar_layout);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appbar.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        if (behavior != null && appbar != null) {
            int[] consumed = new int[2];
            behavior.onNestedPreScroll(coordinator, appbar, null, 0, -1000, consumed);
        }
        mainTabLayout.setVisibility(View.GONE);
    }

    private void switchFragment(int menuItemId) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_content);
        switch (menuItemId) {
            case R.id.nav_expenses:
                if (!(currentFragment instanceof ContainerExpensesFragment)) replaceFragment(ContainerExpensesFragment.newInstance(), false);
                break;
            case R.id.nav_categories:
                if (!(currentFragment instanceof  CategoriesFragment)) replaceFragment(CategoriesFragment.newInstance(), false);
                break;
            case R.id.nav_history:
                if (!(currentFragment instanceof HistoryFragment)) replaceFragment(HistoryFragment.newInstance(), false);
                break;
            case R.id.nav_income:
                if (!(currentFragment instanceof IncomeFragment)) replaceFragment(IncomeFragment.newInstance(), false);

                break;
        }
    }
}
