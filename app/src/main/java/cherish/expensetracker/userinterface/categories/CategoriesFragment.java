package cherish.expensetracker.userinterface.categories;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import cherish.expensetracker.R;
import cherish.expensetracker.entities.Category;
import cherish.expensetracker.expenseandcategoriesadapter.CategoriesAdapter;
import cherish.expensetracker.custom.BaseViewHolder;
import cherish.expensetracker.custom.RecyclerViewItemDecorator;
import cherish.expensetracker.custom.SparseParcelable;
import cherish.expensetracker.interface_helpers.IConstants;
import cherish.expensetracker.interface_helpers.IExpensesType;
import cherish.expensetracker.userinterface.MainActivity;
import cherish.expensetracker.userinterface.MainFragment;
import cherish.expensetracker.utils.DialogManager;
import cherish.expensetracker.utils.RealmManager;
import cherish.expensetracker.utils.Util;

import java.util.ArrayList;
import java.util.List;


public class CategoriesFragment extends MainFragment implements TabLayout.OnTabSelectedListener, BaseViewHolder.RecyclerClickListener{

    private @IExpensesType int mCurrentMode = IExpensesType.MODE_EXPENSES;

//    private List<String> tabList;
    private RecyclerView rvCategories;
    private TextView tvEmpty;

    private List<Category> mCategoryList;
    private CategoriesAdapter mCategoriesAdapter;

    // Action mode for cat.
    private ActionMode mActionMode;

    public static CategoriesFragment newInstance() {
        return new CategoriesFragment();
    }

    public CategoriesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCategoryList = new ArrayList<>();
        mCategoriesAdapter = new CategoriesAdapter(mCategoryList, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_categories, container, false);
        rvCategories = (RecyclerView)rootView.findViewById(R.id.rv_categories);
        tvEmpty = (TextView)rootView.findViewById(R.id.tv_empty);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(IConstants.IS_ACTION_MODE_ACTIVATED, mActionMode != null);
        outState.putParcelable(IConstants.TAG_SELECTED_ITEMS, new SparseParcelable(mCategoriesAdapter.getSelectedBooleanArray()));
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            boolean isActionMode = savedInstanceState.getBoolean(IConstants.IS_ACTION_MODE_ACTIVATED);
            if(isActionMode) {
                mActionMode = mMainActivityListener.setActionMode(mActionModeCallback);
                mActionMode.setTitle(String.valueOf(mCategoriesAdapter.getSelectedItems().size()));
                mActionMode.invalidate();
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        tabList = Arrays.asList(getString(R.string.exp), getString(R.string.income));
//        mMainActivityListener.setTabs(tabList, this);
        mMainActivityListener.setMode(MainActivity.NAVIGATION_MODE_STANDARD);
        reloadData();
        mMainActivityListener.setFAB(R.drawable.ic_add_white_48dp, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCategoryDialog(null);
            }
        });
        mMainActivityListener.setTitle(getString(R.string.category));

        rvCategories.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvCategories.setAdapter(mCategoriesAdapter);
        rvCategories.setHasFixedSize(true);
        rvCategories.addItemDecoration(new RecyclerViewItemDecorator(getResources().getDimension(R.dimen.dimen_10dp)));

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(IConstants.TAG_SELECTED_ITEMS)) {
                mCategoriesAdapter.setSelectedItems((SparseBooleanArray) savedInstanceState.getParcelable(IConstants.TAG_SELECTED_ITEMS));
                mCategoriesAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (tab.getTag()!=null) {
            if (tab.getTag().toString().equalsIgnoreCase(getString(R.string.exp))) {
                mCurrentMode = IExpensesType.MODE_EXPENSES;
            } else if (tab.getTag().toString().equalsIgnoreCase(getString(R.string.income))) {
                mCurrentMode = IExpensesType.MODE_INCOME;
            }
        }
        reloadData();
    }

    private void reloadData() {
        mCategoryList = Category.getCategoriesForType(mCurrentMode);
        if (mCategoryList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }
        mCategoriesAdapter.updateCategories(mCategoryList);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onClick(RecyclerView.ViewHolder vh, int position) {
        if (mActionMode == null) {
            createCategoryDialog(vh);
        } else {
            toggleSelection(position);
        }
    }

    private void createCategoryDialog(final RecyclerView.ViewHolder vh) {
        AlertDialog alertDialog = DialogManager.getInstance().createEditTextDialog(getActivity(), vh != null ? getString(R.string.edit_cat) : getString(R.string.create_cat), getString(R.string.save), getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    EditText etTest = (EditText) ((AlertDialog) dialog).findViewById(R.id.et_main);
                    if (!Util.isEmptyField(etTest)) {
                        Category category = new Category(etTest.getText().toString(), mCurrentMode);
                        if (vh != null) {
                            Category categoryToUpdate = (Category)vh.itemView.getTag();
                            category.setId(categoryToUpdate.getId());
                            RealmManager.getInstance().update(category);
                        } else {
                            RealmManager.getInstance().save(category, Category.class);
                        }
                        reloadData();
                    } else {
                        DialogManager.getInstance().showShortToast(getString(R.string.err_name));
                    }
                }
            }
        });
        if (vh != null) {
            EditText etCategoryName = (EditText) alertDialog.findViewById(R.id.et_main);
            Category category = (Category) vh.itemView.getTag();
            etCategoryName.setText(category.getName());
        }
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.expenses_context_menu, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete:
                    eraseCategories();
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mCategoriesAdapter.clearSelection();
            mActionMode = null;
        }
    };

    private void eraseCategories() {
        DialogManager.getInstance().createCustomAcceptDialog(getActivity(), getString(R.string.del), getString(R.string.conf_del_items), getString(R.string.confirm), getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    List<Category> categoriesToDelete = new ArrayList<>();
                    for (int position : mCategoriesAdapter.getSelectedItems()) {
                        categoriesToDelete.add(mCategoryList.get(position));
                    }
                    RealmManager.getInstance().delete(categoriesToDelete);
                }
                reloadData();
                mActionMode.finish();
                mActionMode = null;
            }
        });
    }

    @Override
    public void onLongClick(RecyclerView.ViewHolder vh, int position) {
        if (mActionMode == null) {
            mActionMode = mMainActivityListener.setActionMode(mActionModeCallback);
        }
        toggleSelection(position);
    }

    public void toggleSelection(int position) {
        mCategoriesAdapter.toggleSelection(position);
        int count = mCategoriesAdapter.getSelectedItemCount();
        if (count == 0) {
            mActionMode.finish();
        } else {
            mActionMode.setTitle(String.valueOf(count));
            mActionMode.invalidate();
        }
    }

}