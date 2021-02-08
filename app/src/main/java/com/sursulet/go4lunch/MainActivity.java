package com.sursulet.go4lunch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.sursulet.go4lunch.injection.ViewModelFactory;
import com.sursulet.go4lunch.ui.OnItemClickListener;
import com.sursulet.go4lunch.ui.autocomplete.PlaceAutocompleteAdapter;
import com.sursulet.go4lunch.ui.detail.DetailPlaceActivity;
import com.sursulet.go4lunch.ui.SettingsActivity;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnItemClickListener {

    //private static final String TAG = MainActivity.class.getSimpleName();

    private static final int RC_SIGN_IN = 123;
    private static final int SIGN_OUT_TASK = 10;

    MainViewModel mainViewModel;

    private DrawerLayout drawer;
    private TextView userName;
    private TextView userEmail;
    private ImageView userPhoto;
    private RecyclerView recyclerView;
    private PlaceAutocompleteAdapter adapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //EventHandler.periodRequest();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navView = findViewById(R.id.nav_view);

        View headerView = navView.getHeaderView(0);
        userName = headerView.findViewById(R.id.drawer_username);
        userEmail = headerView.findViewById(R.id.drawer_email);
        userPhoto = headerView.findViewById(R.id.drawer_photo);

        BottomNavigationView bottom = findViewById(R.id.bottom_nav);

        recyclerView = findViewById(R.id.autocomplete_recyclerview);

        mainViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(MainViewModel.class);

        if (mainViewModel.isCurrentUserLogged()) {
            getUserProfile();
        } else {
            createSignInIntent();
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close
        );

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navView.setNavigationItemSelectedListener(this);

        AppBarConfiguration mAppBarConfig = new AppBarConfiguration.Builder(
                R.id.action_map, R.id.action_list, R.id.action_workmates)
                .build();
        NavController navCtrl = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navCtrl, mAppBarConfig);
        NavigationUI.setupWithNavController(bottom, navCtrl);

        configureRecyclerView();

        mainViewModel.getPredictionsLiveData().observe(this, strings -> adapter.submitList(strings));
    }

    private void configureRecyclerView(){
        recyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL));
        this.adapter = new PlaceAutocompleteAdapter(
                PlaceAutocompleteAdapter.DIFF_CALLBACK,
                this);
        recyclerView.setAdapter(this.adapter);
    }

    @SuppressLint("NonConstantResourceId")
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
                this.signOutUserFromFirebase();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);

        searchView = ((SearchView) menuItem.getActionView());
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mainViewModel.onQueryTextChange(newText);
                return false;
            }
        });

        return true;
    }

    public void getUserProfile() {
        mainViewModel.getUiModelLiveData()
                .observe(this, mainUiModel -> {
                    userName.setText(mainUiModel.getUsername());
                    userEmail.setText(mainUiModel.getEmail());

                    Glide.with(userPhoto)
                            .load(mainUiModel.getPhotoUrl())
                            .apply(RequestOptions.circleCropTransform())
                            .into(userPhoto);
                });
    }

    //TODO: doit revenir à la page de connexion
    public void signOutUserFromFirebase() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, aVoid -> startSignInActivity());
    }

    public void startSignInActivity() {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }

    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin) {
        return aVoid -> {
            if (origin == SIGN_OUT_TASK) {
                finish();
            }
        };
    }

    public void createSignInIntent() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false, true)
                        .setTheme(R.style.LoginTheme)
                        .setLogo(R.drawable.g22)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                mainViewModel.createUser();
                showSnackBar(this.drawer, getString(R.string.connection_succeed));
            } else {
                if (response == null) {
                    showSnackBar(this.drawer, getString(R.string.error_authentication_canceled));
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackBar(this.drawer, getString(R.string.error_no_internet));
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackBar(this.drawer, getString(R.string.error_unknown_error));
                    Toast.makeText(this, ""+response.getError().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void showSnackBar(DrawerLayout drawer, String message){
        Snackbar.make(drawer, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(int position) {
        String text = adapter.getCurrentList().get(position);
        searchView.setQuery(text, false);
        mainViewModel.onQuerySelected(text);
    }
}