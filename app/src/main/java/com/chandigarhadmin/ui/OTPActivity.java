package com.chandigarhadmin.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.chandigarhadmin.App;
import com.chandigarhadmin.R;
import com.chandigarhadmin.interfaces.ResponseCallback;
import com.chandigarhadmin.models.CreateUserResponse;
import com.chandigarhadmin.models.LoginUserModel;
import com.chandigarhadmin.models.RequestParams;
import com.chandigarhadmin.session.SessionManager;
import com.chandigarhadmin.utils.Constant;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Response;

public class OTPActivity extends AppCompatActivity implements ResponseCallback {
    @BindView(R.id.et_email)
    EditText etEmail;
    // EditText etPhoneNumber;
    @BindView(R.id.submitbtn)
    Button submitBtn;
    private ProgressDialog progressDialog;
    private SessionManager sessionManager;

    @OnClick(R.id.submitbtn)
    public void submitClick() {
        if (validateEmail()) {
            getUserByEmail(etEmail.getText().toString());
        }
    }

    private void getUserByEmail(String email) {
        if (Constant.isNetworkAvailable(OTPActivity.this)) {
            if (!progressDialog.isShowing()) {
                progressDialog.show();
            }
            App.getApiController().getUserByEmail(this, email, RequestParams.TYPE_GET_USER_BY);
        } else {
            Constant.showToastMessage(OTPActivity.this, getString(R.string.no_internet));
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        ButterKnife.bind(this);
        progressDialog = Constant.createDialog(this, null);
        sessionManager = new SessionManager(this);
        //  submitBtn.setEnabled(false);
        //  submitBtn.setAlpha(0.4f);   //its like diming the brightness og button
       /* etPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 10) {
                    submitBtn.setAlpha(1.0f);
                    submitBtn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/
    }

    private boolean validateEmail() {
        if (!TextUtils.isEmpty(etEmail.getText()) && etEmail.getText().toString().matches(Constant.EMAIL_PATTERN)) {
            return true;
        } else {
            //  etPhoneNumber.setError(getString(R.string.phone_error));
            etEmail.setError(getString(R.string.email_error));
        }
        return false;
    }

    @Override
    public void onResponse(Response result, String type) {
        progressDialog.dismiss();
        Log.i("RESPONSE FROM server=", "" + result);


        if (result.isSuccessful()) {
            if (type.equalsIgnoreCase(RequestParams.TYPE_GET_USER_BY)) {
                CreateUserResponse response = (CreateUserResponse) result.body();

                //checking whether email returned in response matching with passed email or not
                if (null != response && Constant.checkString(response.getEmail())
                        && response.getEmail().contains(etEmail.getText().toString())) {
                    Constant.showToastMessage(OTPActivity.this, getString(R.string.email_exist));
                    sessionManager.createLoginSession(response.getFirstName(), response.getLastName(), response.getEmail(), response.getApiUser().getIsActive());
                    sessionManager.setKeyUserId(response.getId());
                    navigateToDashBoard();
                }
            } else {
                if (type.equalsIgnoreCase(RequestParams.TYPE_CREATE_USER)) {
                    Log.i("RESPONSE FROM server=", "TYPE_CREATE_USER   " + result.body());
                    if (result.isSuccessful()) {
                        CreateUserResponse response = (CreateUserResponse) result.body();
                        if (null != response && !Constant.checkString(response.getError())) {
                            sessionManager.createLoginSession(response.getFirstName(), response.getLastName(), response.getEmail(), response.getApiUser().getIsActive());
                            sessionManager.setKeyUserId(response.getId());
                            navigateToDashBoard();
                        } else if (null != response) {
                            Constant.showToastMessage(OTPActivity.this, response.getError());
                        }
                    } else {
                        try {
                            JSONObject jObjError = new JSONObject(result.errorBody().string());
                            if (jObjError.has("error")) {
                                Constant.showToastMessage(OTPActivity.this, jObjError.getString("error"));
                            }
                        } catch (Exception e) {
                            Constant.showToastMessage(OTPActivity.this, getString(R.string.something_wrong));
                        }

                    }
                }
            }
        } else {
            try {
                JSONObject jObjError = new JSONObject(result.errorBody().string());
                if (jObjError.has("error")) {
                    if (type.equalsIgnoreCase(RequestParams.TYPE_GET_USER_BY)) {
                        // navigateToConfirmOtp();
                        saveLoginDetail();
                    }
                }
            } catch (Exception e) {
                Constant.showToastMessage(OTPActivity.this, getString(R.string.something_wrong));
            }
        }


    }

    private void navigateToConfirmOtp() {
        Intent confirmOtp = new Intent(OTPActivity.this, ConfirmOtpActivity.class);
        confirmOtp.putExtra("phone", etEmail.getText().toString());
        confirmOtp.putExtra(Constant.INPUT_FIRST_NAME, getIntent().getStringExtra(Constant.INPUT_FIRST_NAME));
        if (null != getIntent() && getIntent().hasExtra(Constant.INPUT_EMAIL)) {
            confirmOtp.putExtra(Constant.INPUT_EMAIL, getIntent().getStringExtra(Constant.INPUT_EMAIL));
        }
        startActivity(confirmOtp);
    }

    private void saveLoginDetail() {
        if (null != progressDialog && progressDialog.isShowing()) {
            //already showing progress dialog
        } else {
            progressDialog.show();
        }

        LoginUserModel user = new LoginUserModel(etEmail.getText().toString(), getIntent().getStringExtra(Constant.INPUT_FIRST_NAME), getIntent().getStringExtra(Constant.INPUT_LAST_NAME));
        if (Constant.isNetworkAvailable(OTPActivity.this)) {
            App.getApiController().confirmOtp(this, user, RequestParams.TYPE_CREATE_USER);
        } else {
            Constant.showToastMessage(OTPActivity.this, getString(R.string.no_internet));
        }
    }

    private void navigateToDashBoard() {
        Intent it = new Intent(OTPActivity.this, AdminAgentActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(it);
        finish();
    }

    @Override
    public void onFailure(String message) {
        Constant.showToastMessage(OTPActivity.this, message);
    }

}
