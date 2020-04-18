package com.socialmediaapp.ui.login;

import android.Manifest;
import android.app.Activity;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.client.results.SignInResult;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.socialmediaapp.R;

import static com.amazonaws.mobile.client.UserState.SIGNED_IN;
import static com.amazonaws.mobile.client.UserState.SIGNED_OUT;

public class LoginActivity extends AppCompatActivity {
    private String username, password;
    private EditText usernameText, passwordText;
    private String TAG;
    private AmazonDynamoDBClient dynamoDBClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameText = findViewById(R.id.username);
        passwordText = findViewById(R.id.password);
        AWSMobileClient.getInstance().initialize(getApplicationContext(), new Callback<UserStateDetails>() {
            @Override
            public void onResult(UserStateDetails result) {
                switch (result.getUserState()) {
                    case SIGNED_IN:
                        startActivity(new Intent(getApplicationContext(), HomeScreen.class));
                        finish();
                        break;
                    case SIGNED_OUT:
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    //Thread.sleep(2000);
                                } catch(Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    //showSignIn();
                                    //startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                    //finish();
                                }
                            }
                        }).start();
                        break;
                    default:
                        AWSMobileClient.getInstance().signOut();
                        //startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        //showSignIn();
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
                                makeToast("Sign-in done.");
                                Log.i("User ID: ", AWSMobileClient.getInstance().getUsername());
                                Log.i("Identity: ", AWSMobileClient.getInstance().getIdentityId());
                                dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentials());
                                //BackgroundWorker backgroundWorker = new BackgroundWorker();
                                //backgroundWorker.execute();
                                Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
                                startActivity(intent);
                                finish();
                                break;
                            case SMS_MFA:
                                makeToast("Please confirm sign-in with SMS.");
                                break;
                            case NEW_PASSWORD_REQUIRED:
                                makeToast("Please confirm sign-in with new password.");
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