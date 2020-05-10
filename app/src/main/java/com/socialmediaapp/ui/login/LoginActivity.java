package com.socialmediaapp.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.client.results.ForgotPasswordResult;
import com.amazonaws.mobile.client.results.SignInResult;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.socialmediaapp.R;

import java.util.prefs.Preferences;

public class LoginActivity extends AppCompatActivity {
    private String username, password;
    private EditText usernameText, passwordText;
    private String TAG;
    private AmazonDynamoDBClient dynamoDBClient;
    public static final String MY_PREFS = "MyPrefs";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameText = findViewById(R.id.username);
        passwordText = findViewById(R.id.password);
        sharedPreferences = getSharedPreferences(MY_PREFS, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        usernameText.setText(sharedPreferences.getString("username", ""));
        passwordText.setText(sharedPreferences.getString("password", ""));
        AWSMobileClient.getInstance().initialize(getApplicationContext(), new Callback<UserStateDetails>() {
            @Override
            public void onResult(UserStateDetails result) {
                switch (result.getUserState()) {
                    case SIGNED_IN:
                        startActivity(new Intent(getApplicationContext(), HomeScreen.class));
                        finish();
                        break;
                    case SIGNED_OUT:
                        break;
                    default:
                        AWSMobileClient.getInstance().signOut();
                        break;
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    public void login(View view) {
        username = usernameText.getText().toString();
        password = passwordText.getText().toString();
        AWSMobileClient.getInstance().signIn(username, password, null, new Callback<SignInResult>() {
            @Override
            public void onResult(final SignInResult signInResult) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Sign-in callback state: " + signInResult.getSignInState());
                        switch (signInResult.getSignInState()) {
                            case DONE:
                                editor.putString("username", username);
                                editor.putString("password", password);
                                editor.apply();
                                makeToast("Sign-in done.");
                                Log.i("User ID: ", AWSMobileClient.getInstance().getUsername());
                                Log.i("Identity: ", AWSMobileClient.getInstance().getIdentityId());
                                dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentials());
                                Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
                                startActivity(intent);
                                finish();
                                break;
                            case SMS_MFA:
                                makeToast("Please confirm sign-in with SMS.");
                                break;
                            case NEW_PASSWORD_REQUIRED:
                                makeToast("Please confirm sign-in with new password.");
                                intent = new Intent(getApplicationContext(), ForgotPassword.class);
                                startActivity(intent);
                                break;
                            default:
                                makeToast("Unsupported sign-in confirmation: " + signInResult.getSignInState());
                                break;
                        }
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Sign-in error", e);
                if (e.toString().contains("PasswordResetRequiredException")) {
                    AWSMobileClient.getInstance().forgotPassword(username, new Callback<ForgotPasswordResult>() {
                        @Override
                        public void onResult(final ForgotPasswordResult result) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG, "forgot password state: " + result.getState());
                                    switch (result.getState()) {
                                        case CONFIRMATION_CODE:
                                            Intent intent = new Intent(getApplicationContext(), ForgotPassword.class);
                                            startActivity(intent);
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            });
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e(TAG, "forgot password error", e);
                        }
                    });
                }
            }
        });
    }

    public void createAccount(View view) {
        Intent intent = new Intent(this, CreateAccount.class);
        startActivity(intent);
    }

    private void makeToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}