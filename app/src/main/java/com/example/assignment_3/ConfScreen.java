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
import com.amplifyframework.auth.AuthException;
import com.amplifyframework.core.Amplify;

import DButils.LoginUtilsClient;
import DButils.model.AccInputReduced;
import DButils.model.AccOutput;
import DButils.model.ConfInput;

public class ConfScreen extends AppCompatActivity {

    private EditText uname, ccode;

    private Button submit, login;

    private Toast successtoast, errortoast;

    private Context context;

    private ApiClientFactory factory;

    private LoginUtilsClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conf_screen);

        context = getApplicationContext();
        successtoast = Toast.makeText(context, "Account validation successful!", Toast.LENGTH_SHORT);
        errortoast = Toast.makeText(context, "Error validating account.", Toast.LENGTH_SHORT);
        factory = new ApiClientFactory();
        client = factory.build(LoginUtilsClient.class);

        uname = (EditText) findViewById(R.id.username);
        ccode = (EditText) findViewById(R.id.ccode);

        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                ConfInput acc = new ConfInput();
                acc.setUname(uname.getText().toString());
                acc.setCcode(ccode.getText().toString());

                ConfInput[] params = new ConfInput[1];
                params[0] = acc;

                ConfAcc temp = new ConfAcc();
                temp.execute(params);

            }
        });

        login = (Button) findViewById(R.id.continuetologin);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent toLoginScreen = new Intent(ConfScreen.this, LoginScreen.class);
                startActivity(toLoginScreen);
            }
        });
    }


    private class ConfAcc extends AsyncTask<ConfInput, Void, Void> {

        @Override
        protected Void doInBackground(ConfInput... ConfInputs) {
            ConfInput acc = ConfInputs[0];
            Intent toLoginPage = new Intent(ConfScreen.this, LoginScreen.class);

            Amplify.Auth.confirmSignUp(
                    acc.getUname(),
                    acc.getCcode(),
                    result -> {
                        successtoast.show();
                        startActivity(toLoginPage);
                    },
                    error -> {
                        Log.e("Conferror", error.getMessage());
                        errortoast.show();
                    }
            );

            return null;
        }

    }
}