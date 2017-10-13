package com.chandigarhadmin.ui;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chandigarhadmin.App;
import com.chandigarhadmin.R;
import com.chandigarhadmin.interfaces.ResponseCallback;
import com.chandigarhadmin.models.CreateUserResponse;
import com.chandigarhadmin.models.LoginUserModel;
import com.chandigarhadmin.models.RequestParams;
import com.chandigarhadmin.session.SessionManager;
import com.chandigarhadmin.utils.Constant;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, ResponseCallback {

    @BindView(R.id.titletv)
    TextView tvTitle;
    @BindView(R.id.proceedbtn)
    Button btnProceed;

    @BindView(R.id.etlastname)
    EditText etlastname;
    @BindView(R.id.etusername)
    EditText etUserName;
    @BindView(R.id.sign_in_button)
    SignInButton signInButton;
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog progressDialog;
    private SessionManager sessionManager;
    private String userEmail,userFirstName,userLastname;

    @OnClick(R.id.proceedbtn)
    public void clickProceedButton() {
        if (validateInputs()) {
            Intent it = new Intent(LoginActivity.this, OTPActivity.class);
            it.putExtra(Constant.INPUT_FIRST_NAME, etUserName.getText().toString());
            it.putExtra(Constant.INPUT_LAST_NAME, etlastname.getText().toString());
            startActivity(it);
        }
    }
    @OnClick(R.id.sign_in_button)
    public void googleSignIN() {
       signIn();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginscreen);
        ButterKnife.bind(this);
        progressDialog=Constant.createDialog(this,null);
        sessionManager=new SessionManager(this);
        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END build_client]

        // [START customize_button]
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        // [END customize_button]
    }
    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
           // Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            progressDialog.show();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    progressDialog.hide();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
       progressDialog.hide();
    }
    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    // [END onActivityResult]

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
      //  Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            userEmail=acct.getEmail();
            userFirstName=acct.getGivenName();
            userLastname=acct.getFamilyName();
            getUserByEmail(userEmail);
            /*Log.i("SIGIN SUCCESS",acct.getEmail());
            Log.i("DISPLAY NAME",acct.getDisplayName());
            Log.i("GIVE NAME",acct.getGivenName());
            Log.i("FAMILY NAME",acct.getFamilyName());*/

        } else {
            // Signed out, show unauthenticated UI.
          //  updateUI(false);
        }
    }
    // [END handleSignInResult]

    private boolean validateInputs() {

        if (!TextUtils.isEmpty(etUserName.getText().toString().trim()) && etUserName.getText().length() > 3) {

            return validateLastName();
        } else {
            etUserName.setError(getString(R.string.username_error));
        }
        return false;
    }

    private boolean validateLastName() {
        if (!TextUtils.isEmpty(etlastname.getText().toString().trim()) && etlastname.getText().length() > 3) {
            return true;
        } else {
            etlastname.setError(getString(R.string.username_error));
        }
        return false;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    // [START signIn]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]

    // [START signOut]
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                       // updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [START revokeAccess]
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                       // updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END revokeAccess]
    @Override
    protected void onStop() {
        super.onStop();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
    private void getUserByEmail(String email) {
        if (Constant.isNetworkAvailable(this)) {
            if (!progressDialog.isShowing()) {
                progressDialog.show();
            }
            App.getApiController().getUserByEmail(this, email, RequestParams.TYPE_GET_USER_BY);
        } else {
            Constant.showToastMessage(this, getString(R.string.no_internet));
        }

    }


    @Override
    public void onResponse(Response result, String type) {
        progressDialog.dismiss();
     //   Log.i("RESPONSE FROM server=", "" + result);

        if (result.isSuccessful()) {
            if (type.equalsIgnoreCase(RequestParams.TYPE_GET_USER_BY)) {
                CreateUserResponse response = (CreateUserResponse) result.body();

                //checking whether email returned in response matching with passed email or not
                if (null != response && Constant.checkString(response.getEmail())
                        && response.getEmail().contains(userEmail)) {
                    Constant.showToastMessage(LoginActivity.this, getString(R.string.email_exist));
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
                            Constant.showToastMessage(LoginActivity.this, response.getError());
                        }
                    } else {
                        try {
                            JSONObject jObjError = new JSONObject(result.errorBody().string());
                            if (jObjError.has("error")) {
                                Constant.showToastMessage(LoginActivity.this, jObjError.getString("error"));
                            }
                        } catch (Exception e) {
                            Constant.showToastMessage(LoginActivity.this, getString(R.string.something_wrong));
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
                Constant.showToastMessage(this, getString(R.string.something_wrong));
            }
        }

    }

    @Override
    public void onFailure(String message) {
        Constant.showToastMessage(this, message);

    }
    private void saveLoginDetail() {
        if (null != progressDialog && progressDialog.isShowing()) {
            //already showing progress dialog
        } else {
            progressDialog.show();
        }

        LoginUserModel user = new LoginUserModel(userEmail, userFirstName, userLastname);
        if (Constant.isNetworkAvailable(this)) {
            App.getApiController().confirmOtp(this, user, RequestParams.TYPE_CREATE_USER);
        } else {
            Constant.showToastMessage(this, getString(R.string.no_internet));
        }
    }

    private void navigateToDashBoard() {
        Intent it = new Intent(this, AdminAgentActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(it);
        finish();
    }
}
