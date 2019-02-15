package com.emargystudio.bohemeav0021;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.emargystudio.bohemeav0021.Model.User;
import com.emargystudio.bohemeav0021.helperClasses.SharedPreferenceManger;
import com.emargystudio.bohemeav0021.helperClasses.URLS;
import com.emargystudio.bohemeav0021.helperClasses.VolleyHandler;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.facebook.ParseFacebookUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLogin = findViewById(R.id.btnlogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                facebookLogin();

            }
        });

    }


    public void facebookLogin() {
        Collection<String> permissions = Arrays.asList("public_profile", "email");

        ParseFacebookUtils.logInWithReadPermissionsInBackground(MainActivity.this, permissions, new LogInCallback() {

            @Override
            public void done(ParseUser user, ParseException err) {
                if (err != null) {

                    ParseUser.logOut();
                    Log.e("err", "err", err);
                }
                if (user == null) {
                    ParseUser.logOut();
                    Toast.makeText(MainActivity.this, "Make sure you have access to your facebook account", Toast.LENGTH_LONG).show();

                } else if (user.isNew()) {

                    Toast.makeText(MainActivity.this, "Thank you for choosing our app", Toast.LENGTH_LONG).show();
                    getUserDetailFromFB();


                } else {

                    Toast.makeText(MainActivity.this, "Welcome back "+user.getUsername(), Toast.LENGTH_LONG).show();
                    loginUser(user);

                }
            }
        });
    }

    public void getUserDetailFromFB(){
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),new  GraphRequest.GraphJSONObjectCallback(){
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                final ParseUser user = ParseUser.getCurrentUser();
                try{
                    user.setUsername(object.getString("name"));
                }catch(JSONException e){
                    e.printStackTrace();
                }
                try{
                    user.setEmail(object.getString("email"));
                }catch(JSONException e){
                    e.printStackTrace();
                }
                if(response.getError() == null)
                {
                    try
                    {
                        final JSONObject mPicture = object.getJSONObject("picture");
                        final JSONObject mPictureData = mPicture.getJSONObject("data");
                        final boolean mSilhouette = mPictureData.getBoolean("is_silhouette");
                        final String mImageUrl = mPictureData.getString("url");
                        user.put("image", mImageUrl);
                    }

                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        alertTable(user);
                    }
                });
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields","name,email,picture.type(large)");
        request.setParameters(parameters);
        request.executeAsync();


    }

    private void alertDisplayer(){

        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    public void registerUser(final ParseUser user , final String phone_number){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLS.register_api,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")){
                                JSONObject jsonObjectUser =  jsonObject.getJSONObject("user");

                                User user = new User(jsonObjectUser.getInt("id"),jsonObjectUser.getString("user_name"),jsonObjectUser.getString("user_email")
                                        ,jsonObjectUser.getString("user_photo"),jsonObjectUser.getInt("user_phone_number"));


                                //store user data inside sharedPreferences
                                SharedPreferenceManger.getInstance(getApplicationContext()).storeUserData(user);
                                alertDisplayer();
                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        ){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map userData = new HashMap<>();
                userData.put("user_name",user.getUsername());
                userData.put("user_email",user.getEmail());
                userData.put("user_photo",user.getString("image"));
                userData.put("user_phone_number",phone_number);
                return  userData;
            }
        };//end of string Request

        VolleyHandler.getInstance(getApplicationContext()).addRequetToQueue(stringRequest);




    }

    public void loginUser(final ParseUser user){
        String email = user.getEmail();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.login_user+email,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if(!jsonObject.getBoolean("error")){
                                JSONObject jsonObjectUser =  jsonObject.getJSONObject("user");
                                Log.d(TAG, "onResponse: "+jsonObjectUser.toString());

                                User user = new User(jsonObjectUser.getInt("id"),jsonObjectUser.getString("user_name"),jsonObjectUser.getString("user_email")
                                        ,jsonObjectUser.getString("user_photo"),jsonObjectUser.getInt("user_phone_number"));
                                //store user data inside sharedPreferences
                                SharedPreferenceManger.getInstance(getApplicationContext()).storeUserData(user);
                                alertDisplayer();
                            }

                        }catch (JSONException e){
                            Log.d(TAG, "onResponse: "+e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }

        );



        VolleyHandler.getInstance(getApplicationContext()).addRequetToQueue(stringRequest);
    }


    public void alertTable(final ParseUser user){
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
                    registerUser(user,phone_number);
                    dialog.dismiss();
                }else {
                    layout.setError("Can't be empty");
                }
            }
        });




    }
}
