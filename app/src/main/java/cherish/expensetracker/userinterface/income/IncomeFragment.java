package cherish.expensetracker.userinterface.income;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cherish.expensetracker.R;
import cherish.expensetracker.userinterface.BaseFragment;




public class IncomeFragment extends BaseFragment {

    public static IncomeFragment newInstance() {
        return new IncomeFragment();
    }

    public IncomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_income, container, false);
        setHasOptionsMenu(true);
        return rootView;
    }
    
}
