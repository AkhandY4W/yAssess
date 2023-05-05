package com.youth4work.yassess_new.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import com.youth4work.yassess_new.R;
import com.youth4work.yassess_new.ui.base.BaseActivity;
import com.youth4work.yassess_new.utils.Constants;
import com.youth4work.yassess_new.utils.PreferencesManager;

public class SplashActivity extends BaseActivity {
    private static int SPLASH_SCREEN_TIME_OUT = 2000;
    static String mPasskey = "";
    ProgressBar progress;
    public static void show(String passkey, Context context) {
        mPasskey = passkey;
        context.startActivity(new Intent(context, SplashActivity.class));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //this will bind your MainActivity.class file with activity_main.
        progress  =findViewById(R.id.progressBar);

        String token = PreferencesManager.instance(SplashActivity.this).getToken();
        if (token == null) {
            Constants.getAuthToken(SplashActivity.this);
        }
       new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progress.setVisibility(View.VISIBLE);
                if(mUserManager.getUser()!=null && mUserManager.isUserLoggedIn()){
                    if(!mPasskey.equals("")){
                        TestSelectionActivity.show(mPasskey,SplashActivity.this);
                        finish();
                    }
                    else {
                        startActivity(new Intent(SplashActivity.this,TestSelectionActivity.class));
                        finish();
                    }
                }
                else {
                    startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                    finish();
                }

            }
        }, SPLASH_SCREEN_TIME_OUT);

    }

}
