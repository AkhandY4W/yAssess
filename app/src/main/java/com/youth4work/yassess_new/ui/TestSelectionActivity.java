package com.youth4work.yassess_new.ui;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.vlonjatg.progressactivity.ProgressRelativeLayout;
import com.youth4work.yassess_new.R;
import com.youth4work.yassess_new.adapter.TestListingAdapter;
import com.youth4work.yassess_new.network.model.TestModel;
import com.youth4work.yassess_new.network.model.response.CheckPasskeyStatusResp;
import com.youth4work.yassess_new.ui.base.BaseActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestSelectionActivity extends BaseActivity {
    RecyclerView categoriesTestList;
    TestListingAdapter adapter;
    TestModel testModel;
    ProgressRelativeLayout progressRelativeLayout;
    static String mPasskey = "";
    int currentpage = 1;
    boolean doubleBackToExitPressedOnce = false;
    private AppUpdateManager appUpdateManager;
    Button btnCheckPasskey;
    EditText edtPasskey;
    private static final int REQUEST_APP_UPDATE = 123;
    TextView txtError;
    public static void show(String passkey, Context context) {
        mPasskey = passkey;
        context.startActivity(new Intent(context, TestSelectionActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_selection);
        categoriesTestList = findViewById(R.id.categories_list);
        progressRelativeLayout = findViewById(R.id.progressActivity);
        btnCheckPasskey = findViewById(R.id.button);
        edtPasskey = findViewById(R.id.edt_passkey);
       txtError = findViewById(R.id.txt_error);
        progressRelativeLayout.showLoading();
       appUpdateManager = AppUpdateManagerFactory.create(TestSelectionActivity.this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(
                appUpdateInfo -> {
                    // Checks that the platform will allow the specified type of update.
                    if ((appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE)
                            && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                        // Request the update.
                        SharedPreferences preferences = getSharedPreferences("progress", MODE_PRIVATE);
                        int appUsedCount = preferences.getInt("appUsedCount", 0);
                        appUsedCount++;
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt("appUsedCount", appUsedCount);
                        editor.apply();
                        if (appUsedCount % 5 == 0) {
                            try {
                                appUpdateManager.startUpdateFlowForResult(
                                        appUpdateInfo,
                                        AppUpdateType.IMMEDIATE,
                                        this,
                                        REQUEST_APP_UPDATE);
                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
        if (!mPasskey.equals("")) {
            CheckPasskey(mPasskey);
        } else {
            getAllyAssessments(currentpage);
        }
        btnCheckPasskey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(edtPasskey.getText().toString().trim())) {
                    CheckPasskey(edtPasskey.getText().toString().trim());
                } else {
                    edtPasskey.setError("Passkey can't be empty");
                    edtPasskey.requestFocus();
                    // Toast.makeText(TestSelectionActivity.this, "Please check passkey", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void CheckPasskey(String mPasskey) {
        progressRelativeLayout.showLoading();
        yAssessServices.CheckPasskeyStatus(mPasskey).enqueue(new Callback<CheckPasskeyStatusResp>() {
            @Override
            public void onResponse(Call<CheckPasskeyStatusResp> call, Response<CheckPasskeyStatusResp> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CheckPasskeyStatusResp checkPasskeyStatusResp = response.body();
                    if (checkPasskeyStatusResp.getTestId() > 0 && checkPasskeyStatusResp.getStatus().equals("Active")) {
                        yAssessServices.UpdateInvitation(mPasskey, mUserManager.getUser().getUserId()).enqueue(new Callback<Boolean>() {
                            @Override
                            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                if (response.isSuccessful()) {
                                    progressRelativeLayout.showContent();
                                    PracticeTestActivity.show(checkPasskeyStatusResp.getTestId(), TestSelectionActivity.this,true);
                                }
                            }

                            @Override
                            public void onFailure(Call<Boolean> call, Throwable t) {

                            }
                        });

                    } else {
                        Toast.makeText(TestSelectionActivity.this, "Passkey is not valid", Toast.LENGTH_SHORT).show();
                        getAllyAssessments(currentpage);
                    }
                }
            }

            @Override
            public void onFailure(Call<CheckPasskeyStatusResp> call, Throwable t) {

            }
        });
    }

    private void getAllyAssessments(int currentPage) {

        yAssessServices.yAsessments(mUserManager.getUser().getUserId(),mUserManager.getUser().getEmailID(), currentPage, 100).enqueue(new Callback<TestModel>() {
            @Override
            public void onResponse(Call<TestModel> call, Response<TestModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    progressRelativeLayout.showContent();
                    //swipeRefresh.setRefreshing(false);
                    testModel = response.body();
                    if (testModel.getYAssessment().size() > 0) {
                        txtError.setVisibility(View.GONE);
                        categoriesTestList.setVisibility(View.VISIBLE);
                        adapter = new TestListingAdapter(testModel.getYAssessment(), TestSelectionActivity.this);
                        categoriesTestList.setLayoutManager(new LinearLayoutManager(TestSelectionActivity.this, LinearLayoutManager.VERTICAL, false));
                        categoriesTestList.setAdapter(adapter);
                    } else {
                        progressRelativeLayout.showContent();
                        categoriesTestList.setVisibility(View.GONE);
                        txtError.setVisibility(View.VISIBLE);
                        //progressRelativeLayout.showEmpty(emptyDrawable,"No test found","");
                        // Toast.makeText(TestSelectionActivity.this, "Sorry no test found", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<TestModel> call, Throwable t) {
                progressRelativeLayout.showContent();
                Toast.makeText(TestSelectionActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_item_logout:
                mUserManager.setUser(null);
                LoginActivity.showFromRegister(false, TestSelectionActivity.this);
                Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_LONG).show();
                return true;
                case R.id.menu_item_refresh:
                getAllyAssessments(1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}


