package com.youth4work.yassess_new.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.vlonjatg.progressactivity.ProgressRelativeLayout;
import com.youth4work.yassess_new.R;
import com.youth4work.yassess_new.ui.base.BaseActivity;
import com.youth4work.yassess_new.utils.PreferencesManager;
import com.youth4work.yassess_new.utils.YAssessApplication;

import org.apache.http.util.EncodingUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AllWebView extends BaseActivity {
    WebView mWebView;
    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private static final int FILECHOOSER_RESULTCODE = 1;
    private static final String TAG = AllWebView.class.getSimpleName();
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    protected static String url4load, parentActivity;
    String userName = "", password = "";
    ProgressRelativeLayout progressActivity;
    Toolbar toolbar;
    TextView txtTitle;
    Button btnBack;
    boolean doubleBackToExitPressedOnce = false;
    // String[] idsToHide = { "y4wfooter", "row", "downloadAppContainer","footer-design","wrapper","alert-danger" };
    SwipeRefreshLayout mySwipeRefreshLayout;
    private static final int PERMISSION_REQUEST_CODE = 200;
    String currentUrl = "";
    //SharedPreferences settings;
    //SharedPreferences.Editor editor;
    //int warnigCount = 0;
    Button btnLogout;
    static int mTestId;
    public static void LoadWebView(Context mContext, String loadURL, String parentActivityName,int testId) {
        url4load = loadURL;
        parentActivity = parentActivityName;
        mTestId=testId;
        Intent cv = new Intent(mContext, AllWebView.class);
        mContext.startActivity(cv);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }
            Uri[] results = null;
            // Check that the response is a good one
            if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    // If there is not data, then we may have taken a photo
                    if (mCameraPhotoPath != null) {
                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                    }
                } else {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
            }
            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            if (requestCode != FILECHOOSER_RESULTCODE || mUploadMessage == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }
            if (requestCode == FILECHOOSER_RESULTCODE) {
                if (null == this.mUploadMessage) {
                    return;
                }
                Uri result = null;
                try {
                    if (resultCode != RESULT_OK) {
                        result = null;
                    } else {
                        // retrieve from the private variable if the intent is null
                        result = data == null ? mCapturedImageURI : data.getData();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "activity :" + e,
                            Toast.LENGTH_LONG).show();
                }
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }
        return;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_web_view);
        mWebView = findViewById(R.id.allwebview);
        progressActivity = findViewById(R.id.activity_term_and_condition);
        toolbar = findViewById(R.id.toolbar_top);
        txtTitle = findViewById(R.id.toolbar_title);
        btnBack = findViewById(R.id.btn_back);
        btnLogout = findViewById(R.id.btn_logout);
        setSupportActionBar(toolbar);
        mWebView.bringChildToFront(toolbar);
        switch (parentActivity) {
            case "SignUpActivity":
                txtTitle.setText("Terms And Conditions");
                break;
            case "yAssessments":
                txtTitle.setText("yAssessments");
                btnBack.setVisibility(View.GONE);
                break;
            case "RegisterationActivity":
                txtTitle.setText("Terms And Conditions");
                break;
            case "VerificationActivity":
                txtTitle.setText("Edit Profile");
                break;
            default:
                txtTitle.setText(parentActivity);
        }

     /*   if (checkPermission()) {
        */
     mainLogic();
     /*   } else {
            requestPermission();
        }
     */
     //settings = getSharedPreferences("IS_WARN", 0);
        //editor = settings.edit();
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }

    private void mainLogic() {
       /* mySwipeRefreshLayout = findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setColorScheme(android.R.color.holo_blue_dark, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_green_dark);
        mySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // This method performs the actual data-refresh operation.
                // The method calls setRefreshing(false) when it's finished.

                mySwipeRefreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mySwipeRefreshLayout.setRefreshing(false);
                        mWebView.loadUrl(mWebView.getUrl());
                    }
                }, 4000);
            }
        });*/
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.webkit.CookieManager.getInstance().removeAllCookie();
                YAssessApplication.getInstance().clearApplicationData();
                //AllWebView.LoadWebView(AllWebView.this, "https://www.youth4work.com/master/logout", "YAssessments",123);
                Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_LONG).show();
            }
        });
        btnBack.setOnClickListener(v -> {
            if (parentActivity.equals("Result")||parentActivity.equals("Test Submit")) {
                startActivity(new Intent(AllWebView.this, TestSelectionActivity.class));
            }
            finish();
        });
        progressActivity.showLoading();
        String[] arr = PreferencesManager.instance(AllWebView.this).loadPreferences();
        if (arr.length > 0 && arr != null) {
            userName = arr[0];
            password = arr[1];
        }
        mWebView.clearCache(true);
        mWebView.clearHistory();
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        mWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.setWebViewClient(new WebViewClient() {
                                      @Override
                                      public void onPageFinished(WebView view, String url) {
                                          // TODO Auto-generated method stub
                                          mWebView.loadUrl("javascript:(function() {document.getElementsByClassName('alert-danger')[0].style.display='none';})()");
                                          mWebView.loadUrl("javascript:(function() {document.getElementsByClassName('header-body skin-blue')[0].style.display='none';})()");
                                          mWebView.loadUrl("javascript:(function() {document.getElementsByClassName('header')[0].style.display='none';})()");
                                          mWebView.loadUrl("javascript:(function() {document.getElementsByClassName('master_header')[0].style.display='none';})()");
                                          mWebView.loadUrl("javascript:(function() {document.getElementsByClassName('alert-danger')[0].style.display='none';})()");
                                          mWebView.loadUrl("javascript:(function() {document.getElementsByClassName('y4wfooter')[0].style.display='none';})()");
                                          mWebView.loadUrl("javascript:(function() {document.getElementsByClassName('downloadAppContainer')[0].style.display='none';})()");
                                          mWebView.loadUrl("javascript:(function() {document.getElementsById('footer-design')[0].style.display='none';})()");
                                          mWebView.loadUrl("javascript:(function() {document.getElementsById('header-design')[0].style.display='none';})()");
                                          mWebView.loadUrl("javascript:(function() {document.getElementsById('IdActiveInfo')[0].style.display='none';})()");
                                          mWebView.loadUrl("javascript:(function() {document.getElementsById('slideLeftMenu')[0].style.display='none';})()");
                                          mWebView.loadUrl("javascript:(function() {document.getElementsByClassName('wrapper')[0].style.display='none';})()");
                                          super.onPageFinished(view, url);
                                          txtTitle.setText(view.getTitle());
                                          Handler handler = new Handler();
                                          handler.postDelayed(() ->
                                                  progressActivity.showContent(), 5000);
                                      }

                                      @SuppressWarnings("deprecation")
                                      @Override
                                      public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                          currentUrl = url;
                                          if (currentUrl.startsWith("https://www.ytest.youth4work.com/yAssess/yScore/")) {
                                              if(mTestId==123){
                                              if (currentUrl.length() > 5) {
                                                  mTestId = Integer.valueOf(currentUrl.substring(currentUrl.length() - 5));
                                              } else {
                                                  mTestId = Integer.valueOf(currentUrl);
                                              }
                                              }
                                              ResultActivity.show(false,mTestId,AllWebView.this);
                                          }
                                          /*else if(currentUrl.startsWith("https://www.yassess.youth4work.com/yAssess/SubjectiveTest/")){
                                              txtTitle.setText("Subjective Test");
                                          }else if(currentUrl.startsWith("https://www.yassess.youth4work.com/yAssess/coding-test/")){
                                              txtTitle.setText("Coding Test");
                                          }else if(currentUrl.startsWith("https://www.yassess.youth4work.com/yAssess/TypingTest/")){
                                              txtTitle.setText("Typing Test");
                                          }else if(currentUrl.contains("/PsychometricTest/Instruction/1")){
                                              txtTitle.setText("Psychometric Test");
                                          }*/
                                          else if (url.startsWith("mailto:")) {
                                              //Handle mail Urls
                                              startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse(url)));
                                          } else if (url.startsWith("tel:")) {
                                              //Handle telephony Urls
                                              startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(url)));
                                          } else {
                                              view.loadUrl(url);
                                          }
                                          return true;
                                      }

                                      @TargetApi(Build.VERSION_CODES.N)
                                      @Override
                                      public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                                          final Uri uri = request.getUrl();
                                          currentUrl = uri.toString();
                                          if (currentUrl.startsWith("https://www.ytest.youth4work.com/yAssess/yScore/")) {
                                              if(mTestId==123){
                                                  if (currentUrl.length() > 5) {
                                                      mTestId = Integer.valueOf(currentUrl.substring(currentUrl.length() - 5));
                                                  } else {
                                                      mTestId = Integer.valueOf(currentUrl);
                                                  }
                                              }
                                              ResultActivity.show(false,mTestId,AllWebView.this);
                                          }
                                          /*else if(currentUrl.startsWith("https://www.yassess.youth4work.com/yAssess/SubjectiveTest/")){
                                              txtTitle.setText("Subjective Test");
                                          }else if(currentUrl.startsWith("https://www.yassess.youth4work.com/yAssess/coding-test/")){
                                              txtTitle.setText("Coding Test");
                                          }else if(currentUrl.startsWith("https://www.yassess.youth4work.com/yAssess/TypingTest/")){
                                              txtTitle.setText("Typing Test");
                                          }else if(currentUrl.contains("/PsychometricTest/Instruction/1")){
                                              txtTitle.setText("Psychometric Test");
                                          }*/
