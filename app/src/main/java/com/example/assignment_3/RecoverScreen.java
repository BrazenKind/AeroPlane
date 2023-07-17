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
import com.amplifyframework.core.Amplify;

import DButils.LoginUtilsClient;
import DButils.model.AccInputReduced;
import DButils.model.AccOutput;
import DButils.model.UpdatePwordInput;

public class RecoverScreen extends AppCompatActivity {

    private EditText uname, pword, nword;

    private Button pwordrecover, login, pwordrecover2;

    private Toast successtoast, errortoast;

    private Context context;

    private ApiClientFactory factory;

    private LoginUtilsClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_screen);

        context = getApplicationContext();
        successtoast = Toast.makeText(context, "Password reset requested. Check your email for a confirmation code.", Toast.LENGTH_SHORT);
        errortoast = Toast.makeText(context, "Error: username does not exist.", Toast.LENGTH_SHORT);
        factory = new ApiClientFactory();
        client = factory.build(LoginUtilsClient.class);

        uname = (EditText) findViewById(R.id.username);

        pwordrecover = (Button) findViewById(R.id.reset);
        pwordrecover.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                UpdatePwordInput acc = new UpdatePwordInput();
                acc.setUname(uname.getText().toString());

                UpdatePwordInput[] params = new UpdatePwordInput[1];
                params[0] = acc;

                UpdatePwordAcc temp = new UpdatePwordAcc();
                temp.execute(params);
            }
        });

        login = (Button) findViewById(R.id.continuetologin);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent toLoginScreen = new Intent(RecoverScreen.this, LoginScreen.class);
                startActivity(toLoginScreen);
            }
        });

        pwordrecover2 = (Button) findViewById(R.id.continuetopwordreset2);
        pwordrecover2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toRecoverPage2 = new Intent(RecoverScreen.this, RecoverScreen2.class);
                startActivity(toRecoverPage2);
            }
        });
    }

    private class UpdatePwordAcc extends AsyncTask<UpdatePwordInput, Void, Void> {

        @Override
        protected Void doInBackground(UpdatePwordInput... updatePwordInputs) {
            String uname = updatePwordInputs[0].getUname();
            Intent toRecoverPage2 = new Intent(RecoverScreen.this, RecoverScreen2.class);

            Amplify.Auth.resetPassword(
                    uname,
                    result -> {
                        successtoast.show();
                        startActivity(toRecoverPage2);
                    },
                    error -> {
                        errortoast.show();
                    }
            );
            return null;
        }

    }
}