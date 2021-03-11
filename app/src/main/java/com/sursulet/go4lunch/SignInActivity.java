package com.sursulet.go4lunch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.sursulet.go4lunch.injection.ViewModelFactory;

import java.util.Arrays;
import java.util.List;

public class SignInActivity extends AppCompatActivity {

    //private static final String TAG = SignInActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 123;

    private SignInViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initViewModel();

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            startMainActivity();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            createSignInIntent();
        } /*else {
            startMainActivity();
        }*/
    }

    public void initViewModel() {
        viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(SignInViewModel.class);
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void createSignInIntent() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false, true)
                        .setTheme(R.style.LoginTheme)
                        .setLogo(R.drawable.ic_hot_food_in_a_bowl)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                Toast.makeText(this, getString(R.string.connection_succeed), Toast.LENGTH_SHORT).show();
                viewModel.createUser();
                startMainActivity();
                finish();
            } else {
                if(response == null) {
                    Toast.makeText(this, getString(R.string.error_authentication_canceled), Toast.LENGTH_SHORT).show();
                } else if(response.getError() != null) {
                    if(response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                        Toast.makeText(this,getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show();
                    } else if(response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                        Toast.makeText(this, getString(R.string.error_unknown_error), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

}