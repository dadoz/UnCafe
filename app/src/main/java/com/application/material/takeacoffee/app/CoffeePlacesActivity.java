package com.application.material.takeacoffee.app;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.application.material.takeacoffee.app.fragments.CoffeePlacesFragment;
import com.application.material.takeacoffee.app.utils.PermissionManager;

public class CoffeePlacesActivity extends AppCompatActivity {
    public static final int RESULT_FAILED = -1;
    public static String EXTRA_DATA = "EXTRA_DATA";
    public static String ACTION_EDIT_REVIEW_RESULT = "EDIT_RESULT";
    public static final String ERROR_MESSAGE_KEY = "EMK";

    @Bind(R.id.coffeeToolbarId)
    public android.support.v7.widget.Toolbar toolbar;
    private PermissionManager permissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coffee_machine);
        ButterKnife.bind(this);

        permissionManager = PermissionManager.getInstance();
        initView();
    }

    /**
     * init action bar
     */
    private void initActionBar() {
        setSupportActionBar(toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));
    }

    /**
     * pre init view
     */
    private void initView() {
        initActionBar();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.coffeeAppFragmentContainerId, new CoffeePlacesFragment(),
                        CoffeePlacesFragment.COFFEE_MACHINE_FRAG_TAG)
                .commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}