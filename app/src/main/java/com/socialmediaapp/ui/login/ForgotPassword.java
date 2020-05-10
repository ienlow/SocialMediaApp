package com.socialmediaapp.ui.login;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.results.ForgotPasswordResult;
import com.socialmediaapp.R;

public class ForgotPassword extends AppCompatActivity {
    private static final String TAG = "NewPassword";
    EditText newPassword, confirmPassword, confirmCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);
        newPassword = findViewById(R.id.resetPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        confirmCode = findViewById(R.id.confirmResetCode);
    }

    public void changePassword(View view) {
        if (newPassword.getText().toString().equals(confirmPassword.getText().toString())) {
            AWSMobileClient.getInstance().confirmForgotPassword(newPassword.getText().toString(), confirmCode.getText().toString(), new Callback<ForgotPasswordResult>() {
                @Override
                public void onResult(final ForgotPasswordResult result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "forgot password state: " + result.getState());
                            switch (result.getState()) {
                                case DONE:
                                    makeToast("Password changed successfully");
                                    finish();
                                    break;
                                default:
                                    Log.e(TAG, "un-supported forgot password state");
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
        else {
            makeToast("Passwords do not match.");
        }
    }

    private void makeToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }


}
