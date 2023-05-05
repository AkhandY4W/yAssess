package com.youth4work.yassess_new.ui;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.mobsandgeeks.saripaar.QuickRule;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.vlonjatg.progressactivity.ProgressRelativeLayout;
import com.youth4work.yassess_new.R;
import com.youth4work.yassess_new.infrastructure.UserManager;
import com.youth4work.yassess_new.network.model.User;
import com.youth4work.yassess_new.network.model.request.UserRegister;
import com.youth4work.yassess_new.ui.base.BaseActivity;
import com.youth4work.yassess_new.utils.Constants;
import com.youth4work.yassess_new.utils.PreferencesManager;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterationActivity extends BaseActivity implements Validator.ValidationListener {
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS_and_ACCOUNT = 1;
    private static final String TAG = "LOCATION_TAG";
    static String mEmailId="";
    static String mobilenofrommobile="";
    static String locationfromGps="";
    static String  mPasskey="";
    static String emailfrommobile="";
    TextView txtTermsCondition, txtSignIn;
    ProgressRelativeLayout progressActivity;
    CheckBox checkBox;
    Boolean isChecked = false;
    @Order(1)
    MaterialEditText etFullName;
    @Order(2)
    MaterialEditText etEmail;
    @Order(3)
    MaterialEditText etUserName;
    @NotEmpty(sequence = 5)
    @Password(sequence = 6, message = "Password must have atleast 6 characters")
    @Order(4)
    MaterialEditText etPassword;
    @NotEmpty(sequence = 8)
    @Order(5)
    MaterialEditText etMobile;
    private Validator validator;
    private FusedLocationProviderClient fusedLocationClient;
    Location mLastLocation;
    ScrollView sv;
    Button btnSignUp, btnSeePass;

    public static void show(String emailid, Context mContxt) {
        mEmailId = emailid;
        mContxt.startActivity(new Intent(mContxt, RegisterationActivity.class));
    }

    public static void showFromPasskey(String passkey, Context mContxt) {
        mPasskey = passkey;
        mContxt.startActivity(new Intent(mContxt, RegisterationActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);
        txtTermsCondition = findViewById(R.id.txt_terms);
        txtSignIn = findViewById(R.id.txt_sign_in);
        etEmail = findViewById(R.id.et_email);
        etFullName = findViewById(R.id.et_full_name);
        etMobile = findViewById(R.id.et_mobile);
        etPassword = findViewById(R.id.et_password);
        etUserName = findViewById(R.id.et_user_name);
        sv = findViewById(R.id.scroll);
        btnSignUp = findViewById(R.id.btn_signUp);
        btnSeePass = findViewById(R.id.yourButton);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validateAndNextPhase();
            }
        });
        btnSeePass.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                    btnSeePass.setBackground(getResources().getDrawable(R.drawable.assets_01_ic_view));
                    break;
                case MotionEvent.ACTION_UP:
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    btnSeePass.setBackground(getResources().getDrawable(R.drawable.ic_password_view_off_24dp));
                    break;
            }
            return true;
        });
        progressActivity = findViewById(R.id.progressActivity);
        String text = "I have read & agree to Youth4work terms & conditions";
        SpannableString ss = new SpannableString(text);
        String text2 = "Already Registered Sign In";
        SpannableString ss2 = new SpannableString(text2);
        checkBox = findViewById(R.id.check_box);
        txtTermsCondition.setOnClickListener(v -> {
            if (!isChecked && !checkBox.isChecked()) {
                isChecked = true;
                checkBox.setChecked(isChecked);
            } else {
                isChecked = false;
                checkBox.setChecked(isChecked);
            }
        });
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                switch (widget.getId()) {
                    case R.id.txt_terms:
                        AllWebView.LoadWebView(RegisterationActivity.this, "https://www.youth4work.com/terms", "RegisterationActivity",123);
                        break;
                    case R.id.txt_sign_in:
                        SignInActivity.showWithPasskey(mPasskey, RegisterationActivity.this);
                        break;
                }
            }


            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.colorPrimary));
                ds.setUnderlineText(false);
            }
        };

        ss.setSpan(clickableSpan, 34, 52, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtTermsCondition.setText(ss);
        txtTermsCondition.setMovementMethod(LinkMovementMethod.getInstance());


        ss2.setSpan(clickableSpan, 19, 26, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtSignIn.setText(ss2);
        txtSignIn.setMovementMethod(LinkMovementMethod.getInstance());
        validator = new Validator(this);
        validator.setValidationListener(this);
        validator.setValidationMode(Validator.Mode.IMMEDIATE);
        etFullName.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence cs, int start,
                                               int end, Spanned spanned, int dStart, int dEnd) {
                        // TODO Auto-generated method stub
                        if (cs.equals("")) { // for backspace
                            return cs;
                        }
                        if (cs.toString().matches("[a-zA-Z ]+")) {
                            return cs;
                        }
                        return "";
                    }
                }
        });
        etUserName.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence cs, int start,
                                               int end, Spanned spanned, int dStart, int dEnd) {
                        // TODO Auto-generated method stub
                        if (cs.equals("")) { // for backspace
                            return cs;
                        }
                        if (cs.toString().matches("[a-zA-Z0-9_]+")) {
                            return cs;
                        }
                        return "";
                    }
                }
        });
        etEmail.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence cs, int start,
                                               int end, Spanned spanned, int dStart, int dEnd) {
                        // TODO Auto-generated method stub
                        if (cs.equals("")) { // for backspace
                            return cs;
                        }
                        if (cs.toString().matches("[a-zA-Z0-9_@.]+")) {
                            return cs;
                        }
                        return "";
                    }
                }
        });
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED ||
                (ContextCompat.checkSelfPermission(RegisterationActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(RegisterationActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.GET_ACCOUNTS,
                            Manifest.permission.READ_PHONE_STATE,
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS_and_ACCOUNT);
        } else {
            if (mEmailId.equals("")) {
                etEmail.setText(Constants.getPrimaryEmailID(this));
                emailfrommobile = etEmail.getText().toString().trim();
            } else {
                etEmail.setText(mEmailId);
               // emailfrommobile = etEmail.getText().toString().trim();
            }
          //  etMobile.setText(Constants.getMobileNo(this));
            mobilenofrommobile = etMobile.getText().toString().trim();
            getLocation();
        }
        setupValidations();

    }

    private void validateAndNextPhase() {

        validator.validate(true);

    }

    private void setupValidations() {
        validator.put(etUserName, usernameValidationRule);
        validator.put(etEmail, emailValidationRule);
        validator.put(etMobile, mobileValidationRule);
        validator.put(etFullName, fullNameValidationRule);
    }

    private QuickRule fullNameValidationRule = new QuickRule<MaterialEditText>(15) {

        @Override
        public boolean isValid(MaterialEditText materialEditText) {
            boolean isValid = true;
            String name = materialEditText.getText().toString().trim();
            if (name.contains(" ") && (name.charAt(name.length() - 1)) != ' ') {
                String split[] = name.split("\\s+");
                isValid = split[0].length() > 1 && split[1].length() > 0;
            } else {
                isValid = false;
            }
            return isValid;
        }

        @Override
        public String getMessage(Context context) {
            return "Write your full name";
        }
    };
    private QuickRule emailValidationRule = new QuickRule<MaterialEditText>(15) {

        @Override
        public boolean isValid(MaterialEditText materialEditText) {
            boolean isValid = true;
            String email = materialEditText.getText().toString().trim();
            if (isValidEmail(email)) {
                if (email != null && email.length() > 0) {
                    String responseString;
                    //  Toaster.showShort(SignUpActivity.this, "Validating Email...");
                    try {
                        responseString = yAssessServices.emailIDExists(email).execute().body().string();
                        if (responseString != null)
                            if (responseString.equalsIgnoreCase("\"0\"")) {
                                isValid = true;

                            } else if (responseString.equalsIgnoreCase("\"1\"")) {
                                isValid = false;
                            }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                isValid = false;
            }
            return isValid;
        }

        @Override
        public String getMessage(Context context) {
            return (!isValidEmail(etEmail.getText().toString().trim())) ? "Please enter correct email" : "Email already registered!";
        }
    };

    private QuickRule mobileValidationRule = new QuickRule<MaterialEditText>(16) {
        String msg="";
        @Override
        public boolean isValid(MaterialEditText materialEditText) {
            boolean isValid = true;

            String mobile = materialEditText.getText().toString().trim().replace("+", "");

            if (mobile != null && mobile.length() > 6&&mobile.length() <= 13) {
                try {
                    String responseString = yAssessServices.mobileExists(mobile).execute().body().string();
                    if (responseString != null)
                        if (responseString.equalsIgnoreCase("\"0\"")) {
                            isValid = true;
                        } else if (responseString.contains("1")) {
                            isValid = false;
                            msg="Mobile number already registered!";

                        }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                isValid=false;
                msg="Please enter valid 10 digit phone number";
            }
            return isValid;
        }

        @Override
        public String getMessage(Context context) {
            return msg;
        }
    };

    private QuickRule usernameValidationRule = new QuickRule<MaterialEditText>(17) {
        String msg = "";

        @Override
        public boolean isValid(MaterialEditText materialEditText) {
            boolean isValid = true;
            String username = materialEditText.getText().toString().trim();
            if (username != null && username.length() > 5) {
                // Toaster.showShort(SignUpActivity.this, "Validating UserName...");
                String responseString;
                try {
                    responseString = yAssessServices.usernameExists(username).execute().body().string();
                    if (responseString != null)
                        if (responseString.equalsIgnoreCase("\"0\"")) {
                            isValid = true;
                        } else if (responseString.equalsIgnoreCase("\"1\"")) {
                            isValid = false;
                            msg = "Username already exists ! Try different !";
                        }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                msg = "Username must have atleast 6 characters!";
                isValid = false;
            }

            return isValid;
        }

        @Override
        public String getMessage(Context context) {
            return msg;
        }
    };


    private void getLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                mLastLocation = location;
                setAddress(location);
            }
        });

    }

    private void setAddress(Location location) {
        Geocoder geocoder = new Geocoder(RegisterationActivity.this,
                Locale.getDefault());
        List<Address> addresses = null;
        String resultMessage = "";

        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, get just a single address
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems
            resultMessage = RegisterationActivity.this
                    .getString(R.string.service_not_available);
            Log.e(TAG, resultMessage, ioException);
        }

        if (addresses == null || addresses.size() == 0) {
            if (resultMessage.isEmpty()) {
                resultMessage = RegisterationActivity.this
                        .getString(R.string.no_address_found);
                Log.e(TAG, resultMessage);
            }
        } else {
            Address address = addresses.get(0);
            //StringBuilder out = new StringBuilder();
            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread
           /* for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                out.append(address.getAddressLine(i));
            }*/
            resultMessage = !address.getLocality().trim().equals("") ? address.getLocality().trim() : resultMessage;

        }
        locationfromGps = resultMessage;
        //etLocation.setText(resultMessage);
    }

    private void doRegister() {
        if (!checkBox.isChecked()) {
            Toast.makeText(RegisterationActivity.this, "Please accept terms and conditions", Toast.LENGTH_SHORT).show();
            checkBox.requestFocus();
        } else {
            progressActivity.showLoading();
            user = new UserRegister();
            if (user != null) {
                String finalmobileno = etMobile.getText().toString().trim();
                String finalEmailid = etEmail.getText().toString().trim();
                String ustatus = emailfrommobile.equals(finalEmailid) ? "A" : "P";
                Boolean mobilestatus = mobilenofrommobile.equals(finalmobileno);
                user.setData(etFullName.getText().toString(),
                        etUserName.getText().toString(),
                        etPassword.getText().toString().replace(" ", ""),
                        "ST",
                        etEmail.getText().toString(),
                        etMobile.getText().toString().replace("+1", "").replace("+91", "").replace("+", ""),
                        ustatus, "other",
                        "other",
                        locationfromGps,
                        "other",
                        "other",
                        "other", mobilestatus);
                //Log.e("UserJson",user.toString());
                yAssessServices.doSignUp(user).enqueue(new Callback<Long>() {
                    @Override
                    public void onResponse(Call<Long> call, @NonNull Response<Long> response) {
                        if (response.isSuccessful() && response.body() > 0) {
                            signIn();
                        }
                    }

                    @Override
                    public void onFailure(Call<Long> call, Throwable t) {
                        progressActivity.showContent();
                        Toast.makeText(RegisterationActivity.this, "Something went wrong,Please try again...", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void signIn() {
        yAssessServices.doSignIn(etEmail.getText().toString().trim(), etPassword.getText().toString().trim()).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getUserId() > 0) {
                    UserManager.getInstance(RegisterationActivity.this).setUser(response.body());
                    //doRegisterGcmUser(false);
                    PreferencesManager.instance(RegisterationActivity.this).saveLoginDetails(etEmail.getText().toString().trim(), etPassword.getText().toString().trim());
                    progressActivity.showContent();
                    //Toaster.showLong(RegisterationActivity.this, "Registration complete!");
                    Toast.makeText(RegisterationActivity.this, "Registration complete!", Toast.LENGTH_LONG).show();
                    finish();
                    if (!mPasskey.equals("")) {
                        SplashActivity.show(mPasskey, RegisterationActivity.this);
                    } else {
                        Intent intent = new Intent(RegisterationActivity.this, SplashActivity.class);
                        //AppEventsLogger logger = AppEventsLogger.newLogger(RegisterationActivity.this);
                        //logger.logEvent(AppEventsConstants.EVENT_NAME_COMPLETED_REGISTRATION);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });

    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    @Override
    public void onValidationSucceeded() {
        doRegister();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);
            if (view instanceof MaterialEditText) {
                ((MaterialEditText) view).setError(message);
                view.requestFocus();
                sv.scrollTo(0, view.getTop());
            } else if (view instanceof MaterialAutoCompleteTextView) {
                ((MaterialAutoCompleteTextView) view).setError(message);
                view.requestFocus();
                sv.scrollTo(0, view.getTop());
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS_and_ACCOUNT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 2) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                       if(mEmailId.equals("")){
                           etEmail.setText(Constants.getPrimaryEmailID(this));
                           emailfrommobile=etEmail.getText().toString().trim();
                       }
                    else {
                        etEmail.setText(mEmailId);
                       }
                    if (grantResults[1] == PackageManager.PERMISSION_GRANTED)
                      //  etMobile.setText(Constants.getMobileNo(this));
                    if (grantResults[2] == PackageManager.PERMISSION_GRANTED)
                        getLocation();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }

    }

    private boolean isValidMobile(String phone) {
        if(!Pattern.matches("[a-zA-Z]+", phone)) {
            return phone.length() > 6 && phone.length() <= 13;
        }
        return false;
    }
}
