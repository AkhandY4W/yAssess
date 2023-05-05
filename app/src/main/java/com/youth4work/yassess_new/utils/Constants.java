package com.youth4work.yassess_new.utils;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Patterns;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.youth4work.yassess_new.network.model.request.LoginRequest;
import com.youth4work.yassess_new.network.model.response.EditprofileResponse;
import com.youth4work.yassess_new.network.model.response.LoginResponse;
import com.youth4work.yassess_new.network.yAssessApi;
import com.youth4work.yassess_new.network.yAssessServices;

import java.io.IOException;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Constants {
    public  static String authtoken="";
    static yAssessServices yAssessServices;
    Context context;
    // slide the view from below itself to the current position
    public static void slideUp(View view) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                view.getWidth(),                 // fromXDelta
                0,                 // toXDelta
                0,  // fromYDelta
                0);                // toYDelta
        animate.setDuration(1000);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    // slide the view from its current position to below itself
    public static void slideDown(View view) {
        view.setVisibility(View.GONE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                view.getWidth(),// toXDelta
                0,                 // fromYDelta
                0); // toYDelta
        animate.setDuration(1000);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }
    public static  String getAuthToken(Context context){
        LoginRequest loginRequest = new LoginRequest("YOUTH4WORKAPP","YOUTH4WORK@14FEB");
        yAssessServices = yAssessApi.createService(yAssessServices.class);
        yAssessServices.getAuth(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String authtoken = response.body().getToken();
                    PreferencesManager.instance(context).settoken(authtoken);
                }
            }
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
              Toast.makeText(context, t.toString()+" ", Toast.LENGTH_SHORT).show();
            }
        });
        return  authtoken;
    }
    public static void getEditProfile(int testId){
       EditprofileResponse editprofileResponse=new EditprofileResponse();
        try {
            editprofileResponse=yAssessServices.Editprofile(testId).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //get EmailID
    public static String getPrimaryEmailID(Context context) {
        return getInfofromAcc(context, Patterns.EMAIL_ADDRESS);
    }


    //get Mobile
/*
    public static String getMobileNo(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        String number = tm.getLine1Number();
        if (number == null) {
            number = getInfofromAcc(context, Patterns.PHONE);
        }
        return number != null ? number : "";
    }
*/

    public static String getInfofromAcc(Context context, Pattern p) {
        String possibleEmail = "";
        Pattern emailPattern = p; // API level 8+
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS)
                == PackageManager.PERMISSION_GRANTED) {
            Account[] accounts = AccountManager.get(context).getAccounts();
            for (Account account : accounts) {
                if (emailPattern.matcher(account.name).matches()) {
                    possibleEmail = account.name;
                }
            }
        }
        return possibleEmail;
    }

    public static String EncodeMaster(String val) {
        return val != null ? val.replace(".", "_").replace("-", "_").replace(" ", "-") : "";
    }


}
