package com.youth4work.yassess_new.ui;

import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vlonjatg.progressactivity.ProgressRelativeLayout;
import com.youth4work.yassess_new.R;
import com.youth4work.yassess_new.network.model.response.GetYouthDetailsModel;
import com.youth4work.yassess_new.ui.base.BaseActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultActivity extends BaseActivity {
static Boolean mFlag=false;
static Context mContext;
CardView mWarningCard;
TextView mTxtWarning,txtUserName,txtUserHeadline,
        txtUserLocation,txtUserCollege,txtUserTalent,txtNameIntial;
ProgressRelativeLayout progressRelativeLayout;
static int mtestId;
GetYouthDetailsModel.ProfileDetail getYouthDetails;
ImageView imgBanner,imgUser;
    public static void show(Boolean flag,int testid, Context context){
        mFlag=flag;
        mtestId=testid;
        mContext=context;
        context.startActivity(new Intent(context,ResultActivity.class));
        }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        mWarningCard=findViewById(R.id.card_warning);
        mTxtWarning=findViewById(R.id.txt_waring);
        progressRelativeLayout=findViewById(R.id.progressActivity);
        txtUserName=findViewById(R.id.txt_user_name);
        txtUserHeadline=findViewById(R.id.txt_user_headline);
        txtUserLocation=findViewById(R.id.txt_user_location);
        txtUserTalent=findViewById(R.id.txt_user_talent);
        txtUserCollege=findViewById(R.id.txt_user_college);
        txtNameIntial=findViewById(R.id.txt_name_intial);
         imgBanner=findViewById(R.id.img_banner);
        imgUser=findViewById(R.id.img_user);
        Linkify.addLinks(mTxtWarning,Linkify.ALL);
        if(mFlag){
        mWarningCard.setVisibility(View.VISIBLE);
        }
        GetYouthDetails(mUserManager.getUser().getUserId(),mtestId);
    }

    private void GetYouthDetails(Long userId, int mtestId) {
        progressRelativeLayout.showLoading();
        yAssessServices.GetYouthDetails(mtestId,userId).enqueue(new Callback<GetYouthDetailsModel>() {
            @Override
            public void onResponse(Call<GetYouthDetailsModel> call, Response<GetYouthDetailsModel> response) {
                if(response.isSuccessful()&&response.body()!=null){
                    progressRelativeLayout.showContent();
                    getYouthDetails=response.body().getProfileDetail();
                    txtUserName.setText(getYouthDetails.getName());
                    txtUserCollege.setText(getYouthDetails.getCourse());
                    txtUserHeadline.setText(getYouthDetails.getHeadLine());
                    if(getYouthDetails.getTalents().size()>4) {
                        txtUserTalent.setText(getYouthDetails.getTalents().get(0) + ", " + getYouthDetails.getTalents().get(1) + ", " + getYouthDetails.getTalents().get(2) + "..." + (getYouthDetails.getTalents().size() - 3) + " more");
                        txtUserTalent.setVisibility(View.VISIBLE);
                    }
                    else if(getYouthDetails.getTalents().size()<1){
                        txtUserTalent.setVisibility(View.GONE);
                    }
                    else {
                        String listString = "";
                        for (String s : getYouthDetails.getTalents()) {
                            listString += s + ",";
                        }
                        listString=removeLastChar(listString);
                        txtUserTalent.setText(listString);
                        txtUserTalent.setVisibility(View.VISIBLE);
                    }
                    if(getYouthDetails.getPic()!=null&&!getYouthDetails.getPic().equals("") ){
                    Picasso.get().load(getYouthDetails.getPic()).into(imgUser);
                    }
                    else if(!mUserManager.getUser().getImgUrl().equals("")){
                        Picasso.get().load(mUserManager.getUser().getImgUrl()).into(imgUser);
                    }
                    else {
                        txtNameIntial.setVisibility(View.VISIBLE);
                        txtNameIntial.setText(getIntialName(getYouthDetails.getName()));
                    }

                }
            }

            @Override
            public void onFailure(Call<GetYouthDetailsModel> call, Throwable t) {

            }
        });
    }

    private String getIntialName(String name) {
        String str =name;
        String[] strArray = str.split(" ");
        StringBuilder builder = new StringBuilder();
        if (strArray.length > 0)
            builder.append(strArray[0], 0, 1);
        if (strArray.length > 1)
            builder.append(strArray[1], 0, 1);
        Log.d("New Text" , builder.toString());
        return  builder.toString();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ResultActivity.this,TestSelectionActivity.class));
        super.onBackPressed();
    }
    public String removeLastChar(String s) {
        if (s == null || s.length() == 0) {
            return s;
        }
        return s.substring(0, s.length()-1);
    }
}