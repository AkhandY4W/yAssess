package com.youth4work.yassess_new.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vlonjatg.progressactivity.ProgressRelativeLayout;
import com.youth4work.yassess_new.R;
import com.youth4work.yassess_new.network.model.User;
import com.youth4work.yassess_new.network.model.response.EditprofileResponse;
import com.youth4work.yassess_new.ui.base.BaseActivity;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class YouthExtraDetailActivity extends BaseActivity implements View.OnClickListener {
static EditprofileResponse mEditProfile;
    ProgressRelativeLayout progressRelativeLayout;
    EditText edtAadhar,edtUserHeadline;
    CheckBox checkBoxMale,checkBoxFemale;
    TextView txtAadhar,txtUserGender,txtUserHeadline;
    LinearLayout layoutGender;
    Button btnSave;
    static int mTestId;
    String sex="";
    public static void  show(EditprofileResponse editprofileResponse, int testId,Context context){
        mEditProfile=editprofileResponse;
        mTestId=testId;
        context.startActivity(new Intent(context,YouthExtraDetailActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youth_extra_detail);
        progressRelativeLayout=findViewById(R.id.progressActivity);
         edtAadhar=findViewById(R.id.edt_aadhar);
         edtUserHeadline=findViewById(R.id.edt_user_headline);
         checkBoxMale=findViewById(R.id.checkBox_male);
         checkBoxFemale=findViewById(R.id.checkBox_female);
         txtAadhar=findViewById(R.id.txt_aadhar);
         txtUserGender=findViewById(R.id.txt_user_gender);
         txtUserHeadline=findViewById(R.id.txt_user_headline);
         layoutGender=findViewById(R.id.layout_gender);
        btnSave=findViewById(R.id.btn_save);
        checkBoxMale.setOnClickListener(this);
        checkBoxFemale.setOnClickListener(this);
         setVisibility(mEditProfile);
         getYouthDetails(mUserManager.getUser().getUserId());
        /*if(mUser.getAadhar()!=null)
            edtAadhar.setText(mUser.getAadhar());
        if(mUser.getHeadLine()!=null)
            edtUserHeadline.setText(mUser.getHeadLine());
        if(mUser.getSex()==null||mUser.getSex().equals("M")){
            checkBoxMale.isChecked();
        }
        else {
            checkBoxFemale.isChecked();
        }*/

        btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(getAdhar())
                    {
                       edtAadhar.setError("length must be exactly 12 characters");
                    }
                    else if(getProfileHeadline()) {
                           edtUserHeadline.setError("Enter Your Profile Heading");
                        }
                    else {
                        submitDetails(mUserManager.getUser().getUserId(),edtAadhar.getText().toString().trim(),edtUserHeadline.getText().toString().trim(),sex);
                    }

                }
            });
    }


        private boolean getProfileHeadline() {

        boolean isRequire=false;
        if(mEditProfile.isProfileHeading()){
            if(TextUtils.isEmpty(edtUserHeadline.getText().toString().trim())){
                isRequire=true;
            }
        }
        return isRequire;
    }

    private boolean getAdhar() {
        boolean isrequire=false;
        if(mEditProfile.isAadharNo()){
            if(TextUtils.isEmpty(edtAadhar.getText().toString())||!validateAadharNumber(edtAadhar.getText().toString())){
                isrequire=true;
            }
        }
        return isrequire;
    }

    public static boolean validateAadharNumber(String aadharNumber){
        Pattern aadharPattern = Pattern.compile("\\d{12}");
        boolean isValidAadhar = aadharPattern.matcher(aadharNumber).matches();
       /* if(isValidAadhar){
            isValidAadhar = Verhoeff.validateVerhoeff(aadharNumber);
        }  */
       return isValidAadhar;
    }
    private void submitDetails(Long userId, String aadhar, String headline, String sex) {
        progressRelativeLayout.showLoading();
        yAssessServices.SummitEditprofile(userId,sex,aadhar,headline).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if(response.isSuccessful()){
                    if(response.body()){
                        PracticeTestActivity.show(mTestId, YouthExtraDetailActivity.this,false);
                    }
                    else {
                        Toast.makeText(YouthExtraDetailActivity.this, "Something went wrong, please try again" , Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
            //eranil@youth4work.co.in
            }
        });

    }

    private void setVisibility(EditprofileResponse mEditProfile) {
        if(!mEditProfile.isProfileHeading()){
            txtUserHeadline.setVisibility(View.GONE);
            edtUserHeadline.setVisibility(View.GONE);
        }
        if(!mEditProfile.isAadharNo()){
            txtAadhar.setVisibility(View.GONE);
            edtAadhar.setVisibility(View.GONE);
        }
        if(!mEditProfile.isGender()){
            txtUserGender.setVisibility(View.GONE);
            layoutGender.setVisibility(View.GONE);
        }

    }

    private void getYouthDetails(Long userId) {
        progressRelativeLayout.showLoading();
        yAssessServices.GetYouthProfile(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()&&response.body()!=null){
                    User user=response.body();
                    progressRelativeLayout.showContent();
                    if(user.getAadhar()!=null)
                    edtAadhar.setText(user.getAadhar());
                    if(user.getHeadLine()!=null)
                    edtUserHeadline.setText(user.getHeadLine());
                    if(user.getSex()==null||user.getSex().equals("M")||user.getSex().equals("O")){
                       checkBoxMale.setChecked(true);
                        }
                        else {
                            checkBoxFemale.setChecked(true);
                        }

                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.checkBox_male:
                sex="M";
                checkBoxFemale.setChecked(false);
                break;
            case R.id.checkBox_female:
                sex="F";
                checkBoxMale.setChecked(false);
                break;
        }
    }
}