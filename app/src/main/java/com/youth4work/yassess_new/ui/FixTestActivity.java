package com.youth4work.yassess_new.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
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
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.tabs.TabLayout;
import com.google.common.util.concurrent.ListenableFuture;
import com.innodroid.expandablerecycler.ExpandableRecyclerAdapter;
import com.vlonjatg.progressactivity.ProgressRelativeLayout;
import com.youth4work.yassess_new.R;
import com.youth4work.yassess_new.adapter.FixTestAdapter;
import com.youth4work.yassess_new.network.model.MockSection;
import com.youth4work.yassess_new.network.model.Question;
import com.youth4work.yassess_new.network.model.request.CaptureImageRequest;
import com.youth4work.yassess_new.network.model.response.GetAllFDTestModel;
import com.youth4work.yassess_new.network.model.response.TestDetailsModel;
import com.youth4work.yassess_new.ui.base.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.youth4work.yassess_new.utils.PrepDialogsUtils2Kt.showQuitMockTestDialog;

public class FixTestActivity extends BaseActivity implements View.OnClickListener {
    FrameLayout simpleFrameLayout;
    static TabLayout tabLayout;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    ImageButton imageButton;
    GridLayout gridLayoutRight;
    Context context = this;
    private static int questionno;
    TextView txtPrevious, txtNext, txtSkip;
    TextView titleTest, txtTotalTime;
    ProgressBar progressBarTimer;
    static ArrayList<GetAllFDTestModel.MockQuestion> items;
    static GetAllFDTestModel allMockQS;
    GetAllFDTestModel.MockTest mockTest;
    static GetAllFDTestModel.MockQuestion mockQuestion;
    Boolean browserFlag=false;
    // private MockQuizAdapter mQuizAdapter;
    List<FixTestAdapter.QuestionListItem> itemList = new ArrayList<>();
    RecyclerView mRecyclerViewList;
    static FixTestAdapter adapter;
    Float pStatus = 0.0F;
    private Handler handler = new Handler();
    Fragment fragment = new FixQuizFragment();
    ProgressRelativeLayout progressActivity;
    Button btnSubmitMockTest;
    int timer;
    static boolean changeData = true;
    static TestDetailsModel mTestDetails;
    //ArrayList<String> sectionName = new ArrayList<>();
    static ArrayList<MockSection> mockSections = new ArrayList<>();
    String key="0";

    private Executor executor = Executors.newSingleThreadExecutor();
    private int REQUEST_CODE_PERMISSIONS = 1001;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    PreviewView mPreviewView;
    ImageView captureImage;
    ImageCapture imageCapture;
    View cameraPreviewLayout;
    int takephoto=0;
    public static void show(@NonNull Context fromActivity, TestDetailsModel testDetails, int questionNo) {
        mTestDetails = testDetails;
        questionno = questionNo;
        Intent intent = new Intent(fromActivity, FixTestActivity.class);
        fromActivity.startActivity(intent);
    }

    public static void setQuestionNo(int questionNo) {
        questionno = questionNo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock_test2);
        simpleFrameLayout = findViewById(R.id.simpleFrameLayout);
        tabLayout = findViewById(R.id.simpleTabLayout);
        drawerLayout = findViewById(R.id.drawer_layout);
        imageButton = findViewById(R.id.btn_right_side_navigation);
        gridLayoutRight = findViewById(R.id.drawer_right);
        mRecyclerViewList = findViewById(R.id.recycler_view_list);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        titleTest = findViewById(R.id.title);
        txtTotalTime = findViewById(R.id.txt_total_time);
        progressBarTimer = findViewById(R.id.progress_bar_timer);
        progressActivity = findViewById(R.id.progressActivity);
        progressActivity.showLoading();
        btnSubmitMockTest = findViewById(R.id.btn_submit_test);
        txtSkip = findViewById(R.id.btn_skip);
        txtNext = findViewById(R.id.btn_next);
        txtPrevious = findViewById(R.id.btn_previous);
        mPreviewView = findViewById(R.id.previewView);
        captureImage = findViewById(R.id.captureImg);
        cameraPreviewLayout = findViewById(R.id.camera_view);
        txtSkip.setOnClickListener(this);
        txtPrevious.setOnClickListener(this);
        txtNext.setOnClickListener(this);
        btnSubmitMockTest.setOnClickListener(this);
        Resources res = getResources();
        Drawable drawable = res.getDrawable(R.drawable.circular);
        // Maximum Progress
        progressBarTimer.setProgressDrawable(drawable);
        if(mTestDetails.getProctoring()==1){
            if(allPermissionsGranted()) {
                cameraPreviewLayout.setVisibility(View.VISIBLE);
                startCamera(); //start camera if permission has been granted by user

            }
            else {
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
            }
        }
        CanAttemptMockTest();
        /*
        if(mTestModel.getProctoring()==1){
            if(allPermissionsGranted()) {
                cameraPreviewLayout.setVisibility(View.VISIBLE);
                startCamera(); //start camera if permission has been granted by user
            }
            else {
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
            }
        }*/
        imageButton.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(gridLayoutRight)) {
                drawerLayout.closeDrawer(gridLayoutRight);
                onResume();
            } else if (!drawerLayout.isDrawerOpen(gridLayoutRight)) {
                drawerLayout.openDrawer(gridLayoutRight);
                drawerLayout.bringChildToFront(gridLayoutRight);
                onPause();
            }

        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
