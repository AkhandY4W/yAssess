package com.youth4work.yassess_new.ui.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.youth4work.yassess_new.infrastructure.UserManager;
import com.youth4work.yassess_new.network.model.request.LoginRequest;
import com.youth4work.yassess_new.network.model.request.UserRegister;
import com.youth4work.yassess_new.network.model.response.LoginResponse;
import com.youth4work.yassess_new.network.model.response.TestDetailsModel;
import com.youth4work.yassess_new.network.yAssessApi;
import com.youth4work.yassess_new.network.yAssessServices;
import com.youth4work.yassess_new.ui.AllWebView;
import com.youth4work.yassess_new.ui.NoInternetActivity;
import com.youth4work.yassess_new.ui.ResultActivity;
import com.youth4work.yassess_new.ui.TestSelectionActivity;
import com.youth4work.yassess_new.utils.CheckNetwork;
import com.youth4work.yassess_new.utils.PreferencesManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;*/

public class BaseActivity extends RxAppCompatActivity {

    public static yAssessServices yAssessServices;
    protected static Context self;
    protected static UserRegister user;
    protected PreferencesManager mPreferencesManager;
    protected static UserManager mUserManager;
    protected String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        self = this;
        if(!CheckNetwork.isInternetAvailable(this)){
            {
                Intent intent=new Intent(BaseActivity.this, NoInternetActivity.class);
                startActivity(intent);
            }
        }

        mPreferencesManager = PreferencesManager.instance(this);
        mUserManager = UserManager.getInstance(this);
        //Constants.logEvent4FCM(self,self.getClass().getSimpleName(),self.getClass().getSimpleName(),new Date(),"Screen","SELECT_CONTENT");
        String token=mPreferencesManager.getToken();
        if((token == null)) {
            LoginRequest loginRequest = new LoginRequest("YOUTH4WORKAPP", "YOUTH4WORK@14FEB");
            yAssessServices = yAssessApi.createService(yAssessServices.class);
            yAssessServices.getAuth(loginRequest).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String authtoken = response.body().getToken();
                        mPreferencesManager.settoken(authtoken);
                        yAssessServices = yAssessApi.createService(yAssessServices.class, mPreferencesManager.getToken());

                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    //Toast.makeText(context, "Somethig went wrong,Please try again...", Toast.LENGTH_SHORT).show();
                }
            });

        }
        else {
            yAssessServices = yAssessApi.createService(yAssessServices.class, mPreferencesManager.getToken());
        }

    }
    public View.OnClickListener errorClickListener = v -> {
        finish();
        startActivity(getIntent());
    };
    public static void getExtraTest(int testid, long userId, boolean browserFlag, TestDetailsModel mTestModel){
        yAssessServices.yScore(testid,userId).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.isSuccessful()&&response.body()!=null){
                    // Toast.makeText(DailyTestActivity.this, "Test is submit successfully", Toast.LENGTH_SHORT).show();
                    switch (response.body())
                    {
                                        /*case 1:
                                            AllWebView.LoadWebView(DailyTestActivity.this,"https://www.yassess.youth4work.com/yAssess/TypingTest/"+mTestModel.getTestId(),"Typing Test");
                                            break;*/
                        case 2:
                            AllWebView.LoadWebView(self,"https://www.yassess.youth4work.com/yAssess/SubjectiveTest/"+mTestModel.getTestId(),"Subjective Test",mTestModel.getTestId());
                            break;
                        case 3:
                            AllWebView.LoadWebView(self,"https://www.yassess.youth4work.com/yAssess/"+mTestModel.getTestId()+"/PsychometricTest/Instruction/1","Psychometric Test",mTestModel.getTestId());
                            break;
                        case 4:
                            AllWebView.LoadWebView(self,"https://www.yassess.youth4work.com/yAssess/coding-test/"+mTestModel.getTestId(),"Coding Test",mTestModel.getTestId());
                            break;/*
                                        case 5:
                                            AllWebView.LoadWebView(DailyTestActivity.this,"https://www.yassess.youth4work.com/yAssess/html-css-javascript-online-editor/"+mTestModel.getTestid(),"Test");*/
                        default:
                            ResultActivity.show(browserFlag,mTestModel.getTestId(),self);

                    }

                }
                else {
                    Toast.makeText(self, "Something went wrong,Please try again", Toast.LENGTH_SHORT).show();
                    self.startActivity(new Intent(self, TestSelectionActivity.class));
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });
    }
    }

