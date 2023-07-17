package com.example.assignment_3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.amplifyframework.auth.cognito.result.AWSCognitoAuthSignOutResult;
import com.amplifyframework.auth.options.AuthSignOutOptions;
import com.amplifyframework.core.Amplify;

import DButils.LoginUtilsClient;
import DButils.model.AccOutput;
import DButils.model.NewCreds;

public class loggedin extends AppCompatActivity implements SensorEventListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText username, password, email, service;
    private SensorManager SM;
    private Sensor accelerometerSensor;

    private Toast traptoast, toast;

    // Accelerometer values
    private float lastX, lastY, lastZ;
    private float shakeThreshold = 10f;

    private String owner;
    private long lastShakeTime;
    private static final int SHAKE_TIMEOUT = 500; // Adjust the timeout duration as needed
    private static final int SHAKE_INTERVAL = 1000; // Adjust the interval duration as needed
    private Button buttonUpdate, viewstoredcredentials;

    private ApiClientFactory factory;
    private LoginUtilsClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loggedin);
        username = (EditText) findViewById(R.id.editTextUname);
        password = (EditText) findViewById(R.id.editTextPword);
        email = (EditText) findViewById(R.id.editTextEmail);
        service = (EditText) findViewById(R.id.editTextSname);
        buttonUpdate = (Button) findViewById(R.id.buttonUpdate);
        traptoast = Toast.makeText(getApplicationContext(), "You activated my trap card!", Toast.LENGTH_SHORT);
        Bundle extras = getIntent().getExtras();
        owner = extras.getString("owner");

        factory = new ApiClientFactory();
        client = factory.build(LoginUtilsClient.class);

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NewCreds n = new NewCreds();

                n.setEmail(email.getText().toString());
                n.setPword(password.getText().toString());
                n.setOwner(owner);
                n.setUname(username.getText().toString());
                n.setSname(service.getText().toString());

                submitCreds s = new submitCreds();
                NewCreds[] params = new NewCreds[1];
                params[0] = n;
                s.execute(params);
            }
        });

        viewstoredcredentials = (Button) findViewById(R.id.viewstoredcredentials);
        viewstoredcredentials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(loggedin.this, userinfo.class);
                intent.putExtra("owner", owner);
                startActivity(intent);
            }
        });


        SM = (SensorManager) getSystemService(SENSOR_SERVICE);

    }

    @Override
    public void onResume() {
        super.onResume();
        // Register the accelerometer sensor listener
        accelerometerSensor = (Sensor) SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SM.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister the accelerometer sensor listener
        SM.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();

        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            // Get the acceleration values
            float currentX = event.values[0];
            float currentY = event.values[1];
            float currentZ = event.values[2];

            // Calculate the acceleration difference
            float deltaX = currentX - lastX;
            float deltaY = currentY - lastY;
            float deltaZ = currentZ - lastZ;

            // Update the last known values
            lastX = currentX;
            lastY = currentY;
            lastZ = currentZ;

            // Calculate the acceleration magnitude
            double magnitude = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

            // Check if the magnitude exceeds the shake threshold
            if (magnitude > shakeThreshold) {
                long currentTime = System.currentTimeMillis();
                // Check if enough time has passed since the last shake
                if (currentTime - lastShakeTime > SHAKE_INTERVAL) {
                    // Handle the shake event (e.g., go back to the main activity)
                    goBackToMainActivity();

                    lastShakeTime = currentTime;
                }
            }
        }
    }

    private class submitCreds extends AsyncTask<NewCreds, Void, AccOutput>{

        @Override
        protected AccOutput doInBackground(NewCreds... newCreds) {
            NewCreds input = newCreds[0];
            AccOutput result = client.addcredsPost(input);

            return result;
        }

        protected void onPostExecute(AccOutput result){
            toast = Toast.makeText(getApplicationContext(), result.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private class LogOut extends AsyncTask<Void, Void, Void> {

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

    private void goBackToMainActivity() {
        LogOut l = new LogOut();
        l.execute();

        traptoast.show();
        Intent intent = new Intent(loggedin.this, LoginScreen.class);
        startActivity(intent);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}