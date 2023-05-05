package com.youth4work.yassess_new.ui;

import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.StrictMode;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vlonjatg.progressactivity.ProgressRelativeLayout;
import com.youth4work.yassess_new.R;
import com.youth4work.yassess_new.network.model.GetUserDetailsModel;
import com.youth4work.yassess_new.ui.base.BaseActivity;
import com.youth4work.yassess_new.utils.Constants;
import com.youth4work.yassess_new.utils.OtpEditText;

import java.io.IOException;

public class ForgetPasswordActivity extends BaseActivity implements View.OnClickListener {
    static String mEmailId;
    CardView layoutFindUser, layoutVerifyOtp,
            layoutEnterOtp,layoutChangePassword;
    Button btnSearch, btnSend, btnContinue, btnTryAgain,
            btnSeePass,btnContinuePassword;
    CheckBox checkBox, checkBox2;
    TextView txtSignIn,txtDummyEnterOtp2,txtRetry,txtError;
    EditText edtEmaill,etPasswordConfirm,etPassword;
    TextView txtEmail,txtMobile;
    GetUserDetailsModel getUserDetailsModel=new GetUserDetailsModel();
    String encryptEmail,encryptMobile;
    ProgressRelativeLayout progressRelativeLayout;
    String verificationType="";
    Boolean isCorrect=false;
    OtpEditText txtPinEntry;
    public static void show(String emailid, Context mContxt) {
        mEmailId = emailid;
        mContxt.startActivity(new Intent(mContxt, ForgetPasswordActivity.class));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
       txtPinEntry = findViewById(R.id.et_otp);
        layoutFindUser = findViewById(R.id.layout_find_user);
        layoutVerifyOtp = findViewById(R.id.layout_verify_otp);
        layoutEnterOtp = findViewById(R.id.layout_enter_otp);
        layoutChangePassword = findViewById(R.id.layout_change_password);
        btnSearch = findViewById(R.id.btn_search);
        btnContinue = findViewById(R.id.btn_continue);
        btnTryAgain = findViewById(R.id.btn_try_again);
        btnSend = findViewById(R.id.btn_send);
        btnContinuePassword = findViewById(R.id.btn_continue_password);
        btnSeePass = findViewById(R.id.btn_see_pass);
        checkBox = findViewById(R.id.checkBox);
        checkBox2 = findViewById(R.id.checkBox2);
        txtSignIn = findViewById(R.id.txt_sign_in);
        edtEmaill = findViewById(R.id.edt_email);
        etPassword = findViewById(R.id.et_password);
        etPasswordConfirm = findViewById(R.id.et_password_confirm);
        txtEmail  = findViewById(R.id.text_email);
        txtMobile = findViewById(R.id.text_mobile);
        txtRetry = findViewById(R.id.txt_retry);
        txtError = findViewById(R.id.txt_error);
        txtDummyEnterOtp2 = findViewById(R.id.txt_dummy_enter_otp2);
        progressRelativeLayout = findViewById(R.id.progressActivity);
        edtEmaill.setText(mEmailId);
        btnSend.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        btnContinue.setOnClickListener(this);
        btnTryAgain.setOnClickListener(this);
        btnContinuePassword.setOnClickListener(this);
        checkBox.setOnClickListener(this);
        checkBox2.setOnClickListener(this);
        txtSignIn.setOnClickListener(this);
        txtRetry.setOnClickListener(this);
       // int count=mEmailId.length();
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        btnSeePass.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    etPasswordConfirm.setInputType(InputType.TYPE_CLASS_TEXT);
                    btnSeePass.setBackground(getResources().getDrawable(R.drawable.assets_01_ic_view));
                    break;
                case MotionEvent.ACTION_UP:
                    etPasswordConfirm.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    btnSeePass.setBackground(getResources().getDrawable(R.drawable.ic_password_view_off_24dp));
                    break;
            }
            return true;
        });
        edtEmaill.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                edtEmaill.setSelection(edtEmaill.getText().toString().trim().length());
            } else {
                edtEmaill.setSelection(0);
            }
        });
        etPasswordConfirm.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                etPasswordConfirm.setSelection(etPasswordConfirm.getText().toString().trim().length());
            } else {
                etPasswordConfirm.setSelection(0);
            }
        });
        txtPinEntry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() == "1234".length()) {
                    try {
                       isCorrect= yAssessServices.checkverificationcode(s.toString(),verificationType).execute().body();
                    } catch (IOException e) {
                        isCorrect=false;
                        e.printStackTrace();
                    }
                    if (isCorrect) {
                        Toast.makeText(ForgetPasswordActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(ForgetPasswordActivity.this, "InCorrect OTP", Toast.LENGTH_SHORT).show();
                        txtPinEntry.setText(null);
                    }
                }
                /*else {
                    txtError.setVisibility(View.VISIBLE);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            // Actions to do after 2 seconds
                            txtError.setVisibility(View.GONE);

                        }
                    }, 2000);
                }*/
                }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_search:
                progressRelativeLayout.showLoading();
                if(!TextUtils.isEmpty(edtEmaill.getText().toString().trim())){
                try {
                   getUserDetailsModel = yAssessServices.VerifyForgetPwdDtl(edtEmaill.getText().toString().trim()).execute().body();
                  if(getUserDetailsModel.getUserId()!=0) {
                      progressRelativeLayout.showContent();
                      encryptEmail=getUserDetailsModel.getEmailid().replaceAll("(^[^@]{3}|(?!^)\\G)[^@]", "$1*");
                      txtEmail.setText(encryptEmail);
                      String input = getUserDetailsModel.getMobileno();
                      String lastFourDigits = "";     //substring containing last 4 characters

                      if (input.length() > 4) {
                          lastFourDigits = input.substring(input.length() - 4);
                      } else {
                          lastFourDigits = input;
                      }
                      encryptMobile="******" + lastFourDigits;
                      txtMobile.setText(encryptMobile);
                      Constants.slideDown(layoutFindUser);
                      Constants.slideUp(layoutVerifyOtp);
                      layoutFindUser.setVisibility(View.GONE);
                      layoutEnterOtp.setVisibility(View.GONE);
                      layoutChangePassword.setVisibility(View.GONE);
                      layoutVerifyOtp.setVisibility(View.VISIBLE);
                  }
                  else {
                      progressRelativeLayout.showContent();
                      Toast.makeText(this, "We can't find any user, Please try again", Toast.LENGTH_SHORT).show();
                  }
                } catch (IOException e) {
                    e.printStackTrace();
                    progressRelativeLayout.showContent();
                    Toast.makeText(this, e.getMessage()+" ", Toast.LENGTH_SHORT).show();
                }
                }
            else {
                    Toast.makeText(this, "Please enter valid input", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.txt_retry:
                if(txtRetry.getText().equals("Didn't get a code?")){
                    sendOtp();
                }
                break;
             case R.id.btn_send:
                sendOtp();
                break;

            case R.id.btn_try_again:
                Constants.slideDown(layoutVerifyOtp);
                Constants.slideUp(layoutFindUser);
                layoutChangePassword.setVisibility(View.GONE);
                layoutFindUser.setVisibility(View.VISIBLE);
                layoutEnterOtp.setVisibility(View.GONE);
                layoutVerifyOtp.setVisibility(View.GONE);
                break;

            case R.id.txt_sign_in:
                SignInActivity.show(edtEmaill.getText().toString(), ForgetPasswordActivity.this);
                break;
            case R.id.btn_continue:
                if(isCorrect){
                Constants.slideDown(layoutVerifyOtp);
                Constants.slideUp(layoutChangePassword);
                layoutFindUser.setVisibility(View.GONE);
                layoutEnterOtp.setVisibility(View.GONE);
                layoutVerifyOtp.setVisibility(View.GONE);
                layoutChangePassword.setVisibility(View.VISIBLE);
                }
                else {
                    txtError.setVisibility(View.VISIBLE);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            // Actions to do after 2 seconds
                            txtError.setVisibility(View.GONE);

                        }
                    }, 2000);
                }
                break;
            case R.id.btn_continue_password:
                ChangePassword();
                break;

            case R.id.checkBox:
                checkBox2.setChecked(false);
                break;
            case R.id.checkBox2:
                checkBox.setChecked(false);
                break;

        }
    }

    private void sendOtp() {

        Boolean isSend=false;
        progressRelativeLayout.showLoading();
        if(checkBox.isChecked()){
            verificationType="email";
            try {
                String response=yAssessServices.verifynsendcodeemail(getUserDetailsModel.getEmailid()).execute().body().string();
                if(response.equals("\"1\"")){
                    isSend=true;
                    progressRelativeLayout.showContent();
                    txtDummyEnterOtp2.setText("A text message with 4 digit verification code was just sent to "+encryptEmail);
                }else {
                    isSend=false;
                    progressRelativeLayout.showContent();
                    Toast.makeText(this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                progressRelativeLayout.showContent();
                e.printStackTrace();
            }
        }
        else if(checkBox2.isChecked()) {
            verificationType="mobile";
            progressRelativeLayout.showLoading();
            try {
                isSend = yAssessServices.verifynsendcodemobile(getUserDetailsModel.getMobileno()).execute().body();
                progressRelativeLayout.showContent();
                txtDummyEnterOtp2.setText("A text message with 4 digit verification code was just sent to " + encryptMobile);
            } catch (IOException e) {
                progressRelativeLayout.showContent();
                e.printStackTrace();
            }
        }
        if(isSend){
            progressRelativeLayout.showContent();
            Constants.slideDown(layoutVerifyOtp);
            Constants.slideUp(layoutEnterOtp);
            layoutFindUser.setVisibility(View.GONE);
            layoutChangePassword.setVisibility(View.GONE);
            layoutEnterOtp.setVisibility(View.VISIBLE);
            new CountDownTimer(30000, 1000) {

                public void onTick(long millisUntilFinished) {
                    txtRetry.setText(String.valueOf(millisUntilFinished / 1000));
                    //here you can have your logic to set text to edittext
                }

                public void onFinish() {
                    txtRetry.setText("Didn't get a code?");
                }

            }.start();
            layoutVerifyOtp.setVisibility(View.GONE);
        }
        else {
            progressRelativeLayout.showContent();
            Toast.makeText(this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }
    }


    private void ChangePassword() {
        if (!TextUtils.isEmpty(etPassword.getText().toString())&&!etPassword.getText().toString().equals("")) {
            if (etPassword.getText().toString().equals(etPasswordConfirm.getText().toString())) {
                try {
                    Boolean isCHange=yAssessServices.changenewpassword(getUserDetailsModel.getUserId(),getUserDetailsModel.getEncryptPassword(),etPassword.getText().toString().trim()).execute().body();
                    if(isCHange){
                        Toast.makeText(this, "Password Change Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ForgetPasswordActivity.this,LoginActivity.class));
                    }
                    else {
                        Toast.makeText(this, "Password Change Unsuccessfully,Please try again", Toast.LENGTH_SHORT).show();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Something went wrong,please try again", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(this, "Password Not Match", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
