package uk.co.joelwalker.bebop_follow_me;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONObject;

import uk.co.joelwalker.bebop_follow_me.API.APIManager;
import uk.co.joelwalker.bebop_follow_me.API.APIResponse;
import uk.co.joelwalker.bebop_follow_me.Fragments.CreateFragment;
import uk.co.joelwalker.bebop_follow_me.Fragments.HomeFragment;
import uk.co.joelwalker.bebop_follow_me.Fragments.TourFragment;


public class MainActivity extends AppCompatActivity implements APIResponse {

    private APIManager api;

    private Fragment fragment;
    private FragmentManager fragmentManager;
    private View mainView, mBottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mainView = findViewById(R.id.container);
        api = new APIManager(this.getApplicationContext(), mainView, this);

        mBottomNav = findViewById(R.id.navigation);

        fragment = new HomeFragment();

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_container, fragment).commit();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        api.disconnectServer();

    }


    //Tabbed GUI Selector
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = new HomeFragment();
                    break;
                case R.id.navigation_dashboard:
                    fragment = new TourFragment();
                    break;
                case R.id.navigation_notifications:
                    fragment = new CreateFragment();
                    break;
            }


            final FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.main_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
            return true;
        }
    };

    @Override
    public void apiResponse(JSONObject res) {

    }
}
