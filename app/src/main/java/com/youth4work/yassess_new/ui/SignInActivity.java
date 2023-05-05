package com.youth4work.yassess_new.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vlonjatg.progressactivity.ProgressRelativeLayout;
import com.youth4work.yassess_new.R;
import com.youth4work.yassess_new.infrastructure.UserManager;
import com.youth4work.yassess_new.network.model.User;
import com.youth4work.yassess_new.ui.base.BaseActivity;
import com.youth4work.yassess_new.utils.PreferencesManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends BaseActivity {
    MaterialEditText edtUser,edtPass;
    Button btnSignIn;
    TextView txtForgotPAssword,txtError;
    ProgressRelativeLayout progressRelativeLayout;
    static String mEmail="";
static String mPasskey="";

    public static void show(String email, Context contxt){
    mEmail=email;
    contxt.startActivity(new Intent(contxt,SignInActivity.class));
    }

    public static void showWithPasskey(String passkey, Context contxt){
    mPasskey=passkey;
    contxt.startActivity(new Intent(contxt,SignInActivity.class));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        edtUser=findViewById(R.id.edt_user);
        edtPass=findViewById(R.id.edt_pass);
        btnSignIn=findViewById(R.id.btn_sign_in);
        txtForgotPAssword=findViewById(R.id.txt_forget_password);
        txtError=findViewById(R.id.txt_error);
        progressRelativeLayout=findViewById(R.id.progressActivity);
        if(!mEmail.equals("")){
            edtUser.setText(mEmail);
        }
        txtForgotPAssword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ForgetPasswordActivity.show(edtUser.getText().toString(),SignInActivity.this);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressRelativeLayout.showLoading();
                if(TextUtils.isEmpty(edtUser.getText().toString())||TextUtils.isEmpty(edtPass.getText().toString())){
                    progressRelativeLayout.showContent();
                    txtError.setVisibility(View.VISIBLE);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            // Actions to do after 10 seconds
                            txtError.setVisibility(View.GONE);

                        }
                    }, 3000);
                }
                else {
                    yAssessServices.doSignIn(edtUser.getText().toString().trim(), edtPass.getText().toString().trim()).enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if(response.isSuccessful()&&response.body()!=null){
                                User newUser=response.body();
                                if(newUser.getUserId()!=null&&newUser.getUserId()>0) {
                                    if (newUser.getUserType().equals("ST")) {
                                        progressRelativeLayout.showContent();
                                        UserManager.getInstance(SignInActivity.this).setUser(response.body());
                                        PreferencesManager.instance(SignInActivity.this).saveLoginDetails(edtUser.getText().toString().trim(), edtPass.getText().toString().trim());
                                        SignInActivity.this.finish();
                                        if(mPasskey.equals("")){
                                            startActivity(new Intent(SignInActivity.this, TestSelectionActivity.class));
                                        }
                                        else {
                                            TestSelectionActivity.show(mPasskey,SignInActivity.this);
                                        }
                                    } else {
                                        progressRelativeLayout.showContent();
                                        Toast.makeText(SignInActivity.this, "Please login as youth", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    progressRelativeLayout.showContent();
                                    txtError.setText("You have entered wrong password.");
                                    txtError.setVisibility(View.VISIBLE);
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        public void run() {
                                            // Actions to do after 10 seconds
                                            txtError.setVisibility(View.GONE);

                                        }
                                    }, 3000);
                                }
                            }
                            else {
                                progressRelativeLayout.showContent();
                                Toast.makeText(SignInActivity.this, "Invalid email or password!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            progressRelativeLayout.showContent();
                            Toast.makeText(SignInActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }
}