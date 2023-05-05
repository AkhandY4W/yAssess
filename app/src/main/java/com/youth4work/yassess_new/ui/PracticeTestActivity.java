package com.youth4work.yassess_new.ui;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.vlonjatg.progressactivity.ProgressRelativeLayout;
import com.youth4work.yassess_new.R;
import com.youth4work.yassess_new.adapter.PractiseTestAdapter;
import com.youth4work.yassess_new.network.model.response.EditprofileResponse;
import com.youth4work.yassess_new.network.model.response.SampleQuestionModel;
import com.youth4work.yassess_new.network.model.response.TestDetailsModel;
import com.youth4work.yassess_new.ui.base.BaseActivity;
import com.youth4work.yassess_new.utils.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.youth4work.yassess_new.utils.PrepDialogsUtils2Kt.showQuitTestDialog;

public class PracticeTestActivity extends BaseActivity {

    static int testId;
    TestDetailsModel mTestDetailsModel;
    Button btnStartPractice,btnBeginTest;
    CardView cardDummyPracticeTest;
    TextView txtTitle,txtQuestion;
    RecyclerView dailyTestRecyclerView;
    SampleQuestionModel mSampleQuestionModel;
    ProgressRelativeLayout progressRelativeLayout;
    ImageView imgQuestion;
    private TimeRemainingTimer mTimeRemainingTimer;
    private int mTimeTaken = 0;
    PractiseTestAdapter practiseTestAdapter;
    private QuestionState state = QuestionState.INITIAL;
    RelativeLayout quizLayout;
    FrameLayout frameToHide;
    TextView mTxtTestName;
    EditprofileResponse editprofileResponse;
    static boolean mIsFlag;
    public static void show(int mTestid, Context mContext,boolean isflag){
        testId=mTestid;
        mIsFlag=isflag;
        mContext.startActivity(new Intent(mContext,PracticeTestActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_test);
        btnStartPractice=findViewById(R.id.btn_start_practice);
        btnBeginTest=findViewById(R.id.btn_begin_test);
        cardDummyPracticeTest=findViewById(R.id.card_dummy_practice_test);
        progressRelativeLayout=findViewById(R.id.progressActivity);
        dailyTestRecyclerView=findViewById(R.id.option_layout);
        txtQuestion=findViewById(R.id.txt_question);
        imgQuestion=findViewById(R.id.qsImage);
        quizLayout=findViewById(R.id.quiz_layout);
        frameToHide=findViewById(R.id.frame_to_hide);
        mTxtTestName=findViewById(R.id.txt_test_name);
        if(mIsFlag) {
            getEditNeeded(testId);
        }
        else {
            getTestDetails(testId);
        }
        btnStartPractice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardDummyPracticeTest.setVisibility(View.GONE);
                quizLayout.setVisibility(View.VISIBLE);
                getPracticeQuestion(testId,mUserManager.getUser().getUserId());
            }
        });
        btnBeginTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InstructionsActivity.show(mTestDetailsModel,PracticeTestActivity.this);
            }
        });
    }



    private void getEditNeeded(int testId) {
     progressRelativeLayout.showLoading();
    yAssessServices.Editprofile(testId).enqueue(new Callback<EditprofileResponse>() {
        @Override
        public void onResponse(Call<EditprofileResponse> call, Response<EditprofileResponse> response) {
            if(response.isSuccessful()&&response.body()!=null){
                editprofileResponse=response.body();
                if(editprofileResponse.isGender()||editprofileResponse.isAadharNo()||editprofileResponse.isProfileHeading()){
                    //getYouthDetails(mUserManager.getUser().getUserId());
                    YouthExtraDetailActivity.show(editprofileResponse,testId,PracticeTestActivity.this);
                }
                else {
                    getTestDetails(testId);
                }
            }
        }

        @Override
        public void onFailure(Call<EditprofileResponse> call, Throwable t) {

        }
    });
    }

    /*private void getYouthDetails(Long userId) {
        progressRelativeLayout.showLoading();
        yAssessServices.GetYouthProfile(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()&&response.body()!=null){
                    User user=response.body();
                    progressRelativeLayout.showContent();
                    if(editprofileResponse.isAadharNo()&&user.getAadhar()==null){
                        YouthExtraDetailActivity.show(editprofileResponse,testId,user,PracticeTestActivity.this);
                    }
                    else if(editprofileResponse.isGender()&&user.getSex()==null){
                        YouthExtraDetailActivity.show(editprofileResponse,testId,user,PracticeTestActivity.this);
                    }
                    else if(editprofileResponse.isProfileHeading()&&user.getHeadLine()==null){
                        YouthExtraDetailActivity.show(editprofileResponse,testId,user,PracticeTestActivity.this);
                    }
                    else {
                        getTestDetails(testId);
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }*/

    private void getPracticeQuestion(int testId, Long userId) {
        progressRelativeLayout.showLoading();
        yAssessServices.SampleQuestion(testId,userId,1).enqueue(new Callback<SampleQuestionModel>() {
            @Override
            public void onResponse(Call<SampleQuestionModel> call, Response<SampleQuestionModel> response) {
                if(response.isSuccessful()&&response.body()!=null){
                    progressRelativeLayout.showContent();
                    mSampleQuestionModel=response.body();
                    setupToolbar();
                    initQuizUI();
                    initTimer();
                    mSampleQuestionModel.setSampleOptions(getNotEmptyOptionOnly(mSampleQuestionModel.getSampleOptions()));
                    txtQuestion.setText(mSampleQuestionModel.getQuestion());
                    if(!mSampleQuestionModel.getQuestionImageUrl().equals("")){
                        imgQuestion.setVisibility(View.VISIBLE);
                        Picasso.get().load(mSampleQuestionModel.getQuestionImageUrl()).into(imgQuestion);
                        }
                    practiseTestAdapter=new PractiseTestAdapter(mSampleQuestionModel,PracticeTestActivity.this);
                    dailyTestRecyclerView.setAdapter(practiseTestAdapter);
                    practiseTestAdapter.setOnItemClickListener(new PractiseTestAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View itemView, int position) {
                            String colorCode="";
                            btnBeginTest.setVisibility(View.VISIBLE);
                            mTimeRemainingTimer.cancel();
                            if(mSampleQuestionModel.getCorrect().equals(mSampleQuestionModel.getSampleOptions().get(position).getOption())){
                                colorCode="#64D739";
                            }
                            else {
                                colorCode="#f65454";
                            }
                            practiseTestAdapter.updateList(position,colorCode);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<SampleQuestionModel> call, Throwable t) {
                Toast.makeText(PracticeTestActivity.this, "Something went wrong, Please try later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initQuizUI( ) {
        dailyTestRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dailyTestRecyclerView.setHasFixedSize(true);
        dailyTestRecyclerView.addItemDecoration(new DividerItemDecoration(PracticeTestActivity.this, false));
    }


    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.daily_test_toolbar);
        if (toolbar != null) {
            txtTitle = findViewById(R.id.txt_action_bar_title);
            txtTitle.setText(mTestDetailsModel.getTestName());
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        }
          }


    private void getTestDetails(int testId) {
        progressRelativeLayout.showLoading();
    yAssessServices.Practice(testId).enqueue(new Callback<TestDetailsModel>() {
        @Override
        public void onResponse(Call<TestDetailsModel> call, Response<TestDetailsModel> response) {
            if(response.isSuccessful()&&response.body()!=null){
                progressRelativeLayout.showContent();
                mTestDetailsModel=response.body();
                mTxtTestName.setText(mTestDetailsModel.getTestName());
            }
            else {
                Toast.makeText(PracticeTestActivity.this,"Something went wrong,Please try again",Toast.LENGTH_SHORT).show();
                btnBeginTest.setEnabled(false);
                btnBeginTest.setBackgroundColor(getResources().getColor(R.color.colorPrimary30));
            }
        }

        @Override
        public void onFailure(Call<TestDetailsModel> call, Throwable t) {

        }
    });
    }
        enum QuestionState {
            INITIAL,
            SELECTED,
            TIME_UP
        }
    private void initTimer() {
        mTimeTaken = 0;
        mTimeRemainingTimer = new TimeRemainingTimer((mSampleQuestionModel.getTime2solve()) * 1000, 1000);
        mTimeRemainingTimer.start();
    }

    public class TimeRemainingTimer extends CountDownTimer {

        public TimeRemainingTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int progress = Math.round((float) millisUntilFinished / 1000.0f);
            /*if (progress == 5) {
                //playMusic(R.raw.tick_tock);
            }*/
            mTimeTaken++;
            ((TextView) findViewById(R.id.timerView)).setText(Integer.toString(progress));

        }

        @Override
        public void onFinish() {
            mTimeTaken = mSampleQuestionModel.getTime2solve();
            ((TextView) findViewById(R.id.timerView)).setText("0");
            frameToHide.setVisibility(View.VISIBLE);
            //Toast.makeText(PracticeTestActivity.this, "Time Up", Toast.LENGTH_SHORT).show();
            btnBeginTest.setVisibility(View.VISIBLE);
        }

    }
    @Override
    public void onBackPressed() {
        if (mTimeRemainingTimer != null) {
            mTimeRemainingTimer.cancel();
        }
        showQuitTestDialog(PracticeTestActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimeRemainingTimer != null) {
            mTimeRemainingTimer.cancel();
        }
    }
    private List<SampleQuestionModel.sampleOptions> getNotEmptyOptionOnly(List<SampleQuestionModel.sampleOptions> options) {
        List<SampleQuestionModel.sampleOptions> newoptions = new ArrayList<>();
        for (int i = 0; i < options.size(); i++) {
            if (!options.get(i).getOption().equals("")) {
                newoptions.add(options.get(i));
            }
        }
        return newoptions;
    }

}