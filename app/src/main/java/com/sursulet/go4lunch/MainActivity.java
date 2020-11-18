package com.sursulet.go4lunch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.sursulet.go4lunch.ui.DetailPlaceActivity;
import com.sursulet.go4lunch.ui.SettingsActivity;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    FirebaseUser user;
    Intent intent;

    private DrawerLayout drawer;
    private TextView username;
    private TextView usermail;
    private ImageView photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) {
            getUserProfile();
        } else {
            startActivity(new Intent(this, SignInActivity.class));
        }

 */

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);

        View headerView = navView.getHeaderView(0);
        username = headerView.findViewById(R.id.drawer_username);
        usermail = headerView.findViewById(R.id.drawer_email);
        photo  = headerView.findViewById(R.id.drawer_photo);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        BottomNavigationView bottom = findViewById(R.id.bottom_nav);

        AppBarConfiguration mAppBarConfig = new AppBarConfiguration.Builder(
                R.id.action_map, R.id.action_list, R.id.action_workmates)
                .build();
        NavController navCtrl = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navCtrl, mAppBarConfig);
        NavigationUI.setupWithNavController(bottom, navCtrl);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_your_lunch:
                startActivity(new Intent(getApplicationContext(), DetailPlaceActivity.class));
                break;

            case R.id.action_settings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                break;

            case R.id.action_logout:
                //signOut();
                Toast.makeText(this, "LOGOUT", Toast.LENGTH_SHORT).show();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public void getUserProfile() {
        //user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            //set User Profile
            username.setText(name);
            usermail.setText(email);
            Glide.with(this)
                    .load(photoUrl)
                    .circleCrop()
                    .into(photo);
        }
    }

    public void signOut() {
        AuthUI.getInstance().signOut(MainActivity.this);
        /*AuthUI.getInstance().signOut(MainActivity.this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                startActivity(new Intent(MainActivity.this, SignInActivity.class));
                finish();
            }
        });*/
    }
}