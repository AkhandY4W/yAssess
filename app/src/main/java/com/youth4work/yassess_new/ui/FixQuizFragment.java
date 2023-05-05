package com.youth4work.yassess_new.ui;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vlonjatg.progressactivity.ProgressRelativeLayout;
import com.youth4work.yassess_new.R;
import com.youth4work.yassess_new.adapter.FixQuizAdapter;
import com.youth4work.yassess_new.network.model.response.GetAllFDTestModel;
import com.youth4work.yassess_new.network.yAssessApi;
import com.youth4work.yassess_new.network.yAssessServices;
import com.youth4work.yassess_new.ui.base.BaseFragment;
import com.youth4work.yassess_new.utils.PreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FixQuizFragment extends BaseFragment {
    GetAllFDTestModel.MockQuestion mockQuestion;
    RecyclerView recyclerQuestionOption;
    private FixQuizAdapter mQuizAdapter;
    TextView txtQuestionCount, txtQuestionCountTotal;
    static int questionNo;
    LinearLayout markForReview;
    ImageView imageMarkForReview;
    TextView txtMarkForReview;
    yAssessServices yAssessService;
    private int mWinOrLose = 0;
    ProgressRelativeLayout progressActivity;
    static ArrayList<GetAllFDTestModel.MockQuestion> arrayList = new ArrayList<GetAllFDTestModel.MockQuestion>();
    private QuestionState state = QuestionState.INITIAL;
    private TimeRemainingTimer mTimeRemainingTimer;
    private int mTimeTaken = 0;

    public FixQuizFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static FixQuizFragment newInstance(int questionno, ArrayList<GetAllFDTestModel.MockQuestion> mocktestList) {
        questionNo = questionno;
        FixTestActivity.setQuestionNo(questionNo);
        arrayList = mocktestList;
        FixQuizFragment fragment = new FixQuizFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mock_quiz, container, false);
        yAssessService = yAssessApi.createService(yAssessServices.class, PreferencesManager.instance(self).getToken());
        progressActivity = view.findViewById(R.id.progressActivity);
        txtQuestionCount = view.findViewById(R.id.txt_question_count);
        txtQuestionCountTotal = view.findViewById(R.id.txt_question_total);
        markForReview = view.findViewById(R.id.mark_for_review);
        imageMarkForReview = view.findViewById(R.id.image_mark_for_review);
        txtMarkForReview = view.findViewById(R.id.txt_mark_for_review);
        recyclerQuestionOption = view.findViewById(R.id.recycler_question_option);
        recyclerQuestionOption.setLayoutManager(new LinearLayoutManager(self));
        recyclerQuestionOption.setHasFixedSize(true);
        //recyclerQuestionOption.addItemDecoration(new DividerItemDecoration(self, false));
        //arrayList = mPreferencesManager.getMockQuestions(self);
        if (questionNo != 0) {
            mockQuestion = arrayList.get(questionNo - 1);
            getQuestionBySNo(questionNo - 1);
        } else {
            mockQuestion = arrayList.get(0);
            getQuestionBySNo(0);
        }
        if (!mockQuestion.isIsflagged()) {
            txtMarkForReview.setTextColor(getResources().getColor(R.color.text_grey));
            imageMarkForReview.setColorFilter(getResources().getColor(R.color.text_grey), PorterDuff.Mode.SRC_IN);
            markForReview.setBackground(getResources().getDrawable(R.drawable.gray_border));
        } else {
            txtMarkForReview.setTextColor(getResources().getColor(R.color.colorPrimary));
            imageMarkForReview.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
            markForReview.setBackground(getResources().getDrawable(R.drawable.blue_border));
        }

        markForReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mockQuestion.isIsflagged()) {
                    Toast.makeText(self, "Question Unmark for review", Toast.LENGTH_SHORT).show();
                    txtMarkForReview.setTextColor(getResources().getColor(R.color.text_grey));
                    imageMarkForReview.setColorFilter(getResources().getColor(R.color.text_grey), PorterDuff.Mode.SRC_IN);
                    markForReview.setBackground(getResources().getDrawable(R.drawable.gray_border));
                    arrayList.get(mockQuestion.getsNo()-1).setIsflagged(false);

                } else {
                    Toast.makeText(self, "Question Mark for review", Toast.LENGTH_SHORT).show();
                    txtMarkForReview.setTextColor(getResources().getColor(R.color.colorPrimary));
                    imageMarkForReview.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
                    markForReview.setBackground(getResources().getDrawable(R.drawable.blue_border));
                    arrayList.get(mockQuestion.getsNo()-1).setIsflagged(true);
                    arrayList.get(mockQuestion.getsNo()-1).setIsattempted(false);
                }
                if(FixTestActivity.adapter!=null){
                    FixTestActivity.adapter.notifyDataSetChanged();}
            }
        });
        return view;
    }

    enum QuestionState {
        INITIAL,
        SELECTED,
        TIME_UP
    }

    private void getQuestionBySNo(int i) {
        // txtQuestion.setText(Html.fromHtml(items.get(i).getQuestion()));
        txtQuestionCount.setText(String.valueOf(i + 1));
        String text = " of <font color='#666666'>" + arrayList.size() + " </font> Questions";
        txtQuestionCountTotal.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
        mockQuestion.setOptions(mockQuestion.getOptions());
        initOptions();
        initTimer();
    }

    private void initOptions() {
        mQuizAdapter = new FixQuizAdapter(mockQuestion, false, false, self);
        assert recyclerQuestionOption != null;
        recyclerQuestionOption.setAdapter(mQuizAdapter);
        mQuizAdapter.setOnItemClickListener(new FixQuizAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {

                if (state != QuestionState.TIME_UP) {
                    mockQuestion.setOptionChoosen(Integer.parseInt(mockQuestion.getOptions().get(position - 1).getOptionID()));
                    arrayList.get(mockQuestion.getsNo() - 1).setOptionChoosen(mockQuestion.getOptionChoosen());
                    mockQuestion.setOptionSelected(position-1);
                    arrayList.get(mockQuestion.getsNo() - 1).setOptionSelected(position-1);
                    if (mockQuestion.isCorrectOrWrong()) {
                        mWinOrLose = 1;
                    } else {
                        mWinOrLose = 0;
                    }
                    if (mockQuestion.isIsattempted()) {
                        UpdateAnswer();
                    } else {
                        pushAnswer();
                    }
                    arrayList.get(mockQuestion.getsNo() - 1).setIsattempted(true);
                    state = QuestionState.SELECTED;
                    mQuizAdapter.updateList(mockQuestion, true);
                    txtMarkForReview.setTextColor(getResources().getColor(R.color.text_grey));
                    imageMarkForReview.setColorFilter(getResources().getColor(R.color.text_grey), PorterDuff.Mode.SRC_IN);
                    markForReview.setBackground(getResources().getDrawable(R.drawable.gray_border));
                    arrayList.get(mockQuestion.getsNo() - 1).setIsflagged(false);
                }
            }
        });
    }

    private void pushAnswer() {
        progressActivity.showLoading();
        if (mTimeTaken > mockQuestion.getTime2solve()) {
            mTimeTaken = mockQuestion.getTime2solve();
        }
      //  PushAnswerFDTestRequest answer = new PushAnswerFDTestRequest(mockQuestion.getSelectedAnswerId(), mockQuestion.getCorrect(), mockQuestion.getQuestionid(), mUserManager.getUser().getUserId(), mockQuestion.getTime2solve(), mTimeTaken);
        Call<ResponseBody> answerCall = yAssessService.PushAnswerFDTest(mUserManager.getUser().getUserId(), mockQuestion.getCorrect(),mockQuestion.getSelectedAnswerId(),String.valueOf(mTimeTaken),String.valueOf(mockQuestion.getQuestionid()),String.valueOf(mockQuestion.getTime2solve()));
        answerCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    progressActivity.showContent();
                    JSONObject jsonObject;
                    String responseStr = response.body() != null ? response.body().string() : "{ rating = 1500 }";
                    try {
                        jsonObject=new JSONObject(responseStr.substring(responseStr.indexOf("{"), responseStr.lastIndexOf("}") + 1));
                        int rating = Integer.parseInt(jsonObject.getString("rating"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (mockQuestion.getsNo() + 1 < arrayList.size()) {
                        FixTestActivity.getCurrentSelectedTab(arrayList.get(questionNo).getSectionName(),false);
                        Fragment fragment = new FixQuizFragment();
                        FixQuizFragment.newInstance(mockQuestion.getsNo() + 1, arrayList);
                        loadFragment(fragment);
                        if(FixTestActivity.adapter!=null){
                            FixTestActivity.adapter.notifyDataSetChanged();}
                    } else {
                        Toast.makeText(self, "No More Question Left", Toast.LENGTH_SHORT).show();
                    }
                    try {
                        jsonObject = new JSONObject(responseStr);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressActivity.showContent();
            }
        });
    }

    private void UpdateAnswer() {
        progressActivity.showLoading();
        if (mTimeTaken > mockQuestion.getTime2solve()) {
            mTimeTaken = mockQuestion.getTime2solve();
        }
       // PushAnswerFDTestRequest answer = new PushAnswerFDTestRequest(mockQuestion.getSelectedAnswerId(), mockQuestion.getCorrect(), arrayList.get(mockQuestion.getsNo() - 1).getQuestionid(), mUserManager.getUser().getUserId(), arrayList.get(mockQuestion.getsNo() - 1).getTime2solve(), mTimeTaken);
        Call<ResponseBody> answerCall = yAssessService.UpdateAnswerFDTest(mUserManager.getUser().getUserId(), mockQuestion.getCorrect(),mockQuestion.getSelectedAnswerId(),String.valueOf(mTimeTaken),String.valueOf(mockQuestion.getQuestionid()),String.valueOf(mockQuestion.getTime2solve()));
        answerCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                progressActivity.showContent();
                if (mockQuestion.getsNo() + 1 < arrayList.size()) {
                    FixTestActivity.getCurrentSelectedTab(arrayList.get(questionNo).getSectionName(),false);
                    Fragment fragment = new FixQuizFragment();
                    FixQuizFragment.newInstance(mockQuestion.getsNo() + 1, arrayList);
                    loadFragment(fragment);
                    if(FixTestActivity.adapter!=null){
                    FixTestActivity.adapter.notifyDataSetChanged();}
                } else {
                    Toast.makeText(self, "No More Question Left", Toast.LENGTH_SHORT).show();
                }
                try {

                    JSONObject jsonObject;
                    String responseStr = response.body() != null ? response.body().string() : "{ rating = 1500 }";
                    try {
                        jsonObject=new JSONObject(responseStr.substring(responseStr.indexOf("{"), responseStr.lastIndexOf("}") + 1));
                        int rating = Integer.parseInt(jsonObject.getString("rating"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressActivity.showContent();
            }
        });
    }

    public class TimeRemainingTimer extends CountDownTimer {

        public TimeRemainingTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            mTimeTaken++;

        }

        @Override
        public void onFinish() {

        }
    }

    private void initTimer() {
        mTimeTaken = 0;
        mTimeRemainingTimer = new TimeRemainingTimer((mockQuestion.getTime2solve()) * 1000, 1000);
        mTimeRemainingTimer.start();
    }

    public void loadFragment(Fragment fragment) {
        AppCompatActivity activity = (AppCompatActivity) self;
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.simpleFrameLayout, fragment);
        fragmentTransaction.commit();

    }
}