// get the current selected tab's position and replace the fragment accordingly
                if (changeData) {
                    Fragment fragment;
                    int pos = getFirstQuestionPos(mockSections.get(tab.getPosition()).getSectionName());
                    fragment = new FixQuizFragment();
                    FixQuizFragment.newInstance(pos, items);
                    questionno = pos;
                    loadFragment(fragment);
                } else {
                    changeData = true;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
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
                            ActivityCompat.requestPermissions(FixTestActivity.this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
                        }
                    }
                }, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        Toast.makeText(FixTestActivity.this, "Need to all permission to attempt test", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(FixTestActivity.this,TestSelectionActivity.class));
                    }
                });
                //    this.finish();
            }
            this.finish();
        }
    }

    public String getBatchDirectoryName() {

        String app_folder_path = "";
        String destPath = getExternalFilesDir(null).getAbsolutePath();
        app_folder_path =destPath + "/yAssess/Images";
        File dir = new File(app_folder_path);
        if (!dir.exists() && !dir.mkdirs()) {

        }

        return app_folder_path;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener,DialogInterface.OnCancelListener onCancelListener) {
        new AlertDialog.Builder(FixTestActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                //  .setNegativeButton("Cancel", (DialogInterface.OnClickListener) onCancelListener)
                .create()
                .show();
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

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview,  imageCapture);
    }
    private class TakePhoto extends AsyncTask<ImageCapture,Integer,String> {

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
                            CaptureImageRequest captureImageRequest=new CaptureImageRequest((takephoto+1)+"_"+mockQuestion.getQuestionid(),ba1,String.valueOf(mTestDetails.getTestId()),mUserManager.getUser().getUserId(),".jpg");
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


    private Integer getFirstQuestionPos(String sectionname) {
        int res = -1;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getSectionName().equals(sectionname)) {
                res = items.get(i).getsNo();
                break;
            }
        }
        return res;
    }

    private void CanAttemptMockTest() {

            yAssessServices.yassesFDTest(mTestDetails.getTestId(),mUserManager.getUser().getUserId()).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()&&response.body()!=null){
                        JSONObject jsonObject;

                        try {
                            String responseStr = response.body() != null ? response.body().string() : "0";
                            try {
                                jsonObject = new JSONObject(responseStr);
                                key = jsonObject.getString("Key");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(key.equals("1")){

                        StartTest();
                    }
                    else {
                        Toast.makeText(context, "Sorry,you have not been allowed to attempt this test !", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(context,TestSelectionActivity.class));

                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });

    }





    private void StartTest() {
        yAssessServices.StartTestFd(mTestDetails.getTestId(), mUserManager.getUser().getUserId()).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    timer = response.body();
                    if (timer >= 1) {
                        getMockTestData();
                        int timerMint = timer / 60;
                        startTimer(timerMint);
                        progressBarTimer.setProgress(0);   // Main Progress
                        progressBarTimer.setSecondaryProgress(timer); // Secondary Progress
                        progressBarTimer.setMax(timer);
                        new Thread(() -> {

                            // TODO Auto-generated method stub
                            while (pStatus < timer) {
                                pStatus += 1;
                                handler.post(() -> {
                                    // TODO Auto-generated method stub
                                    progressBarTimer.setProgress(Math.round(pStatus));
                                    //tv.setText(pStatus + "%");

                                });
                                try {
                                    // Sleep for 200 milliseconds.
                                    // Just to display the progress slowly
                                    Thread.sleep(1000); //thread will take approx 1 second to finish
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    } else {
                        Toast.makeText(context, "You already attempted this test", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(context,TestSelectionActivity.class));


                    }
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });
    }

    private List<GetAllFDTestModel.MockQuestion> getMockTestData() {
        yAssessServices.GetAllFDTest(mTestDetails.getTestId(), mUserManager.getUser().getUserId()).enqueue(new Callback<GetAllFDTestModel>() {
            @Override
            public void onResponse(Call<GetAllFDTestModel> call, Response<GetAllFDTestModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allMockQS = response.body();
                    items = getQuestionOptionNotNull(allMockQS.getMockQuestions());
                    mockTest = allMockQS.getMockTest();
                    progressActivity.showContent();
                    if (allMockQS != null && mockTest != null && items != null) {
                        mockQuestion = items.get(questionno - 1);
                        //sharedprefernceMockQuestionList = mPreferencesManager.getMockQuestions(FixTestActivity.this);
                        titleTest.setText(mockTest.getTestName());
                        for (int i = 0; i < items.size(); i++) {
                            String sectionNameSingle = items.get(i).getSectionName();
                            int sectionSNo = items.get(i).getSectionId();
                            int serialNo = items.get(i).getsNo();
                            mockSections.add(new MockSection(sectionNameSingle, sectionSNo, serialNo));
                        }
                        //Old Way
                        /*HashSet<MockSection> hashSet = new HashSet<MockSection>();
                        hashSet.addAll(mockSections);*/

                        //New Way
                        HashSet<MockSection> hashSet = new HashSet<>(mockSections);
                        mockSections.clear();
                        mockSections.addAll(hashSet);
                        Collections.sort(mockSections);
                        for (int i = 0; i < mockSections.size(); i++) {
                            TabLayout.Tab firstTab = tabLayout.newTab();
                            firstTab.setText(mockSections.get(i).getSectionName());
                            tabLayout.addTab(firstTab);
                        }
                        for (int j = 0; j < mockSections.size(); j++) {
                            itemList.add(new FixTestAdapter.QuestionListItem(mockSections.get(j).getSectionName(), mTestDetails.getTestId()));
                            for (int i = 0; i < items.size(); i++) {
                                if (items.get(i).getSectionId() == mockSections.get(j).getSectionId()) {
                                    itemList.add(new FixTestAdapter.QuestionListItem(items.get(i).getsNo(), items.get(i).getQuestion()));
                                }
                            }
                        }
                        FixQuizFragment.newInstance(1, items);
                        loadFragment(fragment);
                        if (itemList != null) {
                            adapter = new FixTestAdapter(context, itemList, items);
                            adapter.setMode(ExpandableRecyclerAdapter.MODE_ACCORDION);
                            mRecyclerViewList.setLayoutManager(new LinearLayoutManager(context));
                            mRecyclerViewList.setAdapter(adapter);
                            adapter.setOnItemClickListener((itemView, position) ->
                            {
                                drawerLayout.closeDrawer(gridLayoutRight);
                                fragment = new FixQuizFragment();
                                FixQuizFragment.newInstance(position, items);
                                questionno = position;
                                //mockQuestion.setsNo(position);
                                loadFragment(fragment);
                                adapter.notifyDataSetChanged();
                                getCurrentSelectedTab(items.get(position - 1).getSectionName(), false);
                                if (mTestDetails.getProctoring() == 1) {
                                    if (takephoto < 10 ) {
                                        new FixTestActivity.TakePhoto().execute(imageCapture);
                                        takephoto++;
                                    }
                                }
                            });
                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<GetAllFDTestModel> call, Throwable t) {
                Toast.makeText(FixTestActivity.this, t + "error", Toast.LENGTH_SHORT).show();

            }
        });
        return items;
    }

    private ArrayList<GetAllFDTestModel.MockQuestion> getQuestionOptionNotNull(ArrayList<GetAllFDTestModel.MockQuestion> mockQuestions) {
        ArrayList<GetAllFDTestModel.MockQuestion>fixtestQuestion=new ArrayList<>();
        for(int j=0;j<mockQuestions.size();j++) {
            List<Question.Option> newoptions = new ArrayList<>();
            for (int i = 0; i <mockQuestions.get(j).getOptions().size(); i++) {
                if (!mockQuestions.get(j).getOptions().get(i).getOption().equals("")||!mockQuestions.get(j).getOptions().get(i).getOptionImgUrl().equals("")) {
                    newoptions.add(mockQuestions.get(j).getOptions().get(i));
                }
            }
            mockQuestions.get(j).setOptions(newoptions);
            fixtestQuestion.add(mockQuestions.get(j));

        }
        return fixtestQuestion;
    }

    public static boolean getCurrentSelectedTab(String sectionName, Boolean loadData) {
        int pos = tabLayout.getSelectedTabPosition();
        if (sectionName.equals(mockSections.get(pos).getSectionName())) {
            changeData=true;
        } else {
            for (int i = 0; i < mockSections.size(); i++) {
                if (mockSections.get(i).getSectionName().equals(sectionName)) {
                    pos=i;
                    changeData = loadData;
                    break;
                }
            }
        }
        Objects.requireNonNull(tabLayout.getTabAt(pos)).select();
        return changeData;
    }

    private void startTimer(final int minuti) {
        CountDownTimer countDownTimer = new CountDownTimer(60 * minuti * 1000, 500) {
            // 500 means, onTick function will be called at every 500 milliseconds

            @Override
            public void onTick(long leftTimeInMilliseconds) {
                long seconds = leftTimeInMilliseconds / 1000;
                txtTotalTime.setText(String.format(String.format("%02d", seconds / 3600) + ":" + String.format(String.format("%02d", (seconds % 3600) / 60)) + ":" + String.format("%02d", seconds % 60)));
                // format the textview to show the easily readable format
            }

            @Override
            public void onFinish() {
                if (txtTotalTime.getText().equals("00:00:00")) {
                    txtTotalTime.setText("Stop");
                    SubmitMockTest(mTestDetails.getTestId(), mUserManager.getUser().getUserId());
                } else {
                    txtTotalTime.setText("02:00");
                }
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = new FixQuizFragment();
        switch (v.getId()) {

            case R.id.btn_previous:
                if (questionno <= 1) {
                    //txtPrevious.setClickable(false);
                    FixQuizFragment.newInstance(1, items);
                    loadFragment(fragment);
                    getCurrentSelectedTab(items.get(1).getSectionName(), false);
                } else {
                    questionno = questionno - 1;
                    FixQuizFragment.newInstance(questionno, items);
                    loadFragment(fragment);
                    int no = questionno - 1;
                    getCurrentSelectedTab(items.get(no).getSectionName(), false);
                    }
                break;
            case R.id.btn_next:
                if (drawerLayout.isDrawerOpen(gridLayoutRight)) {
                    drawerLayout.closeDrawer(gridLayoutRight);
                } else if (!drawerLayout.isDrawerOpen(gridLayoutRight)) {
                    drawerLayout.openDrawer(gridLayoutRight);
                    drawerLayout.bringChildToFront(gridLayoutRight);
                }
                //adapter.notifyDataSetChanged();
                break;
            case R.id.btn_skip:
                if (questionno == items.size()) {
                    Toast.makeText(context, "No more question left to skip", Toast.LENGTH_SHORT).show();
                } else {
                    getCurrentSelectedTab(items.get(questionno).getSectionName(), false);
                    questionno = questionno + 1;
                    FixQuizFragment.newInstance(questionno, items);
                    loadFragment(fragment);
                }
                break;
            case R.id.btn_submit_test:
                SubmitMockTest(mTestDetails.getTestId(), mUserManager.getUser().getUserId());
                break;
        }
    }


    public void SubmitMockTest(int testId, Long userid) {
        yAssessServices.SubmitTest(userid,testId).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if(response.isSuccessful()){
                    if (response.body()){
      //                  ResultActivity.show(false,testId,self);
                            getExtraTest(testId,userid,browserFlag,mTestDetails);
                    }
                }

            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {

            }
        });
    }
    public void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.simpleFrameLayout, fragment);
        fragmentTransaction.commit();

    }
    @Override
    public void onBackPressed() {

        showQuitMockTestDialog(FixTestActivity.this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        if(!mTestDetails.getCanminchangeWin()&&browserFlag){
            showAlertMessage();
            //mTimeRemainingTimer.cancel();
        }  super.onResume();
    }

    @Override
    protected void onStop() {
        if(!mTestDetails.getCanminchangeWin()) {
            browserFlag = true;
        }super.onStop();
    }
    private void showAlertMessage() {
        new AlertDialog.Builder(FixTestActivity.this)
                .setTitle("Warning")
                .setMessage("Sorry, Now you cannot attempt the test any more, You tried to minimize/switch the screen/app.")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        //browserFlag=true;
                      // SubmitAdjectiveTest(mTestModel.getTestId(),mUserManager.getUser().getUserId());

                        dialog.dismiss();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                //.setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .show();
    }
}
