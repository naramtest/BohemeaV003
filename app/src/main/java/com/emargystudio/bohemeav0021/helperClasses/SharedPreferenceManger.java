package com.emargystudio.bohemeav0021.helperClasses;

import android.content.Context;
import android.content.SharedPreferences;

import com.emargystudio.bohemeav0021.Model.User;

public class SharedPreferenceManger {

    private static final String FILENAME = "BOHEMEAARTCAFE";
    private static final String USERNAME = "username";
    private static final String EMAIL = "email";
    private static final String IMAGE = "image";
    private static final String ID = "id";
    private static final String PHONE_NUMBER = "phone_number";

    private static SharedPreferenceManger mSharedPreferenceManger;
    private static Context mContext;

    public SharedPreferenceManger(Context context) {
        this.mContext = context;
    }

    public static synchronized SharedPreferenceManger getInstance(Context context){

        if(mSharedPreferenceManger == null){
            mSharedPreferenceManger = new SharedPreferenceManger(context);
        }
        return mSharedPreferenceManger;
    }

    public void storeUserData(User user){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERNAME,user.getUserName());
        editor.putString(EMAIL,user.getUserEmail());
        editor.putString(IMAGE,user.getUserPhoto());
        editor.putInt(ID,user.getUserId());
        editor.putInt(PHONE_NUMBER,user.getUserPhoneNumber());
        editor.apply();

    }

    public User getUserData(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        User user = new User(sharedPreferences.getInt(ID,-1),sharedPreferences.getString(USERNAME,null),
                sharedPreferences.getString(EMAIL,null),sharedPreferences.getString(IMAGE,null),
                sharedPreferences.getInt(PHONE_NUMBER,0));
        return user;
    }

    public void logUserOut(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

    }

    public boolean isUserLogggedIn(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);

        if(sharedPreferences.getString(USERNAME,null) != null){
            return true;
        }

        return false;
    }

    public void updatPhoneNumber(int phone_number){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PHONE_NUMBER,phone_number);
        editor.apply();


    }

}
