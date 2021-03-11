package com.sursulet.go4lunch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.sursulet.go4lunch.injection.ViewModelFactory;
import com.sursulet.go4lunch.notifications.EventHandler;
import com.sursulet.go4lunch.ui.OnItemClickListener;
import com.sursulet.go4lunch.ui.autocomplete.PlaceAutocompleteAdapter;
import com.sursulet.go4lunch.ui.detail.DetailPlaceActivity;
import com.sursulet.go4lunch.ui.settings.SettingsActivity;

import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnItemClickListener {

    //private static final String TAG = MainActivity.class.getSimpleName();

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

        EventHandler.periodRequest();

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

        getUserProfile();

        mainViewModel.getSingleLiveEventOpenDetailActivity().observe(this, this::openDetailActivity);
        mainViewModel.getPredictionsLiveData().observe(this, strings -> adapter.submitList(strings));
    }

    private void openDetailActivity(String id) {
        startActivity(DetailPlaceActivity.getStartIntent(this, id));
    }

    private void configureRecyclerView() {
        recyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL));
        recyclerView.setBackgroundColor(getResources().getColor(R.color.white));
        this.adapter = new PlaceAutocompleteAdapter(
                PlaceAutocompleteAdapter.DIFF_CALLBACK,
                this);
        recyclerView.setAdapter(this.adapter);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_your_lunch) {
            mainViewModel.getCurrentUserRestaurant().observe(this, currentRestaurant -> {
                if (currentRestaurant != null) {
                    mainViewModel.openDetailActivity(currentRestaurant);
                } else {
                    Toast.makeText(MainActivity.this, "Aucun restaurant choisi", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (itemId == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        } else if (itemId == R.id.action_logout) {
            this.signOut();
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

        searchView.setOnCloseListener(() -> {
            mainViewModel.onQuerySelected("");
            return true;
        });

        return true;
    }

    public void getUserProfile() {
        mainViewModel.getUiModelLiveData().observe(this, this::updateUI);
    }

    public void updateUI(MainUiModel mainUiModel) {
        userName.setText(mainUiModel.getUsername());
        userEmail.setText(mainUiModel.getEmail());

        Glide.with(userPhoto)
                .load(mainUiModel.getPhotoUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(userPhoto);
    }

    public void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, aVoid -> startSignInActivity());
    }

    public void startSignInActivity() {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemClick(int position) {
        String text = adapter.getCurrentList().get(position);
        searchView.setQuery(text, false);
        mainViewModel.onQuerySelected(text);
    }

    @Override
    public void onItemName(Map<String, String> position) {}
}