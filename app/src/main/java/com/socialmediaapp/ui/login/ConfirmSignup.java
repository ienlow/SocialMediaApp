package com.socialmediaapp.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.results.SignInResult;
import com.amazonaws.mobile.client.results.SignUpResult;
import com.amazonaws.mobile.client.results.UserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.socialmediaapp.R;

public class ConfirmSignup extends AppCompatActivity {

    private String TAG;
    DynamoDBMapper dynamoDBMapper;
    String username, password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_account);
        Intent intent = getIntent();
        username  = intent.getStringExtra("username");
        password = intent.getStringExtra("password");
    }

    public void confirm(View view) {
        EditText codeText = findViewById(R.id.confirmationCode);
        final String code = codeText.getText().toString();
        AWSMobileClient.getInstance().confirmSignUp(username, code, new Callback<SignUpResult>() {
            @Override
            public void onResult(final SignUpResult signUpResult) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Sign-up callback state: " + signUpResult.getConfirmationState());
                        if (!signUpResult.getConfirmationState()) {
                            final UserCodeDeliveryDetails details = signUpResult.getUserCodeDeliveryDetails();
                            makeToast("Confirm sign-up with: " + details.getDestination());
                        } else {
                            makeToast("Sign-up done.");
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
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Confirm sign-up error", e);
            }
        });
    }

    private void makeToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
