package com.application.material.takeacoffee.app;

import android.content.Context;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import butterknife.ButterKnife;
import com.application.material.takeacoffee.app.fragments.CoffeeMachineFragment;


public class CoffeeMachineActivity extends ActionBarActivity {
    private static final String TAG = "CoffeeMachineActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        CalligraphyConfig.initDefault("fonts/AmaticSC-Regular.ttf", R.attr.fontPath);
        setContentView(R.layout.activity_coffee_machine);
        ButterKnife.inject(this);

        //ACTION BAR
        SpannableString s = new SpannableString("Take a coffee!");
        s.setSpan(new TypefaceSpan("fonts/AmaticSC-Regular.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(s);
            setSupportActionBar(toolbar);
//            toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        }

        //INIT VIEW
        initView();
    }

    private void initView() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.coffeeAppFragmentContainerId, new CoffeeMachineFragment())
                .commit();
    }

//    public static void launch(MainActivity activity, View transitionView, String url) {
//        ActivityOptionsCompat options =
//                ActivityOptionsCompat.makeSceneTransitionAnimation(
//                        activity, transitionView, EXTRA_IMAGE);
//        Intent intent = new Intent(activity, DetailActivity.class);
//        intent.putExtra(EXTRA_IMAGE, url);
//        ActivityCompat.startActivity(activity, intent, options.toBundle());
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.coffee_machine, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id) {
            case android.R.id.home:
                Log.d(TAG, "hey home button");
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
//    }

    public interface setLoadViewInterface {
        public void initOnLoadView();
    }

}
