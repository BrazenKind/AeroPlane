package com.example.assignment_3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.amplifyframework.auth.cognito.result.AWSCognitoAuthSignOutResult;
import com.amplifyframework.auth.options.AuthSignOutOptions;
import com.amplifyframework.core.Amplify;

import java.util.ArrayList;
import java.util.HashMap;

import DButils.LoginUtilsClient;
import DButils.model.LookupRequest;
import DButils.model.LookupResults;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

public class userinfo extends AppCompatActivity implements SensorEventListener {


    ArrayList<HashMap<String, String>> userlist;
    ListView lv;

    private SensorManager SM;
    private Sensor accelerometerSensor;

    private float lastX, lastY, lastZ;
    private float shakeThreshold = 10f;

    private Toast traptoast;

    private long lastShakeTime;
    private static final int SHAKE_TIMEOUT = 500; // Adjust the timeout duration as needed
    private static final int SHAKE_INTERVAL = 1000; // Adjust the interval duration as needed

    private String owner;
    private ApiClientFactory factory;
    private LoginUtilsClient client;

    private Button addmorecredentials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        SM = (SensorManager) getSystemService(SENSOR_SERVICE);
        traptoast = Toast.makeText(getApplicationContext(), "You activated my trap card!", Toast.LENGTH_SHORT);
        Bundle extras = getIntent().getExtras();
        owner = extras.getString("owner");

        factory = new ApiClientFactory();
        client = factory.build(LoginUtilsClient.class);

        LookupRequest r = new LookupRequest();
        r.setOwner(owner);
        getDBEntries gDB = new getDBEntries();
        LookupRequest[] params = new LookupRequest[1];
        params[0] = r;
        gDB.execute(params);

        addmorecredentials = (Button) findViewById(R.id.addmorecredentials);
        addmorecredentials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tologgedin = new Intent(userinfo.this, loggedin.class);
                tologgedin.putExtra("owner", owner);
                startActivity(tologgedin);
            }
        });

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

    private void adduser(ArrayList<HashMap<String, String>> ul, String uname, String pword, String email, String sname){
        HashMap<String, String> user = new HashMap<>();
        user.put("name", uname);
        user.put("password", pword);
        user.put("email", email);
        user.put("service name", sname);

        ul.add(user);

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

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private class getDBEntries extends AsyncTask<LookupRequest, Void, LookupResults>{

        @Override
        protected LookupResults doInBackground(LookupRequest... lookupRequests) {
            LookupRequest input = lookupRequests[0];
            LookupResults r = client.retrievedataPost(input);

            return r;
        }

        protected void onPostExecute(LookupResults r){
            String jsonstring = ("{\"queries\": ").concat(r.getMessage());
            String final_jsonstring = jsonstring.concat("}");
            userlist = new ArrayList<>();
            try {
                final JSONObject obj = new JSONObject(final_jsonstring);
                final JSONArray query_data = obj.getJSONArray("queries");
                Log.i("test", "b");
                final int n = query_data.length();
                for (int i = 0; i < n; i++){
                    final JSONObject creds = query_data.getJSONObject(i);
                    adduser(userlist, creds.getString("Uname"),
                            creds.getString("Pword"),
                            creds.getString("Email"),
                            creds.getString("Sname"));
                }

                lv = (ListView) findViewById(R.id.list_view);
                ListAdapter adapter = new SimpleAdapter(userinfo.this, userlist, R.layout.list_row, new String[]{"name",
                        "password", "email", "service name"}, new int[]{R.id.uname, R.id.pword, R.id.email, R.id.sname});
                lv.setAdapter(adapter);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

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
        Intent intent = new Intent(userinfo.this, LoginScreen.class);
        startActivity(intent);
    }



}