//                                          if (currentUrl.equals("") || currentUrl.startsWith("https://www.yassess.youth4work.com/All-India-Institute-for-Local-Self-Government")) {
//                                              btnLogout.setVisibility(View.GONE);
//                                          } else {
//                                              btnLogout.setVisibility(View.VISIBLE);
//                                          }
                                        /*  if (currentUrl.equals("https://www.youth4work.com/")) {
                                              AllWebView.LoadWebView(AllWebView.this, "https://www.yassess.youth4work.com/All-India-Institute-for-Local-Self-Government", "yAssessments");
                                          } else*/
                                        else if (uri.toString().startsWith("mailto:")) {
                                              //Handle mail Urls
                                              startActivity(new Intent(Intent.ACTION_SENDTO, uri));
                                          } else if (uri.toString().startsWith("tel:")) {
                                              //Handle telephony Urls
                                              startActivity(new Intent(Intent.ACTION_DIAL, uri));
                                          } else {
                                              //Handle Web Urls
                                              view.loadUrl(uri.toString());
                                          }
                                          return true;
                                      }
                                  }
        );
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);
        if (!userName.equals(" ") && !password.equals(" ")) {
            String postData = "doOperation=login&txtEmail=" + userName + "&txtPwd=" + password;
            try {
                    mWebView.postUrl("https://www.youth4work.com/Users/Login?returnurl=" + URLEncoder.encode(url4load, "UTF-8"), EncodingUtils.getBytes(postData, "BASE64"));
            } catch (UnsupportedEncodingException e) {
                mWebView.loadUrl(url4load);
                e.printStackTrace();
            }
        } else {
            mWebView.loadUrl(url4load);
        }
        mWebView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request.grant(request.getResources());
                }
            }


            // For Android 5.0
            public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> filePath, FileChooserParams fileChooserParams) {
                // Double check that we don't have any existing callbacks
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePath;
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.e(TAG, "Unable to create Image File", ex);
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                    } else {
                        takePictureIntent = null;
                    }
                }
                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("image/*");
                Intent[] intentArray;
                if (takePictureIntent != null) {
                    intentArray = new Intent[]{takePictureIntent};
                } else {
                    intentArray = new Intent[0];
                }
                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);
                return true;
            }

            // openFileChooser for Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;
                // Create AndroidExampleFolder at sdcard
                // Create AndroidExampleFolder at sdcard
                File imageStorageDir = new File(
                        Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES)
                        , "AndroidExampleFolder");
                if (!imageStorageDir.exists()) {
                    // Create AndroidExampleFolder at sdcard
                    imageStorageDir.mkdirs();
                }
                // Create camera captured image file path and name
                File file = new File(
                        imageStorageDir + File.separator + "IMG_"
                                + String.valueOf(System.currentTimeMillis())
                                + ".jpg");
                mCapturedImageURI = Uri.fromFile(file);
                // Camera capture image intent
                final Intent captureIntent = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE);
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                // Create file chooser intent
                Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
                // Set camera intent to file chooser
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
                        , new Parcelable[]{captureIntent});
                // On select image call onActivityResult method of activity
                startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
            }

            // openFileChooser for Android < 3.0
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooser(uploadMsg, "");
            }

            //openFileChooser for other Android versions
            public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                        String acceptType,
                                        String capture) {
                openFileChooser(uploadMsg, acceptType);
            }

        });
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        {
            if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            }
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onBackPressed() {
      //  if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
      //  }

       /* this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
        @Override
         public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                    mainLogic();
                    // main logic
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            showMessageOKCancel("You need to allow access permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermission();
                                            }
                                        }
                                    });
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(AllWebView.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

/*

    @Override
    protected void onResume() {
        super.onResume();
        String lastFiveDigits = "";
        if (currentUrl.startsWith("https://www.yassess.youth4work.com/yAssess/Test/"))
            if (currentUrl.length() > 5) {
                lastFiveDigits = currentUrl.substring(currentUrl.length() - 5);
            } else {
                lastFiveDigits = currentUrl;
            }
        if (currentUrl.startsWith("https://www.yassess.youth4work.com/yAssess/Test/")) {
//            warnigCount++;
            int warningcount = settings.getInt("WARNING_COUNT", 0);
            if (warningcount < 3) {
                showAlertMessage();
            } else if (warningcount > 3) {
                Toast.makeText(this, "Test is submit successfully", Toast.LENGTH_SHORT).show();
                AllWebView.LoadWebView(AllWebView.this, "https://www.yassess.youth4work.com/yAssess/yScore/" + lastFiveDigits, "yAssessments");
                editor.putInt("WARNING_COUNT", 0);
                editor.commit();
            }
        }


    }

    private void showAlertMessage() {
        new AlertDialog.Builder(AllWebView.this)
                .setTitle("Warning")
                .setMessage("You are trying to minimize the app,If you minimize app again test will submit")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        dialog.dismiss();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                //.setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //currentUrl=currentUrl.substring(0,currentUrl.length()-8);
        if (currentUrl.startsWith("https://www.yassess.youth4work.com/yAssess/Test/")) {
            warnigCount++;
            editor.putInt("WARNING_COUNT", warnigCount);
            editor.commit();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        // currentUrl=currentUrl.substring(0,currentUrl.length()-8);
        if (currentUrl.startsWith("https://www.yassess.youth4work.com/yAssess/Test/")) {
            warnigCount++;
            editor.putInt("WARNING_COUNT", warnigCount);
            editor.commit();
        }
    }
*/



   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        *//**//*
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.menu_item_logout:
                android.webkit.CookieManager.getInstance().removeAllCookie();
                YAssessApplication.getInstance().clearApplicationData();
               //clearAppData();
                AllWebView.LoadWebView(AllWebView.this,"https://www.youth4work.com/master/logout","YAssessments");
                Toast.makeText(getApplicationContext(),"Logout",Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/
}
