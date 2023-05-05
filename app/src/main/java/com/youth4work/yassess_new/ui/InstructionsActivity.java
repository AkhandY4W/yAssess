package com.youth4work.yassess_new.ui;

import androidx.annotation.NonNull;
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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.util.Linkify;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;
import com.vlonjatg.progressactivity.ProgressRelativeLayout;
import com.youth4work.yassess_new.R;
import com.youth4work.yassess_new.network.model.request.CaptureImageRequest;
import com.youth4work.yassess_new.network.model.response.InstructionInfoModel;
import com.youth4work.yassess_new.network.model.response.TestDetailsModel;
import com.youth4work.yassess_new.network.model.response.YassesgetTestModel;
import com.youth4work.yassess_new.ui.base.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
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

public class InstructionsActivity extends BaseActivity {
    Button btnStartTest;
    TextView txtInst;
    static TestDetailsModel mTestDetailsModel;
    InstructionInfoModel mInstructionInfoModel;
    String key="0";

    ProgressRelativeLayout progressRelativeLayout;

    //For Image Proctoing
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private int REQUEST_CODE_PERMISSIONS = 1001;
    private Executor executor = Executors.newSingleThreadExecutor();
    PreviewView mPreviewView;
    ImageView captureImage;
    ImageCapture imageCapture;
    View cameraPreviewLayout;
    int questionCount=0;
    CheckBox checkBox;
    static List<YassesgetTestModel> mYassesgetTest;
    public static void show(TestDetailsModel testDetailsModel, Context mContext) {
        mTestDetailsModel = testDetailsModel;
        mContext.startActivity(new Intent(mContext, InstructionsActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
        btnStartTest = findViewById(R.id.btn_start_test);
        txtInst = findViewById(R.id.txt_inst);
        progressRelativeLayout = findViewById(R.id.progressActivity);
        mPreviewView = findViewById(R.id.previewView);
        captureImage = findViewById(R.id.captureImg);
        cameraPreviewLayout = findViewById(R.id.camera_view);
        checkBox = findViewById(R.id.inst_check_box);
        Instruction(mTestDetailsModel.getTestId());
        if(mTestDetailsModel.getAssessmentype().equals("AD"))
            getSection(mTestDetailsModel.getTestId(),mUserManager.getUser().getUserId());
        else {
            txtInst.setText("•  This is a self adaptive intelligent test.\n" +
                    "•  This test has "+mTestDetailsModel.getQuestions()+ " questions.\n" +
                    "•  It will take approximately "+mTestDetailsModel.getDuration()+" minutes to attempt all the questions.\n" +
                    "•  You will see 1 question at a time. All questions are compulsory. You cannot skip any question.\n" +
                    "•  You will get only one attempt at all questions. A question once attempted will not appear again.\n" +
                    "•  Please note that this examination does not constitute any offer of employment by any/all participating organization.\n" +
                    "•  Your admission to this examination is strictly provisional. Before giving an examination please ensure that you are eligible according to eligibility criteria.\n" +
                    "•  While the assessment is going on, Youth4Work monitors every activity:\n" +
                    "        ○ Your browsing movements\n" +
                    "        ○ Time taken to attempt each question\n" +
                    "        ○ Your IP address\n" +
                    "•  If at any time during the assessments, you move away from the browser tab, your assessment ends and your score will be calculated based on the questions attempted till that time.\n" +
                    "•  Hence, do not minimize the assessment tab or open new tab, or copy any question, etc.\n" +
                    "•  You will be visually monitored through your webcam. Random photos will be clicked during the course of assessment to check for any fraud.\n" +
                    "•  You will be solely responsible for any malpractice.\n" +
                    "•  For Web Interface – Recommended Browser\n" +
                    "        ○ Windows/Others –\n" +
                    "                 ⁃ Chrome\n" +
                    "                 ⁃ Mozilla Firefox\n" +
                    "                 ⁃ Microsoft Edge\n" +
                    "        ○ Ios -\n" +
                    "                ⁃ Safari\n" +
                    "•  For Mobile Interface –\n" +
                    "•  A latest chrome browser is compatible with all OS like Android, Windows and Ios.\n" +
                    "• In case of internet breakdown, you will not be able to proceed. Take a screenshot of the page you are on with the clock for time stamp and send on\n" +
                    "support@youth4work.com");
            Linkify.addLinks(txtInst, Linkify.ALL);
        }
       btnStartTest.setEnabled(false);
        progressRelativeLayout.showLoading();
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkBox.isChecked()){
                    btnStartTest.setBackground(getResources().getDrawable(R.drawable.state_pressed_ripple));
                    btnStartTest.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
                else {
                    btnStartTest.setBackground(getResources().getDrawable(R.drawable.state_pressed_ripple_grey));
                }
            }
        });
        btnStartTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkBox.isChecked()){
               if(mTestDetailsModel.getProctoring()==1)
                   takePhoto(imageCapture);
               else {
                   if(mTestDetailsModel.getAssessmentype().equals("AD")) {
                       DailyTestActivity.show(InstructionsActivity.this, mTestDetailsModel, mYassesgetTest);
                   }
                   else{
                       //Toast.makeText(InstructionsActivity.this, "Fix Test is coming soon please wait", Toast.LENGTH_LONG).show();
                       //startActivity(new Intent(InstructionsActivity.this, TestSelectionActivity.class));
                       FixTestActivity.show(InstructionsActivity.this,mTestDetailsModel,1);
                   }
               }
           }
                else {
                    Toast.makeText(InstructionsActivity.this, "Please accept term and condition", Toast.LENGTH_LONG).show();
                }
            }

        });
    }

    private void getSection(int testId, Long userId) {

        yAssessServices.yassesgetTest(testId,userId).enqueue(new Callback<List<YassesgetTestModel>>() {
            @Override
            public void onResponse(Call<List<YassesgetTestModel>> call, Response<List<YassesgetTestModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mYassesgetTest=response.body();
                    for(int i=0;i<mYassesgetTest.size();i++){
                        questionCount=questionCount+mYassesgetTest.get(i).getQuestionsSlot();
                    }
                    mTestDetailsModel.setQuestions(questionCount);
                    txtInst.setText("•  This is a self adaptive intelligent test.\n" +
                            "•  This test has "+mTestDetailsModel.getQuestions()+ " questions.\n" +
                            "•  It will take approximately "+mTestDetailsModel.getDuration()+" minutes to attempt all the questions.\n" +
                            "•  You will see 1 question at a time. All questions are compulsory. You cannot skip any question.\n" +
                            "•  You will get only one attempt at all questions. A question once attempted will not appear again.\n" +
                            "•  Please note that this examination does not constitute any offer of employment by any/all participating organization.\n" +
                            "•  Your admission to this examination is strictly provisional. Before giving an examination please ensure that you are eligible according to eligibility criteria.\n" +
                            "•  While the assessment is going on, Youth4Work monitors every activity:\n" +
                            "        ○ Your browsing movements\n" +
                            "        ○ Time taken to attempt each question\n" +
                            "        ○ Your IP address\n" +
                            "•  If at any time during the assessments, you move away from the browser tab, your assessment ends and your score will be calculated based on the questions attempted till that time.\n" +
                            "•  Hence, do not minimize the assessment tab or open new tab, or copy any question, etc.\n" +
                            "•  You will be visually monitored through your webcam. Random photos will be clicked during the course of assessment to check for any fraud.\n" +
                            "•  You will be solely responsible for any malpractice.\n" +
                            "•  For Web Interface – Recommended Browser\n" +
                            "        ○ Windows/Others –\n" +
                            "                 ⁃ Chrome\n" +
                            "                 ⁃ Mozilla Firefox\n" +
                            "                 ⁃ Microsoft Edge\n" +
                            "        ○ Ios -\n" +
                            "                ⁃ Safari\n" +
                            "•  For Mobile Interface –\n" +
                            "•  A latest chrome browser is compatible with all OS like Android, Windows and Ios.\n" +
                            "• In case of internet breakdown, you will not be able to proceed. Take a screenshot of the page you are on with the clock for time stamp and send on\n" +
                            "support@youth4work.com");
                    Linkify.addLinks(txtInst, Linkify.ALL);
                }
            }
            @Override
            public void onFailure(Call<List<YassesgetTestModel>> call, Throwable t) {

            }
        });
    }

    private void Instruction(int testId) {
        yAssessServices.Instruction(testId,mUserManager.getUser().getUserId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
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
                if(key.equals("1")){
                    yAssessServices.InstructionInfo(testId,mUserManager.getUser().getEmailID()).enqueue(new Callback<InstructionInfoModel>() {
                        @Override
                        public void onResponse(Call<InstructionInfoModel> call, Response<InstructionInfoModel> response) {
                            if(response.isSuccessful()&&response.body()!=null){
                                progressRelativeLayout.showContent();
                                mInstructionInfoModel=response.body();
                                if(!mInstructionInfoModel.getInstructions().equals("")){
                                    txtInst.setText(Html.fromHtml(mInstructionInfoModel.getInstructions()));
                                }
                                if(mTestDetailsModel.getProctoring()==1){
                                    if(allPermissionsGranted()) {
                                        cameraPreviewLayout.setVisibility(View.VISIBLE);
                                        startCamera(); //start camera if permission has been granted by user
                                        btnStartTest.setEnabled(true);
                                    }
                                    else{
                                        ActivityCompat.requestPermissions(InstructionsActivity.this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
                                    }
                                }
                                else {
                                    btnStartTest.setEnabled(true);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<InstructionInfoModel> call, Throwable t) {

                        }
                    });
                }
                else {
                    btnStartTest.setBackgroundColor(getResources().getColor(R.color.colorPrimary30));
                    Toast.makeText(InstructionsActivity.this, "You have already attempted this Test", Toast.LENGTH_SHORT).show();
                    progressRelativeLayout.showContent();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

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
                btnStartTest.setEnabled(true);
            } else{
                btnStartTest.setBackgroundColor(getResources().getColor(R.color.colorPrimary30));
                btnStartTest.setEnabled(false);
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                showMessageOKCancel("You need to allow access permissions to attempt test", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            ActivityCompat.requestPermissions(InstructionsActivity.this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
                        }
                    }
                }, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        Toast.makeText(InstructionsActivity.this, "Need to all permission to attempt test", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(InstructionsActivity.this,TestSelectionActivity.class));
                    }
                });
            //    this.finish();
            }
        }
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
        cameraProvider.unbindAll();
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
        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview,imageCapture);

      /*  captureImage.setOnClickListener(v -> {

            takePhoto(imageCapture);
        });*/
    }



    public String getBatchDirectoryName() {

        String app_folder_path = "";
        String destPath = getExternalFilesDir(null).getAbsolutePath();
        app_folder_path =destPath + "/yAssess/Images";
        File dir = new File(app_folder_path);
        if (!dir.exists() && !dir.mkdirs()) {

        }
        Log.e("path",app_folder_path);
        return app_folder_path;
    }
    private void takePhoto(ImageCapture imageCapture) {
        progressRelativeLayout.showLoading();
        SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
        File file = new File(getBatchDirectoryName(), mDateFormat.format(new Date())+ ".jpg");

        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
        imageCapture.takePicture(outputFileOptions, executor, new ImageCapture.OnImageSavedCallback () {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(InstructionsActivity.this, "Image Saved successfully", Toast.LENGTH_SHORT).show();
                        String path=file.getAbsolutePath();
                        Log.e("path",path);
                        Bitmap bitmapOrg = BitmapFactory.decodeFile(path);
                        ByteArrayOutputStream bao = new ByteArrayOutputStream();
                        bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 100, bao);
                        byte [] ba = bao.toByteArray();
                        String ba1= Base64.encodeToString(ba,Base64.DEFAULT);
                        Log.e("ba1",ba1);
                        CaptureImageRequest captureImageRequest=new CaptureImageRequest(String.valueOf(mTestDetailsModel.getTestId()),ba1,String.valueOf(mTestDetailsModel.getTestId()),mUserManager.getUser().getUserId(),".jpg");
                        yAssessServices.CaptureImage(captureImageRequest).enqueue(new Callback<Boolean>() {
                            @Override
                            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                if(response.isSuccessful()){
                                    if(response.body()){
                                        progressRelativeLayout.showContent();
                                        if(mTestDetailsModel.getAssessmentype().equals("AD"))
                                            DailyTestActivity.show(InstructionsActivity.this,mTestDetailsModel,mYassesgetTest);
                                        else {
                                            //Toast.makeText(InstructionsActivity.this, "Fix Test is coming soon please wait", Toast.LENGTH_LONG).show();
                                           // startActivity(new Intent(InstructionsActivity.this, TestSelectionActivity.class));
                                            FixTestActivity.show(InstructionsActivity.this,mTestDetailsModel,1);
                                       }

                                    }
                                    else {
                                        Toast.makeText(InstructionsActivity.this, "Something went wrong, please try later", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<Boolean> call, Throwable t) {
                                progressRelativeLayout.showContent();
                                Toast.makeText(InstructionsActivity.this,t.toString()+"",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }
            @Override
            public void onError(@NonNull ImageCaptureException error) {
                error.printStackTrace();
                Log.e("error",error.toString());
            }
        });
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener,DialogInterface.OnCancelListener onCancelListener) {
        new AlertDialog.Builder(InstructionsActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
              //  .setNegativeButton("Cancel", (DialogInterface.OnClickListener) onCancelListener)
                .create()
                .show();
    }
}