package com.youth4work.yassess_new.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.vlonjatg.progressactivity.ProgressRelativeLayout;
import com.youth4work.yassess_new.R;
import com.youth4work.yassess_new.infrastructure.UserManager;
import com.youth4work.yassess_new.network.model.User;
import com.youth4work.yassess_new.network.model.response.CheckPasskeyStatusResp;
import com.youth4work.yassess_new.ui.base.BaseActivity;
import com.youth4work.yassess_new.utils.PreferencesManager;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    Button btnPasskey, btnEmail, btnEmailSubmit,btnPasskeyOk;
    LinearLayout layoutEmail, layoutPasskey, registerLayout;
    EditText edtEmail, edtPassword,edtPasskey;
    TextView txtForgetPassword, txtRegister,txtHowWorkDetails;
    CardView loginDetailsEmail,loginDetailsPasskey;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z0-9]+\\.+[a-z]+";
    static Boolean fromReg=false;
    ProgressRelativeLayout progressRelativeLayout;
    public static void showFromRegister(Boolean mFromReg, Context mContxt) {
        fromReg = mFromReg;
        mContxt.startActivity(new Intent(mContxt, LoginActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnEmail = findViewById(R.id.btn_email);
        btnPasskey = findViewById(R.id.btn_passkey);
        btnEmailSubmit = findViewById(R.id.button_email_submit);
        btnPasskeyOk = findViewById(R.id.btn_passkey_ok);
        layoutEmail = findViewById(R.id.layout_email);
        edtEmail = findViewById(R.id.editText_email);
        edtPasskey = findViewById(R.id.edt_passkey);
        edtPassword = findViewById(R.id.editText_password);
        registerLayout = findViewById(R.id.register_layout);
        txtForgetPassword = findViewById(R.id.txt_forget_password);
        txtRegister = findViewById(R.id.txt_register);
        txtHowWorkDetails = findViewById(R.id.txt_how_work_details);
        layoutPasskey = findViewById(R.id.layout_passkey);
         loginDetailsEmail= findViewById(R.id.login_details_email);
        loginDetailsPasskey = findViewById(R.id.login_details_passkey);
        progressRelativeLayout = findViewById(R.id.progressActivity);
        btnPasskey.setOnClickListener(this);
        btnEmail.setOnClickListener(this);
        btnEmailSubmit.setOnClickListener(this);
        txtForgetPassword.setOnClickListener(this);
        txtRegister.setOnClickListener(this);
        btnPasskeyOk.setOnClickListener(this);
        //edtPasskey.setText(Constants.authtoken);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
       /* if(fromReg){
            btnEmail.setTextColor(getResources().getColor(R.color.white));
            btnPasskey.setTextColor(getResources().getColor(R.color.txt_black));
            btnEmail.setBackgroundResource(R.drawable.black_border);
            btnPasskey.setBackgroundResource(R.drawable.black_border_with_white_background);
            Constants.slideDown(layoutPasskey);
            Constants.slideUp(layoutEmail);
            loginDetailsPasskey.setVisibility(View.GONE);
            loginDetailsEmail.setVisibility(View.VISIBLE);
            fromReg=false;
        }
        else {
            edtPasskey.requestFocus();
            btnPasskey.setTextColor(getResources().getColor(R.color.white));
            btnEmail.setTextColor(getResources().getColor(R.color.txt_black));
            btnPasskey.setBackgroundResource(R.drawable.black_border);
            btnEmail.setBackgroundResource(R.drawable.black_border_with_white_background);
            //Constants.slideDown(layoutEmail);
            //Constants.slideUp(layoutPasskey);
            loginDetailsEmail.setVisibility(View.GONE);
            loginDetailsPasskey.setVisibility(View.VISIBLE);

        }*/

    }

    @Override
    public void onClick(View view) {
        progressRelativeLayout.showLoading();
        switch (view.getId()) {

            case R.id.btn_passkey:
                fromReg=false;
                edtPasskey.requestFocus();
                btnPasskey.setTextColor(getResources().getColor(R.color.white));
                btnEmail.setTextColor(getResources().getColor(R.color.txt_black));
                btnPasskey.setBackgroundResource(R.drawable.black_border);
                btnEmail.setBackgroundResource(R.drawable.black_border_with_white_background);

                //Constants.slideDown(layoutEmail);
                //Constants.slideUp(layoutPasskey);
                loginDetailsPasskey.bringToFront();
                loginDetailsEmail.setVisibility(View.GONE);
                loginDetailsPasskey.setVisibility(View.VISIBLE);
                txtHowWorkDetails.setText(getString(R.string.how_it_work_details_passkey));
                progressRelativeLayout.showContent();
                break;

            case R.id.btn_email:
                edtEmail.requestFocus();
                btnEmail.setTextColor(getResources().getColor(R.color.white));
                btnPasskey.setTextColor(getResources().getColor(R.color.txt_black));
                btnEmail.setBackgroundResource(R.drawable.black_border);
                btnPasskey.setBackgroundResource(R.drawable.black_border_with_white_background);
                //Constants.slideDown(layoutPasskey);
                //Constants.slideUp(layoutEmail);
                loginDetailsPasskey.setVisibility(View.GONE);
                loginDetailsEmail.setVisibility(View.VISIBLE);
                txtHowWorkDetails.setText(getString(R.string.how_it_work_details));
                progressRelativeLayout.showContent();
                break;

            case R.id.button_email_submit:

                if (btnEmailSubmit.getText().equals("Ok")) {
                    if(edtEmail.getText().toString().isEmpty()) {
                        progressRelativeLayout.showContent();
                        Toast.makeText(getApplicationContext(),"Enter Email address",Toast.LENGTH_SHORT).show();
                    }else {
                        if (isValidEmail(edtEmail.getText().toString().trim())) {
                            if (isEmailRegisterOrNot(edtEmail.getText().toString())) {
                                edtPassword.setVisibility(View.VISIBLE);
                                edtPassword.requestFocus();
                                btnEmailSubmit.setText("Log in");
                                btnEmailSubmit.setAllCaps(false);
                                registerLayout.setVisibility(View.VISIBLE);
                                progressRelativeLayout.showContent();
                            } else {
                                progressRelativeLayout.showContent();
                                RegisterationActivity.show(edtEmail.getText().toString(), LoginActivity.this);
                            }
                        } else {
                            progressRelativeLayout.showContent();
                            Toast.makeText(getApplicationContext(),"Invalid Email address", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    progressRelativeLayout.showLoading();
                    yAssessServices.doSignIn(edtEmail.getText().toString().trim(), edtPassword.getText().toString().trim()).enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if(response.isSuccessful()&&response.body()!=null){
                                User newUser=response.body();
                               if(newUser.getUserId()!=null&&newUser.getUserId()>0){
                                if(newUser.getUserType().equals("ST")){
                                    progressRelativeLayout.showContent();
                                    UserManager.getInstance(LoginActivity.this).setUser(response.body());
                                    PreferencesManager.instance(LoginActivity.this).saveLoginDetails(edtEmail.getText().toString().trim(), edtPassword.getText().toString().trim());
                                    LoginActivity.this.finish();
                                    startActivity(new Intent(LoginActivity.this,TestSelectionActivity.class));
                                }
                                else {
                                    progressRelativeLayout.showContent();
                                    Toast.makeText(LoginActivity.this, "Please login as youth", Toast.LENGTH_SHORT).show();
                                }
                               }
                                else {
                                       progressRelativeLayout.showContent();
                                       Toast.makeText(LoginActivity.this, "Invalid email or password!", Toast.LENGTH_SHORT).show();

                                   }
                            }
                            else {
                                progressRelativeLayout.showContent();
                                Toast.makeText(LoginActivity.this, "Invalid email or password!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            progressRelativeLayout.showContent();
                            Toast.makeText(LoginActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                        }
                    });

                    //startActivity(new Intent(LoginActivity.this,TestSelectionActivity.class));
                }
                break;

            case R.id.txt_register:
                progressRelativeLayout.showContent();
                RegisterationActivity.show(edtEmail.getText().toString(), LoginActivity.this);
                break;

            case R.id.txt_forget_password:
                progressRelativeLayout.showContent();
                ForgetPasswordActivity.show(edtEmail.getText().toString(), LoginActivity.this);
                break;
            case R.id.btn_passkey_ok:
                progressRelativeLayout.showLoading();
                VerifyPasskey();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }

    }

    private void VerifyPasskey() {
        if(TextUtils.isEmpty(edtPasskey.getText().toString())){
            progressRelativeLayout.showContent();
            edtPasskey.setError("Passkey can't be empty");
        }
        else {
            yAssessServices.CheckPasskeyStatus(edtPasskey.getText().toString()).enqueue(new Callback<CheckPasskeyStatusResp>() {
                @Override
                public void onResponse(Call<CheckPasskeyStatusResp> call, Response<CheckPasskeyStatusResp> response) {
                    if(response.isSuccessful()&&response.body()!=null){
                        CheckPasskeyStatusResp checkPasskeyStatusResp = response.body();
                        if (checkPasskeyStatusResp.getTestId() > 0 && checkPasskeyStatusResp.getStatus().equals("Active")) {
                            progressRelativeLayout.showContent();
                            RegisterationActivity.showFromPasskey(edtPasskey.getText().toString(), LoginActivity.this);
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Passkey is not valid", Toast.LENGTH_SHORT).show();
                            progressRelativeLayout.showContent();
                        }
                    }
                }

                @Override
                public void onFailure(Call<CheckPasskeyStatusResp> call, Throwable t) {

                }
            });
           }
    }
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    private boolean isEmailRegisterOrNot(String emailId)  {
        progressRelativeLayout.showLoading();
        Boolean isExist = false;
        String responseBody = null;
        try {
            responseBody = yAssessServices.CheckEmail(emailId).execute().body().string();
            if(responseBody.equals("\"1\"")) {
                progressRelativeLayout.showContent();
                isExist = true;
    }
        } catch (IOException e) {
            progressRelativeLayout.showContent();
            e.printStackTrace();
            isExist = false;
    }

    /*.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()&&response.body()!=null){
                    try {
                        if(response.body().string().equals("1")){
                            isExist[0] =true;
                        }
                    } catch (IOException e) {
                        isExist[0] =false;
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
            }
        });*/
        return isExist;
    }

}
