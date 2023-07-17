package com.example.assignment_3;

import android.app.Application;
import android.util.Log;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;

public class AeroPlane extends Application {
    public void onCreate() {
        super.onCreate();

        try {
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.configure(getApplicationContext());
            Log.i("AeroPlane", "Initialized Amplify");
        } catch (AmplifyException error) {
            Log.e("AeroPlane", "Could not initialize Amplify", error);
        }
    }
}
