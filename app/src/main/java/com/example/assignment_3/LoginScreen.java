package com.example.assignment_3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.amplifyframework.auth.cognito.result.AWSCognitoAuthSignOutResult;
import com.amplifyframework.auth.cognito.result.GlobalSignOutError;
import com.amplifyframework.auth.cognito.result.HostedUIError;
import com.amplifyframework.auth.cognito.result.RevokeTokenError;
import com.amplifyframework.auth.options.AuthSignOutOptions;
import com.amplifyframework.core.Amplify;

import DButils.LoginUtilsClient;
import DButils.model.AccInput;
import DButils.model.AccInputReduced;
import DButils.model.AccOutput;

public class LoginScreen extends AppCompatActivity {

    private EditText uname, pword;

    private Button login, pwordrecover, ccodeenter;

    private Toast successtoast, errortoast;

    private Context context;

    private ApiClientFactory factory;

    private LoginUtilsClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        context = getApplicationContext();
        successtoast = Toast.makeText(context, "User signin successful!", Toast.LENGTH_SHORT);
        errortoast = Toast.makeText(context, "User signin failed.", Toast.LENGTH_SHORT);
        factory = new ApiClientFactory();
        client = factory.build(LoginUtilsClient.class);

        uname = (EditText) findViewById(R.id.username);
        pword = (EditText) findViewById(R.id.password);

        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){

                LogOut l = new LogOut();
                l.execute();

                AccInputReduced acc = new AccInputReduced();
                acc.setUname(uname.getText().toString());
                acc.setPword(pword.getText().toString());

                AccInputReduced[] params = new AccInputReduced[1];
                params[0] = acc;

                LoginAcc temp = new LoginAcc();
                temp.execute(params);

            }
        });

        ccodeenter = (Button) findViewById(R.id.confcodeinput);
        ccodeenter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent toConfScreen = new Intent(LoginScreen.this, ConfScreen.class);
                startActivity(toConfScreen);
            }
        });

        pwordrecover = (Button) findViewById(R.id.pwordrecovery);
        pwordrecover.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent toRecoverScreen = new Intent(LoginScreen.this, RecoverScreen.class);
                startActivity(toRecoverScreen);
            }
        });



    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        LogOut l = new LogOut();
        l.execute();

    }


    private class LogOut extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            AuthSignOutOptions options = AuthSignOutOptions.builder()
                    .globalSignOut(true)
                    .build();

            Amplify.Auth.signOut(options, signOutResult -> {
                if (signOutResult instanceof AWSCognitoAuthSignOutResult.CompleteSignOut) {
                    // handle successful sign out
                } else if (signOutResult instanceof AWSCognitoAuthSignOutResult.PartialSignOut) {
                    // handle partial sign out
                } else if (signOutResult instanceof AWSCognitoAuthSignOutResult.FailedSignOut) {
                    // handle failed sign out
                }
            });
            return null;
        }
    }

    private class LoginAcc extends AsyncTask<AccInputReduced, Void, Void> {

        @Override
        protected Void doInBackground(AccInputReduced... accInputReduceds) {
            AccInputReduced acc = accInputReduceds[0];
            Intent intent = new Intent(LoginScreen.this, loggedin.class);
            intent.putExtra("owner", acc.getUname());

            Amplify.Auth.signIn(
                    acc.getUname(),
                    acc.getPword(),
                    result -> {
                        successtoast.show();
                        startActivity(intent);
                    },
                    error -> {
                        Log.e("AuthError", error.getMessage());
                        errortoast.show();
                    }
            );

            return null;
        }

    }
}