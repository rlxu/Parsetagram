package me.rlxu.parsetagram;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseUser;

public class FinalHomeActivity extends AppCompatActivity implements ProfileFragment.OnFragmentInteractionListener {

    BottomNavigationView bottomNavigationView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_home);

        // set action bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.nav_logo_whiteout);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        final FragmentManager fragmentManager = getSupportFragmentManager();
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // define your fragments here
        final Fragment homeFragment = new HomeFragment();
        final Fragment composeFragment = new ComposeFragment();
        final Fragment profileFragment = new ProfileFragment();

        // handle navigation selection
        bottomNavigationView.setOnNavigationItemSelectedListener(
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_home:
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.flContainer, homeFragment).commit();
                            return true;
                        case R.id.action_compose:
                            FragmentTransaction fragmentTransaction2 = fragmentManager.beginTransaction();
                            fragmentTransaction2.replace(R.id.flContainer, composeFragment).commit();
                            return true;
                        case R.id.action_profile:
                            FragmentTransaction fragmentTransaction3 = fragmentManager.beginTransaction();
                            fragmentTransaction3.replace(R.id.flContainer, profileFragment).commit();
                            return true;
                    }
                    return false;
                }
            });
    }

    @Override
    public void logoutInteraction() {
        ParseUser.logOut();
        Intent intent = new Intent(FinalHomeActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
