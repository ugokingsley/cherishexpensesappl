package cherish.expensetracker.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

import cherish.expensetracker.ExpenseTrackerApp;
import cherish.expensetracker.R;
import cherish.expensetracker.ui.BaseActivity;
import cherish.expensetracker.ui.MainActivity;

public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        boolean logged = PreferenceManager.getDefaultSharedPreferences(ExpenseTrackerApp.getContext()).getBoolean(getString(R.string.already_accepted_user_key), false);
        if (logged) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();
        }
    }

}
