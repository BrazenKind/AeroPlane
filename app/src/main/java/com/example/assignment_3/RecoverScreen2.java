package com.example.assignment_3;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.amplifyframework.core.Amplify;

import DButils.LoginUtilsClient;
import DButils.model.AccOutput;
import DButils.model.UpdatePwordInput;

public class RecoverScreen2 extends AppCompatActivity {

    private EditText uname, nword, ccode;

    private Button pwordrecover, login;

    private Toast successtoast, errortoast;

    private Context context;

    private ApiClientFactory factory;

    private LoginUtilsClient client;

    public class PwordData {

        String uname;
        String ccode;
        String nword;

        public PwordData(String u, String c, String n){

            this.uname = u;
            this.ccode = c;
            this.nword = n;

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_screen2);

        context = getApplicationContext();
        successtoast = Toast.makeText(context, "Password successfully changed!", Toast.LENGTH_SHORT);
        errortoast = Toast.makeText(context, "Password change failed.", Toast.LENGTH_SHORT);
        factory = new ApiClientFactory();
        client = factory.build(LoginUtilsClient.class);

        uname = (EditText) findViewById(R.id.username);
        nword = (EditText) findViewById(R.id.newpassword);
        ccode = (EditText) findViewById(R.id.ccode);

        pwordrecover = (Button) findViewById(R.id.reset);
        pwordrecover.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                String u = uname.getText().toString();
                String n = nword.getText().toString();
                String c = ccode.getText().toString();
                PwordData acc = new PwordData(u, n, c);

                PwordData[] params = new PwordData[1];
                params[0] = acc;

                UpdatePword temp = new UpdatePword();
                temp.execute(params);
            }
        });

        login = (Button) findViewById(R.id.continuetologin);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent toLoginScreen = new Intent(RecoverScreen2.this, LoginScreen.class);
                startActivity(toLoginScreen);
            }
        });
    }

    private class UpdatePword extends AsyncTask<PwordData, Void, Void> {

        @Override
        protected Void doInBackground(PwordData... PwordDatas) {
            PwordData accdata = PwordDatas[0];
            Intent toLoginPage = new Intent(RecoverScreen2.this, LoginScreen.class);

            Amplify.Auth.confirmResetPassword(
                    accdata.uname,
                    accdata.nword,
                    accdata.ccode,
                    () -> {
                        successtoast.show();
                        startActivity(toLoginPage);
                    },
                    error -> {
                        errortoast.show();
                    }
            );

            return null;
        }

    }

}
