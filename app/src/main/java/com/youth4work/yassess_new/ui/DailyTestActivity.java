package com.youth4work.yassess_new.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.extensions.HdrImageCaptureExtender;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.util.concurrent.ListenableFuture;
import com.vlonjatg.progressactivity.ProgressRelativeLayout;
import com.youth4work.yassess_new.R;
import com.youth4work.yassess_new.adapter.QuizAdapter;
import com.youth4work.yassess_new.network.model.Question;
import com.youth4work.yassess_new.network.model.request.CaptureImageRequest;
import com.youth4work.yassess_new.network.model.response.TestDetailsModel;
import com.youth4work.yassess_new.network.model.response.YassesgetTestModel;
import com.youth4work.yassess_new.ui.base.BaseActivity;
import com.youth4work.yassess_new.utils.DividerItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.youth4work.yassess_new.utils.PrepDialogsUtils2Kt.showQuitTestDialog;


/*import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.Tracker;*/

public class DailyTestActivity extends BaseActivity  {

    private TimeRemainingTimer mTimeRemainingTimer;
    TextView txtTitle;
    RecyclerView dailyTestRecyclerView;
    Question mQuestion;
    private int mTimeTaken = 0;
    private QuizAdapter mQuizAdapter;
    private int mWinOrLose = 0;
    ProgressRelativeLayout progressRelativeLayout;
    @NonNull
    private QuestionState state = QuestionState.INITIAL;
    //static int noOfquestion;
    static TestDetailsModel mTestModel;
    int mQuestionAttempted = 1;
    int mSectionCount=0;
    List<Question.Option> newoptions = new ArrayList<>();
    int  count=0;
    int rating=1500;
    int result=0;
    private Executor executor = Executors.newSingleThreadExecutor();
    private int REQUEST_CODE_PERMISSIONS = 1001;
   private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    Boolean browserFlag=false;
    PreviewView mPreviewView;
    ImageView captureImage;
    ImageCapture imageCapture;
    View cameraPreviewLayout;
    Button btnContinue;
    TextView txtTitleTest;
    static List<YassesgetTestModel> mYassesgetTest;
    int currentSection=0;
    FrameLayout layoutTimeUp;
    int takephoto = 0;
    //public yAssessServices yAssessService;
    public static void show(@NonNull Context fromActivity, TestDetailsModel testModel, List<YassesgetTestModel> yassesgetTest) {
        mTestModel = testModel;
        mYassesgetTest=yassesgetTest;
        Intent intent = new Intent(fromActivity, DailyTestActivity.class);
        fromActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_test);
        dailyTestRecyclerView = findViewById(R.id.dailyTestRecyclerView);
        progressRelativeLayout = findViewById(R.id.progressActivity);
        mPreviewView = findViewById(R.id.previewView);
        captureImage = findViewById(R.id.captureImg);
        cameraPreviewLayout = findViewById(R.id.camera_view);
       btnContinue= findViewById(R.id.btn_continue);
        txtTitleTest= findViewById(R.id.txt_title);
      layoutTimeUp= findViewById(R.id.layout_time_up);
       // yAssessService= yAssessApi.createService(yAssessServices.class,mPreferencesManager.getToken());
       // if(allPermissionsGranted()){
        if(mTestModel.getProctoring()==1){
            if(allPermissionsGranted()) {
                cameraPreviewLayout.setVisibility(View.VISIBLE);
                startCamera(); //start camera if permission has been granted by user
            }
            else {
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
            }
            }
            setupToolbar();
            initQuizUI();
            getQuestion();
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getQuestion();
            }
        });

    }

    private void startCamera() {

        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {

                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindPreview(cameraProvider);

                } catch (ExecutionException | InterruptedException e) {
                    // No errors need to be handled for this Future.
                    // This should never be reached.
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {

        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .build();

        ImageCapture.Builder builder = new ImageCapture.Builder();

        //Vendor-Extensions (The CameraX extensions dependency in build.gradle)
        HdrImageCaptureExtender hdrImageCaptureExtender = HdrImageCaptureExtender.create(builder);

        // Query if extension is available (optional).
        if (hdrImageCaptureExtender.isExtensionAvailable(cameraSelector)) {
            // Enable the extension if available.
            hdrImageCaptureExtender.enableExtension(cameraSelector);
        }

        imageCapture = builder
                .setTargetRotation(this.getWindowManager().getDefaultDisplay().getRotation())
                .build();

        preview.setSurfaceProvider(mPreviewView.createSurfaceProvider());

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageCapture);

    }

        /* captureImage.setOnClickListener(v -> {

            takePhoto(imageCapture);
        });*/
       /* if (mTestModel.getProctoring() == 1) {
            int takephoto = 0;
            if (takephoto < 10){
                long phototimer = (((mTestModel.getDuration() * 60) / mTestModel.getQuestions()) * 1000);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    // Actions to do after 10 seconds
                    new TakePhoto().execute(imageCapture);

                }
            }, phototimer);
                takephoto++;
        }
        }
*/



    public String getBatchDirectoryName() {

        String app_folder_path = "";
        String destPath = getExternalFilesDir(null).getAbsolutePath();
        app_folder_path =destPath + "/yAssess/Images";
        File dir = new File(app_folder_path);
        if (!dir.exists() && !dir.mkdirs()) {

        }

        return app_folder_path;
    }

    private boolean allPermissionsGranted(){

        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_PERMISSIONS){
            if(allPermissionsGranted()){
                startCamera();
                cameraPreviewLayout.setVisibility(View.VISIBLE);
            } else{
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                showMessageOKCancel("You need to allow access permissions to attempt test", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            ActivityCompat.requestPermissions(DailyTestActivity.this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
                        }
                    }
                }, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        Toast.makeText(DailyTestActivity.this, "Need to all permission to attempt test", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(DailyTestActivity.this,TestSelectionActivity.class));
                    }
                });
                //    this.finish();
            }
                this.finish();
            }
        }

    private void setupToolbar() {

        Toolbar toolbar = findViewById(R.id.daily_test_toolbar);
        if (toolbar != null) {
            txtTitle = findViewById(R.id.txt_action_bar_title);
            //txtTitle.setText(mTestModel.getTestName());
            setSupportActionBar(toolbar);
           /* getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
      */  }
    }
    @Override
    public void setTitle(CharSequence title) {
        txtTitle.setText(title);
    }
    private void initQuizUI() {
        dailyTestRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dailyTestRecyclerView.setHasFixedSize(true);
        dailyTestRecyclerView.addItemDecoration(new DividerItemDecoration(DailyTestActivity.this, false));
}


    private void getQuestion() {
            progressRelativeLayout.showLoading();
            if(mYassesgetTest.size()>currentSection) {
                if (mYassesgetTest.get(currentSection).getQuestionsSlot() > mSectionCount) {
                    yAssessServices.GetyAsessQuestion(mUserManager.getUser().getUserId(), mYassesgetTest.get(currentSection).getSectionId(), count, result, rating).enqueue(new Callback<Question>() {
                        @Override
                        public void onResponse(Call<Question> call, Response<Question> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                mQuestion = response.body();
                                if (mQuestion.getQuestion() != null && mTestModel.getQuestions() >= mQuestionAttempted) {
                                    btnContinue.setVisibility(View.GONE);
                                    newoptions = getNotEmptyOptionOnly(mQuestion.getOptions());
                                    mQuestion.setOptions(newoptions);
                                    setupQuestionAndAnswers(mQuestion);
                                    mQuestionAttempted++;
                                    mSectionCount++;
                                } else {
                                    /*startActivity(new Intent(DailyTestActivity.this,TestSelectionActivity.class));*/
                                    // AllWebView.LoadWebView(DailyTestActivity.this,"https://www.yassess.youth4work.com/yAssess/yScore/"+mTestModel.getTestid(),"Test Submit");
                                    SubmitAdjectiveTest(mTestModel.getTestId(), mUserManager.getUser().getUserId());

                                }
                            }

                        }


                        @Override
                        public void onFailure(Call<Question> call, Throwable t) {

                        }
                    });
                } else {
                    mSectionCount = 0;
                    currentSection++;
                    getQuestion();
                }
            }else {
                SubmitAdjectiveTest(mTestModel.getTestId(), mUserManager.getUser().getUserId());
            }
        }

    private void SubmitAdjectiveTest(Integer testid, Long userId) {
            yAssessServices.yadSubmit(testid,userId).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (response.isSuccessful()) {
                        getExtraTest(testid,userId,browserFlag,mTestModel);
                    }
                }
                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {

                }
            });
    }
            /*yAssessServices.SubmitTest(mUserManager.getUser().getUserId(), mQuestion.getTestID()).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (response.isSuccessful()) {
                        if (response.body()) {
                            Toast.makeText(DailyTestActivity.this, "Test is submit successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(DailyTestActivity.this, TestSelectionActivity.class));
                        }
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    Toast.makeText(DailyTestActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(DailyTestActivity.this, TestSelectionActivity.class));

                }
            });*/



    private void setupQuestionAndAnswers(Question question) {
        layoutTimeUp.setVisibility(View.GONE);
        try {
            setTitle(mQuestionAttempted+"/"+mTestModel.getQuestions()+" "+ mTestModel.getTestName());
            txtTitleTest.setText(mTestModel.getTestName()+" > "+mYassesgetTest.get(currentSection).getTestName());
            mQuestion = question;
            mQuestion.setOptions(mQuestion.getOptions());
            state = QuestionState.INITIAL;
            initOptions();
            initTimer();
            initButtonUI();
        } catch (Exception e) {
        }
    }

    private void initButtonUI() {
        switch (state) {
            case TIME_UP:
                layoutTimeUp.setVisibility(View.VISIBLE);
               btnContinue.setVisibility(View.VISIBLE);
                break;

            case SELECTED:
                if(mTestModel.getVisibleAnswer()){
                    btnContinue.setVisibility(View.VISIBLE);
                }
        }
    }
    enum QuestionState {
        INITIAL,
        SELECTED,
        TIME_UP
    }

    private void initOptions() {

        mQuizAdapter = new QuizAdapter(mQuestion, false, false, self);
        assert dailyTestRecyclerView != null;
        //dailyTestRecyclerView.getItemAnimator().setChangeDuration(0);
        dailyTestRecyclerView.setAdapter(mQuizAdapter);
        mQuizAdapter.notifyDataSetChanged();
        progressRelativeLayout.showContent();
        mQuizAdapter.setOnItemClickListener(new QuizAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {

                if (state != QuestionState.TIME_UP) {
                    mQuestion.setOptionSelected(position-1);
                    pushAnswer();
                    state = QuestionState.SELECTED;
                    initButtonUI();
                    mQuizAdapter.updateList(mQuestion, true, mTestModel.getVisibleAnswer());
                }
                else if(state!=QuestionState.SELECTED && state==QuestionState.TIME_UP){
                    layoutTimeUp.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    private List<Question.Option> getNotEmptyOptionOnly(List<Question.Option> options) {
        List<Question.Option> newoptions = new ArrayList<>();
        for (int i = 0; i < options.size(); i++) {
            if (!options.get(i).getOption().equals("")||!options.get(i).getOptionImgUrl().equals("")) {
                newoptions.add(options.get(i));
            }
        }
        return newoptions;
    }

    private void pushAnswer() {

        //takePhoto(imageCapture);
        yAssessServices.IsCorect(mUserManager.getUser().getUserId(), mQuestion.getCorrect(), mQuestion.getSelectedAnswerId(), String.valueOf(mTimeTaken), "y4w66", String.valueOf(mQuestion.getId()), String.valueOf(mQuestion.getTime2solve())).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful() && response.body() != null) {

            try {
                        JSONObject jsonObject;
                        String responseStr = response.body() != null ? response.body().string() :"{ resp = 0, rating = 0 }";

                        try {
                            jsonObject = new JSONObject(responseStr.substring(responseStr.indexOf("{"), responseStr.lastIndexOf("}") + 1));
                            result = Integer.parseInt(jsonObject.getString("resp"));
                            rating = Integer.parseInt(jsonObject.getString("rating"));
                            mTimeRemainingTimer.cancel();
                            if(result>2){
                                count=2;
                            }
                            else {
                                count = 0;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                   if(!mTestModel.getVisibleAnswer()) {
                       Handler handler = new Handler();
                       handler.postDelayed(new Runnable() {
                           public void run() {
                               // Actions to do after 10 seconds
                               getQuestion();
                           }
                       }, 2000);
                   }
                   if(mTestModel.getProctoring()==1){

                    if (takephoto < 10){
                        new TakePhoto().execute(imageCapture);
                        takephoto++;
                    }
                   }
            }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void initTimer() {
        mTimeTaken = 0;
        mTimeRemainingTimer = new TimeRemainingTimer((mQuestion.getTime2solve()) * 1000, 1000);
        mTimeRemainingTimer.start();
    }

    public class TimeRemainingTimer extends CountDownTimer {

        public TimeRemainingTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int progress = Math.round((float) millisUntilFinished / 1000.0f);
           /* if (progress == 0) {
                //playMusic(R.raw.tick_tock);
                getQuestion();
            }*/
            mTimeTaken++;
            ((TextView) findViewById(R.id.timerView)).setText(Integer.toString(progress));

        }

        @Override
        public void onFinish() {
            mTimeTaken = mQuestion.getTime2solve();
            ((TextView) findViewById(R.id.timerView)).setText("0");
            mQuizAdapter.updateList(mQuestion, false, mTestModel.getVisibleAnswer());
            state = QuestionState.TIME_UP;
            initButtonUI();
          /*  Toast.makeText(DailyTestActivity.this, "Time up", Toast.LENGTH_SHORT).show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    // Actions to do after 0 seconds
                    getQuestion();
                }
            }, 2000);*/

  }

    }
    @Override
    public boolean onSupportNavigateUp() {
        showQuitTestDialog(DailyTestActivity.this);

        return false;
    }

    @Override
    public void onBackPressed() {
        if (mTimeRemainingTimer != null) {
            mTimeRemainingTimer.cancel();
        }
        showQuitTestDialog(DailyTestActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimeRemainingTimer != null) {
            mTimeRemainingTimer.cancel();
        }
    }

    private class TakePhoto extends AsyncTask<ImageCapture,Integer,String>{

       @Override
        protected String doInBackground(ImageCapture... imageCaptures) {
           SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
           File file = new File(getBatchDirectoryName(), mDateFormat.format(new Date())+ ".jpg");

           ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
           imageCapture.takePicture(outputFileOptions, executor, new ImageCapture.OnImageSavedCallback () {
               @Override
               public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                   new Handler(Looper.getMainLooper()).post(new Runnable() {
                       @Override
                       public void run() {
                           // Toast.makeText(DailyTestActivity.this, "Image Saved successfully", Toast.LENGTH_SHORT).show();
                           String path=file.getAbsolutePath();
                           Log.e("path",path);
                           Bitmap bitmapOrg = BitmapFactory.decodeFile(path);
                           ByteArrayOutputStream bao = new ByteArrayOutputStream();
                           bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 100, bao);
                           byte [] ba = bao.toByteArray();
                           String ba1= Base64.encodeToString(ba,Base64.DEFAULT);
                           Log.e("ba1",ba1);
                           CaptureImageRequest captureImageRequest=new CaptureImageRequest((takephoto+1)+"_"+mQuestion.getId(),ba1,String.valueOf(mTestModel.getTestId()),mUserManager.getUser().getUserId(),".png");
                           yAssessServices.CaptureImage(captureImageRequest).enqueue(new Callback<Boolean>() {
                               @Override
                               public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                   if(response.isSuccessful()){
                                       if(response.body()){

                                       }
                                   }
                               }

                               @Override
                               public void onFailure(Call<Boolean> call, Throwable t) {

                               }
                           });

                       }
                   });
               }
               @Override
               public void onError(@NonNull ImageCaptureException error) {
                   error.printStackTrace();
               }
           });
       return null;
       }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

       }
    }

    private void takePhoto(ImageCapture imageCapture) {
        SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
        File file = new File(getBatchDirectoryName(), mDateFormat.format(new Date())+ ".jpg");

        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
        imageCapture.takePicture(outputFileOptions, executor, new ImageCapture.OnImageSavedCallback () {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        // Toast.makeText(DailyTestActivity.this, "Image Saved successfully", Toast.LENGTH_SHORT).show();
                        String path=file.getAbsolutePath();
                        Log.e("path",path);

                        Bitmap bitmapOrg = BitmapFactory.decodeFile(path);
                        ByteArrayOutputStream bao = new ByteArrayOutputStream();
                        bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 100, bao);
                        byte [] ba = bao.toByteArray();
                        String ba1= Base64.encodeToString(ba,Base64.DEFAULT);
                        Log.e("ba1",ba1);

                    }
                });
            }
            @Override
            public void onError(@NonNull ImageCaptureException error) {
                error.printStackTrace();
            }
        });
    }

    @Override
    protected void onStart() {

        super.onStart();
    }

    @Override
    protected void onResume() {
        if(!mTestModel.getCanminchangeWin()&&browserFlag){
            showAlertMessage();
            mTimeRemainingTimer.cancel();
        }
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {

        if(!mTestModel.getCanminchangeWin()) {
            browserFlag = true;
        }
        super.onStop();
    }

    private void showAlertMessage() {
        new AlertDialog.Builder(DailyTestActivity.this)
                .setTitle("Warning")
                .setMessage("Sorry, Now you cannot attempt the test any more, You tried to minimize/switch the screen/app.")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        //browserFlag=true;
                        SubmitAdjectiveTest(mTestModel.getTestId(),mUserManager.getUser().getUserId());
                        dialog.dismiss();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                //.setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                 .show();
    }

        private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener,DialogInterface.OnCancelListener onCancelListener) {
            new AlertDialog.Builder(DailyTestActivity.this)
                    .setMessage(message)
                    .setPositiveButton("OK", okListener)
                    //  .setNegativeButton("Cancel", (DialogInterface.OnClickListener) onCancelListener)
                    .create()
                    .show();
        }
}
