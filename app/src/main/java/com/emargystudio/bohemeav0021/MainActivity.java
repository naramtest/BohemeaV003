package com.emargystudio.bohemeav0021;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.emargystudio.bohemeav0021.Model.User;
import com.emargystudio.bohemeav0021.helperClasses.SharedPreferenceManger;
import com.emargystudio.bohemeav0021.helperClasses.URLS;
import com.emargystudio.bohemeav0021.helperClasses.VolleyHandler;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    Button btnLogin;
    TextView bohemea;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLogin = findViewById(R.id.btnlogin);
        bohemea = findViewById(R.id.bohemea);
        Typeface face = Typeface.createFromAsset(MainActivity.this.getAssets(),"fonts/NABILA.TTF");
        bohemea.setTypeface(face);

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {


                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {


                        getData(object);
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields","name,email,picture.type(large)");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this,Arrays.asList("public_profile","email"));
            }
        });

    }

    private void getData(JSONObject object) {

        final JSONObject mPicture;
        try {
            mPicture = object.getJSONObject("picture");
            final JSONObject mPictureData = mPicture.getJSONObject("data");
            final boolean mSilhouette = mPictureData.getBoolean("is_silhouette");

            final String mImageUrl = mPictureData.getString("url");
            String name = object.getString("name");
            String email = object.getString("email");

            loginUser(name,email,mImageUrl);



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }




    private void sendIntent(){

        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    //mySql register
    public void registerUser(final String name, final String email , final String profile_image , final String phone_number){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLS.register_api,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")){
                                JSONObject jsonObjectUser =  jsonObject.getJSONObject("user");
                                Log.d(TAG, "onResponse: "+jsonObjectUser.toString());

                                User user = new User(jsonObjectUser.getInt("id"),jsonObjectUser.getString("user_name"),jsonObjectUser.getString("user_email")
                                        ,jsonObjectUser.getString("user_photo"),jsonObjectUser.getInt("user_phone_number"));


                                //store user data inside sharedPreferences
                                SharedPreferenceManger.getInstance(getApplicationContext()).storeUserData(user);
                                sendIntent();
                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: "+error.toString());
                    }
                }
        ){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map userData = new HashMap<>();
                userData.put("user_name",name);
                userData.put("user_email",email);
                userData.put("user_photo",profile_image);
                userData.put("user_phone_number",phone_number);
                return  userData;
            }
        };//end of string Request

        VolleyHandler.getInstance(getApplicationContext()).addRequetToQueue(stringRequest);




    }

    public void loginUser(final String name, final String email , final String profile_image ){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.login_user+email,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d(TAG, "onResponse: "+response);



                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                if (!jsonObject.getBoolean("error")) {
                                    JSONObject jsonObjectUser = jsonObject.getJSONObject("user");

                                    if (jsonObjectUser.isNull("id")){
                                        alertTable(name,email,profile_image);
                                    }else {

                                        User user = new User(jsonObjectUser.getInt("id"), jsonObjectUser.getString("user_name"), jsonObjectUser.getString("user_email")
                                                , jsonObjectUser.getString("user_photo"), jsonObjectUser.getInt("user_phone_number"));
                                        //store user data inside sharedPreferences
                                        SharedPreferenceManger.getInstance(getApplicationContext()).storeUserData(user);
                                        sendIntent();

                                    }
                                }


                            } catch (JSONException e) {
                                Log.d(TAG, "onResponse: " + e.getMessage());
                            }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,getString(R.string.internet_off),Toast.LENGTH_LONG).show();
                    }
                }

        );



        VolleyHandler.getInstance(getApplicationContext()).addRequetToQueue(stringRequest);
    }


    //adding phone_number
    public void alertTable(final String name, final String email , final String profile_image ){
        final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("One more step ...");
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View alertLayout = li.inflate(R.layout.alert_phone, null);
        final EditText edtPhone = alertLayout.findViewById(R.id.edtPhone);
        TextView loginBtn = alertLayout.findViewById(R.id.login);

        final TextInputLayout layout = alertLayout.findViewById(R.id.edtPhoneLayout);

        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        final AlertDialog dialog = alert.create();
        dialog.show();
        Typeface face = Typeface.createFromAsset(getAssets(),"fonts/NABILA.TTF");
        loginBtn.setTypeface(face);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String phone_number = edtPhone.getText().toString();
                if (!phone_number.isEmpty()){
                    registerUser(name,email,profile_image,phone_number);
                    dialog.dismiss();
                }else {
                    layout.setError("Can't be empty");
                }
            }
        });




    }
}
