package com.example.assignment_3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.Response;
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.cognito.result.AWSCognitoAuthSignOutResult;
import com.amplifyframework.auth.cognito.result.GlobalSignOutError;
import com.amplifyframework.auth.cognito.result.HostedUIError;
import com.amplifyframework.auth.cognito.result.RevokeTokenError;
import com.amplifyframework.auth.options.AuthSignOutOptions;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import DButils.LoginUtilsClient;
import DButils.model.AccInput;
import DButils.model.AccOutput;
import DButils.model.CreateAccOutput;
import aws.sdk.kotlin.services.cognitoidentityprovider.model.InvalidPasswordException;

public class MainActivity extends AppCompatActivity{

    private Button continuetoLogin, register;

    private EditText uname, pword, email, rname;
    private Toast successtoast, errortoast;
    private Context context;

    private ApiClientFactory factory;
    private LoginUtilsClient client;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Amplify.Auth.fetchAuthSession(
                result -> Log.i("AmplifyQuickstart", ("AmplifyResult").concat(result.toString())),
                error -> Log.e("AmplifyQuickstart", ("AmplifyError").concat(error.toString()))
        );

        context = getApplicationContext();
        successtoast = Toast.makeText(context, "Registration successful! Check your email for a confirmation code.", Toast.LENGTH_SHORT);
        errortoast = Toast.makeText(context, "ERROR: account registration unsuccessful.", Toast.LENGTH_SHORT);
        factory = new ApiClientFactory();
        client = factory.build(LoginUtilsClient.class);

        uname = (EditText) findViewById(R.id.username);
        pword = (EditText) findViewById(R.id.password);
        rname = (EditText) findViewById(R.id.rname);
        email = (EditText) findViewById(R.id.email);

        continuetoLogin = (Button) findViewById(R.id.continuetologin);
        continuetoLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent toLoginPage = new Intent(MainActivity.this, LoginScreen.class);
                startActivity(toLoginPage);
            }
        });

        String tempname;
        String temppass;

        register = (Button) findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                LogOut l = new LogOut();
                l.execute();

                AccInput acc = new AccInput();
                acc.setUname(uname.getText().toString());
                acc.setPword(pword.getText().toString());
                acc.setEmail(email.getText().toString());
                acc.setRname(rname.getText().toString());

                AccInput[] params = new AccInput[1];
                params[0] = acc;
                MakeAcc temp = new MakeAcc();
                temp.execute(params);
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

            Log.i("logouthandler", "Logged out!");

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

    private class MakeAcc extends AsyncTask<AccInput, Void, Void> {

        @Override
        protected Void doInBackground(AccInput... accInputs) {
            AccInput acc =  accInputs[0];
            CreateAccOutput res = new CreateAccOutput();
            Intent toConfPage = new Intent(MainActivity.this, ConfScreen.class);


            AuthSignUpOptions options = AuthSignUpOptions.builder()
                    .userAttribute(AuthUserAttributeKey.email(), acc.getEmail())
                    .build();
            Amplify.Auth.signUp(acc.getUname(), acc.getPword(), options,
                    result -> {
                        successtoast.show();
                        startActivity(toConfPage);
                    },
                    error -> {
                        errortoast.show();
                    }
            );

            return null;
        }

    }


}